# WheelTimePicker Library Module

This module contains the reusable `WheelTimePicker` Compose library.

## Public API
- `WheelTimePicker`
- `WheelTimePickerState`
- `rememberWheelTimePickerState`
- `WheelTimePickerDefaults`
- `WheelTimePickerColors`
- `WheelTimePickerTextStyles`
- `WheelTimePickerAmPmLabels`

Internal UI building blocks are intentionally hidden with `private`/module-private scope.

## Maven Central (Simple)
This module uses `com.vanniktech.maven.publish`.

1. Fill values from `gradle/publish.properties.sample`
2. Put secrets in `~/.gradle/gradle.properties`
3. Publish:

Upload only (manual release in Central Portal):
```bash
./gradlew :wheeltimepicker:publishToMavenCentral
```

Upload + automatic release:
```bash
./gradlew :wheeltimepicker:publishAndReleaseToMavenCentral
```

## Local Publish
```bash
./gradlew :wheeltimepicker:publishToMavenLocal
```
