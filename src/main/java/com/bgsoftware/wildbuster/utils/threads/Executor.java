package com.bgsoftware.wildbuster.utils.threads;

import com.bgsoftware.wildbuster.WildBusterPlugin;
import org.bukkit.Bukkit;

public final class Executor {

    private static final WildBusterPlugin plugin = WildBusterPlugin.getPlugin();

    public static void sync(Runnable runnable){
        if(!Bukkit.isPrimaryThread())
            Bukkit.getScheduler().runTask(plugin, runnable);
        else
            runnable.run();
    }

    public static void sync(Runnable runnable, long delayedTime){
        Bukkit.getScheduler().runTaskLater(plugin, runnable, delayedTime);
    }

    public static void async(Runnable runnable){
        if(!Bukkit.isPrimaryThread())
            runnable.run();
        else
            Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    public static void async(Runnable runnable, long delay){
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay);
    }


}
