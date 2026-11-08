#include "AMZRuntimeProbe.h"

#include <link.h>
#include <cstdio>
#include <cstring>
#include <string_view>

namespace amz {
namespace {

void readBuildId(const dl_phdr_info *info, RuntimeModule &result) noexcept {
    for (ElfW(Half) i = 0; i < info->dlpi_phnum; ++i) {
        const auto &phdr = info->dlpi_phdr[i];
        if (phdr.p_type != PT_NOTE || phdr.p_memsz < sizeof(ElfW(Nhdr))) continue;

        const auto *cursor = reinterpret_cast<const unsigned char *>(
            static_cast<uintptr_t>(info->dlpi_addr) + static_cast<uintptr_t>(phdr.p_vaddr));
        const auto *end = cursor + phdr.p_memsz;
        while (cursor + sizeof(ElfW(Nhdr)) <= end) {
            ElfW(Nhdr) note{};
            std::memcpy(&note, cursor, sizeof(note));
            cursor += sizeof(note);
            const uintptr_t nameSize = (note.n_namesz + 3U) & ~uintptr_t{3U};
            const uintptr_t descSize = (note.n_descsz + 3U) & ~uintptr_t{3U};
            if (nameSize > static_cast<uintptr_t>(end - cursor) ||
                descSize > static_cast<uintptr_t>(end - cursor) - nameSize) break;
            const auto *name = cursor;
            const auto *desc = cursor + nameSize;
            if (note.n_type == NT_GNU_BUILD_ID && note.n_namesz >= 3 &&
                std::memcmp(name, "GNU", 3) == 0 && note.n_descsz > 0) {
                const size_t bytes = note.n_descsz < 32 ? note.n_descsz : 32;
                for (size_t j = 0; j < bytes; ++j) {
                    std::snprintf(result.buildId.data() + j * 2, 3, "%02x", desc[j]);
                }
                return;
            }
            cursor += nameSize + descSize;
        }
    }
}

int inspectModule(dl_phdr_info *info, size_t, void *opaque) noexcept {
    if (info == nullptr || info->dlpi_name == nullptr) return 0;

    const std::string_view name(info->dlpi_name);
    constexpr std::string_view target = "libminecraftpe.so";
    if (name.size() < target.size() ||
        name.substr(name.size() - target.size()) != target) return 0;

    uintptr_t lowest = UINTPTR_MAX;
    uintptr_t highest = 0;
    for (ElfW(Half) i = 0; i < info->dlpi_phnum; ++i) {
        const auto &header = info->dlpi_phdr[i];
        if (header.p_type != PT_LOAD) continue;
        const uintptr_t start = static_cast<uintptr_t>(info->dlpi_addr) +
                                static_cast<uintptr_t>(header.p_vaddr);
        const uintptr_t end = start + static_cast<uintptr_t>(header.p_memsz);
        if (start < lowest) lowest = start;
        if (end > highest) highest = end;
    }
    if (lowest == UINTPTR_MAX || highest <= lowest) return 0;

    auto *result = static_cast<RuntimeModule *>(opaque);
    result->base = lowest;
    result->size = highest - lowest;
    readBuildId(info, *result);
    return 1;
}

} // namespace

bool findMinecraftModule(RuntimeModule &module) noexcept {
    module = {};
    return dl_iterate_phdr(inspectModule, &module) == 1 &&
           module.base != 0 && module.size != 0;
}

} // namespace amz
