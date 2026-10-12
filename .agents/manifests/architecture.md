Это отличный, вдохновляющий момент. Вы пришли к той самой точке архитектурной зрелости, когда стремление к идеалу соединяется с инженерным прагматизмом.

Перед вами — **Итоговый Архитектурный Манифест и Blueprint** для вашего флагманского KMP-проекта. Это не просто свод правил, а готовый фундамент, который обеспечит максимальное удовольствие от разработки сегодня, выдержит любые эксперименты завтра и позволит при необходимости без боли выделить любую фичу в SDK.

---

# Архитектурный Манифест KMP: «Чистота без оверхеда»

```
┌────────────────────────────────────────────────────────────────────────┐
│                        APPLICATIONS / LAUNCHERS                        │
│             [:androidApp]     [:iosApp]     [:desktopApp]              │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
┌───────────────────────────────────▼────────────────────────────────────┐
│                             ROOT & FLOWS                               │
│                   RootComponent -> FlowComponents                      │
└──────────────────┬─────────────────────────────────┬───────────────────┘
                   │                                 │
┌──────────────────▼───────────────┐ ┌───────────────▼───────────────────┐
│         FEATURE MODULES          │ │        SHARED DATA MODULES        │
│       [:feature:catalog]         │ │          [:data:cart]             │
│        [:feature:cart]           │ │          [:data:auth]             │
│  (Decompose + UseCases + UI)     │ │        (api/impl по нужде)        │
└──────────────────┬───────────────┘ └───────────────┬───────────────────┘
                   │                                 │
┌──────────────────▼─────────────────────────────────▼───────────────────┐
│                              CORE LAYER                                │
│       [:core:designsystem]  [:core:network]  [:core:database]          │
│                [:core:decompose]  [:core:platform]                     │
└────────────────────────────────────────────────────────────────────────┘
```

---

## Часть 1. Фундаментальные принципы (Golden Rules)

### 1. UI — это «глупая» функция от состояния и State Hoisting (Screen vs Content)
Экран не принимает решений, не ходит в сеть и не считает логику. UI строго делится на два уровня:
```kotlin
// 1. Stateful Container (Точка входа, public в api)
@Composable
fun CartScreen(component: CartComponent, modifier: Modifier = Modifier) {
    val state by component.state.subscribeAsState()
    CartContent(
        state = state,
        onIntent = component::onIntent,
        modifier = modifier
    )
}

// 2. Stateless Layout (Чистая верстка, internal в impl)
@Composable
internal fun CartContent(
    state: CartState,
    onIntent: (CartIntent) -> Unit,
    modifier: Modifier = Modifier
)
```
* **Зачем разделять `Screen` и `Content`:**
  1. **Compose Previews в IDE:** `@Preview` невозможно запустить, если функция требует живой `ComponentContext`, DI или репозитории. Чистый `Content(state = ...)` мгновенно отображается в превью для любых состояний (Loading, Error, Success).
  2. **Screenshot-тестирование (Paparazzi / Roborazzi):** Тестировать чистый `Content` элементарно — вы передаете инстанс данных `CartState` и проверяете пиксели без запуска эмуляторов и моков компонентов.
  3. **Стабильность рекомпозиции:** `Content` рекомпозируется только тогда, когда меняются конкретные поля `CartState`.

### 2. Decompose-компоненты свободны от UI-рантайма (Pure Kotlin) и Read-only State
Интерфейс компонента — это **Pure Kotlin**. В нем нет аннотаций `@Composable`, импортов `androidx.compose.*` и методов `Render()`.
Компонент публично выставляет поток состояния: `val state: Value<XState>`.
* **Почему публичный `state: Value<T>` — это Best Practice, а не нарушение инкапсуляции:**
  - `Value<T>` — строго **read-only** (иммутабельный срез данных). Мутация `_state = MutableValue(...)` инкапсулирована внутри `impl`.
  - Позволяет нативно рендерить экран на iOS через SwiftUI (`component.state.asObservableObject()`) без переписывания логики.
  - Компонент тестируется за 5 миллисекунд в чистом JVM-тесте без эмуляторов: проверяется `component.state.value`.

### 3. MVI Intent: Единая точка входа событий (`onIntent`)
**Строгое правило:** Запрещено плодить в интерфейсе компонента десятки разрозненных методов (`fun onRefresh()`, `fun onCartClicked()`, `fun onSearch(q: String)`).
Все пользовательские действия описываются через `sealed interface XIntent`, а компонент предоставляет единственный метод:
```kotlin
fun onIntent(intent: XIntent)
```
* **Профит:**
  - **Масштабируемость:** Добавление новой кнопки или действия на экране не меняет сигнатуру интерфейса компонента и его фабрики.
  - **Централизованное логирование и аналитика:** Все действия пользователя проходят через одну функцию в реализации компонента — элементарно логировать события или настроить Time-Travel Debugging.
  - **Строгая типизация:** Каждое действие задокументировано и типизировано в едином `sealed interface`.

