# AMZ launcher port — source import status

The launcher source is now pinned as a Git submodule at:

`AMZ_LAUNCHER_PORT/LeviLaunchroid`

Upstream: https://github.com/LiteLDev/LeviLaunchroid  
Pinned upstream commit: `ae0153f8ee66df0bb298a7841ca83cc81f942acf`

The pin includes the runtime preparation pipeline and native mod loader source. The upstream project also depends on two Git submodules:

- `app/src/main/cpp/preloader` → `LiteLDev/preloader-android`
- `app/src/main/cpp/libHttpClient` → `microsoft/libHttpClient`

CI checks out these submodules recursively and validates that the required source files and upstream license are present.

## Important build constraints found during inspection

- Upstream is a multi-module Android/Kotlin project (`:app`, `:minecraft`, `:pojav_controls`), not a drop-in Java library.
- It uses Android Gradle Plugin 8.13.2, Kotlin 2.3.0, Java 21, compile SDK 36, NDK 28.2.13676358, and ARM64-only native packaging.
- Its app build references a Discord Social SDK AAR under `app/libs`; the upstream CI downloads it from a private repository using a configured token.
- The upstream workflow also prepares Firebase configuration. A clean AMZ fork must remove or replace those service integrations deliberately rather than fabricate credentials.
- The launcher runtime is not integrated into the AMZ APK merely by adding this submodule. Gradle module import, app identity/rebranding, launcher UI adaptation, installed-game discovery, and overlay integration remain subsequent tasks.

## Validation boundary

The source-validation workflow proves only that the pinned upstream source and its required submodules were checked out. It does not compile the launcher, load AMZ's `.so` into Minecraft, or prove any gameplay feature.
