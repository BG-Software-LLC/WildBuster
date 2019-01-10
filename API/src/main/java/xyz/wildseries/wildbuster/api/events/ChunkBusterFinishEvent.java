package xyz.wildseries.wildbuster.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import xyz.wildseries.wildbuster.api.objects.PlayerBuster;

@SuppressWarnings("unused")
public final class ChunkBusterFinishEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final PlayerBuster playerBuster;
    private final FinishReason finishReason;

    public ChunkBusterFinishEvent(PlayerBuster playerBuster, FinishReason finishReason){
        this.playerBuster = playerBuster;
        this.finishReason = finishReason;
    }

    public PlayerBuster getPlayerBuster() {
        return playerBuster;
    }

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

    public enum FinishReason{
        CANCEL_FINISH, BUSTER_FINISH
    }

}
