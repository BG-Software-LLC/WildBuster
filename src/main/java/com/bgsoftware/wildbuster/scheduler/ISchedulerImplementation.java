package com.bgsoftware.wildbuster.scheduler;

import org.bukkit.World;
import org.bukkit.entity.Entity;

public interface ISchedulerImplementation {

    boolean isRegionScheduler();

    void scheduleTask(World world, int chunkX, int chunkZ, Runnable task, long delay);

    void scheduleTask(Entity entity, Runnable task, long delay);

    void scheduleTask(Runnable task, long delay);

    void scheduleAsyncTask(Runnable task, long delay);

}
