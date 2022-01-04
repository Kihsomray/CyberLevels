package net.zerotoil.dev.cyberlevels.listeners;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinListener implements Listener {

    private final CyberLevels main;

    public JoinListener(CyberLevels main) {
        this.main = main;
        Bukkit.getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        main.levelCache().loadPlayer(event.getPlayer());
    }

    @EventHandler
    private void onLeave(PlayerQuitEvent event) {
        main.levelCache().savePlayer(event.getPlayer(), true);
    }

}
