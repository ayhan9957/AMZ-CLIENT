import { world, system, EntityDamageCause } from "@minecraft/server";

// AMZ KillAura prototype.
// Works only in worlds/servers where this behavior pack is enabled.
// Player entities are deliberately excluded: this first build targets mobs only.
const RANGE = 4.5;
const DAMAGE = 3;
const HIT_INTERVAL_TICKS = 10; // 0.5 seconds
const SCAN_INTERVAL_TICKS = 2;

const lastHit = new Map();
const ignoredTypes = new Set([
  "minecraft:player",
  "minecraft:item",
  "minecraft:xp_orb",
  "minecraft:arrow",
  "minecraft:snowball",
  "minecraft:egg",
  "minecraft:ender_pearl",
  "minecraft:trident",
  "minecraft:experience_orb"
]);

system.runInterval(() => {
  for (const player of world.getPlayers()) {
    try {
      if (!player.isValid || !player.hasTag("amz_killaura")) continue;

      const origin = player.location;
      const nearby = player.dimension.getEntities({
        location: origin,
        maxDistance: RANGE
      });

      for (const target of nearby) {
        if (!target.isValid || target.id === player.id) continue;
        if (ignoredTypes.has(target.typeId)) continue;

        const dx = target.location.x - origin.x;
        const dy = target.location.y - origin.y;
        const dz = target.location.z - origin.z;
        const distanceSquared = dx * dx + dy * dy + dz * dz;

        if (distanceSquared > RANGE * RANGE) continue;

        const key = player.id + ":" + target.id;
        const now = system.currentTick;
        const previous = lastHit.get(key) ?? -HIT_INTERVAL_TICKS;

        if (now - previous < HIT_INTERVAL_TICKS) continue;

        const didDamage = target.applyDamage(DAMAGE, {
          cause: EntityDamageCause.entityAttack,
          damagingEntity: player
        });

        if (didDamage) lastHit.set(key, now);
      }
    } catch (error) {
      // Keep one invalid/unloaded entity from stopping the scan for everyone.
    }
  }
}, SCAN_INTERVAL_TICKS);

// Remove stale cooldown entries periodically to keep memory use bounded.
system.runInterval(() => {
  if (lastHit.size > 500) lastHit.clear();
}, 1200);
