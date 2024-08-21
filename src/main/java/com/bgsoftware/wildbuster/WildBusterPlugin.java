package com.bgsoftware.wildbuster;

import com.bgsoftware.common.dependencies.DependenciesManager;
import com.bgsoftware.common.nmsloader.INMSLoader;
import com.bgsoftware.common.nmsloader.NMSHandlersFactory;
import com.bgsoftware.common.nmsloader.NMSLoadException;
import com.bgsoftware.common.nmsloader.config.NMSConfiguration;
import com.bgsoftware.common.updater.Updater;
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
import com.bgsoftware.wildbuster.nms.NMSAdapter;
import com.bgsoftware.wildbuster.scheduler.Scheduler;
import org.bstats.bukkit.Metrics;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.logging.Level;

public final class WildBusterPlugin extends JavaPlugin implements WildBuster {

    private final Updater updater = new Updater(this, "wildbuster");

    private static WildBusterPlugin plugin;

    private BustersManager bustersManager;
    private SettingsHandler settingsHandler;
    private ProvidersHandler providersHandler;
    private DataHandler dataHandler;

    private NMSAdapter nmsAdapter;

    @Nullable
    private Enchantment glowEnchant;

    private boolean shouldEnable = true;

    @Override
    public void onLoad() {
        plugin = this;

        Scheduler.initialize();

        DependenciesManager.inject(this);

        if (!loadNMSAdapter()) {
            this.shouldEnable = false;
            return;
        }

        this.glowEnchant = nmsAdapter.createGlowEnchantment();
    }

    @Override
    public void onEnable() {
        if (!shouldEnable) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.nmsAdapter.loadLegacy();

        new Metrics(this, 4106);

        log("******** ENABLE START ********");

        getServer().getPluginManager().registerEvents(new BlocksListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayersListener(this), this);
        getServer().getPluginManager().registerEvents(new MenusListener(), this);

        CommandsHandler commandsHandler = new CommandsHandler(this);
        getCommand("buster").setExecutor(commandsHandler);
        getCommand("buster").setTabCompleter(commandsHandler);

        bustersManager = new BustersHandler(this);
        providersHandler = new ProvidersHandler(this);
        settingsHandler = new SettingsHandler(this);
        dataHandler = new DataHandler(this);

        Locale.reload();
        loadAPI();

        if (updater.isOutdated()) {
            log("");
            log("A new version is available (v" + updater.getLatestVersion() + ")!");
            log("Version's description: \"" + updater.getVersionDescription() + "\"");
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
        try {
            INMSLoader nmsLoader = NMSHandlersFactory.createNMSLoader(this, NMSConfiguration.forPlugin(this));
            this.nmsAdapter = nmsLoader.loadNMSHandler(NMSAdapter.class);

            return true;
        } catch (NMSLoadException error) {
            log("The plugin doesn't support your minecraft version.");
            log("Please try a different version.");
            error.printStackTrace();

            return false;
        }
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

    @Nullable
    public Enchantment getGlowEnchant() {
        return glowEnchant;
    }

    public Updater getUpdater() {
        return updater;
    }

    public static void log(String message) {
        plugin.getLogger().log(Level.INFO, message);
    }

    public static WildBusterPlugin getPlugin() {
        return plugin;
    }

}
