package com.bgsoftware.wildbuster.handlers;

import com.bgsoftware.wildbuster.WildBusterPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public final class SettingsHandler {

    public final long bustingInterval, timeBeforeRunning;
    public final int startingLevel, stoppingLevel, bustingLevelsAmount, defaultLimit, minimumCancelLevel;
    public final boolean onlyInsideClaim, skipAirLevels, reverseMode, cancelGUI;
    public final List<String> blockedMaterials;

    public SettingsHandler(WildBusterPlugin plugin){
        WildBusterPlugin.log("Loading configuration started...");
        long startTime = System.currentTimeMillis();
        int bustersAmount = 0;
        File file = new File(plugin.getDataFolder(), "config.yml");

        if(!file.exists())
            plugin.saveResource("config.yml", false);

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        oldDataConvertor(cfg);

        bustingInterval = cfg.getLong("busting-interval", 10);
        int startingLevel = cfg.getInt("starting-level", 255);
        this.startingLevel = startingLevel > 255 ? 255 : startingLevel;
        stoppingLevel = cfg.getInt("stopping-level", 1);
        bustingLevelsAmount = cfg.getInt("busting-levels-amount", 1);
        defaultLimit = cfg.getInt("default-limit", 2);
        onlyInsideClaim = cfg.getBoolean("only-inside-claim", false);
        skipAirLevels = cfg.getBoolean("skip-air-levels", false);
        cancelGUI = cfg.getBoolean("cancel-gui", true);
        reverseMode = cfg.getBoolean("reverse-mode", true);
        minimumCancelLevel = cfg.getInt("minimum-cancel-level", 0);
        timeBeforeRunning = cfg.getLong("time-before-running", 0);
        blockedMaterials = cfg.getStringList("blocked-materials");

        for (String name : cfg.getConfigurationSection("chunkbusters").getKeys(false)) {
            int radius = cfg.getInt("chunkbusters." + name + ".radius", 0);

            ItemStack item;

            try{
                Material type = Material.valueOf(cfg.getString("chunkbusters." + name + ".type", ""));
                short data = (short) cfg.getInt("chunkbusters." + name + ".data", 0);
                item = new ItemStack(type, 1, data);

                ItemMeta meta = item.getItemMeta();

                if(cfg.contains("chunkbusters." + name + ".name"))
                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                            cfg.getString("chunkbusters." + name + ".name")));

                if(cfg.contains("chunkbusters." + name + ".lore")) {
                    List<String> lore = new ArrayList<>();

                    cfg.getStringList("chunkbusters." + name + ".lore")
                            .forEach(line -> lore.add(ChatColor.translateAlternateColorCodes('&', line)));

                    meta.setLore(lore);
                }

                if(cfg.contains("chunkbusters." + name + ".enchants")) {
                    cfg.getStringList("chunkbusters." + name + ".enchants").forEach(line -> {
                        Enchantment enchantment = Enchantment.getByName(line.split(":")[0]);
                        int level = Integer.valueOf(line.split(":")[1]);
                        meta.addEnchant(enchantment, level, true);
                    });
                }

                item.setItemMeta(meta);
            } catch(Exception ex){
                item = null;
            }

            if (radius <= 0 || item == null) {
                WildBusterPlugin.log("Something went wrong while loading chunk-buster '" + name + "'.");
                continue;
            }

            plugin.getBustersManager().createChunkBuster(name, radius, item);
            bustersAmount++;
        }

        WildBusterPlugin.log(" - Found " + bustersAmount + " chunk-busters in config.yml.");
        WildBusterPlugin.log("Loading configuration done (Took " + (System.currentTimeMillis() - startTime) + "ms)");
    }

    public static void reload(){
        try{
            WildBusterPlugin plugin = WildBusterPlugin.getPlugin();
            Field settings = WildBusterPlugin.class.getDeclaredField("settingsHandler");
            settings.setAccessible(true);
            settings.set(plugin, new SettingsHandler(plugin));
        } catch(NoSuchFieldException | IllegalAccessException ex){
            ex.printStackTrace();
        }
    }

    private void oldDataConvertor(YamlConfiguration cfg){
        if(cfg.contains("busted_ylevel_interval"))
            cfg.set("busting-interval", cfg.getLong("busted_ylevel_interval"));
        if(cfg.contains("start_busted_ylevel"))
            cfg.set("starting-level", cfg.getInt("start_busted_ylevel"));
        if(cfg.contains("stop_busted_ylevel"))
            cfg.set("stopping-level", cfg.getInt("stop_busted_ylevel"));
        if(cfg.contains("busted_ylevels_amount"))
            cfg.set("busting-levels-amount", cfg.getInt("busted_ylevels_amount"));
        if(cfg.contains("running_busters_amount"))
            cfg.set("default-limit", cfg.getInt("running_busters_amount"));
        if(cfg.contains("only_inside_claim"))
            cfg.set("only-inside-claim", cfg.getBoolean("only_inside_claim"));
        if(cfg.contains("skip_air_level"))
            cfg.set("skip-air-levels", cfg.getBoolean("skip_air_level"));
        if(cfg.contains("reverse_on_cancel"))
            cfg.set("reverse-mode", cfg.getBoolean("reverse_on_cancel"));
        if(cfg.contains("minimum_level_cancel"))
            cfg.set("minimum-cancel-level", cfg.getInt("minimum_level_cancel"));
        if(cfg.contains("time_before_running"))
            cfg.set("time-before-running", cfg.getLong("time_before_running"));
        if(cfg.contains("blocked_materials"))
            cfg.set("blocked-materials", cfg.getStringList("blocked_materials"));
    }

}
