package org.galaxystudios.hyperitems.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.galaxystudios.hyperitems.hyperitems;
import org.galaxystudios.hyperitems.LoadPlugin.LoadEntityElements;
import org.galaxystudios.hyperitems.LoadPlugin.LoadElementBeatsMap;
import org.jetbrains.annotations.NotNull;
import net.kyori.adventure.text.Component;

import java.util.*;

public class DamageDoneListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();

        if (!(entity instanceof LivingEntity target)) return;

        LivingEntity attacker = null;

        // Handle projectile shooters
        if (damager instanceof Projectile projectile) {
            if (projectile.getShooter() instanceof LivingEntity livingShooter) {
                attacker = livingShooter;
            }
        } else if (damager instanceof LivingEntity living) {
            attacker = living;
        }

        if (attacker == null) return;

        double baseDamage = event.getFinalDamage();

        // --- Get attacker and target stats ---
        EntityStats attackerStats = getEntityStats(attacker);
        EntityStats targetStats = getEntityStats(target);


        // --- Elemental Multiplier ---
        double elementMultiplier;
        if (attacker instanceof Player player) {
            elementMultiplier = calculatePlayerAttackMultiplier(player, target, targetStats.elements);
        } else {
            elementMultiplier = calculateMobMultiplier(attackerStats.elements, targetStats.elements);
        }

        double damage = baseDamage * elementMultiplier;

        // isCritical Hit?
        double critChance = attackerStats.critchance;
        boolean isCrit = Math.random() * 100 < critChance;
        // --- Player lifesteal, damage bonus, critical ---
        if (attacker instanceof Player player) {

            // damage = base damage + (base damage * (damage stat / 20))
            double potentialDamage = damage * (1 + (attackerStats.damage / 20.0));

            // Critical hit multiplier
            double critMultiplier = 3;
            damage = isCrit ? potentialDamage * critMultiplier : potentialDamage;

            // Lifesteal
            if (attackerStats.lifesteal > 0) {
                double heal = damage * (attackerStats.lifesteal / 100.0);
                double newHealth = Math.min(player.getHealth() + heal,
                        Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).getValue());
                player.setHealth(newHealth);
            }
        }

        // --- Set final damage ---
        event.setDamage(damage);

        if (attacker instanceof Player player) {


            String color = isCrit ? "§e§l" : "§c";
            String symbol = isCrit ? "✦" : "❤";

            // Spawn invisible armor stand
            ArmorStand stand = (ArmorStand) target.getWorld().spawnEntity(
                    target.getLocation().add(0, target.getHeight() + 0.5, 0),
                    EntityType.ARMOR_STAND
            );

            stand.setInvisible(true);
            stand.setCustomNameVisible(true);
            stand.setMarker(true);
            stand.setSmall(true);
            stand.setGravity(false);
            stand.customName(Component.text(color + "-" + String.format("%.1f%s", damage, symbol)));
            stand.setCustomNameVisible(true);

            // Optional: float-up animation
            Bukkit.getScheduler().runTaskTimer(hyperitems.getInstance(), task -> {
                if (!stand.isValid()) {
                    task.cancel();
                    return;
                }
                stand.teleport(stand.getLocation().add(0, 0.05, 0)); // move slightly upward
            }, 0L, 1L);

            // Remove after 1 second
            Bukkit.getScheduler().runTaskLater(hyperitems.getInstance(), () -> {
                if (stand.isValid()) stand.remove();
            }, 30L);

            // Optional: sound feedback for crits
            if (isCrit) {
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_ATTACK_CRIT, 1f, 1.2f);
            }
        }

    }

    // --- Calculate player attack multiplier (weighted by target elements) ---
    private double calculatePlayerAttackMultiplier(Player attacker, LivingEntity target, Set<String> targetElements) {
        Map<String, List<String>> beatsMap = LoadElementBeatsMap.getElementBeatsMap();

        // Weapon only
        ItemStack weapon = attacker.getInventory().getItemInMainHand();
        if (weapon.isEmpty() || !weapon.hasItemMeta()) return 1.0;

        String weaponElement = weapon.getItemMeta().getPersistentDataContainer()
                .get(new NamespacedKey(hyperitems.getInstance(), "elementType"), PersistentDataType.STRING);
        if (weaponElement == null) return 1.0;

        // Determine fraction per element
        double fraction = target instanceof Player ? 0.25 : 1.0;

        double multiplier = 1.0;

        for (String tElem : targetElements) {
            List<String> weaponBeats = beatsMap.getOrDefault(weaponElement, Collections.emptyList());
            List<String> targetBeats = beatsMap.getOrDefault(tElem, Collections.emptyList());

            if (weaponBeats.contains(tElem) && !targetBeats.contains(weaponElement)) {
                multiplier += fraction; // advantage
            } else if (targetBeats.contains(weaponElement) && !weaponBeats.contains(tElem)) {
                multiplier -= fraction; // disadvantage
            }
            // else neutral → no change
        }

        return Math.max(0.5, Math.min(2.0, multiplier));
    }

    // --- Mob vs anything multiplier (full 2x / 0.5x) ---
    private double calculateMobMultiplier(Set<String> attackerElements, Set<String> targetElements) {
        Map<String, List<String>> beatsMap = LoadElementBeatsMap.getElementBeatsMap();

        boolean attackerAdvantage = false;
        boolean targetAdvantage = false;

        for (String aElem : attackerElements) {
            List<String> beats = beatsMap.getOrDefault(aElem, Collections.emptyList());
            for (String tElem : targetElements) {
                if (beats.contains(tElem)) {
                    attackerAdvantage = true;
                    break;
                }
            }
        }

        for (String tElem : targetElements) {
            List<String> beats = beatsMap.getOrDefault(tElem, Collections.emptyList());
            for (String aElem : attackerElements) {
                if (beats.contains(aElem)) {
                    targetAdvantage = true;
                    break;
                }
            }
        }

        if (attackerAdvantage && !targetAdvantage) return 2.0;
        if (targetAdvantage && !attackerAdvantage) return 0.5;
        return 1.0;
    }

    // --- Gather stats from entity ---
    private EntityStats getEntityStats(LivingEntity entity) {
        LoadEntityElements loader = LoadEntityElements.getInstance();

        if (!(entity instanceof Player player)) {
            // Mob: element only
            String mobElement = loader.getMobElementMap().getOrDefault(entity.getType().name(), "Null");
            Set<String> elements = new HashSet<>();
            if (!mobElement.equalsIgnoreCase("Null")) elements.add(mobElement);
            return new EntityStats(0, 0, 0, elements);
        }

        // Player stats (damage, critchance, lifesteal)
        double totalDamage = 0;
        double totalCritChance = 0;
        double totalLifesteal = 0;
        Set<String> elements = new HashSet<>();

        // Weapon only
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (!mainHand.isEmpty() && mainHand.hasItemMeta()) {
            PersistentDataContainer data = mainHand.getItemMeta().getPersistentDataContainer();
            totalDamage += data.getOrDefault(key("damage"), PersistentDataType.DOUBLE, 0.0);
            totalCritChance += data.getOrDefault(key("critchance"), PersistentDataType.DOUBLE, 0.0);
            totalLifesteal += data.getOrDefault(key("lifesteal"), PersistentDataType.DOUBLE, 0.0);

            String mainElement = data.get(key("elementType"), PersistentDataType.STRING);
            if (mainElement != null) elements.add(mainElement);
        }

        // Armor pieces (for element only)
        for (EquipmentSlot slot : new EquipmentSlot[]{
                EquipmentSlot.HEAD, EquipmentSlot.CHEST,
                EquipmentSlot.LEGS, EquipmentSlot.FEET}) {

            ItemStack armorPiece = player.getInventory().getItem(slot);
            if (armorPiece.isEmpty() || !armorPiece.hasItemMeta()) continue;

            String pieceElement = armorPiece.getItemMeta().getPersistentDataContainer()
                    .get(key("elementType"), PersistentDataType.STRING);
            if (pieceElement != null && !Objects.equals(pieceElement, "Null")) {
                elements.add(pieceElement);
            }
        }

        return new EntityStats(totalDamage, totalCritChance, totalLifesteal, elements);
    }

    private NamespacedKey key(String name) {
        return new NamespacedKey(hyperitems.getInstance(), name);
    }

    private record EntityStats(double damage, double critchance, double lifesteal, Set<String> elements) {
        @Override
        public @NotNull String toString() {
            return "Damage=" + damage + ", critchance=" + critchance + ", Lifesteal=" + lifesteal +
                    ", Elements=" + elements;
        }
    }
}
