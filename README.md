# Origin Launcher

Origin Launcher is a modern Android launcher focused on simplicity, clarity, and intentional design.

Built on top of Lawnchair, Origin Launcher reimagines the launcher experience through a curated set of design systems instead of endless customization.

## Philosophy

Most Android launchers expose hundreds of settings and tuning options.

Origin Launcher takes a different approach:

- Strong defaults
- Minimal configuration
- Consistent visual language
- Clear design identities
- User-first experience

Instead of overwhelming users with settings, Origin Launcher offers carefully designed experiences that shape the launcher as a whole.

---

## Experiences

### Origin Pure

A clean and familiar Android experience inspired by Pixel Launcher.

Features:

- Material You integration
- Standard Android layout
- Minimal motion
- Consistent spacing
- Traditional dock and search surfaces
- Colorful icon experience

Designed for users who want a calm and distraction-free launcher.

---

### Origin Flow

A modern and expressive experience inspired by glassmorphism and contemporary interface design.

Features:

- Floating dock
- Floating search surfaces
- Glass-inspired UI
- Increased depth and motion
- Monochrome-first icon styling
- Capsule design language
- Layered visual hierarchy

Designed for users who want a distinctive and modern home screen.

---

## Customization

Origin Launcher intentionally limits customization to maintain visual consistency.

### Available Options

- Accent Color
- Icon Pack
- Icon Size
- Icon Shape

### Flow Exclusive

- Dock Blur
- Dock Transparency

---

## Features

- Material You support
- Icon pack support
- Custom icon shapes
- Smartspace / Glance support
- Gesture navigation support
- Backup and restore
- Adaptive theming
- Android 12+ enhancements

---

## Technical Information

### Base Project

Origin Launcher is built upon the Lawnchair Launcher project.

Major parts of the codebase originate from Lawnchair and the Android Open Source Project (AOSP).

Additional design systems, experience modes, branding, and feature modifications are developed by Origin Labs.

---

## Building

### Requirements

- Android Studio
- JDK 17+
- Android SDK

### Build

```bash
./gradlew assembleDebug
```

### Install

```bash
./gradlew installDebug
```

---

## Project Structure

```
Origin Launcher
├── Origin Experiences
│   ├── Origin Pure
│   └── Origin Flow
├── Workspace
├── Dock
├── Library
├── Search
├── Actions
└── System
```

---

## Roadmap

### Current

- Experience System
- Origin Pure
- Origin Flow
- Simplified Settings
- Visual Identity Refresh

### Future

- Origin Monochrome Icon Pack
- Enhanced Glass Surfaces
- Origin App Ecosystem
- Origin Space
- Origin Gallery
- Origin Files
- Origin Notes
- Origin OS

---

## Credits

Origin Launcher is based on:

- Lawnchair Launcher
- Android Open Source Project (AOSP)

Special thanks to all contributors of the open-source Android launcher ecosystem.

---

## License

See the LICENSE file for details.
