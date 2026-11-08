#pragma once

#include <cstdint>

namespace amz {

struct RuntimeModule {
    uintptr_t base{};
    uintptr_t size{};
};

[[nodiscard]] bool findMinecraftModule(RuntimeModule &module) noexcept;

}
