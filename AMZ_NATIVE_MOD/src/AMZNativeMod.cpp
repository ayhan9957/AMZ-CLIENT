#include "AMZNativeMod.h"
#include "AMZRuntimeProbe.h"
#include "AMZKillAura.h"
#include <pl/Mod.hpp>
#include <filesystem>
#include <system_error>

namespace amz {
AMZNativeMod &AMZNativeMod::instance() {
    static AMZNativeMod instance;
    return instance;
}

AMZNativeMod::AMZNativeMod() : mSelf(*ll::mod::NativeMod::current()) {}

bool AMZNativeMod::load() {
    auto &self = getSelf();
    std::error_code ec;
    std::filesystem::create_directories(self.getDataDir(), ec);
    if (ec) {
        self.getLogger().error("AMZ: cannot create data directory: {}", ec.message());
        return false;
    }
    self.getLogger().info("AMZ Native loaded. Minecraft version target: 1.21.93");
    if (!killaura::registerSettings()) {
        self.getLogger().error("AMZ: failed to register KillAura settings");
        return false;
    }
    self.getLogger().info("AMZ: KillAura settings registered (CPS range 1-30, default 10)");
    return true;
}

bool AMZNativeMod::enable() {
    auto &logger = getSelf().getLogger();
    logger.info("AMZ Native enabled; checking Minecraft runtime.");

    RuntimeModule minecraft{};
    if (findMinecraftModule(minecraft)) {
        logger.info("AMZ: libminecraftpe.so detected at {} ({} bytes; ELF build-id: {})",
                    reinterpret_cast<void *>(minecraft.base),
                    minecraft.size,
                    minecraft.buildId[0] != '\0' ? minecraft.buildId.data() : "unavailable");
    } else {
        logger.warn("AMZ: libminecraftpe.so is not mapped at enable time; no game hooks will be installed");
    }
    return true;
}

bool AMZNativeMod::disable() {
    killaura::suspend();
    getSelf().getLogger().info("AMZ Native disabled; KillAura state reset.");
    return true;
}

bool AMZNativeMod::unload() {
    killaura::unregisterSettings();
    getSelf().getLogger().info("AMZ Native unloaded.");
    return true;
}
}
