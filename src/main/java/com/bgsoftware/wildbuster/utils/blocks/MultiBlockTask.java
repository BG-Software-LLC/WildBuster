package com.bgsoftware.wildbuster.utils.blocks;

import com.bgsoftware.wildbuster.WildBusterPlugin;
import com.bgsoftware.wildbuster.api.objects.BlockData;
import com.bgsoftware.wildbuster.api.objects.PlayerBuster;
import com.bgsoftware.wildbuster.hooks.listener.IBusterBlockListener;
import com.bgsoftware.wildbuster.scheduler.Scheduler;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class MultiBlockTask {

    private final Map<ChunkPosition, List<BlockCache>> blocksCache = Maps.newConcurrentMap();
    private final Map<ChunkPosition, List<Location>> tileEntities = Maps.newConcurrentMap();
    private final WildBusterPlugin plugin;
    private final OfflinePlayer offlinePlayer;
    private final PlayerBuster playerBuster;
    private final boolean remover;

    private boolean submitted = false;

    public MultiBlockTask(WildBusterPlugin plugin, OfflinePlayer offlinePlayer, PlayerBuster playerBuster, boolean remover){
        this.plugin = plugin;
        this.offlinePlayer = offlinePlayer;
        this.playerBuster = playerBuster;
        this.remover = remover;
    }

    public void setBlock(Location location, BlockData oldBlockData, BlockData newBlockData, boolean tileEntity){
        if(submitted)
            throw new IllegalArgumentException("This MultiBlockChange was already submitted.");

        ChunkPosition chunkPosition = ChunkPosition.of(location);

        List<BlockCache> blockCaches = blocksCache.computeIfAbsent(chunkPosition, pairs -> new ArrayList<>());

        blockCaches.add(new BlockCache(location, oldBlockData, newBlockData));

        if(tileEntity)
            tileEntities.computeIfAbsent(chunkPosition, list -> new ArrayList<>()).add(location);
    }

    public void submitUpdate(@Nullable Runnable onFinish){
        if(submitted)
            throw new IllegalArgumentException("This MultiBlockChange was already submitted.");

        submitted = true;

        if (Scheduler.isRegionScheduler() || Bukkit.isPrimaryThread()) {
            submitUpdateInternal(onFinish);
        } else {
            Scheduler.runTask(() -> submitUpdateInternal(onFinish));
        }
    }

    private void submitUpdateInternal(@Nullable Runnable onFinish) {
        List<Player> playerList = playerBuster.getNearbyPlayers();

        if (Scheduler.isRegionScheduler()) {
            int curr = 0;
            for (Map.Entry<ChunkPosition, List<BlockCache>> entry : blocksCache.entrySet()) {
                ChunkPosition chunkPosition = entry.getKey();
                boolean injectOnFinishCallback = ++curr == blocksCache.size();
                Scheduler.runTask(Bukkit.getWorld(chunkPosition.getWorld()), chunkPosition.getX(), chunkPosition.getZ(),
                        () -> updateChunk(chunkPosition, entry.getValue(), playerList,
                                injectOnFinishCallback ? onFinish : null));
            }
        } else {
            for (Map.Entry<ChunkPosition, List<BlockCache>> entry : blocksCache.entrySet()) {
                updateChunk(entry.getKey(), entry.getValue(), playerList, null);
            }
            if (onFinish != null)
                onFinish.run();
        }
    }

    private void updateChunk(ChunkPosition chunkPosition, List<BlockCache> blocksCache,
                             List<Player> playerList, @Nullable Runnable onFinish) {
        List<Location> blockLocations = new LinkedList<>();

        for (BlockCache blockCache : blocksCache) {
            plugin.getNMSAdapter().setFastBlock(blockCache.location, blockCache.newData);

            if (blockCache.newData.getType() == Material.AIR) {
                plugin.getProviders().notifyBusterBlockListeners(offlinePlayer, blockCache.location,
                        blockCache.oldData, IBusterBlockListener.Action.BLOCK_BREAK);
            } else {
                plugin.getProviders().notifyBusterBlockListeners(offlinePlayer, blockCache.location,
                        blockCache.newData, IBusterBlockListener.Action.BLOCK_PLACE);
            }

            if (blockCache.newData.hasContents()) {
                ((InventoryHolder) blockCache.location.getBlock().getState())
                        .getInventory().setContents(blockCache.newData.getContents());
            }

            blockLocations.add(blockCache.location);
        }

        Chunk chunk = Bukkit.getWorld(chunkPosition.getWorld()).getChunkAt(chunkPosition.getX(), chunkPosition.getZ());

        plugin.getNMSAdapter().refreshLight(chunk);
        plugin.getNMSAdapter().refreshChunk(chunk, blockLocations, playerList);

        List<Location> tileEntities = this.tileEntities.remove(chunkPosition);

        if (remover && tileEntities != null)
            plugin.getNMSAdapter().clearTileEntities(chunk, tileEntities);

        if (onFinish != null)
            onFinish.run();
    }

    private static class BlockCache {

        private final Location location;
        private final BlockData oldData, newData;

        BlockCache(Location location, BlockData oldBlockData, BlockData newData) {
            this.location = location;
            this.oldData = oldBlockData;
            this.newData = newData;
        }

        public Location getLocation() {
            return location;
        }
    }

}
