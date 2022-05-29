package net.zerotoil.dev.cyberlevels.listeners;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class AntiAbuseListeners implements Listener {

    private final CyberLevels main;

    public AntiAbuseListeners(CyberLevels main) {
        this.main = main;
        Bukkit.getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    private void onPistonExtend(BlockPistonExtendEvent event) {
        if (!event.isCancelled()) fixPlacedAbuse(event.getBlocks(), event.getDirection());
    }

    @EventHandler
    private void onPistonRetract(BlockPistonRetractEvent event) {
        if (!event.isCancelled()) fixPlacedAbuse(event.getBlocks(), event.getDirection());
    }

    private void fixPlacedAbuse(List<Block> blocks, BlockFace direction) {
        for (Block block : blocks) {
            if (block.hasMetadata("CLV_PLACED")) {
                (new BukkitRunnable() {
                    @Override
                    public void run() {
                        Block newBlock = block.getRelative(direction);
                        newBlock.setMetadata("CLV_PLACED", new FixedMetadataValue(main, true));
                    }
                }).runTaskLater(main, 1L);
            }
        }
    }

}

