package com.bgsoftware.wildbuster.scheduler;

import com.bgsoftware.wildbuster.WildBusterPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class BukkitSchedulerImplementation implements ISchedulerImplementation {

    public static final BukkitSchedulerImplementation INSTANCE = new BukkitSchedulerImplementation();

    private static final WildBusterPlugin plugin = WildBusterPlugin.getPlugin();

    private BukkitSchedulerImplementation() {

    }

    @Override
    public boolean isRegionScheduler() {
        return false;
    }

    @Override
    public void scheduleTask(World unused, int unused1, int unused2, Runnable task, long delay) {
        scheduleTask(task, delay);
    }

    @Override
    public void scheduleTask(Entity unused, Runnable task, long delay) {
        scheduleTask(task, delay);
    }

    @Override
    public void scheduleTask(Runnable task, long delay) {
        if (delay <= 0) {
            Bukkit.getScheduler().runTask(plugin, task);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, task, delay);
        }
    }

    @Override
    public void scheduleAsyncTask(Runnable task, long delay) {
        if (delay <= 0) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        } else {
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delay);
        }
    }

}
