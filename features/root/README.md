# Модуль: `:features:root`

Корневой навигационный оркестратор и точка входа в функциональный граф приложения TapReader.

---

## 1. Зона ответственности

* Управление глобальным деревом навигации и стеком экранов (`childStack`).
* Связывание дочерних фичей приложения (`Home`, `Profile`) без жесткого зацепления между ними.
* Отрисовка корневого UI (`RootContent`) с поддержкой кросс-платформенных анимаций переходов Decompose.

---

## 2. Публичный контракт (`api/`)

* **`RootComponent`:**
  ```kotlin
  interface RootComponent {
      val childStack: Value<ChildStack<*, Child>>
      sealed interface Child {
          data class Home(val component: HomeComponent) : Child
          data class Profile(val component: ProfileComponent) : Child
      }
      fun interface Factory
  }
  ```
* **`RootContent`:** Корневая Composable-функция, принимающая `RootComponent` и отрисовывающая активный дочерний экран.
* **`rootModule` (`api/di/`):** Koin-модуль, регистрирующий `RootComponent.Factory`.

---

## 3. Зависимости

### Зависит от:
* `:core:decompose` (базовые утилиты)
* `:features:home` (публичный контракт фичи Home)
* `:features:profile` (публичный контракт фичи Profile)

### От него зависят:
* `:shared` (подключает `RootContent` в корневой `App.kt` и инициализирует платформенные вьюконтейнеры).

---

## 4. История изменений модуля (Recent Changes)

* **2026-10-12 (TP-1):** Реализация корневого навигационного стека Decompose с дочерними компонентами Home и Profile ([ADR-0001](file:///home/emiryanvl/Проекты/tapreader/docs/adr/0001-pure-decompose-and-mvi.md)).
