package net.zerotoil.dev.cyberlevels.addons;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import net.zerotoil.dev.cyberlevels.objects.exp.EXPEarnEvent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class LevelledMobs implements Listener {

    private final CyberLevels main;

    public LevelledMobs(CyberLevels main) {
        this.main = main;
        Bukkit.getPluginManager().registerEvents(this, main);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onEntityDeathEvent(@NotNull final EntityDeathEvent event) {
        /*if (event.getEntity() instanceof Player) return;

        EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();
        if (!(damageEvent instanceof EntityDamageByEntityEvent)) return;

        Entity attacker = ((EntityDamageByEntityEvent) damageEvent).getDamager();
        if (!(attacker instanceof Player)) return;

        LivingEntity target = event.getEntity();
        String eventType;

        if (target instanceof Animals) eventType = "killing-animals";
        else if (target instanceof Monster) eventType = "killing-monsters";
        else return;

        final Plugin lmPlugin = Bukkit.getPluginManager().getPlugin("LevelledMobs");
        if (lmPlugin == null) return;

        final NamespacedKey levelKey = new NamespacedKey(lmPlugin, "level");
        int level = 0;
        PersistentDataContainer container = target.getPersistentDataContainer();
        if (container.has(levelKey, PersistentDataType.INTEGER))
            level = Objects.requireNonNull(container.get(levelKey, PersistentDataType.INTEGER));

        if (level > 0) Bukkit.getLogger().info(target.getType().name() + " died, level: " + level);
        else Bukkit.getLogger().info(target.getType().name() + " died, was not levelled");*/


    }
}
