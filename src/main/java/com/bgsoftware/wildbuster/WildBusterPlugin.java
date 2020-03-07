package com.bgsoftware.wildbuster;

import com.bgsoftware.wildbuster.api.WildBuster;
import com.bgsoftware.wildbuster.api.WildBusterAPI;
import com.bgsoftware.wildbuster.api.handlers.BustersManager;
import com.bgsoftware.wildbuster.command.CommandsHandler;
import com.bgsoftware.wildbuster.handlers.BustersHandler;
import com.bgsoftware.wildbuster.handlers.DataHandler;
import com.bgsoftware.wildbuster.handlers.SettingsHandler;
import com.bgsoftware.wildbuster.hooks.BlockBreakProvider;
import com.bgsoftware.wildbuster.hooks.BlockBreakProvider_Default;
import com.bgsoftware.wildbuster.hooks.BlockBreakProvider_WorldGuard;
import com.bgsoftware.wildbuster.hooks.CoreProtectHook;
import com.bgsoftware.wildbuster.hooks.CoreProtectHook_CoreProtect;
import com.bgsoftware.wildbuster.hooks.CoreProtectHook_Default;
import com.bgsoftware.wildbuster.hooks.FactionsProvider;
import com.bgsoftware.wildbuster.hooks.FactionsProvider_Default;
import com.bgsoftware.wildbuster.hooks.FactionsProvider_FactionsUUID;
import com.bgsoftware.wildbuster.hooks.FactionsProvider_MassiveCore;
import com.bgsoftware.wildbuster.listeners.BlocksListener;
import com.bgsoftware.wildbuster.listeners.MenusListener;
import com.bgsoftware.wildbuster.listeners.PlayersListener;
import com.bgsoftware.wildbuster.metrics.Metrics;
import com.bgsoftware.wildbuster.nms.NMSAdapter;
import com.bgsoftware.wildbuster.utils.threads.Executor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

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

    private Enchantment glowEnchant;

    @Override
    public void onEnable() {
        plugin = this;
        new Metrics(this);

        log("******** ENABLE START ********");

        getServer().getPluginManager().registerEvents(new BlocksListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayersListener(this), this);
        getServer().getPluginManager().registerEvents(new MenusListener(), this);

        CommandsHandler commandsHandler = new CommandsHandler(this);
        getCommand("buster").setExecutor(commandsHandler);
        getCommand("buster").setTabCompleter(commandsHandler);

        loadNMSAdapter();
        registerGlowEnchantment();

        bustersManager = new BustersHandler(this);
        settingsHandler = new SettingsHandler(this);
        dataHandler = new DataHandler(this);

        Locale.reload();
        loadAPI();

        if(Updater.isOutdated()) {
            log("");
            log("A new version is available (v" + Updater.getLatestVersion() + ")!");
            log("Version's description: \"" + Updater.getVersionDescription() + "\"");
            log("");
        }

        log("******** ENABLE DONE ********");

        //Load hooks on first tick
        Executor.sync(this::loadHooks);
    }

    @Override
    public void onDisable() {
        dataHandler.saveBusters();
    }

    private void loadNMSAdapter(){
        String version = getServer().getClass().getPackage().getName().split("\\.")[3];
        try{
            nmsAdapter = (NMSAdapter) Class.forName("com.bgsoftware.wildbuster.nms.NMSAdapter_" + version).newInstance();
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
            if(!getServer().getPluginManager().getPlugin("Factions").getDescription().getAuthors().contains("drtshock")){
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

    private void registerGlowEnchantment(){
        glowEnchant = nmsAdapter.getGlowEnchant();

        try{
            Field field = Enchantment.class.getDeclaredField("acceptingNew");
            field.setAccessible(true);
            field.set(null, true);
            field.setAccessible(false);
        }catch(Exception ignored){}

        try{
            Enchantment.registerEnchantment(glowEnchant);
        }catch(Exception ignored){}
    }

    @Override
    public BustersManager getBustersManager() {
        return bustersManager;
    }

    public SettingsHandler getSettings(){
        return settingsHandler;
    }

    public void setSettings(SettingsHandler settingsHandler){
        this.settingsHandler = settingsHandler;
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

    public Enchantment getGlowEnchant() {
        return glowEnchant;
    }

    public static void log(String message){
        plugin.getLogger().log(Level.INFO, message);
    }

    public static WildBusterPlugin getPlugin(){
        return plugin;
    }

}
