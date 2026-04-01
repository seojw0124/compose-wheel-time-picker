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
The module is configured with Maven Publish.

```bash
./gradlew :wheeltimepicker:publishReleasePublicationToLocalBuildRepoRepository
```

Published artifacts are generated under:

```text
wheeltimepicker/build/repo
```

## Coordinates
Default coordinates:
- groupId: `io.github.your-org`
- artifactId: `wheeltimepicker`
- version: `0.1.0`

Override from command line:

```bash
./gradlew :wheeltimepicker:publishReleasePublicationToLocalBuildRepoRepository \
  -PPUBLISH_GROUP_ID=com.your.group \
  -PPUBLISH_VERSION=1.0.0
```
