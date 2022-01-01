package net.zerotoil.dev.cyberlevels.listeners;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import net.zerotoil.dev.cyberlevels.objects.exp.EXPEarnEvent;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;

public class EXPListeners implements Listener {

    private final CyberLevels main;

    public EXPListeners(CyberLevels main) {
        this.main = main;
        Bukkit.getPluginManager().registerEvents(this, main);
    }

    @EventHandler (priority = EventPriority.LOWEST)
    private void onDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        /*Entity target = event.getEntity();
        if (target instanceof Player) {
            // check player stuff
        } else if (target instanceof Animals) {

        } else if (target instanceof Mob) {

        }*/

    }

    @EventHandler (priority = EventPriority.HIGHEST)
    private void onMobDeath(EntityDeathEvent event) {

    }

    @EventHandler (priority = EventPriority.HIGHEST)
    private void onBreeding(EntityBreedEvent event) {
        if (event.isCancelled()) return;
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    private void onPlacing(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        sendExp(event.getPlayer(), main.expCache().expEarnEvents().get("placing"), event.getBlock().getType().toString());

    }

    @EventHandler (priority = EventPriority.HIGHEST)
    private void onBreaking(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        sendExp(event.getPlayer(), main.expCache().expEarnEvents().get("breaking"), event.getBlock().getType().toString());
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    private void onCrafting(CraftItemEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (event.getCurrentItem() == null) return;

        sendExp((Player) event.getWhoClicked(), main.expCache().expEarnEvents().get("crafting"), event.getCurrentItem().getType().toString());
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    private void onFishing(PlayerFishEvent event) {
        if (event.isCancelled()) return;
        if (event.getCaught() == null) return;

        sendExp(event.getPlayer(), main.expCache().expEarnEvents().get("fishing"), ((Item) event.getCaught()).getItemStack().getType().toString());
        event.getPlayer().sendMessage(((Item) event.getCaught()).getItemStack().getType().toString());

    }

    @EventHandler
    private void onHarvesting(PlayerHarvestBlockEvent event) {
        if (event.isCancelled()) return;
    }

    private void sendExp(Player player, EXPEarnEvent expEarnEvent, String item) {
        double counter = 0;

        if (expEarnEvent.isEnabled())
            if (expEarnEvent.isInGeneralList(item))
                counter += expEarnEvent.getGeneralExp();

        if (expEarnEvent.isSpecificEnabled())
            if (expEarnEvent.isInSpecificList(item))
                counter += expEarnEvent.getSpecificExp(item);

        if (counter > 0) main.levelCache().playerLevels().get(player).addExp(counter);
    }


}
