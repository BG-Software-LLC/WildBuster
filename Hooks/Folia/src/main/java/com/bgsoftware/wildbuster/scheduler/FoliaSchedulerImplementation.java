package com.bgsoftware.wildbuster.scheduler;

import com.bgsoftware.wildbuster.WildBusterPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.concurrent.TimeUnit;

public class FoliaSchedulerImplementation implements ISchedulerImplementation {

    public static final FoliaSchedulerImplementation INSTANCE = new FoliaSchedulerImplementation();

    private static final WildBusterPlugin plugin = WildBusterPlugin.getPlugin();

    private FoliaSchedulerImplementation() {
    }

    @Override
    public boolean isRegionScheduler() {
        return true;
    }

    @Override
    public void scheduleTask(World world, int chunkX, int chunkZ, Runnable task, long delay) {
        if (delay <= 0) {
            Bukkit.getServer().getRegionScheduler().run(plugin, world, chunkX, chunkZ, v -> task.run());
        } else {
            Bukkit.getServer().getRegionScheduler().runDelayed(plugin, world, chunkX, chunkZ, v -> task.run(), delay);
        }
    }

    @Override
    public void scheduleTask(Entity entity, Runnable task, long delay) {
        if (delay <= 0) {
            entity.getScheduler().run(plugin, v -> task.run(), task);
        } else {
            entity.getScheduler().runDelayed(plugin, v -> task.run(), task, delay);
        }
    }

    @Override
    public void scheduleTask(Runnable task, long delay) {
        if (delay <= 0) {
            Bukkit.getServer().getGlobalRegionScheduler().run(plugin, v -> task.run());
        } else {
            Bukkit.getServer().getGlobalRegionScheduler().runDelayed(plugin, v -> task.run(), delay);
        }
    }

    @Override
    public void scheduleAsyncTask(Runnable task, long delay) {
        if (delay <= 0) {
            Bukkit.getServer().getAsyncScheduler().runNow(plugin, v -> task.run());
        } else {
            Bukkit.getServer().getAsyncScheduler().runDelayed(plugin, v -> task.run(), delay * 50L, TimeUnit.MILLISECONDS);
        }
    }

}
