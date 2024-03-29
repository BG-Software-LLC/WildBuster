package com.bgsoftware.wildbuster;

import com.bgsoftware.common.reflection.ReflectMethod;
import com.bgsoftware.wildbuster.api.WildBuster;
import com.bgsoftware.wildbuster.api.WildBusterAPI;
import com.bgsoftware.wildbuster.api.handlers.BustersManager;
import com.bgsoftware.wildbuster.command.CommandsHandler;
import com.bgsoftware.wildbuster.handlers.BustersHandler;
import com.bgsoftware.wildbuster.handlers.DataHandler;
import com.bgsoftware.wildbuster.handlers.ProvidersHandler;
import com.bgsoftware.wildbuster.handlers.SettingsHandler;
import com.bgsoftware.wildbuster.listeners.BlocksListener;
import com.bgsoftware.wildbuster.listeners.MenusListener;
import com.bgsoftware.wildbuster.listeners.PlayersListener;
import com.bgsoftware.wildbuster.metrics.Metrics;
import com.bgsoftware.wildbuster.nms.NMSAdapter;
import com.bgsoftware.wildbuster.utils.Pair;
import com.bgsoftware.wildbuster.utils.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.UnsafeValues;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public final class WildBusterPlugin extends JavaPlugin implements WildBuster {

    private static WildBusterPlugin plugin;

    private BustersManager bustersManager;
    private SettingsHandler settingsHandler;
    private ProvidersHandler providersHandler;
    private DataHandler dataHandler;

    private NMSAdapter nmsAdapter;

    private Enchantment glowEnchant;

    private boolean shouldEnable = true;

    @Override
    public void onLoad() {
        plugin = this;
        shouldEnable = loadNMSAdapter();
    }

    @Override
    public void onEnable() {
        if (!shouldEnable) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        new Metrics(this);

        log("******** ENABLE START ********");

        getServer().getPluginManager().registerEvents(new BlocksListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayersListener(this), this);
        getServer().getPluginManager().registerEvents(new MenusListener(), this);

        CommandsHandler commandsHandler = new CommandsHandler(this);
        getCommand("buster").setExecutor(commandsHandler);
        getCommand("buster").setTabCompleter(commandsHandler);

        registerGlowEnchantment();

        bustersManager = new BustersHandler(this);
        providersHandler = new ProvidersHandler(this);
        settingsHandler = new SettingsHandler(this);
        dataHandler = new DataHandler(this);

        Locale.reload();
        loadAPI();

        if (Updater.isOutdated()) {
            log("");
            log("A new version is available (v" + Updater.getLatestVersion() + ")!");
            log("Version's description: \"" + Updater.getVersionDescription() + "\"");
            log("");
        }

        log("******** ENABLE DONE ********");
    }

    @Override
    public void onDisable() {
        if (!shouldEnable)
            return;

        dataHandler.saveBusters();
    }

    private boolean loadNMSAdapter() {
        String version = null;

        if (ServerVersion.isLessThan(ServerVersion.v1_17)) {
            version = getServer().getClass().getPackage().getName().split("\\.")[3];
        } else {
            ReflectMethod<Integer> getDataVersion = new ReflectMethod<>(UnsafeValues.class, "getDataVersion");
            int dataVersion = getDataVersion.invoke(Bukkit.getUnsafe());

            List<Pair<Integer, String>> versions = Arrays.asList(
                    new Pair<>(2729, null),
                    new Pair<>(2730, "v1_17"),
                    new Pair<>(2974, null),
                    new Pair<>(2975, "v1_18"),
                    new Pair<>(3336, null),
                    new Pair<>(3337, "v1_19"),
                    new Pair<>(3465, "v1_20_1"),
                    new Pair<>(3578, "v1_20_2")
            );

            for (Pair<Integer, String> versionData : versions) {
                if (dataVersion <= versionData.first) {
                    version = versionData.second;
                    break;
                }
            }

            if (version == null) {
                log("Data version: " + dataVersion);
            }
        }

        if (version != null) {
            try {
                nmsAdapter = (NMSAdapter) Class.forName(String.format("com.bgsoftware.wildbuster.nms.%s.NMSAdapter", version)).newInstance();
                return true;
            } catch (Exception error) {
                error.printStackTrace();
            }
        }

        log("The plugin doesn't support your minecraft version.");
        log("Please try a different version.");

        return false;
    }

    private void loadAPI() {
        try {
            Field instance = WildBusterAPI.class.getDeclaredField("instance");
            instance.setAccessible(true);
            instance.set(null, this);
        } catch (Exception ex) {
            log("Failed to set-up API - disabling plugin...");
            setEnabled(false);
            ex.printStackTrace();
        }
    }

    private void registerGlowEnchantment() {
        glowEnchant = nmsAdapter.getGlowEnchant();

        try {
            Field field = Enchantment.class.getDeclaredField("acceptingNew");
            field.setAccessible(true);
            field.set(null, true);
            field.setAccessible(false);
        } catch (Exception ignored) {
        }

        try {
            Enchantment.registerEnchantment(glowEnchant);
        } catch (Exception ignored) {
        }
    }

    @Override
    public BustersManager getBustersManager() {
        return bustersManager;
    }

    public ProvidersHandler getProviders() {
        return providersHandler;
    }

    public SettingsHandler getSettings() {
        return settingsHandler;
    }

    public void setSettings(SettingsHandler settingsHandler) {
        this.settingsHandler = settingsHandler;
    }

    public DataHandler getDataHandler() {
        return dataHandler;
    }

    public NMSAdapter getNMSAdapter() {
        return nmsAdapter;
    }

    public Enchantment getGlowEnchant() {
        return glowEnchant;
    }

    public static void log(String message) {
        plugin.getLogger().log(Level.INFO, message);
    }

    public static WildBusterPlugin getPlugin() {
        return plugin;
    }

}
