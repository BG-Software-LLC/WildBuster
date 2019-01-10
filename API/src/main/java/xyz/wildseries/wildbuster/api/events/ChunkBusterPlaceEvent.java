package xyz.wildseries.wildbuster.api.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import xyz.wildseries.wildbuster.api.objects.ChunkBuster;

@SuppressWarnings("unused")
public final class ChunkBusterPlaceEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final ChunkBuster chunkBuster;
    private final Location location;

    private boolean cancelled;

    public ChunkBusterPlaceEvent(Location location, Player player, ChunkBuster chunkBuster){
        super(player);
        this.location = location;
        this.chunkBuster = chunkBuster;
        this.cancelled = false;
    }

    public Location getLocation() {
        return location.clone();
    }

    public ChunkBuster getChunkBuster(){
        return chunkBuster;
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
