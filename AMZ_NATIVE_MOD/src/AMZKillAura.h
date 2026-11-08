#pragma once

namespace amz::killaura {
bool registerSettings();
void unregisterSettings();
int cps() noexcept;
bool enabled() noexcept;
}
