# Журнал изменений (Changelog)

Все заметные изменения в проекте **TapReader** фиксируются в этом файле.

Формат основан на [Keep a Changelog](https://keepachangelog.com/ru/1.1.0/),
и проект придерживается [Семантического версионирования (SemVer)](https://semver.org/lang/ru/).

---

## [0.1.0] - 2026-10-12

### Added
- Базовая многомодульная архитектура Kotlin Multiplatform (KMP) с поддержкой Android и iOS (Apple Silicon).
- Подключение и настройка **Decompose** для навигации и управления состоянием жизненного цикла.
- Модуль `:features:root` с корневым навигационным стеком `ChildStack` (`Home`, `Profile`) и анимациями переходов.
- Модули `:features:home` и `:features:profile` с чистыми Decompose-компонентами и разделением на `api` и `impl`.
- Внедрение зависимостей на базе **Koin**.
- Инфраструктура **Docs-as-Code**: каталоги `docs/adr/`, `docs/rfc/` и `docs/changelog/`.
- Манифесты разработки в `.agents/manifests/`: [gitflow.md](file:///home/emiryanvl/Проекты/tapreader/.agents/manifests/gitflow.md), [compose.md](file:///home/emiryanvl/Проекты/tapreader/.agents/manifests/compose.md), [documentation.md](file:///home/emiryanvl/Проекты/tapreader/.agents/manifests/documentation.md), [architecture.md](file:///home/emiryanvl/Проекты/tapreader/.agents/manifests/architecture.md).
- [ADR-0001: Использование Pure Kotlin Decompose компонентов и MVI State Hoisting](file:///home/emiryanvl/Проекты/tapreader/docs/adr/0001-pure-decompose-and-mvi.md).
- Корневой гид быстрого старта [README.md](file:///home/emiryanvl/Проекты/tapreader/README.md).
- Универсальный [AGENTS.md](file:///home/emiryanvl/Проекты/tapreader/AGENTS.md) и навык синхронизации документации [.agents/skills/sync-docs/](file:///home/emiryanvl/Проекты/tapreader/.agents/skills/sync-docs/).
- Модульные паспорта (`README.md`) для `:features:root`, `:features:home`, `:features:profile`, `:core:decompose`, `:core:di`.

