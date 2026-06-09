# Origin Launcher — Design Language & Architecture Reference

> **Purpose:** This document captures the full design philosophy, component hierarchy, visual system, animation language, code architecture, and integration contracts of Origin Launcher. Any AI or human contributor should read this first to make changes that blend in as native components.

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Visual Identity](#2-visual-identity)
3. [Typography & Font System](#3-typography--font-system)
4. [Color System & Theming](#4-color-system--theming)
5. [Component Architecture](#5-component-architecture)
6. [Animation & Motion Language](#6-animation--motion-language)
7. [Code Architecture & Patterns](#7-code-architecture--patterns)
8. [System Integration Contracts](#8-system-integration-contracts)
9. [Origin Modes: Pure vs Flow](#9-origin-modes-pure-vs-flow)
10. [Do Not Touch](#10-do-not-touch)
11. [Feature Checklist](#11-feature-checklist)
12. [Design Anti-Patterns](#12-design-anti-patterns)
13. [Critical Values Map](#13-critical-values-map)

---

## 1. Project Overview

**Stack:** Android Launcher (AOSP Launcher3 + Lawnchair fork)
**Language:** 2,589 Kotlin files + 1,756 Java files = 4,345 source files
**Min SDK:** Android 8.0 (API 26)
**Target SDK:** Android 16 (API 37, "Baklava")
**Build System:** Gradle with version catalog (`gradle/libs.versions.toml`)
**DI:** Custom Dagger (no Hilt), scopes `@LauncherAppSingleton` / `@ActivityContextSingleton`

### Module Map

| Module | Path | Purpose |
|--------|------|---------|
| `:app` (root) | `src/com/android/launcher3/` | AOSP Launcher3 core (55+ packages, mostly Java) |
| `:quickstep` | `quickstep/src/` | Recents/Overview system (Java + Kotlin MVVM refactor) |
| `:lawnchair` | `lawnchair/src/app/lawnchair/` | All custom features (Kotlin, 30+ packages) |
| `:systemui:*` | `systemUI/*/` | SystemUI library modules (shared, plugin, animation, etc.) |
| `:platform\_frameworks\_libs\_systemui:*` | submodule | 8 SystemUI libraries (iconloader, searchui, animation, msdl, etc.) |
| `:compatLib:*` | `compatLib/` | Multi-version (API 29-36) compatibility stubs |
| `:wmshell` | `wmshell/` | Window Manager Shell (AIDL interfaces, desktop mode) |
| `:hidden-api` | `hidden-api/` | Hidden Android API stubs via `RefineAs` |
| `:modules:widgetpicker` | `modules/widgetpicker/` | Widget picker dynamic feature module |

### Key Class Hierarchy

```
LauncherApplication (Application)
  └─ LawnchairApp (LauncherApplication)     [lawnchair/src/.../LawnchairApp.kt]

BaseActivity
  └─ StatefulActivity<LauncherState>
       └─ Launcher                            [src/.../Launcher.java]
            └─ QuickstepLauncher              [quickstep/src/.../QuickstepLauncher.java]
                 └─ LawnchairLauncher          [lawnchair/src/.../LawnchairLauncher.kt]
```

---

## 2. Visual Identity

### Design Philosophy

- **Strong defaults over endless customization** — Origin offers two curated modes (Pure, Flow) instead of 100+ individual toggles
- **Material You as foundation** — dynamic colors from wallpaper, system palette integration
- **Clarity and intentional spacing** — no random padding, consistent rhythm
- **Glassmorphism in Flow mode** — frosted glass surfaces, floating elements, depth layering

### Corner Radius Scale

| Token | Pure Default | Flow Default | Used By |
|-------|-------------|--------------|---------|
| General surface radius | 12dp | 28dp | Dialog corners, cards |
| Search group radius | 28dp | 28dp | Search bar container |
| Search result radius | 4dp | 4dp | Individual search results |
| Smartspace card radius | 20dp | 20dp | At-a-glance sub-cards |
| Smartspace secondary radius | 28dp | 28dp | Secondary glance cards |
| Icon shape | `circle` | `roundedSquare` | App icons |
| Folder shape | `circle` | `roundedSquare` | Folder icons |

**Rule:** Always use `lawnchair_dialog_corner_radius` (24dp) for dialogs. Never hardcode corner radii. Read from `OriginModeConfig.cornerRadiusDp` for surface radii.

### Surface System

| Surface | Pure | Flow | Color Token |
|---------|------|------|-------------|
| Background | Solid, 100% opaque | Opaque with optional blur | `ColorTokens.Surface` |
| Dock | Solid bar | Floating, glass (12% opacity) | `ColorTokens.ColorBackgroundFloating` |
| Search bar | Solid/inline | Floating, glass (12% opacity) | `ColorTokens.SearchboxHighlight` |
| App drawer backdrop | 50% opacity scrim | 30% opacity scrim + blur | `ColorTokens.AllAppsScrimColor` |
| Recents background | Solid | Translucent (configurable alpha) | `ColorTokens.OverviewScrim` |

### Icon System

- **Pure mode:** Full color icons, labels on home screen and drawer, page indicator visible
- **Flow mode:** Monochrome icons (when `useMonochromeIcons`), no labels on home screen, no page indicator
- **Icon shapes:** Loaded via `IconShapeManager` — supports `circle` (default Pure), `roundedSquare` (default Flow), `squircle`, `teardrop`, `cylinder`, and custom via `IconShape`
- **Icon packs:** Full support via `lawnchair/src/app/lawnchair/icons/iconpack/`

---

## 3. Typography & Font System

### Base Font

**Google Sans Flex** — variable font loaded from `R.font.googlesansflex_variable`

### Font Axes

| Axis | Tag | Range | Description |
|------|-----|-------|-------------|
| Weight | `wght` | 100–900 | Font weight |
| Roundness | `ROND` | 0–100 | Glyph roundness (set to 100) |
| Grade | `GRAD` | -200–150 | Font grade/optical weight |

### Preset Variants

| Variant | wght | ROND | GRAD | Usage |
|---------|------|------|------|-------|
| `uiRegular` | 400 | 100 | 100 | Default body text |
| `uiMedium` | 500 | 100 | 0 | Medium emphasis |
| `uiText` | 400 | 100 | 0 | Body text (lower grade) |
| `uiTextMedium` | 500 | 100 | 100 | Medium body text |

### Font Roles

| Role | Preference Key | Fallback |
|------|---------------|----------|
| `font_base_icon` | `fontWorkspace` | `sans-serif` |
| `font_button` | `fontHeadingMedium` | `sans-serif-medium` |
| `font_heading` | `fontHeading` | `sans-serif` |
| `font_heading_medium` | `fontHeadingMedium` | `sans-serif-medium` |
| `font_body` | `fontBody` | `sans-serif` |
| `font_body_medium` | `fontBodyMedium` | `sans-serif-medium` |

### Font Loading Pipeline

`FontCache.kt` → `FontManager.kt` (applies via `specMap`) → `CustomFont` styleable attributes (`customFontType`, `customFontWeight`) → Fallback to system sans-serif.

**Supported font types:** Resource (bundled variable), TTF (custom files), System (installed fonts), Google Fonts (via GMS provider), Asset (bundled assets).

---

## 4. Color System & Theming

### Color Pipeline

```
PreferenceManager2 (accentColor, colorStyle)
    ↓
ThemeProvider (singleton, Dagger @LauncherAppSingleton)
    ↓
ColorOption (SystemAccent | WallpaperPrimary | CustomColor | Default)
    ↓
MonetColorSchemeCompat → DynamicColorScheme / MaterialYouTargets
    ↓
ColorTokens (~80 tokens) → SwatchColorToken(scheme.neutral1/accent1/accent2/accent3[shade])
    ↓
DrawableTokens / ColorStateListTokens / Compose ColorScheme
```

### Color Sources (ColorOption sealed class)

| Source | Description | Class |
|--------|-------------|-------|
| `SystemAccent` | From Android system palette (`system_neutral1_*`, `system_accent1_*`) | `SystemColorScheme.kt` |
| `WallpaperPrimary` | Extracted from wallpaper via `WallpaperManagerCompat` | `WallpaperPrimary` |
| `CustomColor` | User-chosen arbitrary color | `CustomColor(color: Int)` |
| `Default` | Falls back to `OriginBlue` = `0xFF0077FF` | `Default` |

### Color Styles (Monet Style)

| Style | Key | Description |
|-------|-----|-------------|
| Spritz | `spritz` | Desaturated, muted |
| Tonal Spot | `tonal_spot` | Default Material You |
| Vibrant | `vibrant` | High saturation |
| Expressive | `expressive` | Bold, expressive |
| Rainbow | `rainbow` | Multi-color |
| Fruit Salad | `fruit_salad` | Playful, varied |
| Content | `content` | Content-based |
| Monochromatic | `monochromatic` | Single hue (Flow default) |

### Color Swatches

5 swatches × 16 shades each (S0, S10, S20, S50, S100, S200, S300, S400, S500, S600, S650, S700, S800, S900, S950, S1000):
- **Neutral1** — Primary neutral palette (surfaces, backgrounds)
- **Neutral2** — Secondary neutral palette (variants, containers)
- **Accent1** — Primary accent (primary buttons, selected states)
- **Accent2** — Secondary accent (popups, secondary surfaces)
- **Accent3** — Tertiary accent (notification dots, folder dots)

### Key Color Tokens (ColorTokens.kt)

**Surfaces:**
- `Surface` / `SurfaceLight` / `SurfaceDark` — Main backgrounds
- `SurfaceDimColor` / `SurfaceBrightColor` — Elevation variants
- `SurfaceContainerHighest` / `SurfaceContainerLow` — Container surfaces
- `ColorBackground` / `ColorBackgroundFloating` — Floating UI backgrounds

**Text:**
- `TextColorPrimary` / `TextColorPrimaryInverse` / `TextColorSecondary`

**Accent/Buttons:**
- `ColorAccent` — Accent color
- `PrimaryButton` — Primary button fill

**All Apps:**
- `AllAppsScrimColor` — Static `#404040` at 40% alpha
- `AllAppsTabBackground` / `AllAppsTabBackgroundSelected`
- `AllAppsHeaderProtectionColor`

**Overview (Recents):**
- `OverviewScrimColor` / `OverviewScrimOverBlurColor`
- `OverviewScrim` (preference-aware, supports translucent mode)

**Search:**
- `SearchboxHighlight` / `SearchboxHighlightBlur`
- `QsbIconTintPrimary/Tertiary/Quaternary`

**Folders:**
- `FolderBackgroundColor` / `FolderDotColor` / `FolderPaginationColor`
- `FolderPreviewColor` / `FolderIconBorderColor`

**Popups:**
- `PopupColorPrimary/Tertiary` — Three-tier popup surface
- `PopupShadeFirst/Second/Third`

**Notifications:**
- `DotColor` = Accent3_200
- Notification dot override via `notificationDotColorOverride` preference

### Token Modifiers

Tokens can be chained:
```kotlin
val OverviewScrim = OverviewScrimColor
    .withPreferences { prefs -> /* preference-aware override */ }
    .setAlpha(0.40f)
    .setLStar(87.0)
    .withContext { context, scheme, colorMode -> /* dynamic override */ }
```

### Compose Theme Bridge

`ColorScheme.toComposeColorScheme(isDark: Boolean)` in `ComposeColorScheme.kt` converts Monet `ColorScheme` → Material3 `darkColorScheme()`/`lightColorScheme()` using luminance-based tone mapping (e.g., `primary(80)` = accent1 at 80% tone).

Custom surface tones: 4, 6, 12, 17, 22, 24, 87, 92, 94, 96, 98.

### Drawable Tokens (~23 tokens in `DrawableTokens.kt`)

Examples:
- `BgCellLayout` — Workspace cell background
- `DropTargetBackground` — Drag target highlight
- `SearchInputFg` — Search bar foreground
- `AllAppsTabsBackground` — Tab strip
- `WidgetAddButtonBackground` — Add widget button
- `WorkCard` — Work profile indicator

---

## 5. Component Architecture

### 5.1 All Apps Screen (App Drawer)

**Key files:**
- `src/com/android/launcher3/allapps/ActivityAllAppsContainerView.java` — Main container (extends `SpringRelativeLayout`)
- `src/com/android/launcher3/allapps/AlphabeticalAppsList.java` — App list model
- `src/com/android/launcher3/allapps/AllAppsRecyclerView.java` — RecyclerView
- `lawnchair/src/app/lawnchair/allapps/LawnchairAlphabeticalAppsList.kt` — Lawnchair override (hidden apps, drawer folders, app categorization)
- `lawnchair/src/app/lawnchair/allapps/AllAppsSearchInput.kt` — Search input (Lawnchair)
- `lawnchair/src/app/lawnchair/allapps/FallbackSearchInputView.kt` — EditText search field

**Architecture:**
- 3 `AdapterHolder` slots: `MAIN` (A-Z), `WORK` (work profile), `SEARCH` (search results)
- Tab strip via `AllAppsPagedView` + `PersonalWorkSlidingTabStrip`
- `SearchTransitionController` for A-Z ↔ search animation
- Spring-based scroll physics via `SpringRelativeLayout`

**Lawnchair modifications:**
- `LawnchairAlphabeticalAppsList.addAppsWithSections()` extends the list with categorized/folder layout
- `AllAppsSearchInput` replaces standard QSB with Lawnchair search (customizable, supports 24 search providers)
- Hidden apps filtering, drawer folders, app categorization via Flowerpot
- **Auto-show keyboard:** The keyboard automatically opens when the app drawer opens (`SearchBarStateHandler.kt`). Controlled by `autoShowKeyboardInDrawer` preference (default: `true`). Toggle in Settings → Drawer → Search → "Auto-show keyboard".

### 5.2 Hotseat / Dock

**Key files:**
- `src/com/android/launcher3/Hotseat.java` — Main hotseat view (extends `CellLayout`)
- `lawnchair/src/app/lawnchair/hotseat/HotseatMode.kt` — Mode enum

**Modes:**
- `LawnchairHotseat` → `R.layout.search_container_hotseat` (Lawnchair QSB)
- `GoogleSearchHotseat` → `R.layout.search_container_hotseat_google_search` (Google QSB)
- `DisabledHotseat` → `R.layout.empty_view` (no search bar)

**Channel system:** `MultiValueAlpha` for independent icon/QSB fade channels:
- `ALPHA_CHANNEL_TASKBAR_ALIGNMENT`
- `PREVIEW_RENDERER`
- `TASKBAR_STASH`
- `ASSISTANT_VISIBILITY`

**Floating dock:** When `originConfig.isFloatingDock`, glass effect background with insets/tint applied in `setUpBackground()`.

### 5.3 Folder

**Key files:**
- `src/com/android/launcher3/folder/Folder.java` — Main folder view (extends `AbstractFloatingView`)
- `src/com/android/launcher3/folder/FolderIcon.java` — Folder icon on workspace/drawer
- `src/com/android/launcher3/folder/FolderAnimationManager.java` — Open/close animation
- `src/com/android/launcher3/folder/FolderAnimationSpringBuilderManager.kt` — Spring physics variant

**Architecture:**
- `Folder` overlays workspace via `AbstractFloatingView`
- Contains `FolderPagedView` (multi-page CellLayout) + `FolderNameEditText` + page indicator + pagination arrows
- `FolderIcon` is the mini preview on workspace (clipped via `ClippedFolderIconPreviewLayout`)
- Open/close animates: scales from icon size → full folder, translates to icon position, reveals background via shape delegate

**Lawnchair modifications:**
- `round_rect_folder` drawable replaced with `DrawableTokens.RoundRectFolder`
- Background alpha, folder color from Lawnchair preferences
- Spring physics when `enableExpressiveFolderExpansion` flag is set

### 5.4 Search / QSB

**Key files:**
- `src/com/android/launcher3/qsb/QsbContainerView.java` — QSB Fragment host
- `lawnchair/src/app/lawnchair/qsb/LawnQsbLayout.kt` — Custom QSB layout
- `lawnchair/src/app/lawnchair/qsb/providers/QsbSearchProvider.kt` — Provider interface

**Search providers:** 24 providers including Google, AppSearch, Bing, Firefox, DuckDuckGo, Startpage, Brave, SearXNG, Yahoo, Ecosia, etc.

**Architecture:**
- `QsbContainerView` is a `FrameLayout` hosting an `AppWidgetHostView` for Google QSB widget
- `LawnQsbLayout` replaces it with customizable view: G-icon, mic, lens, glass/filled background, stroke color/width, corner radius, transparency
- `AllAppsSearchInput` serves as both hotseat QSB and all-apps search bar
- Search algorithm: `LawnchairSearchAlgorithm` (app search + web search delegation)

### 5.5 Widgets

**Key files:**
- `src/com/android/launcher3/widget/BaseWidgetSheet.java` — Bottom sheet base
- `src/com/android/launcher3/widget/picker/WidgetsFullSheet.java` — Full picker
- `src/com/android/launcher3/widget/picker/WidgetsTwoPaneSheet.java` — Two-pane picker
- `lawnchair/src/app/lawnchair/widgets/OriginWidgetStyle.kt` — Widget style enum

**Architecture:**
- `BaseWidgetSheet` is a slide-in bottom sheet
- `WidgetsFullSheet`/`WidgetsTwoPaneSheet` show widget list with section headers, expandable categories, search
- `WidgetCell` renders individual widget previews
- `LauncherAppWidgetHostView` manages widget rendering on workspace

**Lawnchair additions:**
- `OriginGlanceWidget` (Compose-based) — glanceable smartspace targets
- `OriginWidgetStyle` with PURE and FLOW variants

### 5.6 Smartspace / Glance

**Key files:**
- `lawnchair/src/app/lawnchair/smartspace/BcSmartspaceView.kt` — Main view (FrameLayout with ViewPager)
- `lawnchair/src/app/lawnchair/smartspace/BcSmartspaceCard.kt` — Individual card
- `lawnchair/src/app/lawnchair/smartspace/SmartspaceProvider.kt` — Singleton provider

**Architecture:**
- `BcSmartspaceView` contains `ViewPager` for card swiping + `PageIndicator`
- `SmartspaceProvider` singleton emits `List<SmartspaceTarget>` from multiple `SmartspaceDataSource` implementations:

| Data Source | Shows |
|-------------|-------|
| `BatteryStatusProvider` | Battery level |
| `NowPlayingProvider` | Currently playing music |
| `TorchProvider` | Flashlight status |
| `DateProvider` | Date/time with ICU formatting |

**Rendering:** Custom `DoubleShadowIconDrawable`, `DoubleShadowTextView`, ICU-based date formatting.

### 5.7 Settings (Compose UI)

**Key files:**
- `lawnchair/src/app/lawnchair/ui/preferences/Preferences.kt` — Compose entry point
- `lawnchair/src/app/lawnchair/ui/preferences/PreferenceViewModel.kt` — ViewModel
- `lawnchair/src/app/lawnchair/ui/preferences/navigation/PreferenceNavigation.kt` — NavHost
- `lawnchair/src/app/lawnchair/ui/preferences/navigation/PreferenceRoutes.kt` — All routes
- `lawnchair/src/app/lawnchair/ui/preferences/destinations/PreferencesDashboard.kt` — Tabbed dashboard (Simple/Advanced)
- `lawnchair/src/app/lawnchair/ui/preferences/destinations/GeneralPreferences.kt` — Theming hub (colors, icons, notification dots, font)
- `lawnchair/src/app/lawnchair/ui/preferences/destinations/FontCustomizationPreferences.kt` — Font role picker (sub-page of General)
- `lawnchair/src/app/lawnchair/ui/preferences/destinations/FontSelectionPreference.kt` — Full font browser with search & custom fonts
- `lawnchair/src/app/lawnchair/ui/preferences/destinations/FontSelectionPreference.kt` — Full font browser with search & custom fonts

**Architecture:**
- Full Kotlin/Compose UI using Jetpack Navigation + Material3
- 28 destination screens
- Routes defined as `@Serializable` sealed interfaces
- Two-pane layout on large screens via Accompanist TwoPane
- Navigation animation: `materialSharedAxisXIn/Out`

**Preferences system:**
- Legacy: `PreferenceManager` (direct SharedPreferences)
- Modern: `PreferenceManager2` (uses `Opto` library — type-safe prefs with Flow support)

### General Screen (Theming Hub)

**File:** `lawnchair/src/app/lawnchair/ui/preferences/destinations/GeneralPreferences.kt`

The General screen is the central theming hub. It exposes:

| Section | Items |
|---------|-------|
| **Theme** | Accent color, Icon pack, Icon shape, Custom icon shape, Icon size, Monochrome icons toggle, **Font** (navigates to `FontCustomization`) |
| **Notification Dots** | Badge toggle, dot color, text color |

### Font Customization

**Files:**
- `FontCustomizationPreferences.kt` — Lists all 5 font roles with current font preview
- `FontSelectionPreference.kt` — Full font browser with search, system/variable/Google/custom fonts

**Font roles** (5 roles, each independently configurable):

| Role | Preference Key | Default |
|------|---------------|---------|
| Base Icon | `fontWorkspace` | `uiText` |
| Heading | `fontHeading` | `uiRegular` |
| Heading Medium | `fontHeadingMedium` | `uiMedium` |
| Body | `fontBody` | `uiText` |
| Body Medium | `fontBodyMedium` | `uiTextMedium` |

**Flow:** General → Font → tap a role → `FontSelection` (picker with search, radio buttons, variant dropdown)

**Enable/disable:** A toggle at the top of Font Customization screen (`enableFontSelection` preference). When disabled, fonts fall back to system defaults. Previously buried in Experimental Features.

### Dashboard Layout (PreferencesDashboard.kt)

Two-tier tab system (`TwoTabPreferenceLayout`):

| Tab | Description | Items |
|-----|-------------|-------|
| **Simple** | Curated essentials, 5 items | General, At a Glance, App Drawer, Origin Modes, About |
| **Advanced** | Superset of Simple + all configuration | General, At a Glance, App Drawer, Home Screen, Dock, Search Bar, Folders, Gestures, Recents, Backup & Restore, Personalization, Experimental Features |

**Rules:**
- **Advanced tab is always a strict superset of Simple tab** — no item disappears when switching tabs
- Each entry has a **unique, distinct icon** (no `ic_general` reuse)
- New top-level screens must be added to **both** tabs
- Icon assignments: General → `ic_general`, At a Glance → `ic_smartspace`, App Drawer → `ic_apps`, Origin Modes → `ic_lightbulb`, Home Screen → `ic_home_screen`, Dock → `ic_dock`, Search → `ic_search`, Folders → `ic_folder`, Gestures → `ic_gestures`, Recents → `ic_quickstep`, Backup & Restore → `ic_download`, Personalization → `ic_wallpaper`, Experimental → `ic_new_releases`, About → `ic_about`

### 5.8 Recents / Overview (Quickstep)

**Key files:**
- `quickstep/src/com/android/quickstep/views/RecentsView.java` — Main view (7045 lines)
- `quickstep/src/com/android/quickstep/views/TaskView.java` — Task card
- `quickstep/src/com/android/quickstep/recents/` — MVVM refactor (data/domain/viewmodel/ui)

**Architecture:**
- Partially refactored into MVVM:
  - **Data layer:** `TasksRepository`, `RecentTasksRepository`, `RecentsDeviceProfileRepository`
  - **Domain layer:** Use cases + domain models
  - **ViewModel:** `RecentsViewModel` emits `RecentsViewData`
  - **UI mapper:** `TaskUiStateMapper.kt`
- Main view `RecentsView` handles: task layout, split-screen, dismiss animations, desktop mode
- Task cards: `TaskView` (thumbnail + icon + label)
- Gesture integration: `LauncherSwipeHandlerV2`, `AbsSwipeUpHandler`

---

## 6. Animation & Motion Language

### Core Animation Framework

**`AnimatorPlaybackController`** — The central mechanism. A driving `ValueAnimator` (0→1) with `LINEAR` interpolator maps progress to child animations. Supports `start()`, `reverse()`, `setPlayFraction()`, `startWithVelocity()`.

**`SpringAnimationBuilder`** — Underdamped spring physics via differential equation: `x = e^(-beta*t/2) * (a cos(gamma*t) + b sin(gamma*t))`. Default: `STIFFNESS_MEDIUM` + `DAMPING_RATIO_MEDIUM_BOUNCY`.

**`PendingAnimation`** — Collects animators → `AnimatorSet` → `AnimatorPlaybackController`.

**`PhysicsAnimator`** (Lawnchair, `lawnchair/src/app/lawnchair/animation/PhysicsAnimator.kt`) — Builder-pattern physics:
- `spring(property, to, startVelocity, stiffness, dampingRatio)` — SpringAnimation
- `fling(property, startVelocity, friction, min, max)` — FlingAnimation
- `flingThenSpring(property, ...)` — Two-phase (fling → spring back)

### Interpolator Reference

All defined in `platform_frameworks_libs_systemui/animationlib/src/com/android/systemui/animation/Interpolators.java`

| Name | Control Points / Type | When To Use |
|------|----------------------|-------------|
| `EMPHASIZED` | Custom cubic (hero motion) | Hero movement, primary transitions |
| `EMPHASIZED_ACCELERATE` | (0.3, 0, 0.8, 0.15) | Hero content disappearing |
| `EMPHASIZED_DECELERATE` | (0.05, 0.7, 0.1, 1) | Hero content appearing |
| `STANDARD` | (0.2, 0, 0, 1) | **Every normal animation** |
| `STANDARD_ACCELERATE` | (0.3, 0, 1, 1) | Content disappearing |
| `STANDARD_DECELERATE` | (0, 0, 0, 1) | Content appearing |
| `LEGACY` / `FAST_OUT_SLOW_IN` | (0.4, 0, 0.2, 1) | Legacy Material 1 |
| `TOUCH_RESPONSE` | (0.3, 0, 0.1, 1) | Button/icon press feedback |
| `ICON_OVERSHOT` | (0.4, 0, 0.2, 1.4) | Icon overshoot bounce |
| `ZOOM_OUT` | Camera lens formula | Scale-based transitions |
| `SCROLL` | Quintic `t^5 + 1` (t-1) | Fast scroll fling |
| `BOUNCE` | BounceInterpolator | Bouncy UI elements |
| `LINEAR` | LinearInterpolator | Progress drivers, alpha |

### Spring Constants Table

| Context | Stiffness | Damping Ratio | Source |
|---------|-----------|---------------|--------|
| Global default | `MEDIUM` (default) | `MEDIUM_BOUNCY` (default) | `SpringProperty.java` |
| Hint → Normal scale | Resource `hint_scale_stiffness` | Resource `hint_scale_damping_ratio` | `WorkspaceStateTransitionAnimation` |
| Staggered workspace | Resource `staggered_stiffness` | Resource `staggered_damping_ratio` | `StaggeredWorkspaceAnim.java` |
| TransitionAnimator X | 450 | 0.965 | `TransitionAnimator.kt` |
| TransitionAnimator Y | 400 | 0.95 | `TransitionAnimator.kt` |
| TransitionAnimator scale | 500 | 0.99 | `TransitionAnimator.kt` |
| PhysicsAnimator default | `STIFFNESS_MEDIUM` | `DAMPING_RATIO_MEDIUM_BOUNCY` | `PhysicsAnimator.kt` |

### Duration Reference

| Animation | Duration | Notes |
|-----------|----------|-------|
| App launch | 500ms | `APP_LAUNCH_DURATION` |
| App launch alpha | 50ms at 25ms delay | Brief flash |
| Status bar transition | 120ms (+96ms pre-delay) | `STATUS_BAR_TRANSITION_DURATION` |
| Nav bar fade in | 266ms | `ANIMATION_NAV_FADE_IN_DURATION` |
| Nav bar fade out | 133ms | `ANIMATION_NAV_FADE_OUT_DURATION` |
| Recents launch | 336ms | `RECENTS_LAUNCH_DURATION` |
| Split launch | 370ms | `SPLIT_LAUNCH_DURATION` |
| Staggered workspace | 250ms | `StaggeredWorkspaceAnim.DURATION_MS` |
| All Apps revert swipe | 200ms | `REVERT_SWIPE_ALL_APPS_TO_HOME` |
| Folder items initial | 350ms | `FolderPreviewItemManager` |
| Folder items final | 200ms | Same |
| Folder slide-in | 300ms | Same |
| Folder reorder | 230ms | `FolderPagedView` |
| Folder drop-in | 400ms | `FolderIcon` |
| Folder color | 200ms | `Folder` |
| Page indicator | 200ms | `PageIndicatorDots` |
| Wallpaper offset | 250ms | `WallpaperOffsetInterpolator` |
| Fling settle | 1200ms | Velocity-based |
| Preview fade in | 200ms | `PreviewSurfaceRenderer` |
| Transient taskbar | 417ms | `TRANSIENT_TASKBAR_TRANSITION_DURATION` |
| Pinned taskbar | 600ms | `PINNED_TASKBAR_TRANSITION_DURATION` |

### Motion Scaling (Origin Mode)

Flow mode applies motion scaling (`OriginModeApplier.scaleDuration()`):
- `motionScale` = 1.2× (Flow) vs 1.0× (Pure)
- `allAppsOpenDurationScale` = 1.3× (Flow)
- `allAppsCloseDurationScale` = 1.3× (Flow)

**Always wrap animation durations with `OriginModeApplier.scaleDuration()` when creating new animations.**

### Animation Composition Patterns

1. **State transitions:** `StateManager.AtomicAnimationFactory` → `StateAnimationConfig` → per-property interpolators/durations
2. **Folder open/close:** `FolderAnimationManager` (standard) or `FolderAnimationSpringBuilderManager` (spring physics)
3. **Swipe-to-home:** `StaggeredWorkspaceAnim` — bottom-to-top stagger (10ms per row), spring translationY + linear alpha
4. **App launch:** `QuickstepTransitionManager` — coordinated workspace fade + scale + status bar + nav bar
5. **All Apps open:** `AllAppsTransitionController` — scrim fade + workspace scale + list translate

---

## 7. Code Architecture & Patterns

### State Machine

`StateManager<LauncherState, Launcher>` drives all UI state transitions.

**States** (defined as static instances on `LauncherState.java`):
| State | Ordinal | Description |
|-------|---------|-------------|
| `NORMAL` | 0 | Default home screen |
| `SPRING_LOADED` | 1 | Drag-over highlighting |
| `ALL_APPS` | 2 | App drawer open |
| `OVERVIEW` | 3 | Recents/Overview |
| `QUICK_SWITCH_FROM_HOME` | 4 | Quick switch |
| `OVERVIEW_MODAL_TASK` | 5 | Task popup menu |
| `HINT_STATE` | 6 | Drag hint |
| `BACKGROUND_APP` | 7 | App in background |
| `OVERVIEW_SPLIT_SELECT` | 8 | Split-screen selection |
| `EDIT_MODE` | — | Workspace edit mode |

**State transitions use `StateAnimationConfig`** which assigns per-animation-type interpolators via `@AnimType` constants (21 types: `ANIM_VERTICAL_PROGRESS`, `ANIM_WORKSPACE_SCALE`, `ANIM_HOTSEAT_SCALE`, `ANIM_ALL_APPS_FADE`, `ANIM_SCRIM_FADE`, `ANIM_DEPTH`, etc.).

### Dependency Injection (Custom Dagger)

- **No Hilt.** Custom Dagger with `@LauncherAppSingleton` (app-wide) and `@ActivityContextSingleton` (activity-scoped) scopes
- `LauncherBaseAppComponent` defines provision methods for all singletons
- Components generated by Dagger, accessed from `LauncherApplication.getAppComponent()`
- Module bindings in `quickstep/src/com/android/launcher3/dagger/Modules.kt`

### Naming Conventions

| Pattern | Example |
|---------|---------|
| `*Activity` | `Launcher.java`, `LawnchairLauncher.kt` |
| `*View` | `DragView.java`, `BubbleTextView.java` |
| `*Controller` | `DragController.java`, `GestureController.kt` |
| `*Manager` | `StateManager.java`, `ThemeProvider.kt` |
| `*Model.kt` | `LauncherModel.kt`, `BgDataModel.kt` |
| `*Repository.kt` | `AppsListRepository.kt`, `HomeScreenRepository.kt` |
| `*Info.java` | `ItemInfo.java`, `AppInfo.java`, `FolderInfo.java` |
| `*State.java/.kt` | `LauncherState.java`, `AllAppsState.kt` |
| `*Config.java` | `StateAnimationConfig.java` |
| Package `data/` | Room entities, DAOs, repositories |
| Package `model/` | Business logic, data models |
| Package `ui/` | Compose UI |
| Package `util/` | Utilities |

**Rules:**
- AOSP core files are Java (`.java`)
- Lawnchair customization files are Kotlin (`.kt`)
- New UI code should be Kotlin
- New Compose code goes in `ui/` sub-packages
- Test files mirror source packages under `tests/`

### Data Layer

| Approach | Used For | Files |
|----------|----------|-------|
| Direct SQLite | AOSP workspace items, favorites | `DatabaseHelper.java` (schema v32) |
| Room | Lawnchair features | `AppDatabase.kt` (schema v3) with `IconOverride`, `Wallpaper`, `FolderInfoEntity`, `FolderItemEntity` |
| Reactive repositories | New code | `AppsListRepository.kt`, `HomeScreenRepository.kt` using `MutableListenableRef`/`MutableListenableStream` |
| SharedPreferences | Classic prefs | `LauncherPrefs.kt` |
| Opto (type-safe) | Modern prefs | `PreferenceManager2.kt` — Flow-based, reactive |

### Drag and Drop

**Architecture:** `DragController` → `DragDriver` (Internal/System) → `DropTarget` (workspace, hotseat, delete) → state enters `SPRING_LOADED`, exits to `NORMAL`.

Key classes: `DragController.java`, `DragView.java`, `LauncherDragView.java`, `DropTarget.java`, `DragSource.java`, `SpringLoadedDragController.kt`.

### Testing

| Framework | Usage |
|-----------|-------|
| JUnit 4 | Test runner |
| AndroidX Test | Instrumentation |
| UI Automator | UI interactions |
| TAPL | Custom launcher automation (`LauncherInstrumentation`) |
| Custom rules | `TestIsolationRule`, `ShellCommandRule`, `ScreenRecordRule`, etc. |

**Test location:** `tests/src/com/android/launcher3/` — integration-heavy, E2E focused.

---

## 8. System Integration Contracts

### 8.1 SystemUiProxy — The Central Bridge

**File:** `quickstep/src/com/android/quickstep/SystemUiProxy.kt`

A Dagger `@LauncherAppSingleton` holding AIDL proxies to ~15+ SystemUI services:
- `ISystemUiProxy` — overview, status bar, assistant, screen pinning
- `IPip` — Picture-in-Picture
- `IBubbles` — Bubble bar
- `ISplitScreen` — Split screen
- `IOneHanded` — One-handed mode
- `IShellTransitions` — Remote transitions
- `IRecentTasks` — Recents task list
- `IBackAnimation` — Back-to-launcher animation
- `IDesktopMode` — Desktop mode (create/activate/remove desks)
- `IUnfoldAnimation` — Foldable animation

**Critical:** Every call is wrapped in `executeWithErrorLog` try/catch. SystemUiProxy manages death recipients and re-registration.

### 8.2 Gesture Navigation

**Key files:**
- `lawnchair/src/app/lawnchair/gestures/GestureController.kt` — Central orchestrator
- `lawnchair/src/app/lawnchair/gestures/VerticalSwipeTouchController.kt` — Swipe interception

**Gesture types (GestureType enum):** `SWIPE_UP`, `SWIPE_DOWN`, `SWIPE_LEFT`, `SWIPE_RIGHT`

**Gesture handlers (GestureHandler sealed class):**
- `RecentsGestureHandler` — `GLOBAL_ACTION_RECENTS`
- `SleepGestureHandler` — Root (shell keyevent), AccessibilityService, DeviceAdmin
- `OpenNotificationsHandler` — Reflection into `StatusBarManager.expandNotificationsPanel`
- `OpenQuickSettingsHandler` — Reflection into `StatusBarManager.expandSettingsPanel`
- `OpenAssistantHandler` — Resolves default assistant
- `OpenAppGestureHandler` — `LauncherApps.startMainActivity()`
- `OpenAppDrawerGestureHandler` — `animateToAllApps()`

**Fragility note:** Many gesture handlers use hidden/reflection APIs or require special permissions. The AccessibilityService fallback pattern is a critical UX path.

### 8.3 Touch Controllers (Quickstep Overrides)

Defined in `quickstep/src/com/android/launcher3/uioverrides/touchcontrollers/`:

| Controller | Purpose |
|------------|---------|
| `NavBarToHomeTouchController.java` | Nav bar → home |
| `NoButtonNavbarToOverviewTouchController.java` | Gesture nav → overview |
| `NoButtonQuickSwitchTouchController.java` | Quick switch |
| `PortraitStatesTouchController.java` | State transitions (NORMAL, ALL_APPS, OVERVIEW) |
| `StatusBarTouchController.java` | Status bar swipe down |
| `TaskViewTouchControllerDeprecated.java` | Task dismiss/launch |

### 8.4 Google Feed Integration

**File:** `lawnchair/src/com/google/android/libraries/launcherclient/LauncherClient.java`

Binds to Google's overlay service via `ILauncherOverlay` AIDL. Manages activity lifecycle (`setActivityState()`), window attachment, scroll events. Uses `amirz.aidlbridge.IBridge` for microG support. API version negotiation is version-dependent.

### 8.5 Taskbar

**Directory:** `quickstep/src/com/android/launcher3/taskbar/` (60+ files)

`TaskbarControllers.java` hosts all sub-controllers (initialization order is critical):
- `TaskbarDragController`, `TaskbarNavButtonController`, `NavbarButtonsViewController`
- `TaskbarStashController` (auto-hide/stash with flags)
- `TaskbarKeyguardController`, `TaskbarAllAppsController`, `TaskbarPopupController`
- `TaskbarDesktopModeController`, `KeyboardQuickSwitchController`, `NudgeController`
- Full bubble bar: `BubbleBarController`, `BubbleBarViewController`, `BubbleDragController`

### 8.6 Launcher Client (Google)

`quickstep/src/com/android/quickstep/LauncherClient.kt` — Adapts `LauncherClient.java` for Quickstep. Binds to GMS overlay for Google feed.

### 8.7 Desktop Mode

**Files:**
- `wmshell/shared/src/com/android/wm/shell/shared/desktopmode/` — Feature detection, desktop state
- `quickstep/src/com/android/launcher3/statehandlers/DesktopVisibilityController.kt` — Launcher integration

**Critical:** `DesktopVisibilityController` calls `QuickstepLauncher.setPaused()`/`setResumed()` directly — this bypasses normal activity lifecycle and is fragile.

### 8.8 Notification Dots

**Files:** `src/com/android/launcher3/notification/NotificationListener.java` — Extends `NotificationListenerService`. Requires `BIND_NOTIFICATION_LISTENER_SERVICE` permission (protected system API).

`src/com/android/launcher3/dot/DotInfo.java` — Holds notification keys with `MAX_COUNT=999`.

---

## 9. Origin Modes: Pure vs Flow

### OriginMode Enum

**File:** `lawnchair/src/app/lawnchair/origin/OriginMode.kt`

| Mode | Storage Key | Description |
|------|------------|-------------|
| `Pure` | `"pure"` | Clean, familiar, Pixel-like. Default. |
| `Flow` | `"flow"` | Glassmorphism, dense, monochrome. |

### OriginModeConfig (55 fields)

**File:** `lawnchair/src/app/lawnchair/origin/OriginModeConfig.kt`

**Config categories:**

| Category | Fields | Pure Default | Flow Default |
|----------|--------|-------------|--------------|
| Grid/Density | `workspaceDensity`, `appDrawerDensity`, `iconSizeFactor`, `drawerIconSizeFactor`, `drawerCellHeightFactor`, `drawerLeftRightMarginFactor` | 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 | 0.90, 0.90, 0.85, 0.88, 0.85, 0.70 |
| Dock | `isFloatingDock`, `dockLabelEnabled`, `dockAccentColor` | false, false, false | true, false, true |
| Search Bar | `isFloatingSearchBar`, `themedSearchBar` | false, false | true, true |
| Surfaces | `cornerRadiusDp`, `glassOpacity`, `drawerBackgroundOpacity`, `enableWallpaperBlur`, `wallpaperBlurRadius`, `recentsTranslucentBackground` | 12dp, 0, 0.5, false, 25, false | 28dp, 0.12, 0.30, true, 20, true |
| Icons | `useMonochromeIcons`, `iconShapeKey`, `folderShapeKey`, `showIconLabelsOnHomeScreen`, `showIconLabelsOnHomeScreenFolder`, `showIconLabelsInDrawer`, `showSuggestedAppsInDrawer` | false, circle, circle, true, true, true, true | true, roundedSquare, roundedSquare, false, false, true, false |
| Color | `colorStyleKey`, `notificationDotColorOverride` | tonal_spot, false | monochromatic, true |
| Motion | `motionScale`, `allAppsOpenDurationScale`, `allAppsCloseDurationScale` | 1.0, 1.0, 1.0 | 1.2, 1.3, 1.3 |
| Chrome | `showPageIndicator`, `pageIndicatorHeightFactor`, `showTopShadow`, `showScrollbar`, `twoLineAllApps`, `wallpaperScrolling`, `roundedWidgets` | true, 1.0, true, false, false, true, true | false, 0.0, false, true, true, false, false |

### OriginModeManager

**File:** `lawnchair/src/app/lawnchair/origin/OriginModeManager.kt`

- Dagger `@LauncherAppSingleton`
- Applies configs to preferences atomically via `runBlocking`
- `OriginModeApplier` provides `scaleDuration()` for motion scaling

### Layout Categories

Layouts live in `lawnchair/res/layout/` with suffixes:

| Suffix | Purpose |
|--------|---------|
| `_pure` | Pure-mode specific variant |
| `_flow` | Flow-mode specific variant |
| (no suffix) | Shared between both modes |

---

## 10. Do Not Touch

These areas must never be modified without deep understanding:

### 10.1 Hidden API Stubs (`hidden-api/src/main/java/`)

Stubs for hidden Android APIs accessed via `dev.rikka.tools.refine.RefineAs`. These allow the launcher to call internal framework APIs. Changing them breaks compilation against real Android SDKs.

- `android/app/IActivityTaskManagerHidden.java` — `registerRemoteAnimationForNextActivityStart()`
- `android/view/RemoteAnimationAdapter.java` — Remote animation stubs
- `com/android/internal/view/RotationPolicy.java` — Rotation lock

### 10.2 Compatibility Library (`compatLib/`)

7 version-specific sub-modules (`compatLibVQ` through `compatLibVBaklava`) each targeting different API levels. `QuickstepCompatFactory` provides version-aware implementations for:
- `ActivityManagerCompat` — task snapshots, recents, running tasks
- `RecentsAnimationRunnerCompat` — Different `onAnimationCanceled` signatures per API level
- `RemoteTransitionCompat` / `ActivityOptionsCompat`

**If you change these, you break backward compatibility across 8 Android versions.**

### 10.3 System AIDL Interfaces (`wmshell/`)

Copied from AOSP. These AIDL files must match SystemUI's interface definitions exactly. Modifying them causes runtime binder transaction failures.

Key interfaces:
- `IDesktopMode`, `IDesktopTaskListener`, `DisplayDeskState`
- `ISplitScreen`, `SplitBounds`, `SplitScreenConstants`
- `IBubbles`, `BubbleBarLocation`
- `IShellTransitions`, `IShellTransitions2`

### 10.4 SystemUiProxy (`quickstep/src/com/android/quickstep/SystemUiProxy.kt`)

The ~100 methods that bridge to SystemUI via AIDL. Every method is wrapped in try/catch with death recipient management. **Do not add methods here without understanding AIDL lifecycles.**

### 10.5 GestureController Handler Reflection

Handlers like `OpenNotificationsHandler` and `OpenQuickSettingsHandler` use reflection into `StatusBarManager`. These break on new Android versions if the internal API changes.

### 10.6 DesktopVisibilityController Lifecycle Bypass

`DesktopVisibilityController` in `quickstep/src/com/android/launcher3/statehandlers/` calls `QuickstepLauncher.setPaused()/setResumed()` directly. This bypasses normal activity lifecycle. Modifying this without understanding desktop mode's task visibility model will break the desktop experience.

### 10.7 LauncherClient GMS Binding

`lawnchair/src/com/google/android/libraries/launcherclient/LauncherClient.java` binds to GMS. The `amirz.aidlbridge` proxy layer must remain intact for microG support.

### 10.8 Version-Specific Compat (`compatLib`)

The folder structure `compatLibVQ/VR/VS/VT/VU/VV/VBaklava` maps to API levels 29–37. When adding new platform-dependent code, use the existing compat factory pattern — **never use `if (Build.VERSION.SDK_INT >= ...)` checks in core code** for features that have compat stubs.

### 10.9 Room Database Schema

`lawnchair/src/app/lawnchair/data/AppDatabase.kt` — Version 3. Schema files in `schemas/`. Changing entities without proper migration breaks existing user data.

---

## 11. Feature Checklist

When adding any new feature, verify these:

### Design Compliance
- [ ] **Respect Material You** — New UI must use `ColorTokens` for colors, not hardcoded values
- [ ] **Support dynamic colors** — The feature should respond to `ThemeProvider` color scheme changes
- [ ] **Support dark theme** — Use `DayNightColorToken` for light/dark variants
- [ ] **Follow existing spacing** — Use dimension resources, not hardcoded dp values
- [ ] **Avoid duplicate settings** — Check if the feature already exists in OriginModeConfig
- [ ] **Reuse existing components first** — Before creating a new composable/view, check if `BubbleTextView`, `SpringRelativeLayout`, or another existing component fits

### Mode Support
- [ ] **Pure mode compatible** — Feature should work within Pure's visual language
- [ ] **Flow mode compatible** — Feature should adapt to Flow's glassmorphism/monochrome language
- [ ] **No broken defaults** — Feature's default state must match Pure mode expectations

### Motion Compliance
- [ ] **Use `OriginModeApplier.scaleDuration()`** — All animation durations must be scaled
- [ ] **Use standard interpolators** — Prefer `Interpolators.STANDARD` for normal, `EMPHASIZED` for hero motion
- [ ] **Respect `motionScale`** — User's motion preference must be honored

### Code Quality
- [ ] **Follow naming conventions** — File name reflects class name, pattern matches area
- [ ] **Use Dagger injection** — Singletons via `@LauncherAppSingleton`, activity-scoped via `@ActivityContextSingleton`
- [ ] **StateMachine integration** — If changing launcher UI states, integrate with `StateManager`
- [ ] **No hidden API abuse** — Prefer `compatLib` pattern over `if (SDK_INT)` checks
- [ ] **Kotlin for new code** — New files should be Kotlin, existing Java can stay

### Testing
- [ ] **Integration test exists** — For system-level features
- [ ] **No regression** — Existing TAPL tests must pass

---

## 12. Design Anti-Patterns

These are the most common mistakes AI agents make. Learn them as "what not to do."

### ❌ Hardcoded Colors

```kotlin
// BAD — hardcoded purple, breaks dynamic theming
val accentColor = Color(0xFF6200EE)
val background = Color(0xFFFFFFFF)

// GOOD — uses token system, responds to Monet + dark mode
val accentColor = ColorTokens.PrimaryButton
val background = ColorTokens.Surface
```

**Why it matters:** Hardcoded colors ignore Material You dynamic color, dark mode, and the token system. The launcher's entire theming pipeline becomes irrelevant.

### ❌ Direct `ObjectAnimator` / `AnimatorSet` on New UI

```kotlin
// BAD — raw ObjectAnimator, no scaling, no framework integration
val anim = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f)
anim.duration = 300
anim.start()

// GOOD — uses the launcher's animation framework
PhysicsAnimator.get(view).spring(DynamicAnimation.ALPHA, 1f)

// OR for state transitions:
val anim = PendingAnimation(200)
anim.addFloat(view, View.ALPHA, 1f, AlphaUpdateListener.ALPHA_CUTOFF_THRESHOLD)
val controller = anim.createPlaybackController()
controller.start()
```

**Why it matters:** Raw `ObjectAnimator` bypasses `OriginModeApplier.scaleDuration()` (motion scaling is ignored), the `Interpolators` catalog, and the `PhysicsAnimator` spring system. Your animation will feel wrong in Flow mode.

### ❌ `if (Build.VERSION.SDK_INT >= ...)` Instead of compatLib

```java
// BAD — SDK_INT checks scatter version logic everywhere
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    // T+ specific behavior
} else {
    // pre-T fallback
}

// GOOD — use the existing compat factory
QuickstepCompatFactory factory = CompatContext.getQuickstepCompatFactory(context);
factory.getActivityManagerCompat().invalidateHomeTaskSnapshot();
```

**Why it matters:** The project has 7 version-specific compat modules (`compatLibVQ` through `VBaklava`) precisely to centralize version branching. SDK_INT checks scattered across the codebase are impossible to maintain and almost always miss edge cases.

### ❌ New Settings Toggle That Should Be an OriginModeConfig Field

```kotlin
// BAD — standalone boolean in preferences
PreferenceManager2.instance(context).apply {
    enableGlassEffect = true
}

// GOOD — field in OriginModeConfig, applied atomically per mode
val flowConfig = OriginMode.Flow.config()  // already has glassOpacity = 0.12f
OriginModeManager.applyConfig(flowConfig)
```

**Why it matters:** Origin's philosophy is curated modes, not endless toggles. If your feature differentiates Pure vs Flow, it belongs in `OriginModeConfig` — not as a standalone preference.

### ❌ `findViewById` / Direct View Manipulation in Compose Code

```kotlin
// BAD — mixing View system with Compose
@Composable
fun MyComponent() {
    val view = LocalView.current
    val legacyView = view.findViewById<SomeView>(R.id.some_view)
}

// GOOD — use CompositionLocal or state hoisting
@Composable
fun MyComponent() {
    val colorScheme = LawnchairTheme.colorScheme
    val density = LocalDensity.current
}
```

**Why it matters:** The launcher is in transition from View system to Compose. Mixing them carelessly causes recomposition issues and breaks the Compose theming pipeline.

### ❌ Mutating `SystemUiProxy` Without Understanding AIDL Lifecycles

```kotlin
// BAD — adding a method without death recipient handling
fun myNewMethod() {
    try {
        iSystemUiProxy?.myNewCall()
    } catch (e: Exception) {
        Log.e("SystemUiProxy", "failed", e)
    }
}

// GOOD — follow the existing pattern:
fun myNewMethod() {
    executeWithErrorLog {
        iSystemUiProxy?.myNewCall()
    }
}
```

**Why it matters:** AIDL proxies die when SystemUI restarts. Every call needs death recipient re-registration. The `executeWithErrorLog` wrapper handles this.

### ❌ Writing Java When the Rest of the Area Is Kotlin (and Vice Versa)

```java
// BAD — Java file in a Kotlin-only package (lawnchair/ should be Kotlin)
// lawnchair/src/app/lawnchair/something/MyNewFeature.java

// GOOD — match the surrounding module's language
// lawnchair/src/app/lawnchair/something/MyNewFeature.kt
```

**Rule of thumb:**
- `src/com/android/launcher3/` → Java is fine (AOSP core)
- `lawnchair/src/app/lawnchair/` → **Kotlin only**
- `quickstep/src/com/android/quickstep/` → Java for old code, Kotlin for new MVVM refactor
- `quickstep/src/com/android/launcher3/` → Java for overrides

### ❌ Using Wrong Scope for Dagger Injection

```kotlin
// BAD — activity-scoped thing in a @Singleton
@Module
object MyModule {
    @Provides
    @LauncherAppSingleton
    fun provideSomethingThatDependsOnActivity(context: Context): MyThing {
        return MyThing(context)  // activity context held forever = leak
    }
}

// GOOD — match the scope to the lifecycle
@Module
object MyModule {
    @Provides
    @ActivityContextSingleton
    fun provideMyThing(@ActivityContext context: Context): MyThing {
        return MyThing(context)
    }
}
```

**Why it matters:** `@LauncherAppSingleton` lives as long as the process. Holding an activity `Context` there leaks the activity.

### ❌ Adding Dependencies Without Checking the Version Catalog

```kotlin
// BAD — inline version in build.gradle
implementation("androidx.compose.material3:material3:1.3.0")

// GOOD — use the version catalog
libs.compose.material3

// or defined in gradle/libs.versions.toml
```

**Why it matters:** The version catalog (`gradle/libs.versions.toml`) centralizes all dependency versions. Inline versions create conflicts and are invisible to Dependabot/Renovate.

### ❌ Ignoring `StateManager` and Adding Ad-Hoc State

```kotlin
// BAD — view visibility toggles that bypass the state machine
fun showMyPanel() {
    myPanel.visibility = View.VISIBLE
    workspace.visibility = View.GONE
}

// GOOD — define a new state or use existing state handlers
stateManager.goToState(MyCustomState)
// or override stateHandler for an existing state transition
```

**Why it matters:** Ad-hoc visibility toggles break the state machine's animation system, back gesture handling, and predictive back support.

---

## 13. Critical Values Map

### File Locations

| What | Where |
|------|-------|
| Animation interpolators | `platform_frameworks_libs_systemui/animationlib/Interpolators.java` |
| Spring physics constants | `src/com/android/launcher3/anim/SpringProperty.java` + `PhysicsAnimator.kt` |
| Color tokens (80 tokens) | `lawnchair/src/app/lawnchair/theme/color/tokens/ColorTokens.kt` |
| Drawable tokens (20 tokens) | `lawnchair/src/app/lawnchair/theme/drawable/DrawableTokens.kt` |
| Theme provider | `lawnchair/src/app/lawnchair/theme/ThemeProvider.kt` |
| Origin mode config (55 fields) | `lawnchair/src/app/lawnchair/origin/OriginModeConfig.kt` |
| Origin mode definitions | `lawnchair/src/app/lawnchair/origin/OriginMode.kt` |
| State machine | `src/com/android/launcher3/statemanager/StateManager.java` |
| All states | `src/com/android/launcher3/LauncherState.java` |
| State animation config | `src/com/android/launcher3/states/StateAnimationConfig.java` |
| Quickstep transition durations | `quickstep/src/com/android/quickstep/QuickstepTransitionManager.java` |
| System UI proxy (AIDL bridge) | `quickstep/src/com/android/quickstep/SystemUiProxy.kt` |
| Dagger scopes | `dagger/src/com/android/launcher3/dagger/` |
| Launcher activity | `src/com/android/launcher3/Launcher.java` |
| Lawnchair launcher entry | `lawnchair/src/app/lawnchair/LawnchairLauncher.kt` |
| App database (Room) | `lawnchair/src/app/lawnchair/data/AppDatabase.kt` |
| Preferences (modern) | `lawnchair/src/app/lawnchair/preferences2/PreferenceManager2.kt` |
| Settings (Compose) | `lawnchair/src/app/lawnchair/ui/preferences/Preferences.kt` |
| Font system | `lawnchair/src/app/lawnchair/font/FontManager.kt` |
| Monet engine | `lawnchair/src/dev/kdrag0n/monet/theme/` |
| Gesture controller | `lawnchair/src/app/lawnchair/gestures/GestureController.kt` |
| Notification listener | `src/com/android/launcher3/notification/NotificationListener.java` |
| Drag and drop | `src/com/android/launcher3/dragndrop/DragController.java` |
| Launcher model | `src/com/android/launcher3/LauncherModel.kt` |
| Recents view | `quickstep/src/com/android/quickstep/views/RecentsView.java` |
| Taskbar controllers | `quickstep/src/com/android/launcher3/taskbar/TaskbarControllers.java` |
| Desktop mode | `quickstep/src/com/android/launcher3/statehandlers/DesktopVisibilityController.kt` |
| Google Feed client | `lawnchair/src/com/google/android/libraries/launcherclient/LauncherClient.java` |
| Backup system | `lawnchair/src/app/lawnchair/backup/LawnchairBackup.kt` |

---

> **If you're an AI reading this before editing:** You now have the full design philosophy, token system, component hierarchy, animation language, and integration contracts. Your changes should be indistinguishable from hand-crafted native code. Stay consistent, respect the tokens, scale your durations, and never touch the Do-Not-Touch sections.
