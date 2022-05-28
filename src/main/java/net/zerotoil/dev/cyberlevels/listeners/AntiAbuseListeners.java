package net.zerotoil.dev.cyberlevels.listeners;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;

public class AntiAbuseListeners implements Listener {

    private final CyberLevels main;

    public AntiAbuseListeners(CyberLevels main) {
        this.main = main;
        Bukkit.getPluginManager().registerEvents(this, main);
    }

}
