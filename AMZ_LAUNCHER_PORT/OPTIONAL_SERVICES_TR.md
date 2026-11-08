# AMZ service dependency isolation plan

Pinned upstream inspected: `LiteLDev/LeviLaunchroid` at
`ae0153f8ee66df0bb298a7841ca83cc81f942acf`.

This is a source audit only. No upstream source or AMZ app code has been changed by this audit.

## Discord Social SDK — private binary blocker

- `app/build.gradle` unconditionally declares `implementation files('libs/discord_partner_sdk.aar')`.
- Upstream CI downloads that AAR from a separately configured private repository using `DISCORD_SDK_TOKEN`; the repository and token are not part of AMZ.
- `DiscordPresence.java` directly imports `com.discord.socialsdk.DiscordSocialSdkInit`. Setting `discord.application_id=0` disables runtime presence, but does not remove this compile-time class reference.
- `Application.kt` calls `DiscordPresence.initialize(this)`; `SettingsActivity.java` calls `isConfigured()` and `refresh()`.

Safe AMZ route: make Discord presence an explicit build option. When disabled, compile a small no-op `DiscordPresence` implementation with the same public static API and exclude the SDK-dependent implementation and AAR. Do not download or fabricate a private SDK binary.

## Firebase and Google services — separate concerns

- `app/build.gradle` applies Google Services and Firebase Crashlytics Gradle plugins and configures Crashlytics upload tasks for build variants.
- `app/src/main/.../CrashReporter.kt` references Firebase App and Crashlytics runtime classes and checks whether Firebase is configured before sending reports.
- Firebase Messaging is referenced by launcher news and Minecraft/Xbox notification service classes in the `:app` and `:minecraft` modules.
- The upstream CI uses a placeholder `google-services.json` only for compile checks when no real Firebase secret is configured. That placeholder is not a production Firebase setup.

Safe AMZ route: first remove Google Services and Crashlytics *Gradle plugins* and their upload DSL from the isolated build. Keep public Firebase runtime artifacts only where source still requires them, and disable/unregister Firebase-backed notification services if AMZ does not configure them. Do not ship the CI placeholder as a real service configuration.

## Module and toolchain constraints

- `:app` depends on both `:minecraft` and `:pojav_controls`.
- `:minecraft` has its own Firebase Messaging/IID dependencies and Android library configuration.
- `:pojav_controls` is an Android library module, not a source folder that can simply be copied into the AMZ app.
- Upstream requires Java 21, AGP 8.13.2, compile SDK 36, NDK 28.2.13676358, and ARM64 native packaging.
- `local.properties` is read for an optional CurseForge API key; the source defaults it to an empty string.

## Implementation order

1. Create an isolated AMZ validation build that imports the three upstream modules without replacing AMZ's current application ID, manifest, overlay, or Minecraft installation.
2. Add a build flag that excludes Discord's private AAR and substitutes the no-op API when disabled.
3. Remove optional Google/Firebase Gradle plugins and upload configuration; retain only public runtime dependencies required for compilation, and gate service registrations.
4. Run Gradle compile tasks and fix genuine source/dependency errors.
5. Only after the isolated launcher compiles, begin merging AMZ's existing UI/overlay into the launcher runtime.

## What this audit does not prove

It does not build an APK, load `libamz_native.so` inside Minecraft, or make any gameplay module functional. No original Minecraft APK or package is modified.