### 4. Развязанная навигация через коллбэки фабрики (Decoupled Navigation)
Компонент фичи **никогда не знает о других экранах** и не выполняет навигацию самостоятельно.
Все навигационные события (переход к деталям, назад, логаут) передаются наверх родительскому графу через лямбды в `Factory`:
```kotlin
fun interface Factory {
    operator fun invoke(
        componentContext: ComponentContext,
        onNavigateToDetails: (id: String) -> Unit,
        onLogout: () -> Unit,
    ): XComponent
}
```
Родительский компонент (`RootComponent` или Flow) реализует эти коллбэки и переключает свой навигационный стек `childStack`.

### 5. Эволюционная модуляризация: «Правило пакетов против модулей»
* Не делите каждую фичу на 3–4 отдельных Gradle-модуля заранее.
* Фича создается как **один Gradle-модуль**.
* Внутренняя чистота и стабильность гарантируются двумя корневыми пакетами:
  * **`api`** (публичный контракт фичи):
    * `XComponent` (интерфейс компонента на чистом Kotlin).
    * `XState` (дата-класс состояния UI).
    * `XIntent` (sealed interface действий).
    * `XComponent.Factory` (фабрика с навигационными коллбэками).
    * `XScreen` (публичный Composable-контейнер для встраивания родителем).
    * `di/XModule` (Koin-модуль регистрации фабрики).
  * **`impl`** (внутренности фичи, закрытые модификатором `internal`):
    * `XComponentImpl` (управление состоянием, корутинами, юзкейсами).
    * `ui/XContent` (чистая верстка + `@Preview`).
    * `domain/` (юзкейсы для бизнес-логики).
    * локальные DTO и мапперы.
* **Физическое разделение на Gradle-модули `:api` и `:impl`** применяется **только** для общих модулей данных (`:data:*`), от которых зависят 3+ фичи, чтобы защитить инкрементальную компиляцию Gradle.

### 6. Domain-слой — инструмент от разрастания классов
UseCase создается не «для галочки», а чтобы:
1. Забрать нетривиальные вычисления из презентера/компонента (скидки, валидация, форматирование).
2. Объединить данные из двух источников.
3. Быть протестированным изолированно на чистом Kotlin в пакете `internal/domain`.

