# Settings Hierarchy Fix Plan

## Problem Summary
Gemini audit identified 5 issues with Origin Launcher 16's settings hierarchy vs Lawnchair 16:

1. **No "General" settings screen** — Theming (colors, icon packs, shapes, notification dots) is scattered across 3 screens
2. **"Backup & Restore" mislabeled as "System"** — Not discoverable  
3. **App Drawer disappears** when switching from Simple → Advanced tab
4. **At a Glance disappears** when switching from Simple → Advanced tab
5. **`ic_general` icon reused** 4 times (identical icon for distinct entries)

## Implementation Plan

### Step 1: Add strings to `values/strings.xml`
**File:** `lawnchair/res/values/strings.xml`
- Add `general_label` = `"General"`  
- Add `backup_restore_label` = `"Backup & Restore"`

### Step 2: Create `GeneralPreferences.kt`
**File:** `lawnchair/src/app/lawnchair/ui/preferences/destinations/GeneralPreferences.kt`
- New theming hub screen with:
  - **Theme group:** Accent color, Icon pack navigation, Icon shape navigation, Custom icon shape navigation, Icon size slider, Monochrome icons toggle
  - **Notification dots group:** Notification dots settings (reuse `NotificationDotsPreference` composable)

### Step 3: Add `General` route to `PreferenceRoutes.kt`
**File:** `lawnchair/src/app/lawnchair/ui/preferences/navigation/PreferenceRoutes.kt`
- Add `data object General : PreferenceRootRoute, PreferenceDeepLink` with deep link `lawnchair://settings/general`

### Step 4: Add navigation composable
**File:** `lawnchair/src/app/lawnchair/ui/preferences/navigation/PreferenceNavigation.kt`
- Add `composable<General>(deepLinks = getDeepLink(General)) { GeneralPreferences() }`

### Step 5: Restructure `PreferencesDashboard.kt`
**File:** `lawnchair/src/app/lawnchair/ui/preferences/destinations/PreferencesDashboard.kt`
- **Simple tab:** General (new), At a Glance, App Drawer, Origin Modes, About
- **Advanced tab:** All 13 items (including General, At a Glance, App Drawer — nothing disappears)
- Rename "System" → "Backup & Restore" (use `backup_restore_label` string resource)
- Distinct icons:
  - General → `ic_general`
  - Origin Modes → `ic_lightbulb`
  - Backup & Restore → `ic_download`
  - Personalization → `ic_personalization` or `ic_wallpaper`  
  - Experimental → `ic_new_releases`

### Step 6: Update `DESIGN_LANGUAGE.md`
- Document the new General screen under Component Architecture
- Update the settings hierarchy section

## Verification
- Build should succeed with `./gradlew :lawnchair:assembleDebug`
- All settings entries visible in both Simple and Advanced tabs
- No `ic_general` reused more than once
