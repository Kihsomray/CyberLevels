package net.zerotoil.dev.cyberlevels.listeners;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import net.zerotoil.dev.cyberlevels.objects.exp.EXPEarnEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerFishEvent;

public class EXPListeners implements Listener {

    private final CyberLevels main;

    public EXPListeners(CyberLevels main) {
        this.main = main;
        Bukkit.getPluginManager().registerEvents(this, main);
        if (main.serverVersion() >= 10) new EXPListenersV10(main, this);
    }

    // Works 1.7.10 - latest
    @EventHandler (priority = EventPriority.HIGHEST)
    private void onDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getDamager() instanceof Player)) return;

        Player player = ((Player) event.getDamager());
        Entity target = event.getEntity();
        String eventType;

        if (target instanceof Player) eventType = "damaging-players";
        else if (target instanceof Animals) eventType = "damaging-animals";
        else if (target instanceof Mob) eventType = "damaging-monsters";
        else return;

        sendExp(player, main.expCache().expEarnEvents().get(eventType), target.getType().toString());
    }

    // Works 1.7.10 - latest
    @EventHandler
    private void onMobDeath(EntityDeathEvent event) {

        if (event.getEntity().getLastDamageCause() == null) return;
        EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();
        if (!(damageEvent instanceof EntityDamageByEntityEvent)) return;
        Entity attacker = ((EntityDamageByEntityEvent) damageEvent).getDamager();
        if (!(attacker instanceof Player)) return;

        Player player = (Player) attacker;
        Entity target = event.getEntity();
        String eventType;

        if (target instanceof Player) eventType = "killing-players";
        else if (target instanceof Animals) eventType = "killing-animals";
        else if (target instanceof Mob) eventType = "killing-monsters";
        else return;

        sendExp(player, main.expCache().expEarnEvents().get(eventType), target.getType().toString());
    }

    // Works 1.7.10 - latest
    @EventHandler (priority = EventPriority.HIGHEST)
    private void onPlacing(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        sendExp(event.getPlayer(), main.expCache().expEarnEvents().get("placing"), event.getBlock().getType().toString());
    }

    // Works 1.7.10 - latest
    @EventHandler (priority = EventPriority.HIGHEST)
    private void onBreaking(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        sendExp(event.getPlayer(), main.expCache().expEarnEvents().get("breaking"), event.getBlock().getType().toString());
    }

    // Works 1.7.10 - latest
    @EventHandler (priority = EventPriority.HIGHEST)
    private void onCrafting(CraftItemEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (event.getCurrentItem() == null) return;

        sendExp((Player) event.getWhoClicked(), main.expCache().expEarnEvents().get("crafting"), event.getCurrentItem().getType().toString());
    }

    // Works 1.7.10 - latest
    @EventHandler (priority = EventPriority.HIGHEST)
    private void onFishing(PlayerFishEvent event) {
        if (event.isCancelled()) return;
        if (event.getCaught() == null) return;

        sendExp(event.getPlayer(), main.expCache().expEarnEvents().get("fishing"), ((Item) event.getCaught()).getItemStack().getType().toString());

    }

    /* @EventHandler (priority = EventPriority.HIGHEST)
    private void onChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;

        EXPEarnEvent expEarnEvent = main.expCache().expEarnEvents().get("chatting");
        Player player = event.getPlayer();
        String item = event.getMessage().toUpperCase();
        double counter = 0;

        if (expEarnEvent.isEnabled() && expEarnEvent.hasPartialMatches(item, true))
            counter += expEarnEvent.getPartialMatchesExp(item, true);

        if (expEarnEvent.isSpecificEnabled() && expEarnEvent.hasPartialMatches(item, false))
            counter += expEarnEvent.getPartialMatchesExp(item, false);

        if (counter > 0) main.levelCache().playerLevels().get(player).addExp(counter);
        else if (counter < 0) main.levelCache().playerLevels().get(player).removeExp(Math.abs(counter));
    } */

    // Works 1.16.1 - latest
    /* @EventHandler (priority = EventPriority.HIGHEST)
    private void onHarvesting(PlayerHarvestBlockEvent event) {
        if (event.isCancelled()) return;
    } */

    public void sendExp(Player player, EXPEarnEvent expEarnEvent, String item) {
        double counter = 0;

        if (expEarnEvent.isEnabled() && expEarnEvent.isInGeneralList(item))
            counter += expEarnEvent.getGeneralExp();

        if (expEarnEvent.isSpecificEnabled() && expEarnEvent.isInSpecificList(item))
            counter += expEarnEvent.getSpecificExp(item);

        if (counter > 0) main.levelCache().playerLevels().get(player).addExp(counter);
        else if (counter < 0) main.levelCache().playerLevels().get(player).removeExp(Math.abs(counter));
    }
}
