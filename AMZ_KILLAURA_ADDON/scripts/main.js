import { world, system, EntityDamageCause } from "@minecraft/server";
import { ActionFormData } from "@minecraft/server-ui";

// AMZ in-game control menu: use a compass to open it.
// The behavior pack must be installed in the world/server.
const RANGE = 4.5;
const DAMAGE = 3;
const HIT_INTERVAL_TICKS = 10;
const SCAN_INTERVAL_TICKS = 2;
const lastHit = new Map();
const ignoredTypes = new Set([
  "minecraft:item", "minecraft:xp_orb", "minecraft:arrow",
  "minecraft:snowball", "minecraft:egg", "minecraft:ender_pearl",
  "minecraft:trident", "minecraft:experience_orb"
]);

function openAmzMenu(player) {
  const enabled = player.hasTag("amz_killaura");
  new ActionFormData()
    .title("AMZ CLIENT")
    .body("Oyun ici kontrol menusu\n\nKillAura: " + (enabled ? "ACIK" : "KAPALI") +
      "\n\nNot: Paket bu dunyada/sunucuda yuklu olmalidir.")
    .button(enabled ? "KillAura KAPAT" : "KillAura AC")
    .button("Kapat")
    .show(player)
    .then(response => {
      if (response.canceled || response.selection !== 0) return;
      if (enabled) {
        player.removeTag("amz_killaura");
        player.sendMessage("§d[AMZ] §fKillAura kapatildi.");
      } else {
        player.addTag("amz_killaura");
        player.sendMessage("§d[AMZ] §fKillAura acildi.");
      }
    })
    .catch(() => {});
}

// Komut gerekmez: pusulayi kullanarak AMZ menüsünü aç.
world.afterEvents.itemUse.subscribe(event => {
  if (event.itemStack?.typeId !== "minecraft:compass") return;
  openAmzMenu(event.source);
});

system.runInterval(() => {
  for (const player of world.getPlayers()) {
    try {
      if (!player.isValid || !player.hasTag("amz_killaura")) continue;
      const origin = player.location;
      const nearby = player.dimension.getEntities({ location: origin, maxDistance: RANGE });
      for (const target of nearby) {
        if (!target.isValid || target.id === player.id || ignoredTypes.has(target.typeId)) continue;
        const dx = target.location.x - origin.x;
        const dy = target.location.y - origin.y;
        const dz = target.location.z - origin.z;
        if (dx * dx + dy * dy + dz * dz > RANGE * RANGE) continue;
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
    } catch (error) {}
  }
}, SCAN_INTERVAL_TICKS);

system.runInterval(() => {
  if (lastHit.size > 500) lastHit.clear();
}, 1200);
