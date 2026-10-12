# TapReader

> **TapReader** — кроссплатформенный трекер привычек нового поколения, созданный на базе **Kotlin Multiplatform (KMP)** и **Compose Multiplatform** с таргетами под **Android** и **iOS**.

---

## 🚀 Быстрый старт (Hello Guide)

### Требования к окружению:
* **JDK:** 17+
* **Android Studio:** Ladybug / Meerkat (с плагином Kotlin Multiplatform Mobile)
* **Xcode:** 15+ (для сборки iOS-приложения на macOS)

### Запуск приложений:

#### 1. Android
* **Из Android Studio:** Выберите конфигурацию запуска `androidApp` и нажмите **Run**.
* **Из терминала:**
  ```bash
  ./gradlew :androidApp:assembleDebug
  ```

#### 2. iOS
* **Из Xcode:** Откройте проект в папке `iosApp/iosApp.xcodeproj`, выберите симулятор iOS (например, iPhone 16) и нажмите **Run**.
* *Примечание:* KMP-модули собираются в нативный XCFramework автоматически через скрипт сборки Gradle при сборке в Xcode.

#### 3. Сервер / Backend *(в разработке)*
> *Секция будет дополнена инструкцией по развертыванию серверной части (Ktor Server / Docker), когда появится бэкенд проекта.*

---

## 🌿 Правила ветвления и коммитов (Gitflow)

Разработка ведется по модели **«3 Бакета»** (`feature` — продуктовый, `tech` — технический, `fix` — саппорт). Ветки именуются с поддержкой порядковой декомпозиции этапов внутри задачи:

```text
<тип>/TP-<номер-задачи>/<номер-ветки>-<краткое-описание>
```

### Примеры именования веток:
* `feature/TP-1/0-add-project-architecture` — первый этап задачи `TP-1` (Product Bucket).
* `feature/TP-1/1-setup-decompose-navigation` — второй этап задачи `TP-1`.
* `tech/TP-4/0-configure-room-database` — техническое развитие и инфраструктура (Tech Bucket).
* `fix/TP-12/0-fix-timer-notification-leak` — исправление багов (Support Bucket).

Формат сообщений коммитов: `<type>(TP-<номер>): <описание> [ADR-XXXX]`, где `<type>` — это `feature`, `tech` или `fix`.
Подробный регламент: [.agents/manifests/gitflow.md](.agents/manifests/gitflow.md).

---

## 📚 Документация и Архитектура

В проекте принята парадигма **Docs-as-Code (Документация как код)**. Вся инженерная документация версионируется вместе с кодом:

* 🏛️ **[Архитектурный Манифест](.agents/manifests/architecture.md)** — каноническая многомодульная архитектура, правила Decompose и MVI.
* 🎨 **[Манифест Compose](.agents/manifests/compose.md)** — регламент верстки, State Hoisting, Previews и оптимизация рекомпозиций.
* 🌿 **[Манифест Gitflow](.agents/manifests/gitflow.md)** — регламент веток, коммитов и защиты от мусора.
* 📝 **[Манифест Документации](.agents/manifests/documentation.md)** — регламент ведения ADR, RFC и паспортов модулей.
* 📜 **[Реестр решений ADR](docs/adr/)** — историческая летопись ключевых архитектурных решений проекта.
* 📋 **[Журнал релизов](docs/changelog/CHANGELOG.md)** — чейнджлог версий по стандарту Keep a Changelog.

---

## 🏗️ Генерация нового проекта через TapReader CLI

Из этого шаблона можно за пару секунд развернуть новый проект с вашим названием, пакетом и префиксом задач:

```bash
# Интерактивный мастер:
./tapreader.sh

# Или передача параметров через флаги:
./tapreader.sh \
  --name "FocusFlow" \
  --package "com.luckycubes.focusflow" \
  --prefix "FF" \
  --desc "Кроссплатформенный таймер фокусировки" \
  --out "/home/emiryanvl/Проекты/focusflow"
```
