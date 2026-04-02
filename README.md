# WheelTimePicker

**WheelTimePicker** is a highly customizable, wheel-style time picker for Android Jetpack Compose. It features a realistic 3D cylinder effect using `graphicsLayer`, smooth snap animations, and sophisticated state management for a premium user experience.

## Demo

| Default Style | 3D Wheel Effect | Custom Colors & Fade |
|:---:|:---:|:---:|
|![화면 기록 2026-04-02 오후 8 22 18](https://github.com/user-attachments/assets/d7837203-2e73-426e-b1a8-49f14dfb942c)| ![화면 기록 2026-04-02 오후 8 27 13](https://github.com/user-attachments/assets/b82e36ce-b00c-4b86-98de-2f9766e0f324) | ![화면 기록 2026-04-02 오후 8 28 09](https://github.com/user-attachments/assets/421083c4-162f-4040-9dbe-359aa33e751b)|
| Clean and minimal default style | Realistic 3D rotation with `isWheelEffectEnabled` | Custom color schemes and `fadeEdge` gradients |

![Maven Central](https://img.shields.io/maven-central/v/io.github.seojw0124/wheeltimepicker)
![Android SDK](https://img.shields.io/badge/minSdk-26-brightgreen)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-purple)
![Compose](https://img.shields.io/badge/Compose-2026.03.01-blue)
![License](https://img.shields.io/badge/license-Apache%202.0-blue)

## Features

- 💫 **3D Cylinder Effect**: Optional realistic rotation and scaling using `graphicsLayer`.
- 🎨 **Fade Edge**: Smooth gradient transitions on the top and bottom edges.
- 🕰 **Smart State Management**: Internal 24-hour logic with automatic 12-hour UI conversion and AM/PM toggling.
- 📏 **Minute Interval**: Flexible minute steps (e.g., 1, 5, 10 mins).
- 📳 **Haptic Feedback**: Tactile vibration feedback as the wheel rotates, mimicking physical hardware.
- 🌐 **Localization Support**: Automatic AM/PM labels based on system locale (English, Korean, Japanese, etc.).
- 📱 **Android 8.0 (API 26)+** support.

## Installation

### Version Catalog (Recommended)

Add to your `libs.versions.toml`:

```toml
[versions]
wheeltimepicker = "1.0.0"

[libraries]
wheeltimepicker = { group = "io.github.seojw0124", name = "wheeltimepicker", version.ref = "wheeltimepicker" }
```

Then, add the dependency to your module-level `build.gradle.kts`:

```kotlin
dependencies {
    implementation(libs.wheeltimepicker)
}
```

### Traditional Gradle

```kotlin
dependencies {
    implementation("io.github.seojw0124:wheeltimepicker:1.0.0")
}
```

## Usage

### Basic Usage
The simplest way to implement the time picker.

```kotlin
val state = rememberWheelTimePickerState()

WheelTimePicker(
    state = state
)
```

### Advanced Customization
Example with 3D effects, fade edges, and a specific minute interval.

```kotlin
val state = rememberWheelTimePickerState(
    initialHour = 14,
    initialMinute = 30,
    minuteInterval = WheelTimePickerDefaults.MINUTE_INTERVAL_5
)

WheelTimePicker(
    state = state,
    isWheelEffectEnabled = true,   // Enable 3D cylinder effect
    isFadeEdgeEnabled = true,      // Enable edge gradients
    visibleItemCount = 5,          // Number of visible items (must be odd)
    colors = WheelTimePickerDefaults.colors(
        selectedTextColor = Color.Black,
        unSelectedTextColor = Color.Gray,
        fadeColor = Color.White
    )
)
```

## Key Properties

| Property | Description | Default |
|:---|:---|:---|
| `state` | Manages time state (`hour`, `minute`, `isPm`, etc.) | `rememberWheelTimePickerState()` |
| `minuteInterval` | Step interval for minutes (must be a divisor of 60) | `MINUTE_INTERVAL_5` |
| `visibleItemCount` | Number of items displayed simultaneously (odd number ≥ 3) | `3` |
| `isWheelEffectEnabled` | Whether to apply 3D cylinder rotation and scaling | `false` |
| `isFadeEdgeEnabled` | Whether to apply gradient transparency to the edges | `false` |
| `unselectedItemMinAlpha` | Minimum transparency for non-selected items | `0.3f` |
| `colors` | Color configuration for text and background | `WheelTimePickerDefaults.colors()` |

## Requirements

- Android API 26 (Oreo) or higher
- Jetpack Compose environment

## License

WheelTimePicker is available under the **Apache License 2.0**. See the [LICENSE](https://www.apache.org/licenses/LICENSE-2.0.txt) file for more information.

## Author

**Seojeong U**
- [GitHub @seojw0124](https://github.com/seojw0124)
- Email: wjddn2000124@gmail.com

---

### Support

If you find a bug or have a feature request, please open an **Issue** on GitHub. If this library helped you, don't forget to give it a ⭐️ Star!

<div>
  <a href="https://hits.sh/github.com/seojw0124/compose-wheel-time-picker/">
    <img src="https://hits.sh/github.com/seojw0124/compose-wheel-time-picker.svg"/>
  </a>
</div>
