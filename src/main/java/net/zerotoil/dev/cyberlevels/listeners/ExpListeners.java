package net.zerotoil.dev.cyberlevels.listeners;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;

public class ExpListeners implements Listener {

    private final CyberLevels main;

    public ExpListeners(CyberLevels main) {
        this.main = main;
    }

    @EventHandler
    private void onDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
    }

    @EventHandler
    private void onMobDeath(EntityDeathEvent event) {

    }

    @EventHandler
    private void onBreeding(EntityBreedEvent event) {
        if (event.isCancelled()) return;
    }

    @EventHandler
    private void onPlacing(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
    }

    @EventHandler
    private void onBreaking(BlockBreakEvent event) {
        if (event.isCancelled()) return;
    }

    @EventHandler
    private void onCrafting(CraftItemEvent event) {
        if (event.isCancelled()) return;
    }

    @EventHandler
    private void onFishing(PlayerFishEvent event) {
        if (event.isCancelled()) return;
    }

    @EventHandler
    private void onHarvesting(PlayerHarvestBlockEvent event) {
        if (event.isCancelled()) return;
    }
}
