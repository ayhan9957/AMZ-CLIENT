# AMZ Launcher Integration — port plan

## Current verified state

- The user-provided `AMZ_CLIENT_APK_Project.zip` is a small, single-module Android app (`com.amz.client`): 10 ZIP entries, one Java `MainActivity`, one manifest, one MP4 resource, and Gradle files.
- Its `MainActivity` launches `com.mojang.minecraftpe` with `PackageManager.getLaunchIntentForPackage()`. It does not prepare or host the Minecraft native runtime.
- `AMZ_NATIVE_MOD` builds an ARM64 Levi preloader native-mod package. Its current native code only logs load/enable/disable/unload; it contains no gameplay feature implementation.
- The existing AMZ floating overlay is a separate Android Service. It must remain visually unchanged.
- The current `.levipack` cannot be made active by adding it to the standalone AMZ APK. The loader must run in the launcher-owned Minecraft runtime context.

## Chosen integration direction

Use LeviLaunchroid's Apache-2.0 launcher/runtime implementation as the technical base, then rebrand and adapt its launcher entry UI to AMZ. Do not attempt to copy only `ModNativeLoader.java`: it depends on the launcher's `ModManager`, selected `GameVersion`, `GamePackageManager`, preloader initialization, signature rules, native-library preparation, and launch-session flow.

Upstream project: https://github.com/LiteLDev/LeviLaunchroid

Upstream runtime entry point:
`app/src/main/java/org/levimc/launcher/core/minecraft/MinecraftRuntimePreparer.kt`

Upstream native-mod loader:
`app/src/main/java/org/levimc/launcher/core/mods/ModNativeLoader.java`

Upstream license: Apache-2.0. Preserve the license, notices, and attribution for any reused or modified upstream files. Review the licenses of submodules and bundled dependencies independently.

## Port sequence

1. Import the launcher runtime as a separate integration layer, including required Gradle modules and Git submodules. Keep the original standalone AMZ project and the existing overlay source intact during the port.
2. Rebrand the launcher package/application identity as AMZ only after the upstream project builds unchanged.
3. Replace the launcher home screen with the AMZ home design while retaining the runtime's version selection, installed-game discovery, authentication, and launch preparation.
4. Integrate the existing AMZ overlay service without changing its bubble geometry, icon, drag behavior, categories, or panel layout.
5. Add a small, explicit settings bridge between the AMZ overlay controls and the native mod configuration. Do not use clipboard/chat commands as a substitute for game integration.
6. Load the ARM64 `AMZ_NATIVE_MOD` package through the launcher's native-mod manager and verify the native load lifecycle in logs.
7. Only then implement and test individual game features against the exact supported Bedrock build. Never label demo toggles as working features.

## Non-negotiable safety/compatibility constraints

- Do not modify, replace, uninstall, or overwrite the user's original Minecraft installation.
- Prefer the launcher's supported installed-package/runtime discovery path. Do not promise this works for every Minecraft distribution until tested on-device.
- Keep the AMZ overlay's current visual design unchanged.
- A successful APK build proves packaging only. A successful native library load proves loader integration only. Neither proves a gameplay feature works.
- Do not claim multiplayer or anti-cheat compatibility. Server-authoritative actions and server rules remain outside the client mod's control.
- Keep the existing `main` branch as the stable build; perform the port on `amz-launcher-integration` until it builds and passes validation.
