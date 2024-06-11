package com.bgsoftware.wildbuster.scheduler;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class Scheduler {
    private static final ISchedulerImplementation IMP = initializeSchedulerImplementation();

    private static ISchedulerImplementation initializeSchedulerImplementation() {
        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.RegionScheduler");
        } catch (ClassNotFoundException error) {
            return BukkitSchedulerImplementation.INSTANCE;
        }

        // Detected Folia, create its scheduler
        try {
            Class<?> foliaSchedulerClass = Class.forName("com.bgsoftware.wildbuster.scheduler.FoliaSchedulerImplementation");
            return (ISchedulerImplementation) foliaSchedulerClass.getField("INSTANCE").get(null);
        } catch (Throwable error) {
            throw new RuntimeException(error);
        }
    }

    private Scheduler() {

    }

    public static void initialize() {
        // Do nothing, load static initializer
    }

    public static boolean isRegionScheduler() {
        return IMP.isRegionScheduler();
    }

    public static void runTask(World world, int chunkX, int chunkZ, Runnable task, long delay) {
        IMP.scheduleTask(world, chunkX, chunkZ, task, delay);
    }

    public static void runTask(Entity entity, Runnable task, long delay) {
        IMP.scheduleTask(entity, task, delay);
    }

    public static void runTask(Runnable task, long delay) {
        IMP.scheduleTask(task, delay);
    }

    public static void runTaskAsync(Runnable task, long delay) {
        IMP.scheduleAsyncTask(task, delay);
    }

    public static void runTask(World world, int chunkX, int chunkZ, Runnable task) {
        runTask(world, chunkX, chunkZ, task, 0L);
    }

    public static void runTask(Location location, Runnable task, long delay) {
        runTask(location.getWorld(), location.getBlockX() >> 4, location.getBlockZ() >> 4, task, delay);
    }

    public static void runTask(Chunk chunk, Runnable task, long delay) {
        runTask(chunk.getWorld(), chunk.getX(), chunk.getZ(), task, delay);
    }

    public static void runTask(Chunk chunk, Runnable task) {
        runTask(chunk, task, 0L);
    }

    public static void runTask(Entity entity, Runnable task) {
        runTask(entity, task, 0L);
    }

    public static void runTask(Runnable task) {
        runTask(task, 0L);
    }

    public static void runTaskAsync(Runnable task) {
        runTaskAsync(task, 0L);
    }

}
