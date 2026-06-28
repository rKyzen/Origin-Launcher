# Origin Launcher — Agent Guide

This is **Origin Launcher**, a fork of Lawnchair/AOSP Launcher3 for Android 16 (Baklava). Not a browser — the GeckoView notes from `~/.config/opencode/AGENTS.md` do not apply.

## Build

| Command | Purpose |
|---------|---------|
| `./gradlew assembleDebug` | Debug APK |
| `./gradlew installDebug` | Install on device |
| `./gradlew spotlessApply` | Format Kotlin (`lawnchair/src/**/*.kt`) + Java (`compatLib/**/src/**/*.java`) |
| `./gradlew :baseline-profile:connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.androidx.benchmark.enabledRules=Macrobenchmark` | Benchmark |
| `./gradlew :launch:generateReleaseBaselineProfile` | Generate baseline profile |

- Gradle 9.5.1, AGP 9.2.1, Kotlin 2.3.21, JDK 21
- Version catalog: `gradle/libs.versions.toml` — never inline versions
- Compose BOM `2026.05.01`, Material3 `1.5.0-alpha20`, Room `2.8.4`, Dagger `2.59.2`
- Configuration cache enabled — delete `.gradle/configuration-cache/` if stale

## Project Structure

Monolithic app module at root (no `:app`). Source sets aggregate multiple directories:

| Source set | Directories |
|------------|-------------|
| `main` | `src/`, `src_plugins/`, `compose/facade/`, `compose/features/` |
| `lawn` | `src_flags/`, `src_shortcuts_overrides/`, `lawnchair/src/` |
| `withQuickstep` | `quickstep/src/`, `quickstep/dagger/`, `quickstep/recents_ui_overrides/` |

Flavor dimensions: `app` (lawn), `recents` (withQuickstep), `channel` (github/nightly/play).

**Key modules referenced from the root build:**
- `:iconloaderlib` → `platform_frameworks_libs_systemui/iconloaderlib`
- `:wmshell` → `wmshell/` (Window Manager Shell, AIDL interfaces)
- `:hidden-api` → `hidden-api/` (hidden Android API stubs via RefineAs)
- `:dagger` → `dagger/` (custom Dagger scopes)
- `:modules:widgetpicker` → `modules/widgetpicker/` (Compose dynamic feature)
- `:compatLib:*` → 8 version-specific shims per API level 29–37

## Class Hierarchy

```
LauncherApplication
  └─ LawnchairApp (lawnchair/src/.../LawnchairApp.kt)

BaseActivity → StatefulActivity<LauncherState> → Launcher (src/.../Launcher.java)
  → QuickstepLauncher (quickstep/src/.../QuickstepLauncher.java)
    → LawnchairLauncher (lawnchair/src/.../LawnchairLauncher.kt)
```

## Key Architecture Facts

- **DI:** Custom Dagger, **no Hilt**. Scopes: `@LauncherAppSingleton` (app-wide), `@ActivityContextSingleton` (activity-scoped). Components in `dagger/src/`. Module bindings in `quickstep/.../dagger/Modules.kt`.
- **State machine:** `StateManager<LauncherState, Launcher>` drives all UI transitions. 10 states defined in `LauncherState.java`. Don't add ad-hoc visibility toggles.
- **Animation framework:** `AnimatorPlaybackController` (progress-driver), `SpringAnimationBuilder`, `PhysicsAnimator` (builder-pattern physics). Always wrap durations with `OriginModeApplier.scaleDuration()`. Use `Interpolators.STANDARD` / `EMPHASIZED` from `platform_frameworks_libs_systemui/animationlib/`.
- **Two UI modes:** Pure (Pixel-like, Material You) and Flow (glassmorphism, monochrome). Both defined in `OriginModeConfig` (~55 fields). Never add standalone toggles for mode-specific features — extend `OriginModeConfig`.
- **Theming pipeline:** `PreferenceManager2` → `ThemeProvider` → `MonetColorSchemeCompat` → `ColorTokens` (~80 tokens) / `DrawableTokens` (~20 tokens). **Always** use tokens, never hardcoded colors.
- **Data layer:** Direct SQLite (AOSP, schema v32), Room (Lawnchair, `AppDatabase.kt` schema v3), Opto type-safe prefs (`PreferenceManager2`), SharedPreferences legacy.
- **System bridge:** `SystemUiProxy` (~100 AIDL methods). Every call wrapped in `executeWithErrorLog`. Do not add methods without understanding death recipient lifecycles.

## Language Conventions

| Area | Language |
|------|----------|
| `src/com/android/launcher3/` | Java (AOSP core) |
| `lawnchair/src/app/lawnchair/` | **Kotlin only** |
| `quickstep/` | Java for legacy, Kotlin for new MVVM |
| New UI code | Kotlin; Compose goes in `ui/` sub-packages |

## Version Branching

Never use `if (Build.VERSION.SDK_INT >= ...)` — use the compat factory pattern in `compatLib/` instead. The library has 8 version-specific sub-modules (`VQ` through `VBaklava`) covering API 29–37.

## Do Not Touch

- `hidden-api/` — stubs for hidden Android APIs via RefineAs. Changing them breaks compilation.
- `compatLib/` — breaks backward compatibility across 8 Android versions.
- `wmshell/` — AIDL files must match SystemUI exactly. Runtime binder failure if modified.
- `schemas/` — Room migration history. Changing entities without proper migration breaks existing user data.

## Testing

Tests are integration-heavy, live in `tests/src/com/android/launcher3/`. Frameworks: JUnit 4, AndroidX Test, UI Automator, TAPL (`LauncherInstrumentation`). No unit-test-focused CI workflow exists in `.github/workflows/`.

## Reference Documents

- **`DESIGN_LANGUAGE.md`** — definitive reference: architecture, component map, animation system, anti-patterns, critical values map. Read this first before any significant change.
- `gradle/libs.versions.toml` — all dependency versions
- `AndroidManifest-common.xml` — shared component manifest
