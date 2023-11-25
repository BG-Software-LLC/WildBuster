package com.bgsoftware.wildbuster.handlers;

import com.bgsoftware.wildbuster.WildBusterPlugin;
import com.bgsoftware.wildbuster.api.objects.BlockData;
import com.bgsoftware.wildbuster.hooks.ClaimsProvider;
import com.bgsoftware.wildbuster.hooks.FactionsProvider;
import com.bgsoftware.wildbuster.hooks.FactionsProvider_Default;
import com.bgsoftware.wildbuster.hooks.listener.IBusterBlockListener;
import com.bgsoftware.wildbuster.utils.threads.Executor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public final class ProvidersHandler {

    private final WildBusterPlugin plugin;

    private final Set<ClaimsProvider> claimsProviders = new HashSet<>();
    private final Set<IBusterBlockListener> busterBlockListeners = new HashSet<>();
    private FactionsProvider factionsProvider;

    public ProvidersHandler(WildBusterPlugin plugin) {
        this.plugin = plugin;

        Executor.sync(() -> {
            WildBusterPlugin.log("Loading providers started...");
            long startTime = System.currentTimeMillis();

            WildBusterPlugin.log(" - Using " + plugin.getNMSAdapter().getVersion() + " adapter.");

            loadGeneralHooks();
            loadFactionProvider();
            loadClaimsProviders();

            WildBusterPlugin.log("Loading providers done (Took " + (System.currentTimeMillis() - startTime) + "ms)");
        }, 1L);

    }

    public FactionsProvider getFactionsProvider() {
        return factionsProvider;
    }

    public boolean canBuild(OfflinePlayer player, Block block) {
        return claimsProviders.stream().allMatch(p -> p.canBuild(player, block));
    }

    public void registerBusterBlockListener(IBusterBlockListener busterBlockListener) {
        this.busterBlockListeners.add(busterBlockListener);
    }

    public void notifyBusterBlockListeners(OfflinePlayer offlinePlayer, Location location, BlockData blockData,
                                           IBusterBlockListener.Action action) {
        this.busterBlockListeners.forEach(busterBlockListener -> busterBlockListener
                .recordBlockAction(offlinePlayer, location, blockData, action));
    }

    private void loadFactionProvider() {
        Optional<FactionsProvider> factionsProvider = Optional.empty();

        if (Bukkit.getPluginManager().isPluginEnabled("FactionsX")) {
            factionsProvider = createInstance("FactionsProvider_FactionsX");
        } else if (Bukkit.getPluginManager().isPluginEnabled("Factions")) {
            if (!Bukkit.getPluginManager().getPlugin("Factions").getDescription().getAuthors().contains("drtshock")) {
                factionsProvider = createInstance("FactionsProvider_MassiveCore");
            } else {
                factionsProvider = createInstance("FactionsProvider_FactionsUUID");
            }
        }

        if (!factionsProvider.isPresent()) {
            factionsProvider = Optional.of(new FactionsProvider_Default());
            WildBusterPlugin.log(" - Couldn't find any factions providers, using default one.");
        }

        this.factionsProvider = factionsProvider.get();
    }

    private void loadClaimsProviders() {
        claimsProviders.clear();

        if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            Optional<ClaimsProvider> claimsProvider = createInstance("ClaimsProvider_WorldGuard");
            claimsProvider.ifPresent(claimsProviders::add);
        }
        if (Bukkit.getPluginManager().isPluginEnabled("GriefPrevention")) {
            Optional<ClaimsProvider> claimsProvider = createInstance("ClaimsProvider_GriefPrevention");
            claimsProvider.ifPresent(claimsProviders::add);
        }
        if (Bukkit.getPluginManager().isPluginEnabled("Lands")) {
            Optional<ClaimsProvider> claimsProvider = createInstance("ClaimsProvider_Lands");
            claimsProvider.ifPresent(claimsProviders::add);
        }
        if (Bukkit.getPluginManager().isPluginEnabled("RedProtect")) {
            Optional<ClaimsProvider> claimsProvider = createInstance("ClaimsProvider_RedProtect");
            claimsProvider.ifPresent(claimsProviders::add);
        }
        if (Bukkit.getPluginManager().isPluginEnabled("PlotSquared")) {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("PlotSquared");
            int plotSquaredVersion = Integer.parseInt(plugin.getDescription().getVersion().split("\\.")[0]);
            if (plotSquaredVersion >= 6) {
                try {
                    Optional<ClaimsProvider> claimsProvider = createInstance("ClaimsProvider_PlotSquared6");
                    claimsProvider.ifPresent(claimsProviders::add);
                } catch (Exception ex) {
                    WildBusterPlugin.log("&cYour version of PlotSquared is not supported. Please contact Ome_R for support.");
                }
            } else if (plotSquaredVersion == 5) {
                Optional<ClaimsProvider> claimsProvider = createInstance("ClaimsProvider_PlotSquared5");
                claimsProvider.ifPresent(claimsProviders::add);
            } else if (plugin.getDescription().getMain().contains("com.github")) {
                Optional<ClaimsProvider> claimsProvider = createInstance("ClaimsProvider_PlotSquared4");
                claimsProvider.ifPresent(claimsProviders::add);
            } else {
                Optional<ClaimsProvider> claimsProvider = createInstance("ClaimsProvider_PlotSquaredLegacy");
                claimsProvider.ifPresent(claimsProviders::add);
            }
        }
    }

    private void loadGeneralHooks() {
        if (Bukkit.getPluginManager().isPluginEnabled("CoreProtect")) {
            registerHook("CoreProtectHook");
        }
    }

    private void registerHook(String className) {
        try {
            Class<?> clazz = Class.forName("com.bgsoftware.wildbuster.hooks." + className);
            Method registerMethod = clazz.getMethod("register", WildBusterPlugin.class);
            registerMethod.invoke(null, plugin);
        } catch (Exception ignored) {
        }
    }

    private <T> Optional<T> createInstance(String className) {
        try {
            Class<?> clazz = Class.forName("com.bgsoftware.wildbuster.hooks." + className);
            try {
                Method compatibleMethod = clazz.getDeclaredMethod("isCompatible");
                if (!(boolean) compatibleMethod.invoke(null))
                    return Optional.empty();
            } catch (Exception ignored) {
            }

            try {
                Constructor<?> constructor = clazz.getConstructor(WildBusterPlugin.class);
                // noinspection unchecked
                return Optional.of((T) constructor.newInstance(plugin));
            } catch (Exception error) {
                // noinspection unchecked
                return Optional.of((T) clazz.newInstance());
            }
        } catch (ClassNotFoundException ignored) {
            return Optional.empty();
        } catch (Exception error) {
            error.printStackTrace();
            return Optional.empty();
        }
    }

}
