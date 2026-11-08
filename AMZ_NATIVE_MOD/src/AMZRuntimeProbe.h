#pragma once

#include <array>
#include <cstdint>

namespace amz {

struct RuntimeModule {
    uintptr_t base{};
    uintptr_t size{};
    std::array<char, 65> buildId{};
};

[[nodiscard]] bool findMinecraftModule(RuntimeModule &module) noexcept;

}
