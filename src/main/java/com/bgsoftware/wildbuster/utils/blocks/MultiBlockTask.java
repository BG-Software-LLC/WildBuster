package com.bgsoftware.wildbuster.utils.blocks;

import com.bgsoftware.wildbuster.WildBusterPlugin;
import com.bgsoftware.wildbuster.api.objects.BlockData;
import com.bgsoftware.wildbuster.api.objects.PlayerBuster;
import com.bgsoftware.wildbuster.hooks.CoreProtectHook_CoreProtect;
import com.bgsoftware.wildbuster.utils.threads.Executor;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class MultiBlockTask {

    private final Map<ChunkPosition, List<Pair<Location, BlockData>>> blocksCache = Maps.newConcurrentMap();
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
        blocksCache.computeIfAbsent(chunkPosition, pairs -> new ArrayList<>()).add(new Pair<>(location, blockData));

        if(tileEntity)
            tileEntities.computeIfAbsent(chunkPosition, list -> new ArrayList<>()).add(location);
    }

    public void submitUpdate(Runnable onFinish){
        if(submitted)
            throw new IllegalArgumentException("This MultiBlockChange was already submitted.");

        submitted = true;

        ExecutorService executor = Executors.newCachedThreadPool();
        for(Map.Entry<ChunkPosition, List<Pair<Location, BlockData>>> entry : blocksCache.entrySet()){
            executor.execute(() -> {
                for(Pair<Location, BlockData> pair : entry.getValue()) {
                    plugin.getNMSAdapter().setFastBlock(pair.key, pair.value);
                }
            });
        }

        Executor.async(() -> {
           try{
               executor.shutdown();
               executor.awaitTermination(1, TimeUnit.MINUTES);
           }catch(Exception ex){
               ex.printStackTrace();
               return;
           }

           Executor.sync(() -> {
               List<Player> playerList = playerBuster.getNearbyPlayers();
               blocksCache.forEach((chunkPosition, blockDatas) -> {
                   blockDatas.forEach(pair -> {
                       if (plugin.getCoreProtectHook() instanceof CoreProtectHook_CoreProtect)
                           plugin.getCoreProtectHook().recordBlockChange(offlinePlayer, pair.key, pair.value, pair.value.getType() != Material.AIR);

                       if (pair.value.hasContents())
                           ((InventoryHolder) pair.key.getBlock().getState()).getInventory().setContents(pair.value.getContents());
                   });

                   Chunk chunk = Bukkit.getWorld(chunkPosition.getWorld()).getChunkAt(chunkPosition.getX(), chunkPosition.getZ());

                   plugin.getNMSAdapter().refreshLight(chunk);
                   plugin.getNMSAdapter().refreshChunk(chunk, blockDatas.stream().map(Pair::getKey).collect(Collectors.toList()), playerList);

                   List<Location> tileEntities = this.tileEntities.remove(chunkPosition);

                   if (remover && tileEntities != null)
                       plugin.getNMSAdapter().clearTileEntities(chunk, tileEntities);
               });

               blocksCache.clear();

               if(onFinish != null)
                   onFinish.run();
           });
        });

    }

    private static class Pair<K, V>{

        private final K key;
        private final V value;

        Pair(K key, V value){
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }
    }

}
