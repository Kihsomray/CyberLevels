package net.zerotoil.dev.cyberlevels.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class EarnExpEvent extends PlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    public EarnExpEvent(@NotNull Player player) {
        super(player);
    }

    @NotNull
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
