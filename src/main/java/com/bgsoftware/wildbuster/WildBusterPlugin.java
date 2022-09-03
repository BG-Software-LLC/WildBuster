package com.bgsoftware.wildbuster;

import com.bgsoftware.common.mappings.MappingsChecker;
import com.bgsoftware.common.remaps.TestRemaps;
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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
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
        String version = getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            nmsAdapter = (NMSAdapter) Class.forName(String.format("com.bgsoftware.wildbuster.nms.%s.NMSAdapter", version)).newInstance();

            String mappingVersionHash = nmsAdapter.getMappingsHash();

            if (mappingVersionHash != null && !MappingsChecker.checkMappings(mappingVersionHash, version, error -> {
                log("&cFailed to retrieve allowed mappings for your server, skipping...");
                return true;
            })) {
                log("Error while loading adapter - your version mappings are not supported. Please contact @Ome_R");
                log("Your current mappings version: " + mappingVersionHash);
                return false;
            }

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            log("Couldn't load up with an adapter " + version + ". Please contact @Ome_R");
            return false;
        }

        File mappingsFile = new File("mappings");
        if (mappingsFile.exists()) {
            try {
                TestRemaps.testRemapsForClassesInPackage(mappingsFile,
                        plugin.getClassLoader(), "com.bgsoftware.wildbuster.nms." + version);
            } catch (Exception error) {
                error.printStackTrace();
            }
        }

        return true;
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
