#pragma once
#include <pl/Mod.hpp>

namespace amz {
class AMZNativeMod {
public:
    static AMZNativeMod &instance();
    AMZNativeMod();
    [[nodiscard]] ll::mod::NativeMod &getSelf() const { return mSelf; }
    bool load();
    bool enable();
    bool disable();
    bool unload();
private:
    ll::mod::NativeMod &mSelf;
};
}
