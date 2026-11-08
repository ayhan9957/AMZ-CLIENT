#include "AMZNativeMod.h"
#include "AMZRuntimeProbe.h"
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

    RuntimeModule minecraft{};
    if (findMinecraftModule(minecraft)) {
        self.getLogger().info("AMZ: libminecraftpe.so detected at {} ({} bytes)",
                              reinterpret_cast<void *>(minecraft.base),
                              minecraft.size);
    } else {
        self.getLogger().warn("AMZ: libminecraftpe.so is not mapped yet; runtime hooks remain inactive");
    }
    return true;
}

bool AMZNativeMod::enable() {
    getSelf().getLogger().info("AMZ Native enabled.");
    return true;
}

bool AMZNativeMod::disable() {
    getSelf().getLogger().info("AMZ Native disabled.");
    return true;
}

bool AMZNativeMod::unload() {
    getSelf().getLogger().info("AMZ Native unloaded.");
    return true;
}
}
