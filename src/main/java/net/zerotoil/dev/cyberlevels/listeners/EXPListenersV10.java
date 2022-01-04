package net.zerotoil.dev.cyberlevels.listeners;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;

public class EXPListenersV10 implements Listener {

    private final CyberLevels main;
    private final EXPListeners expListeners;

    public EXPListenersV10(CyberLevels main, EXPListeners expListeners) {
        this.main = main;
        this.expListeners = expListeners;
        Bukkit.getPluginManager().registerEvents(this, main);
    }

    // Works 1.10 - latest
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onBreeding(EntityBreedEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getBreeder() instanceof Player)) return;

        expListeners.sendExp((Player) event.getBreeder(), main.expCache().expEarnEvents().get("breeding"), event.getEntity().getType().toString());
    }

}
