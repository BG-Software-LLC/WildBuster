package xyz.wildseries.wildbuster.api;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.wildseries.wildbuster.api.objects.BlockData;
import xyz.wildseries.wildbuster.api.objects.ChunkBuster;
import xyz.wildseries.wildbuster.api.objects.PlayerBuster;

import java.util.List;

@SuppressWarnings("unused")
public final class WildBusterAPI {

    private static WildBuster instance;

    /**
     * Get a chunk-buster object by it's name
     *
     * @param name a name to check
     * @return chunk-buster object
     */
    public static ChunkBuster getChunkBuster(String name){
        return instance.getBustersManager().getChunkBuster(name);
    }

    /**
     * Get a chunk-buster object by it's buster item
     *
     * @param item an item to check
     * @return chunk-buster object
     */
    public static ChunkBuster getChunkBuster(ItemStack item){
        return instance.getBustersManager().getChunkBuster(item);
    }

    /**
     * Get a player-buster from a chunk.
     *
     * @param chunk chunk to check
     * @return player-buster object
     */
    public static PlayerBuster getPlayerBuster(Chunk chunk){
        return instance.getBustersManager().getPlayerBuster(chunk);
    }

    /**
     * Get all player-buster that are ran by a player.
     *
     * @param player player to check
     * @return list of player-buster objects
     */
    public static List<PlayerBuster> getPlayerBusters(OfflinePlayer player){
        return instance.getBustersManager().getPlayerBusters(player);
    }

    /**
     * Create a new chunk-buster.
     *
     * @param name The name of the new chunk-buster
     * @param radius The radius of the new chunk-buster
     * @param busterItem The item of the new chunk-buster
     * @return A new chunk-buster object
     */
    public static ChunkBuster createChunkBuster(String name, int radius, ItemStack busterItem){
        return instance.getBustersManager().createChunkBuster(name, radius, busterItem);
    }

    /**
     * Create a new player-buster.
     *
     * @param player player that placed the chunk-buster.
     * @param placedLocation location of chunk-buster
     * @param buster the chunk-buster object
     * @return A new player-buster object
     */
    public static PlayerBuster createPlayerBuster(Player player, Location placedLocation, ChunkBuster buster){
        return instance.getBustersManager().createPlayerBuster(player, placedLocation, buster);
    }

    /**
     * Remove a player-buster from cache.
     * Note: IT DOESN'T STOP IT!
     *
     * @param buster a player-buster to remove
     */
    public static void removePlayerBuster(PlayerBuster buster){
        instance.getBustersManager().removePlayerBuster(buster);
    }

    /**
     * Get the player-buster that send notifications to the player.
     *
     * @param player a player to check
     * @return The player-buster that notifies.
     */
    public static PlayerBuster getNotifyBuster(OfflinePlayer player){
        return instance.getBustersManager().getNotifyBuster(player.getUniqueId());
    }

    /**
     * Get block-data from a block object
     *
     * @param block a block to check
     * @return block data of the provided block
     */
    public static BlockData getBlockData(Block block){
        return instance.getBustersManager().getBlockData(block);
    }

    /**
     * Get the wildbuster object
     *
     * @return wildbuster object
     */
    public static WildBuster getWildBuster(){
        return instance;
    }

}