### 7. Документация как код (Docs-as-Code) и фиксация решений в ADR
* Архитектурные решения принимаются осознанно и протоколируются в виде **ADR** в каталоге `docs/adr/`.
* При проектировании нетривиальных функциональных блоков создается **RFC** в каталоге `docs/rfc/`.
* Каждый модуль проекта имеет паспорт **`README.md`** рядом со своим `build.gradle.kts`.
* Коммиты оформляются по стандарту Conventional Commits с идентификатором задачи `TP-<номер>` и ссылкой на ADR: `tech(TP-1): ... [ADR-0001]` или `feature(TP-2): ...`. Подробности: [gitflow.md](file:///home/emiryanvl/Проекты/tapreader/.agents/manifests/gitflow.md).
* **Запрет мусора:** Мелкие правки и опечатки в документации объединяются через `git commit --amend` или склеиваются в атомарный Squash PR, чтобы не загрязнять историю коммитов. Регламент: [documentation.md](file:///home/emiryanvl/Проекты/tapreader/.agents/manifests/documentation.md).

---

## Часть 2. Каноническая структура проекта

```text
├── build-logic/                      # Convention Plugins (наше спасение от Gradle-ада)
│   ├── src/main/kotlin/
│   │   ├── kmp.feature.gradle.kts    # Compose + Decompose + KotlinX
│   │   ├── kmp.data.gradle.kts       # Ktor + Room/SQL + Coroutines (без UI)
│   │   └── kmp.core.gradle.kts       # Базовая конфигурация таргетов
│
├── core/                             # Фундамент (не знает о фичах)
│   ├── designsystem/                 # Цвета, типографика, базовые компоненты, Res
│   ├── network/                      # Сконфигурированный базовый Ktor-клиент
│   ├── database/                     # Базовые драйверы / настройки Room
│   ├── platform/                     # Expect/Actual (файлы, системные алерты, haptics)
│   └── decompose/                    # Базовые утилиты Decompose (BaseComponent, CoroutineScope)
│
├── data/                             # Общие доменные сущности и хранилища (Shared Data)
│   ├── cart/
│   │   ├── api/                      # CartRepository, CartItem, CartSummary (Pure Kotlin)
│   │   └── impl/                     # CartRepositoryImpl, локальный кэш, запросы к API
│   └── auth/
│       ├── api/                      # TokenStorage, AuthState, UserRepository
│       └── impl/
│
├── features/                         # Вертикальные фичи приложения
│   ├── catalog/                      # Экран каталога товаров (Single Gradle Module)
│   │   ├── src/commonMain/kotlin/
│   │   │   └── com/luckycubes/tapreader/catalog/
│   │   │       ├── api/              # Публичный контракт фичи
│   │   │       │   ├── CatalogComponent.kt        # Pure Kotlin интерфейс с Factory
│   │   │       │   ├── CatalogState.kt            # Модель UI-состояния
│   │   │       │   ├── CatalogIntent.kt           # Пользовательские действия (MVI)
│   │   │       │   ├── CatalogScreen.kt           # Stateful Compose-контейнер
│   │   │       │   └── di/CatalogModule.kt        # Koin-модуль с регистрацией Factory
│   │   │       └── impl/             # Внутренняя реализация (internal)
│   │   │           ├── CatalogComponentImpl.kt    # Реализация Decompose-компонента
│   │   │           ├── domain/                    # Чистая бизнес-логика и юзкейсы
│   │   │           └── ui/
│   │   │               └── CatalogContent.kt      # Stateless Compose-верстка + @Preview
│   │   └── build.gradle.kts
│   ├── cart/
│   └── profile/
│
└── apps/                             # Запускаемые приложения (Launchers)
    ├── shared-root/                  # Корневой граф навигации (RootComponent) и общий DI
    ├── androidApp/                   # Android Single Activity
    ├── iosApp/                       # Xcode проект (SwiftUI Lifecycle + ComposeRoot)
    └── desktopApp/                   # Точка входа для Desktop (JVM)
```

---

## Часть 3. Эталонный код архитектурного среза (Code Blueprints)

### 1. Контракт фичи: Компонент, Стейт, Интенты (`api/`)
Чистый Kotlin. Внешний мир (Root) знает только этот контракт.

```kotlin
// api/CatalogState.kt
package com.luckycubes.tapreader.catalog.api

data class CatalogState(
    val products: List<ProductItem> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
)

// api/CatalogIntent.kt
package com.luckycubes.tapreader.catalog.api

sealed interface CatalogIntent {
    data object Refresh : CatalogIntent
    data class UpdateSearch(val query: String) : CatalogIntent
    data class SelectProduct(val id: String) : CatalogIntent
    data class AddToCart(val product: ProductItem) : CatalogIntent
}

// api/CatalogComponent.kt
package com.luckycubes.tapreader.catalog.api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value

interface CatalogComponent {
    val state: Value<CatalogState>
    fun onIntent(intent: CatalogIntent)

    // Фабрика с явным разделением на ComponentContext + Навигационные коллбэки
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onProductSelected: (productId: String) -> Unit,
            onOpenCart: () -> Unit,
        ): CatalogComponent
    }
}
```

---

### 2. UI-слой: Разделение Stateful Screen и Stateless Content

```kotlin
// api/CatalogScreen.kt (Public Stateful Container)
package com.luckycubes.tapreader.catalog.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.luckycubes.tapreader.catalog.impl.ui.CatalogContent

@Composable
fun CatalogScreen(
    component: CatalogComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.state.subscribeAsState()

    CatalogContent(
        state = state,
        onIntent = component::onIntent,
        modifier = modifier,
    )
}

// impl/ui/CatalogContent.kt (Internal Stateless Layout)
package com.luckycubes.tapreader.catalog.impl.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.luckycubes.tapreader.catalog.api.CatalogIntent
import com.luckycubes.tapreader.catalog.api.CatalogState

@Composable
internal fun CatalogContent(
    state: CatalogState,
    onIntent: (CatalogIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            Text(text = "Товаров: ${state.products.size}")
        }
    }
}

// Previews запускаются мгновенно без моков Decompose и DI!
@Preview
@Composable
private fun CatalogContentPreview_Loading() {
    CatalogContent(
        state = CatalogState(isLoading = true),
        onIntent = {},
    )
}
```

---

### 3. Decompose-реализация (`impl/CatalogComponentImpl.kt`)
Оркестратор: управляет жизненным циклом, хранит изменяемый стейт, диспетчеризирует интенты и вызывает юзкейсы/репозитории.

```kotlin
package com.luckycubes.tapreader.catalog.impl

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.launch
import com.luckycubes.tapreader.catalog.api.CatalogComponent
import com.luckycubes.tapreader.catalog.api.CatalogIntent
import com.luckycubes.tapreader.catalog.api.CatalogState
import com.luckycubes.tapreader.catalog.impl.domain.FilterProductsUseCase
import com.luckycubes.tapreader.data.cart.api.CartRepository

internal class CatalogComponentImpl(
    componentContext: ComponentContext,
    private val onProductSelected: (String) -> Unit,
    private val onOpenCart: () -> Unit,
    private val cartRepository: CartRepository,
    private val filterProductsUseCase: FilterProductsUseCase,
) : CatalogComponent, ComponentContext by componentContext {

    private val scope = coroutineScope()
    private val _state = MutableValue(CatalogState())
    override val state: Value<CatalogState> = _state

    override fun onIntent(intent: CatalogIntent) {
        when (intent) {
            is CatalogIntent.Refresh -> loadProducts()
            is CatalogIntent.SelectProduct -> onProductSelected(intent.id)
            is CatalogIntent.OpenCart -> onOpenCart()
            is CatalogIntent.AddToCart -> addToCart(intent.product)
            is CatalogIntent.UpdateSearch -> updateSearch(intent.query)
        }
    }

    private fun loadProducts() {
        _state.update { it.copy(isLoading = true) }
        // Загрузка через репозиторий / юзкейс
    }

    private fun addToCart(product: ProductItem) {
        scope.launch {
            cartRepository.addProduct(product.id)
        }
    }

    private fun updateSearch(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }
}
```

---

### 4. Доменный слой внутри фичи (`impl/domain/FilterProductsUseCase.kt`)
Чистая бизнес-логика. Ноль зависимостей от UI и платформы.

```kotlin
package com.luckycubes.tapreader.catalog.impl.domain

import com.luckycubes.tapreader.catalog.api.ProductItem

internal class FilterProductsUseCase {
    operator fun invoke(
        products: List<ProductItem>,
        query: String,
        selectedCategory: String?
    ): List<ProductItem> {
        return products
            .asSequence()
            .filter { selectedCategory == null || it.category == selectedCategory }
            .filter { it.title.contains(query, ignoreCase = true) }
            .sortedBy { it.price }
            .toList()
    }
}
```

---

### 5. Регистрация в DI (`api/di/CatalogModule.kt`)
Фабрика фичи инжектируется через DI, инкапсулируя статические зависимости:

```kotlin
package com.luckycubes.tapreader.catalog.api.di

import com.luckycubes.tapreader.catalog.api.CatalogComponent
import com.luckycubes.tapreader.catalog.impl.CatalogComponentImpl
import com.luckycubes.tapreader.catalog.impl.domain.FilterProductsUseCase
import org.koin.dsl.module

val catalogModule = module {
    factory { FilterProductsUseCase() }

    factory<CatalogComponent.Factory> {
        CatalogComponent.Factory { context, onProductSelected, onOpenCart ->
            CatalogComponentImpl(
                componentContext = context,
                onProductSelected = onProductSelected,
                onOpenCart = onOpenCart,
                cartRepository = get(),
                filterProductsUseCase = get(),
            )
        }
    }
}
```

---

## Часть 4. Готовность к будущему (Extension Roadmap)

Благодаря заложенным «линиям разреза», ваше приложение готово к любым трансформациям:

```text
                                ТЕКУЩЕЕ СОСТОЯНИЕ
                        [:feature:cart] (Single Module)
                                        │
        ┌──────────────────────────────┼──────────────────────────────┐
        │ Потребовался SDK             │ Потребовался SwiftUI         │ Нужен Web / Desktop
        ▼                              ▼                              ▼
[:kits:cart-sdk]              Выделяем [:feature:cart:ui]      Просто добавляем target:
  - Собирает AAR              Логика остается в core-impl.     jvm(), wasmJs()
  - Собирает XCFramework      iOS пишет свой CartView.swift    Decompose и Data уже готовы!
  - 0 переписывания кода!     на чистом SwiftUI.               (0 платформенных связей)
```

1. **Если завтра нужен SDK:** Создаем модуль `:kits:my-sdk`, подключаем нужные фичи, генерируем `XCFramework` / `AAR`. Сами фичи менять не нужно.
2. **Если завтра нужен чистый SwiftUI на iOS:** Выносим Composable-функцию в `:feature:X:ui`, отдаем iOS-команде чистый `CatalogComponent` — они подписываются на стейт и рисуют нативный UI.
3. **Если завтра нужен Desktop / Web:** Добавляем в `build-logic` таргеты `jvm()` и `wasmJs()`. Поскольку ваши компоненты, юзкейсы и данные не знают об Android-специфичных штуках (нет `android.content.Context`), они заведутся на десктопе и в браузере «из коробки».

---

## Часть 5. Пошаговый алгоритм создания новой фичи (Feature Checklist)

Каждый разработчик или ИИ-ассистент при добавлении новой фичи должен строго следовать этому алгоритму:

1. **Шаг 1. Описать состояние и действия в `api/`:**
   - Создать `XState` (иммутабельный `data class` с дефолтными значениями).
   - Создать `sealed interface XIntent` (все возможные события от пользователя).
2. **Шаг 2. Создать контракт компонента в `api/`:**
   - Объявить `interface XComponent`:
     - `val state: Value<XState>`
     - `fun onIntent(intent: XIntent)`
     - `fun interface Factory` с `ComponentContext` и навигационными лямбда-коллбэками.
3. **Шаг 3. Реализовать компонент в `impl/`:**
   - Создать `internal class XComponentImpl : XComponent, ComponentContext by componentContext`.
   - Инкапсулировать `private val _state = MutableValue(XState())` и `override val state = _state`.
   - Реализовать `override fun onIntent(intent: XIntent) = when(intent) { ... }`.
4. **Шаг 4. Создать UI-слой:**
   - В `impl/ui/XContent.kt` создать `internal fun XContent(state, onIntent, modifier)` + `@Preview`.
   - В `api/XScreen.kt` создать `fun XScreen(component, modifier)` (подписывается на `component.state` и делегирует `component::onIntent`).
5. **Шаг 5. Зарегистрировать DI в `api/di/`:**
   - Создать `val xModule = module { factory<XComponent.Factory> { ... } }`.
   - Подключить `xModule` в общее DI-дерево (`initKoin`).
6. **Шаг 6. Встроить в родительский граф навигации:**
   - Добавить дочерний инстанс в `Child` и `Config` родительского компонента (`RootComponent` / Flow).
   - Отрендерить экран в родительской функции контента (`RootContent`).

---

## Часть 6. Каталог антипаттернов (Anti-Patterns / Code Smells)

При код-ревью и валидации архитектуры строго запрещены:

* ❌ **Антипаттерн 1: Compose-зависимости в интерфейсе компонента.**
  * *Симптом:* В `XComponent` появляются аннотации `@Composable`, параметры `Modifier` или метод `fun Render()`.
  * *Правило:* Интерфейс компонента — это **Pure Kotlin**. Вся верстка живёт строго в `XScreen` и `XContent`.
* ❌ **Антипаттерн 2: Разрастание методов в интерфейсе компонента вместо `onIntent`.**
  * *Симптом:* В `XComponent` добавляются `fun onRefreshClicked()`, `fun onProductClicked(id: String)`.
  * *Правило:* Все события передаются только через `fun onIntent(intent: XIntent)`.
* ❌ **Антипаттерн 3: Сокрытие `state` из публичного интерфейса компонента.**
  * *Симптом:* Компонент не выставляет `val state: Value<XState>`.
  * *Правило:* Компонент обязан выставлять read-only `Value<XState>`, чтобы обеспечить поддержку SwiftUI на iOS и быстрые JVM-тесты.
* ❌ **Антипаттерн 4: Прямая навигация из фичи к другим фичам.**
  * *Симптом:* Фича A зависит от фичи B или сама манипулирует глобальным роутером.
  * *Правило:* Фича изолирована. Любые переходы наружу объявляются как лямбды в `XComponent.Factory`.
* ❌ **Антипаттерн 5: Монолитный Composable-экран без разделения на `Screen` и `Content`.**
  * *Симптом:* Весь UI написан в `XScreen(component: XComponent)`, нет функции `XContent(state: XState, onIntent: (XIntent) -> Unit)`.
  * *Правило:* Отсутствие `XContent` ломает Compose Previews в Android Studio и усложняет UI-тестирование.
* ❌ **Антипаттерн 6: Утечка деталей реализации из `impl`.**
  * *Симптом:* `XComponentImpl` или `XContent` объявлены как `public`.
  * *Правило:* Все классы в `impl` должны быть строго `internal`. Внешний мир видит только то, что находится в `api`.

---

## Финальное напутствие

Вы нашли идеальный баланс: **не плодить лишние модули и бойлерплейт сегодня, но писать код так, чтобы его можно было безболезненно масштабировать завтра.**

Держите компоненты независимыми от UI, делайте экраны управляемыми через MVI-стейт и интенты, выносите тяжелую логику в маленькие юзкейсы — и ваш проект станет эталоном современной, мощной и элегантной KMP-архитектуры. В добрый путь!