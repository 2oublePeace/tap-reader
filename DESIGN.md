---
tokens:
  color:
    # Base Neutral Surfaces & Content
    surface:
      light: { value: "#FAFAFA", type: "color" }
      light_elevated: { value: "#FFFFFF", type: "color" }
      dark: { value: "#121214", type: "color" }
      dark_elevated: { value: "#1A1A1E", type: "color" }
    text:
      light_primary: { value: "#1A1A1A", type: "color" }
      light_secondary: { value: "#666666", type: "color" }
      light_tertiary: { value: "#9E9E9E", type: "color" }
      dark_primary: { value: "#EDEDED", type: "color" }
      dark_secondary: { value: "#A1A1A6", type: "color" }
      dark_tertiary: { value: "#636366", type: "color" }
    border:
      light: { value: "#E5E5EA", type: "color" }
      dark: { value: "#2C2C2E", type: "color" }

    # Brand & Interactive Accent
    primary:
      light: { value: "#2C5282", type: "color" }
      dark: { value: "#63B3ED", type: "color" }

    # Reader Reading Tone Palette (Utility / Future custom theme)
    reading_tones:
      sepia_bg: { value: "#F4ECD8", type: "color" }
      sepia_text: { value: "#433422", type: "color" }
      parchment_bg: { value: "#EFE6D5", type: "color" }
      parchment_text: { value: "#382F26", type: "color" }

    # Reader Highlight Tokens (Subtle Pastel Tints & Underlines)
    highlight:
      # Word tap
      word_tap_tint: { value: "rgba(66, 153, 225, 0.16)", type: "color" }
      word_tap_line: { value: "#3182CE", type: "color" }
      # Sentence multi-touch drag
      sentence_tint: { value: "rgba(226, 232, 240, 0.45)", type: "color" }
      sentence_line: { value: "#A0AEC0", type: "color" }
      # Detected Idioms / Phrasal Verbs
      idiom_tint: { value: "rgba(183, 148, 244, 0.18)", type: "color" }
      idiom_line: { value: "#805AD5", type: "color" }
      # Saved vocabulary / Flashcards
      saved_tint: { value: "rgba(246, 224, 94, 0.22)", type: "color" }
      saved_line: { value: "#D69E2E", type: "color" }

  typography:
    # Reader Typography (High Legibility Long-form)
    reader:
      font_family: { value: "'Source Serif 4', 'Merriweather', Georgia, serif", type: "fontFamily" }
      font_size: { value: "19px", type: "dimension" }
      line_height: { value: "1.65", type: "number" }
      letter_spacing: { value: "0.01em", type: "dimension" }
    # UI Typography (Neutral Sans-Serif)
    ui_font_family: { value: "system-ui, -apple-system, 'SF Pro Display', Roboto, sans-serif", type: "fontFamily" }
    ui_sizes:
      caption: { font_size: "11px", line_height: "14px", font_weight: "500" }
      body_sm: { font_size: "13px", line_height: "18px", font_weight: "400" }
      body_md: { font_size: "15px", line_height: "20px", font_weight: "400" }
      title_sm: { font_size: "16px", line_height: "22px", font_weight: "600" }
      title_md: { font_size: "20px", line_height: "26px", font_weight: "600" }

  spacing:
    unit: { value: "4px", type: "dimension" }
    sm: { value: "8px", type: "dimension" }
    md: { value: "12px", type: "dimension" }
    lg: { value: "16px", type: "dimension" }
    xl: { value: "24px", type: "dimension" }

  radius:
    badge: { value: "4px", type: "dimension" }
    card: { value: "8px", type: "dimension" }
    popover: { value: "10px", type: "dimension" }
    sheet: { value: "16px", type: "dimension" }
---

# Tap Reader Design System

## 1. Visual Theme & Philosophy
- **Style:** Minimalist, editorial, flat, distraction-free modern mobile UI.
- **Form Follows Reading:** Maximum screen estate is reserved for typography. Fixed UI elements (status bars, app bars) fade away or collapse cleanly to minimize visual noise.
- **Cross-Platform Neutrality:** Avoid platform-heavy accents. Use clean geometry, standard system fonts for chrome UI, and refined serif typography for text consumption.

## 2. Text Highlighting Mechanics
- **Interaction rule:** The font color of highlighted text remains unchanged to preserve natural readability.
- **Visual styling:** Highlights use a dual visual affordance:
   1. A low-opacity soft pastel rectangular box tint (15–20% alpha) behind the text span.
   2. A crisp 1.5px solid or dashed bottom underline marker using the respective highlight line token.
- **Selection States:**
   - `Single Word Tap`: Fast lookup marker with a micro translation popover.
   - `Sentence Drag`: Continuous sentence tint with an anchored expandable sheet.
   - `Idiom Detection`: Subtle purple-tinted span indicating an idiomatic phrase.

## 3. Core Components

### Book Shelf Card (`BookCard`)
- 2-column balanced grid with `16px` outer gutters and `12px` cross-axis spacing.
- **Cover Image:** Aspect ratio `3:4` or `2:3` with subtle `8px` border radius and a 1px inner hairline border (`border.light` / `border.dark`).
- **Card Content:**
   - Primary title (truncated at 2 lines, `title_sm`).
   - Author name (`body_sm`, muted tertiary color).
   - Badges row: Language tag (e.g., `EN`, `DE`) and CEFR level pill badge (e.g., `B1`, `C1`).
   - Linear Progress Bar: 4px track height placed below cover or title with percentage label (`caption`).

### Quick Translation Popover (`TapPopover`)
- Attached directly above or below the tapped word.
- Compact floating panel (`radius.popover`, flat border, subtle surface elevation).
- Elements: Target word, phonetic transcription (`/tæp/`), primary contextual translation, and quick "Bookmark / Save" icon button.

### Deep Translation & Grammar Sheet (`BottomSheet`)
- Modally docked at the bottom with `radius.sheet` top corners.
- Displays full contextual sentence translation, alternative meanings, and idiom grammar breakdowns.

## 4. Do's and Don'ts
- **DO** keep margins generous around reading text (minimum 16dp to 20dp horizontal padding).
- **DO** ensure highlight backgrounds use low opacity so black/white text maintains accessible contrast.
- **DON'T** introduce heavy drop shadows, skeuomorphic gradients, or flashy rounded bubbles.
- **DON'T** let interface chrome compete with the book text; toolbar icons should use thin/regular weights.