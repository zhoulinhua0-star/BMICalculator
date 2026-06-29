# Design Specification — BMI Calculator

A clean, professional SaaS design system inspired by [automanus.io](https://automanus.io/).
Light mode · Inter typeface · generous whitespace · subtle borders over heavy shadows.

These tokens are the single source of truth for the web version (`web/`). When in doubt,
prefer light backgrounds, restrained color, and breathing room over visual density.

---

## 1. Color / 配色

The palette is built on near-neutral slate tones with a single blue accent reserved for
interactive focus. Color is used sparingly — categories and status are the only places
where saturated hues appear.

### Core tokens

| Role | Token | Value | Usage |
|------|-------|-------|-------|
| Background (主背景) | `--bg` | `#ffffff` | Page + card surfaces |
| Background subtle | `--bg-subtle` | `#f8fafc` | Badges, ghost-button hover, advice block |
| Background muted | `--bg-muted` | `#f1f5f9` | Segmented-control track |
| Text primary (主文字) | `--text` | `#0f172a` | Headings, BMI number, key values |
| Text secondary | `--text-2` | `#374151` | Body copy, labels, advice text |
| Muted (辅助文字) | `--muted` | `#64748b` | Subtitles, captions, meta labels |
| Muted light | `--muted-2` | `#94a3b8` | Placeholders, footnotes, scale labels |
| Border (边框) | `--border` | `#e2e8f0` | Card / input / pill borders |
| Border light | `--border-2` | `#f1f5f9` | Internal dividers (history rows) |
| Accent (主色) | `--accent` | `#2563eb` | Focus ring, active-input border only |

### Semantic / category colors (辅助色)

Each health category uses a tinted background + matching border + saturated text, kept low
in saturation so they read as labels rather than alerts.

| Category | Background | Border | Text |
|----------|-----------|--------|------|
| Underweight | `#eff6ff` | `#bfdbfe` | `#1d4ed8` (blue) |
| Normal weight | `#f0fdf4` | `#bbf7d0` | `#15803d` (green) |
| Overweight | `#fffbeb` | `#fde68a` | `#92400e` (amber) |
| Obesity (I–III) | `#fff1f2` | `#fecdd3` | `#be123c` (rose) |

**Principle:** primary brand expression is *typography + whitespace*, not color. The blue
accent never fills large areas — the primary button is dark slate (`--text`), not blue.

---

## 2. Typography / 字体

**Family:** `Inter` (Google Fonts, weights 400/500/600/700/800), falling back to the system
UI stack. Tight negative letter-spacing on large headings; standard tracking on body.

| Level | Size | Weight | Line height | Letter-spacing | Color |
|-------|------|--------|-------------|----------------|-------|
| Hero title (H1) | `clamp(36px, 5.5vw, 54px)` | 800 | 1.08 | -0.035em | `--text` |
| BMI number (display) | `54px` | 800 | 1.0 | -0.04em | `--text` |
| Section / card title | `~0.9em` (≈15px) | 700 | 1.5 | normal | `--text` |
| Body / subtitle | `1.05em` (≈17px) | 400 | 1.68 | normal | `--muted` |
| Advice / paragraph | `0.9em` (≈14px) | 400 | 1.7 | normal | `--text-2` |
| Label | `0.84em` (≈13px) | 600 | 1.5 | normal | `--text-2` |
| Meta / eyebrow | `0.7em` (≈11px) | 700 | 1.5 | 0.08–0.09em (UPPERCASE) | `--muted` |
| Footnote | `0.77em` (≈12px) | 400 | 1.6 | normal | `--muted-2` |

**Hierarchy principles:**
- Large, bold, tightly-tracked headings vs. relaxed, muted body — high contrast in *weight
  and color*, not size alone.
- Body copy capped at ~460px max-width for readability.
- Uppercase micro-labels (eyebrows) with wide tracking mark each result block.

---

## 3. Spacing / 间距

Roughly an 8px-based rhythm. Whitespace is the primary structuring tool — sections get large
vertical padding; related elements stay tight.

### Layout

| Token | Value | Usage |
|-------|-------|-------|
| Content max-width | `760px` | Centered container (`.wrap`, nav, footer) |
| Page side padding | `20px` | Horizontal gutter |
| Hero padding | `84px` top / `52px` bottom | Section breathing room |
| Nav height | `56px` | Sticky bar |

### Inner / outer spacing

| Context | Value |
|---------|-------|
| Card padding (内边距) | `28px` |
| Card-to-card gap (模块间距) | `14px` |
| Form field group gap | `22px` |
| Side-by-side input gap | `14px` |
| Label → input gap | `7px` |
| Button gap | `10px` |
| Result block internal padding | `14–16px` |
| Hero badge → title | `28px` |
| Title → subtitle | `20px` |

**Principle:** generous outer whitespace, moderate inner padding, tight label/element gaps.
Sections should feel separated by air, not by lines.

---

## 4. Components / 组件样式

### Border-radius scale (圆角)

| Size | Value | Applied to |
|------|-------|-----------|
| Large | `20px` | Cards |
| Medium | `12px` | Info / advice blocks, segmented track |
| Standard | `10px` | Buttons, inputs |
| Small | `8px` | Segmented buttons |
| Pill | `999px` | Category pills, badges, history pills |

### Shadows (阴影)

Shadows are soft and minimal — depth comes mostly from borders.

| Token | Value | Usage |
|-------|-------|-------|
| Card | `0 1px 3px rgba(0,0,0,0.06), 0 4px 20px rgba(0,0,0,0.06)` | Cards |
| Segmented active | `0 1px 4px rgba(0,0,0,0.11)` | Selected unit chip |
| Focus ring | `0 0 0 3px rgba(37,99,235,0.1)` | Active input |
| Scale marker | `0 1px 6px rgba(0,0,0,0.22)` | BMI position marker |

### Buttons (按钮)

| Variant | Background | Text | Border | Radius | Padding | Weight |
|---------|-----------|------|--------|--------|---------|--------|
| Primary | `--text` (#0f172a) → hover `#1e293b` | `#ffffff` | none | `10px` | `12px 20px` | 700 |
| Ghost (secondary) | transparent → hover `--bg-subtle` | `--muted` → `--text-2` | `1px solid --border` | `10px` | `12px 20px` | 600 |
| Link | none | `--muted` → `--text` | none | — | 0 | 500 |
| Disabled | opacity `0.5`, no hover | — | — | — | — | — |

All buttons use `transition: 0.18s` on background/color.

### Cards (卡片)

- Background `--bg` (white)
- Border `1px solid --border` (`#e2e8f0`)
- Radius `20px`
- Shadow: card token above (soft, double-layer)
- Padding `28px`
- Stacked with `14px` gap

### Navigation bar (导航栏)

- Background `rgba(255,255,255,0.88)` with `backdrop-filter: blur(16px)` (frosted glass)
- Bottom border `1px solid --border` (no shadow)
- `position: sticky; top: 0`
- Height `56px`, content constrained to `760px`
- Brand text: 800 weight, -0.02em tracking; tag: pill badge, `--bg-subtle` fill

### Inputs

- Border `1px solid --border`, radius `10px`, padding `12px 14px`
- Focus: border → `--accent`, plus `3px` accent focus ring
- Placeholder color `--muted-2`

### Segmented control (unit selector)

- Track: `--bg-muted` fill, `999px`-ish radius (`11px`), `4px` inner padding
- Inactive chip: transparent, `--muted` text, weight 500
- Active chip: white fill, `--text`, weight 700, soft shadow

---

## 5. Motion / 动效

**Scroll Reveal** via the Intersection Observer API (no external library):
- Elements start at `opacity: 0; translateY(26px)`.
- On entering the viewport (threshold `0.08`), `.visible` is added → fade + slide up.
- Transition: `0.6s cubic-bezier(.4,0,.2,1)` on opacity + transform.
- Programmatically-shown cards (result, history) force a layout flush before revealing so the
  transition fires.

Micro-interactions use `0.18s` (controls) and the BMI scale marker eases over `0.4s`.

---

## Quick reference — CSS custom properties

```css
:root {
  --bg:          #ffffff;
  --bg-subtle:   #f8fafc;
  --bg-muted:    #f1f5f9;
  --text:        #0f172a;
  --text-2:      #374151;
  --muted:       #64748b;
  --muted-2:     #94a3b8;
  --border:      #e2e8f0;
  --border-2:    #f1f5f9;
  --accent:      #2563eb;
  --card-shadow: 0 1px 3px rgba(0,0,0,0.06), 0 4px 20px rgba(0,0,0,0.06);
}
```
