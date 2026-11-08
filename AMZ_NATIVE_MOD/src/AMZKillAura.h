#pragma once

namespace amz::killaura {
bool registerSettings();
void unregisterSettings();
void suspend() noexcept;
int cps() noexcept;
bool enabled() noexcept;
}
