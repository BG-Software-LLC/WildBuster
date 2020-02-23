package com.bgsoftware.wildbuster.api.events;

import com.bgsoftware.wildbuster.api.objects.PlayerBuster;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

@SuppressWarnings("unused")
/**
 * ChunkBusterCancelEvent is called when a chunk buster is cancelled.
 */
public final class ChunkBusterCancelEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final PlayerBuster playerBuster;

    private boolean cancelled;

    /**
     * The constructor of the event.
     * @param player The player who cancelled the chunk buster.
     * @param playerBuster The chunk buster that was cancelled.
     */
    public ChunkBusterCancelEvent(Player player, PlayerBuster playerBuster){
        super(player);
        this.playerBuster = playerBuster;
        this.cancelled = false;
    }

    /**
     * Get the chunk buster that was cancelled.
     */
    public PlayerBuster getPlayerBuster() {
        return playerBuster;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
