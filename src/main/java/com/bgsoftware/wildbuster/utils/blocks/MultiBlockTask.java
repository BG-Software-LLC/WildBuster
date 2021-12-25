package com.bgsoftware.wildbuster.utils.blocks;

import com.bgsoftware.wildbuster.WildBusterPlugin;
import com.bgsoftware.wildbuster.api.objects.BlockData;
import com.bgsoftware.wildbuster.api.objects.PlayerBuster;
import com.bgsoftware.wildbuster.hooks.listener.IBusterBlockListener;
import com.bgsoftware.wildbuster.objects.WBlockData;
import com.bgsoftware.wildbuster.utils.threads.Executor;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public void setBlock(Location location, BlockData blockData, boolean tileEntity){
        if(submitted)
            throw new IllegalArgumentException("This MultiBlockChange was already submitted.");

        ChunkPosition chunkPosition = ChunkPosition.of(location);
        Block upperBlock = location.clone().add(0, 1, 0).getBlock();

        List<BlockCache> blockCaches = blocksCache.computeIfAbsent(chunkPosition, pairs -> new ArrayList<>());

        if(plugin.getNMSAdapter().isTallGrass(upperBlock.getType()))
            blockCaches.add(new BlockCache(upperBlock.getLocation(), blockData));

        blockCaches.add(new BlockCache(location, blockData));

        if(tileEntity)
            tileEntities.computeIfAbsent(chunkPosition, list -> new ArrayList<>()).add(location);
    }

    public void submitUpdate(Runnable onFinish){
        if(!Bukkit.isPrimaryThread()){
            Executor.sync(() -> submitUpdate(onFinish));
            return;
        }

        if(submitted)
            throw new IllegalArgumentException("This MultiBlockChange was already submitted.");

        submitted = true;

        for(Map.Entry<ChunkPosition, List<BlockCache>> entry : blocksCache.entrySet()){
            for(BlockCache blockCache : entry.getValue()) {
                plugin.getNMSAdapter().setFastBlock(blockCache.location, blockCache.newData);
            }
        }

        List<Player> playerList = playerBuster.getNearbyPlayers();
        blocksCache.forEach((chunkPosition, blockDatas) -> {
            blockDatas.forEach(blockCache -> {
                plugin.getProviders().notifyBusterBlockListeners(offlinePlayer, blockCache.location,
                        blockCache.oldData, blockCache.newData.getType() == Material.AIR ?
                                IBusterBlockListener.Action.BLOCK_BREAK : IBusterBlockListener.Action.BLOCK_PLACE);

                if (blockCache.newData.hasContents()) {
                    ((InventoryHolder) blockCache.location.getBlock().getState())
                            .getInventory().setContents(blockCache.newData.getContents());
                }
            });

            Chunk chunk = Bukkit.getWorld(chunkPosition.getWorld()).getChunkAt(chunkPosition.getX(), chunkPosition.getZ());

            plugin.getNMSAdapter().refreshLight(chunk);
            plugin.getNMSAdapter().refreshChunk(chunk, blockDatas.stream()
                    .map(BlockCache::getLocation).collect(Collectors.toList()), playerList);

            List<Location> tileEntities = this.tileEntities.remove(chunkPosition);

            if (remover && tileEntities != null)
                plugin.getNMSAdapter().clearTileEntities(chunk, tileEntities);
        });

        blocksCache.clear();

        if(onFinish != null)
            onFinish.run();
    }

    private static class BlockCache{

        private final Location location;
        private final BlockData oldData, newData;

        BlockCache(Location location, BlockData newData){
            this.location = location;
            this.oldData = new WBlockData(location.getBlock(), null);
            this.newData = newData;
        }

        public Location getLocation() {
            return location;
        }
    }

}
