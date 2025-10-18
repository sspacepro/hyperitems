// language: java
package org.galaxystudios.dungeonCreate.Listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageDoneListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();

        if (!(entity instanceof LivingEntity target)) return;

        LivingEntity attacker = null;

        if (damager instanceof Projectile projectile) {
            if (projectile.getShooter() instanceof LivingEntity) {
                attacker = (LivingEntity) projectile.getShooter();
            }
        } else if (damager instanceof LivingEntity) {
            attacker = (LivingEntity) damager;
        }

        if (attacker == null) return;

        double damage = event.getFinalDamage();



        event.setDamage(damage);
    }

}