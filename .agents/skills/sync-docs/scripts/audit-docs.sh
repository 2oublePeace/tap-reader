#!/usr/bin/env bash
# Скрипт аудита актуальности документации модулей
set -euo pipefail

ROOT_DIR="$(git rev-parse --show-toplevel 2>/dev/null || pwd)"
CHANGED_FILES="$(git diff --name-only HEAD 2>/dev/null || true)"

if [ -z "$CHANGED_FILES" ]; then
    CHANGED_FILES="$(git status -s | awk '{print $2}')"
fi

if [ -z "$CHANGED_FILES" ]; then
    echo "ℹ️  Рабочая директория чиста. Нет измененных файлов для проверки."
    exit 0
fi

echo "🔍 Проверка синхронизации документации с измененным кодом..."

WARNINGS=0

# Проверяем модули features и core
MODULES=$(echo "$CHANGED_FILES" | grep -E '^(features|core)/[^/]+/' | awk -F'/' '{print $1"/"$2}' | sort -u || true)

for mod in $MODULES; do
    if [ -d "$ROOT_DIR/$mod" ]; then
        # Проверяем, менялся ли код в модуле
        MOD_CODE_CHANGED=$(echo "$CHANGED_FILES" | grep "^$mod/src/" || true)
        if [ -n "$MOD_CODE_CHANGED" ]; then
            # Проверяем, менялся ли README.md в этом модуле
            MOD_README_CHANGED=$(echo "$CHANGED_FILES" | grep "^$mod/README.md" || true)
            if [ -z "$MOD_README_CHANGED" ]; then
                echo "⚠️  [ВНИМАНИЕ] В модуле '$mod' изменен исходный код, но паспорт '$mod/README.md' не обновлен!"
                WARNINGS=$((WARNINGS + 1))
            fi
        fi
    fi
done

# Проверяем CHANGELOG.md
CHANGELOG_CHANGED=$(echo "$CHANGED_FILES" | grep "docs/changelog/CHANGELOG.md" || true)
if [ -z "$CHANGELOG_CHANGED" ]; then
    CODE_CHANGES=$(echo "$CHANGED_FILES" | grep -E '\.(kt|kts)$' || true)
    if [ -n "$CODE_CHANGES" ]; then
        echo "⚠️  [ВНИМАНИЕ] Внесены изменения в код Kotlin, но 'docs/changelog/CHANGELOG.md' не обновлен!"
        WARNINGS=$((WARNINGS + 1))
    fi
fi

if [ "$WARNINGS" -eq 0 ]; then
    echo "✅ Все паспорта модулей и чейнджлог синхронизированы с кодом!"
    exit 0
else
    echo "------------------------------------------------------------------------"
    echo "💡 Обнаружено несоответствие документации ($WARNINGS предупреждений)."
    echo "👉 Попросите ИИ-ассистента: «актуализируй доку» или «/sync-docs»,"
    echo "   и он автоматически обновит паспорта модулей и чейнджлог!"
    echo "------------------------------------------------------------------------"
    exit 1
fi
