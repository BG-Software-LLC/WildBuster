package com.bgsoftware.wildbuster.utils.blocks;

import com.bgsoftware.wildbuster.WildBusterPlugin;
import com.bgsoftware.wildbuster.api.objects.BlockData;
import com.bgsoftware.wildbuster.hooks.CoreProtectHook_CoreProtect;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class MultiBlockTask {

    private final Map<ChunkPosition,  List<Pair<Location, BlockData>>> blocksCache = Maps.newConcurrentMap();
    private final WildBusterPlugin plugin;
    private final OfflinePlayer offlinePlayer;

    private boolean submitted = false;

    public MultiBlockTask(WildBusterPlugin plugin, OfflinePlayer offlinePlayer){
        this.plugin = plugin;
        this.offlinePlayer = offlinePlayer;
    }

    public void setBlock(Location location, BlockData blockData){
        if(submitted)
            throw new IllegalArgumentException("This MultiBlockChange was already submitted.");

        ChunkPosition chunkPosition = ChunkPosition.of(location);
        blocksCache.computeIfAbsent(chunkPosition, pairs -> new ArrayList<>()).add(new Pair<>(location, blockData));
    }

    public void submitUpdate(Runnable onFinish){
        if(submitted)
            throw new IllegalArgumentException("This MultiBlockChange was already submitted.");

        submitted = true;

        ExecutorService executor = Executors.newCachedThreadPool();
        for(Map.Entry<ChunkPosition, List<Pair<Location, BlockData>>> entry : blocksCache.entrySet()){
            executor.execute(() -> {
                for(Pair<Location, BlockData> pair : entry.getValue()) {
                    synchronized (MultiBlockTask.class) {
                        plugin.getNMSAdapter().setFastBlock(pair.key, pair.value);
                    }
                }
            });
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
           try{
               executor.shutdown();
               executor.awaitTermination(1, TimeUnit.MINUTES);
           }catch(Exception ex){
               ex.printStackTrace();
               return;
           }

           Bukkit.getScheduler().runTask(plugin, () -> {
               blocksCache.keySet().forEach(chunkPosition -> {
                   if(plugin.getCoreProtectHook() instanceof CoreProtectHook_CoreProtect) {
                       blocksCache.get(chunkPosition).forEach(pair -> {
                           plugin.getCoreProtectHook().recordBlockChange(offlinePlayer, pair.key, pair.value, pair.value.getType() != Material.AIR);

                           if(pair.value.hasContents())
                               ((InventoryHolder) pair.key.getBlock().getState()).getInventory().setContents(pair.value.getContents());
                       });
                   }

                   plugin.getNMSAdapter().refreshChunk(Bukkit.getWorld(chunkPosition.getWorld()).getChunkAt(chunkPosition.getX(), chunkPosition.getZ()));
               });

               blocksCache.clear();

               if(onFinish != null)
                   onFinish.run();
           });
        });

    }

    private static class Pair<K, V>{

        private K key;
        private V value;

        Pair(K key, V value){
            this.key = key;
            this.value = value;
        }

    }

}
