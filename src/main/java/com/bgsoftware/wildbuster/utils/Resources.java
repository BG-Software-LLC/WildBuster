package com.bgsoftware.wildbuster.utils;

import com.bgsoftware.wildbuster.WildBusterPlugin;

import java.io.File;
import java.io.InputStream;

public class Resources {

    private static final WildBusterPlugin plugin = WildBusterPlugin.getPlugin();

    public static void saveResource(String resourcePath) {
        try {
            String newResourcePath = getResourceNameInternal(resourcePath);
            plugin.saveResource(newResourcePath, true);

            if (!resourcePath.equals(newResourcePath)) {
                File src = new File(plugin.getDataFolder(), newResourcePath);
                File dest = new File(plugin.getDataFolder(), resourcePath);
                //noinspection ResultOfMethodCallIgnored
                src.renameTo(dest);
            }

        } catch (Exception error) {
            WildBusterPlugin.log("An unexpected error occurred while saving resource:");
            error.printStackTrace();
        }
    }

    public static InputStream getResource(String resourcePath) {
        try {
            resourcePath = getResourceNameInternal(resourcePath);
            return plugin.getResource(resourcePath);
        } catch (Exception error) {
            WildBusterPlugin.log("An unexpected error occurred while retrieving resource:");
            error.printStackTrace();
            return null;
        }
    }

    private static String getResourceNameInternal(String resourcePath) {
        for (ServerVersion serverVersion : ServerVersion.getByOrder()) {
            String version = serverVersion.name().substring(1);
            if (resourcePath.endsWith(".yml") && plugin.getResource(resourcePath.replace(".yml", version + ".yml")) != null) {
                return resourcePath.replace(".yml", version + ".yml");
            }
        }

        return resourcePath;
    }

    private Resources() {

    }

}
