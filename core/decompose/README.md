# Модуль: `:core:decompose`

Базовый инфраструктурный модуль для интеграции и расширения библиотеки Decompose.

---

## 1. Зона ответственности

* Предоставление общих утилит жизненного цикла и скоупов корутин для Decompose-компонентов.
* Экспорт Decompose runtime и расширений Compose для функциональных модулей.
* Не содержит бизнес-логики и фича-специфичного кода.

---

## 2. Публичные компоненты

* **`CoroutineScopeExt.kt`:** Хелпер для создания `CoroutineScope`, привязанного к жизненному циклу компонента через Essenty `Lifecycle`. Автоматически отменяет выполнение корутин при уничтожении компонента (`doOnDestroy`).

---

## 3. Зависимости

### Экспортирует (api):
* `com.arkivanov.decompose:decompose`
* `com.arkivanov.decompose:extensions-compose`
* `com.arkivanov.essenty:lifecycle`

---

## 4. История изменений модуля (Recent Changes)

* **2026-10-12 (TP-1):** Удаление устаревших абстракций `RenderableComponent` и `Renderable` в пользу Pure Kotlin Decompose стандартов ([ADR-0001](file:///home/emiryanvl/Проекты/tapreader/docs/adr/0001-pure-decompose-and-mvi.md)).
