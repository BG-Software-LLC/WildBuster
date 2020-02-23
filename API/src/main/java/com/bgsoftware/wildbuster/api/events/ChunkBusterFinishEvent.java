package com.bgsoftware.wildbuster.api.events;

import com.bgsoftware.wildbuster.api.objects.PlayerBuster;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@SuppressWarnings("unused")
/**
 * ChunkBusterFinishEvent is called when a chunk buster is finished.
 */
public final class ChunkBusterFinishEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final PlayerBuster playerBuster;
    private final FinishReason finishReason;

    /**
     * The constructor of the event.
     * @param playerBuster The chunk buster that finished it's task.
     * @param finishReason The reason to finish the task.
     */
    public ChunkBusterFinishEvent(PlayerBuster playerBuster, FinishReason finishReason){
        super(!Bukkit.isPrimaryThread());
        this.playerBuster = playerBuster;
        this.finishReason = finishReason;
    }

    /**
     * The chunk buster that finished it's task.
     */
    public PlayerBuster getPlayerBuster() {
        return playerBuster;
    }

    /**
     * The reason to finish the task of the chunk buster.
     */
    public FinishReason getFinishReason() {
        return finishReason;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * FinishReason holds the reasons for finishing tasks of chunk busters.
     */
    public enum FinishReason{
        CANCEL_FINISH, BUSTER_FINISH
    }

}
