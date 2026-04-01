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

## Local Publish
```bash
./gradlew :wheeltimepicker:publishReleasePublicationToLocalBuildRepoRepository
```

Published artifacts are generated under:
```text
wheeltimepicker/build/repo
```

## Maven Central Publish
1. Fill your values using `gradle/publish.properties.sample`
2. Put secrets in `~/.gradle/gradle.properties`
3. Use `SNAPSHOT` suffix for snapshot releases

Release publish command:
```bash
./gradlew :wheeltimepicker:publishReleasePublicationToSonatypeRepository
```

Snapshot publish command:
```bash
./gradlew :wheeltimepicker:publishReleasePublicationToSonatypeRepository \
  -PPUBLISH_VERSION=0.1.1-SNAPSHOT
```
