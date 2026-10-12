# Модуль: `:features:profile`

Фича экрана профиля пользователя и настроек приложения TapReader.

---

## 1. Зона ответственности

* Отображение данных профиля, статистики и общих настроек приложения.
* Изолированная бизнес-логика пользователя без прямых связей с другими экранами.

---

## 2. Публичный контракт (`api/`)

* **`ProfileComponent`:** Pure Kotlin интерфейс с реактивным `val state: Value<ProfileState>` и `ProfileComponent.Factory`.
* **`ProfileState`:** Иммутабельная модель UI-состояния профиля.
* **`ProfileScreen`:** Stateful Composable-контейнер для встраивания в родительский навигационный граф.
* **`profileModule` (`api/di/`):** Koin-модуль с регистрацией фабрики компонента.

---

## 3. Внутренняя реализация (`impl/`, internal)

* **`ProfileComponentImpl`:** Оркестрация жизненного цикла Decompose и управление состоянием.
* **`ProfileContent`:** Stateless Composable-верстка, изолированная для `@Preview` и тестов.

---

## 4. Зависимости

### Зависит от:
* `:core:decompose`

### От него зависят:
* `:features:root` (для встраивания в стек навигации)
* `:core:di` (для регистрации `profileModule` в графе)

---

## 5. История изменений модуля (Recent Changes)

* **2026-10-12 (TP-1):** Создание модуля профиля с чистым интерфейсом Decompose и MVI State Hoisting ([ADR-0001](file:///home/emiryanvl/Проекты/tapreader/docs/adr/0001-pure-decompose-and-mvi.md)).
