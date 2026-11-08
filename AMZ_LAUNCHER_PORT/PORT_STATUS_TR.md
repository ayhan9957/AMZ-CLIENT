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


## Gradle integration assessment (2026-11-08)

The pinned upstream Gradle files were inspected directly:

- `settings.gradle` includes three Android modules: `:app`, `:minecraft`, and `:pojav_controls`.
- The app plugin aliases resolve through the upstream version catalog; copying only `app/build.gradle` into the current AMZ project would leave those aliases and project modules unresolved.
- The upstream app namespace and application ID are both `org.levimc.launcher`; they must be adapted carefully for AMZ without changing the installed Minecraft package.
- The app requires Java 21, compile SDK 36, NDK 28.2.13676358, CMake, and ARM64 native packaging.
- The app build references `app/libs/discord_partner_sdk.aar`. This file and its provenance must be handled explicitly; CI cannot build from source if it is absent.
- Firebase plugins/configuration and Google Play Games metadata are upstream service integrations, not AMZ credentials. They must be removed or made optional for a clean AMZ build.
- The upstream manifest registers a large launcher activity/service graph and an application subclass. Importing only the native loader Java class is insufficient because it relies on the runtime preparer, ModManager, package/version management, preloader, and Minecraft module.

## Safe next implementation boundary

Do not merge the upstream launcher manifest or replace AMZ's application identity in one step. First create an isolated Gradle build-validation target for the pinned upstream modules, identify every unavailable binary/service dependency, and make those integrations optional without modifying upstream gameplay/runtime logic. Only after that target builds should AMZ UI/overlay migration begin.

The existing AMZ overlay and native skeleton remain separate. No native injection into the installed Minecraft app is claimed by this source assessment.
