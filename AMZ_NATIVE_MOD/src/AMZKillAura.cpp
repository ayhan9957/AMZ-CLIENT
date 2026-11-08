#include "AMZKillAura.h"

#include <algorithm>
#include <atomic>
#include <charconv>
#include <string>
#include <string_view>

#include <pl/ModMenu.hpp>

namespace amz::killaura {
namespace {
std::atomic<int> g_cps{10};
std::atomic<bool> g_enabled{false};

int parseCps(std::string_view value) noexcept {
    int parsed = 10;
    const auto *begin = value.data();
    const auto *end = begin + value.size();
    const auto result = std::from_chars(begin, end, parsed);
    if (result.ec != std::errc{} || result.ptr != end) return 10;
    return std::clamp(parsed, 1, 30);
}
}

int cps() noexcept { return g_cps.load(std::memory_order_relaxed); }
bool enabled() noexcept { return g_enabled.load(std::memory_order_relaxed); }

bool registerSettings() {
    pl::modmenu::ModuleBuilder module("amz.killaura", "KillAura");
    module.description("Automatic combat module")
        .defaultEnabled(false)
        .config("cps", "Attacks per second (CPS)",
                pl::modmenu::ConfigType::SliderInt, "10", "1", "30")
        .onToggle([](std::string_view, bool value) {
            g_enabled.store(value, std::memory_order_relaxed);
        })
        .onConfigChanged([](std::string_view, std::string_view key,
                            std::string_view value) {
            if (key == "cps") {
                g_cps.store(parseCps(value), std::memory_order_relaxed);
            }
        });
    return module.registerModule();
}

void unregisterSettings() {
    g_enabled.store(false, std::memory_order_relaxed);
    pl::modmenu::unregisterModule("amz.killaura");
}
}
