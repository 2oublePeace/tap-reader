# Манифест Compose: Стандарты верстки и оптимизации рекомпозиций

Данный манифест регламентирует правила разработки пользовательского интерфейса на **Jetpack & Multiplatform Compose**, требования к Compose Previews, стабильности типов и глубокой оптимизации производительности рекомпозиций в проекте **TapReader**.

---

## 1. Архитектурное разделение: Stateful Screen vs Stateless Content

Каждый экран строго делится на два уровня (паттерн **State Hoisting**):

```text
┌────────────────────────────────────────────────────────┐
│  XScreen (Stateful Container, public в api/)           │
│  - Зависит от XComponent                               │
│  - Подписывается на state через subscribeAsState()     │
│  - Делегирует onIntent = component::onIntent           │
└──────────────────────────┬─────────────────────────────┘
                           │ передает State и лямбду onIntent
┌──────────────────────────▼─────────────────────────────┐
│  XContent (Stateless Layout, internal в impl/ui/)      │
│  - Чистая верстка (ноль зависимостей от Decompose/DI)  │
│  - Принимает: state: XState, onIntent: (XIntent) -> Unit│
│  - Обязательно сопровождается @Preview                 │
└────────────────────────────────────────────────────────┘
```

### Пример эталонного разделения:
```kotlin
// api/ProfileScreen.kt (Stateful Container)
@Composable
fun ProfileScreen(
    component: ProfileComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.state.subscribeAsState()
    ProfileContent(
        state = state,
        onIntent = component::onIntent,
        modifier = modifier,
    )
}

// impl/ui/ProfileContent.kt (Stateless Layout)
@Composable
internal fun ProfileContent(
    state: ProfileState,
    onIntent: (ProfileIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        // Верстка экрана
    }
}
```

---

## 2. Правило Modifier

Каждая переиспользуемая Composable-функция **обязана принимать `modifier: Modifier = Modifier`**:

1. **Позиция параметра:** `modifier` всегда идет **первым опциональным параметром** после всех обязательных аргументов данных:
   ```kotlin
   @Composable
   fun BookCard(
       book: BookItem,                       // Обязательный параметр данных
       onBookClick: () -> Unit,              // Обязательный коллбэк
       modifier: Modifier = Modifier,        // Первый опциональный параметр
       isEnabled: Boolean = true,            // Последующие параметры с дефолтами
   )
   ```
2. **Применение:** Переданный `modifier` обязательно применяется к **корневому layout-элементу** внутри Composable-функции:
   ```kotlin
   @Composable
   fun BookCard(...) {
       Card(modifier = modifier.fillMaxWidth()) { ... }
   }
   ```
3. **Запрет жестких внешних отступов:** Запрещено задавать внешние `padding` внутри переиспользуемого компонента. Внешнее позиционирование — это ответственность родительского контейнера.

---

## 3. Требования к Compose Previews

Каждый файл `*Content.kt` **обязан** содержать минимум 2 приватные `@Preview`-функции:
1. Светлая тема с валидным состоянием контента.
2. Темная тема или краевое состояние (загрузка `isLoading = true`, ошибка `error != null`, пустое состояние).

```kotlin
@Preview
@Composable
private fun ProfileContentLightPreview() {
    TapReaderTheme(darkTheme = false) {
        ProfileContent(
            state = ProfileState(userName = "Алексей", booksReadCount = 5),
            onIntent = {},
        )
    }
}

@Preview
@Composable
private fun ProfileContentDarkLoadingPreview() {
    TapReaderTheme(darkTheme = true) {
        ProfileContent(
            state = ProfileState(isLoading = true),
            onIntent = {},
        )
    }
}
```

---

## 4. Оптимизация рекомпозиций и Best Practices (Performance Guide)

Compose работает в три фазы: **Composition -> Layout -> Draw**. Рекомпозиция (Composition) — самая тяжелая фаза. Наша цель — сделать Composable-функции **пропускаемыми (Skippable)** и минимизировать вызовы фазы Composition.

