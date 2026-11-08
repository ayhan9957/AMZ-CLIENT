#include "AMZRuntimeProbe.h"

#include <link.h>
#include <string_view>

namespace amz {
namespace {

int inspectModule(dl_phdr_info *info, size_t, void *opaque) noexcept {
    if (info == nullptr || info->dlpi_name == nullptr) {
        return 0;
    }

    const std::string_view name(info->dlpi_name);
    constexpr std::string_view target = "libminecraftpe.so";
    if (name.size() < target.size() ||
        name.substr(name.size() - target.size()) != target) {
        return 0;
    }

    uintptr_t lowest = UINTPTR_MAX;
    uintptr_t highest = 0;
    for (ElfW(Half) i = 0; i < info->dlpi_phnum; ++i) {
        const auto &header = info->dlpi_phdr[i];
        if (header.p_type != PT_LOAD) {
            continue;
        }

        const uintptr_t start = static_cast<uintptr_t>(info->dlpi_addr) +
                                static_cast<uintptr_t>(header.p_vaddr);
        const uintptr_t end = start + static_cast<uintptr_t>(header.p_memsz);
        if (start < lowest) lowest = start;
        if (end > highest) highest = end;
    }

    if (lowest == UINTPTR_MAX || highest <= lowest) {
        return 0;
    }

    auto *result = static_cast<RuntimeModule *>(opaque);
    result->base = lowest;
    result->size = highest - lowest;
    return 1;
}

} // namespace

bool findMinecraftModule(RuntimeModule &module) noexcept {
    module = {};
    return dl_iterate_phdr(inspectModule, &module) == 1 && module.base != 0 &&
           module.size != 0;
}

} // namespace amz
