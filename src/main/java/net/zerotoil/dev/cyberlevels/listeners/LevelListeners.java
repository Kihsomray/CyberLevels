package net.zerotoil.dev.cyberlevels.listeners;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import net.zerotoil.dev.cyberlevels.events.EarnExpEvent;
import net.zerotoil.dev.cyberlevels.events.LevelUpEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LevelListeners implements Listener {

    private final CyberLevels main;

    public LevelListeners(CyberLevels main) {
        this.main = main;
    }

    @EventHandler
    private void onExpEarning(EarnExpEvent event) {

    }

    @EventHandler
    private void onLevelingUp(LevelUpEvent event) {

    }
}