### 4.1. Стабильность типов и проблема коллекций Kotlin
Компилятор Compose пропускает рекомпозицию только тогда, когда **все** параметры функции являются **стабильными** (Stable / Immutable).
* **Ловушка стандартных коллекций:** Интерфейсы `List<T>`, `Set<T>`, `Map<T>` в стандартной библиотеке Kotlin **считаются нестабильными** компилятором Compose, потому что за интерфейсом `List` может скрываться мутабельный `ArrayList`! Из-за этого Composable с параметром `items: List<BookItem>` будет **рекомпозироваться всегда**, даже если список не изменился!
* **Решение:**
  1. Оборачивать списки в `@Immutable` дата-классы состояния UI:
     ```kotlin
     @Immutable
     data class LibraryState(
         val items: List<BookItem> = emptyList(),
     )
     ```
  2. Использовать `kotlinx.collections.immutable` (`ImmutableList<T>`, `persistentListOf()`).

### 4.2. Использование `derivedStateOf` для частых изменений
Если состояние меняется очень часто (например, пиксели скролла или прогресс чтения), но UI реагирует только на пороговое значение, **обязательно** используйте `derivedStateOf`:

```kotlin
// ПЛОХО: рекомпозирует экран на каждый пиксель скролла!
val showButton = listState.firstVisibleItemIndex > 0

// ИДЕАЛЬНО: рекомпозирует экран ТОЛЬКО в момент смены true <-> false
val showButton by remember {
    derivedStateOf { listState.firstVisibleItemIndex > 0 }
}
```

### 4.3. Отложенное чтение состояния (Чтение в Layout / Draw фазе)
Если значение меняется непрерывно (анимация, прозрачность, смещение жеста выделения текста), не передавайте его значение напрямую в фазу композиции. Используйте **лямбда-модификаторы**:

```kotlin
// ПЛОХО: смещение вызывает полную рекомпозицию функции 60/120 раз в секунду!
Modifier.offset(x = scrollOffset.dp, y = 0.dp)

// ИДЕАЛЬНО: пропускает фазу Composition и срабатывает сразу в Layout/Draw фазе!
Modifier.offset { IntOffset(x = scrollOffset.roundToInt(), y = 0) }

// ИДЕАЛЬНО: прозрачность через graphicsLayer минует рекомпозицию
Modifier.graphicsLayer { alpha = animatedAlpha }
```

### 4.4. Обязательный `key` в списках (`LazyColumn` / `LazyRow`)
При отображении списков всегда передавайте уникальный и стабильный `key`:

```kotlin
LazyColumn {
    items(
        items = state.books,
        key = { book -> book.id } // ОБЯЗАТЕЛЬНО!
    ) { book ->
        BookItemRow(book = book)
    }
}
```
*Без `key` при добавлении элемента в начало списка Compose полностью перерисует все элементы ниже. С `key` Compose просто анимированно сдвинет существующие элементы.*

### 4.5. Стабильность коллбэков и `rememberUpdatedState`
* Передавайте ссылки на методы (`component::onIntent`) вместо создания новых лямбд в теле верстки.
* Внутри `LaunchedEffect` или кастомных жестов оборачивайте коллбэки в `rememberUpdatedState`:
  ```kotlin
  val currentOnTimeout by rememberUpdatedState(onTimeout)
  LaunchedEffect(Unit) {
      delay(3000)
      currentOnTimeout() // всегда актуальный инстанс без перезапуска корутины
  }
  ```

### 4.6. Запрет тяжелых вычислений в теле `@Composable`
Запрещено выполнять сортировку списков, парсинг дат (`DateTimeFormatter`), токенизацию текста или тяжелую фильтрацию напрямую внутри тела `@Composable` функции:
* Все данные должны приходить в `*State` уже отформатированными и подготовленными из Presenter / UseCase.
* Если локальное преобразование неизбежно, оно должно быть обернуто в `remember(key) { ... }`.

---

## 5. Паттерн слотов (Slot API) для встраиваемых виджетов

Если экран или контейнер должен включать динамические дочерние блоки (виджет карточки перевода, кастомный заголовок, баннер):
```kotlin
@Composable
internal fun ReaderSectionCard(
    title: String,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Text(title)
        content() // Слот для передачи произвольного содержимого
    }
}
```
Это полностью исключает жесткую связность между модулями и экранами.
