package xyz.wildseries.wildbuster;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.wildseries.wildbuster.api.WildBuster;
import xyz.wildseries.wildbuster.api.WildBusterAPI;
import xyz.wildseries.wildbuster.api.handlers.BustersManager;
import xyz.wildseries.wildbuster.command.CommandsHandler;
import xyz.wildseries.wildbuster.handlers.BustersHandler;
import xyz.wildseries.wildbuster.handlers.DataHandler;
import xyz.wildseries.wildbuster.handlers.SettingsHandler;
import xyz.wildseries.wildbuster.hooks.BlockBreakProvider;
import xyz.wildseries.wildbuster.hooks.BlockBreakProvider_Default;
import xyz.wildseries.wildbuster.hooks.BlockBreakProvider_WorldGuard;
import xyz.wildseries.wildbuster.hooks.CoreProtectHook;
import xyz.wildseries.wildbuster.hooks.CoreProtectHook_CoreProtect;
import xyz.wildseries.wildbuster.hooks.CoreProtectHook_Default;
import xyz.wildseries.wildbuster.hooks.FactionsProvider;
import xyz.wildseries.wildbuster.hooks.FactionsProvider_Default;
import xyz.wildseries.wildbuster.hooks.FactionsProvider_FactionsUUID;
import xyz.wildseries.wildbuster.hooks.FactionsProvider_MassiveCore;
import xyz.wildseries.wildbuster.listeners.BlocksListener;
import xyz.wildseries.wildbuster.listeners.InventorysListener;
import xyz.wildseries.wildbuster.listeners.PlayersListener;
import xyz.wildseries.wildbuster.metrics.Metrics;
import xyz.wildseries.wildbuster.nms.NMSAdapter;

import java.lang.reflect.Field;
import java.util.logging.Level;

public final class WildBusterPlugin extends JavaPlugin implements WildBuster {

    private static WildBusterPlugin plugin;

    private BustersManager bustersManager;
    private SettingsHandler settingsHandler;
    private DataHandler dataHandler;

    private NMSAdapter nmsAdapter;
    private FactionsProvider factionsProvider;
    private BlockBreakProvider blockBreakProvider;
    private CoreProtectHook coreProtectHook;

    @Override
    public void onEnable() {
        plugin = this;
        new Metrics(this);

        log("******** ENABLE START ********");

        getServer().getPluginManager().registerEvents(new BlocksListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayersListener(this), this);
        getServer().getPluginManager().registerEvents(new InventorysListener(this), this);

        CommandsHandler commandsHandler = new CommandsHandler(this);
        getCommand("buster").setExecutor(commandsHandler);
        getCommand("buster").setTabCompleter(commandsHandler);

        loadNMSAdapter();

        bustersManager = new BustersHandler();
        settingsHandler = new SettingsHandler(this);
        dataHandler = new DataHandler(this);

        Locale.reload();
        loadHooks();
        loadAPI();

        if(Updater.isOutdated()) {
            log("");
            log("A new version is available (v" + Updater.getLatestVersion() + ")!");
            log("Version's description: \"" + Updater.getVersionDescription() + "\"");
            log("");
        }

        log("******** ENABLE DONE ********");
    }

    @Override
    public void onDisable() {
        dataHandler.saveBusters();
    }

    private void loadNMSAdapter(){
        String version = getServer().getClass().getPackage().getName().split("\\.")[3];
        try{
            nmsAdapter = (NMSAdapter) Class.forName("xyz.wildseries.wildbuster.nms.NMSAdapter_" + version).newInstance();
        } catch(ClassNotFoundException | InstantiationException | IllegalAccessException ex){
            log("Couldn't load up with an adapter " + version + ". Please contact @Ome_R");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void loadHooks(){
        log("Loading providers started...");
        long startTime = System.currentTimeMillis();
        log(" - Using " + nmsAdapter.getVersion() + " adapter.");

        //Load factions provider
        if(getServer().getPluginManager().isPluginEnabled("Factions")){
            if(getServer().getPluginManager().isPluginEnabled("MassiveCore")){
                factionsProvider = new FactionsProvider_MassiveCore();
                log(" - Using MassiveCore as FactionsProvider.");
            }else{
                factionsProvider = new FactionsProvider_FactionsUUID();
                log(" - Using FactionsUUID as FactionsProvider.");
            }
        }else{
            factionsProvider = new FactionsProvider_Default();
            log(" - Couldn't find any factions providers, using default one.");
        }
        //Load block-break provider
        if(getServer().getPluginManager().isPluginEnabled("WorldGuard")){
            blockBreakProvider = new BlockBreakProvider_WorldGuard();
            log(" - Using WorldGuard as BlockBreakProvider.");
        }else{
            blockBreakProvider = new BlockBreakProvider_Default();
            log(" - Couldn't find any block-break providers, using default one.");
        }

        //Load CoreProtect hook
        if(getServer().getPluginManager().isPluginEnabled("CoreProtect")){
            coreProtectHook = new CoreProtectHook_CoreProtect();
        }else{
            coreProtectHook = new CoreProtectHook_Default();
        }

        log("Loading providers done (Took " + (System.currentTimeMillis() - startTime) + "ms)");
    }

    private void loadAPI(){
        try{
            Field instance = WildBusterAPI.class.getDeclaredField("instance");
            instance.setAccessible(true);
            instance.set(null, this);
        }catch(Exception ex){
            log("Failed to set-up API - disabling plugin...");
            setEnabled(false);
            ex.printStackTrace();
        }
    }

    @Override
    public BustersManager getBustersManager() {
        return bustersManager;
    }

    public SettingsHandler getSettings(){
        return settingsHandler;
    }

    public DataHandler getDataHandler(){
        return dataHandler;
    }

    public NMSAdapter getNMSAdapter(){
        return nmsAdapter;
    }

    public FactionsProvider getFactionsProvider(){
        return factionsProvider;
    }

    public BlockBreakProvider getBlockBreakProvider(){
        return blockBreakProvider;
    }

    public CoreProtectHook getCoreProtectHook() {
        return coreProtectHook;
    }

    public static void log(String message){
        plugin.getLogger().log(Level.INFO, message);
    }

    public static WildBusterPlugin getPlugin(){
        return plugin;
    }

}
