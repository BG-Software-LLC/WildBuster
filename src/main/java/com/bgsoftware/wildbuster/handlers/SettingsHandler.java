package com.bgsoftware.wildbuster.handlers;

import com.bgsoftware.wildbuster.WildBusterPlugin;
import com.bgsoftware.wildbuster.config.CommentedConfiguration;
import com.bgsoftware.wildbuster.utils.items.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class SettingsHandler {

    public final long bustingInterval, timeBeforeRunning;
    public final int startingLevel, stoppingLevel, bustingLevelsAmount, defaultLimit, minimumCancelLevel;
    public final boolean onlyInsideClaim, skipAirLevels, reverseMode, cancelGUI, confirmPlacement;
    public final List<String> blockedMaterials;

    public SettingsHandler(WildBusterPlugin plugin){
        WildBusterPlugin.log("Loading configuration started...");
        long startTime = System.currentTimeMillis();
        int bustersAmount = 0;
        File file = new File(plugin.getDataFolder(), "config.yml");

        if(!file.exists())
            plugin.saveResource("config.yml", false);

        CommentedConfiguration cfg = CommentedConfiguration.loadConfiguration(file);
        oldDataConvertor(cfg);
        cfg.syncWithConfig(file, plugin.getResource("config.yml"), "chunkbusters");

        bustingInterval = cfg.getLong("busting-interval", 10);
        startingLevel = Math.min(cfg.getInt("starting-level", 255), 255);
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
        confirmPlacement = cfg.getBoolean("confirm-placement", false);

        plugin.getBustersManager().removeChunkBusters();

        for (String name : cfg.getConfigurationSection("chunkbusters").getKeys(false)) {
            int radius = cfg.getInt("chunkbusters." + name + ".radius", 0);

            ItemBuilder itemBuilder = null;

            try{
                Material type = Material.valueOf(cfg.getString("chunkbusters." + name + ".type", ""));
                short data = (short) cfg.getInt("chunkbusters." + name + ".data", 0);

                itemBuilder = new ItemBuilder(type, data);

                if(cfg.contains("chunkbusters." + name + ".name"))
                    itemBuilder.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                            cfg.getString("chunkbusters." + name + ".name")));

                if(cfg.contains("chunkbusters." + name + ".lore")) {
                    List<String> lore = new ArrayList<>();

                    cfg.getStringList("chunkbusters." + name + ".lore")
                            .forEach(line -> lore.add(ChatColor.translateAlternateColorCodes('&', line)));

                    itemBuilder.setLore(lore);
                }

                if(cfg.contains("chunkbusters." + name + ".enchants")) {
                    for(String line : cfg.getStringList("chunkbusters." + name + ".enchants")){
                        Enchantment enchantment = Enchantment.getByName(line.split(":")[0]);
                        int level = Integer.parseInt(line.split(":")[1]);
                        itemBuilder.addEnchant(enchantment, level);
                    }
                }

                if(cfg.getBoolean("chunkbusters." + name + ".glow", false)) {
                    itemBuilder.addEnchant(plugin.getGlowEnchant(), 1);
                }

                if(cfg.contains("chunkbusters." + name + ".skull")) {
                    itemBuilder.setTexture(cfg.getString("chunkbusters." + name + ".skull"));
                }
            } catch(Exception ignored){}

            if (radius <= 0 || itemBuilder == null) {
                WildBusterPlugin.log("Something went wrong while loading chunk-buster '" + name + "'.");
                continue;
            }

            plugin.getBustersManager().createChunkBuster(name, radius, itemBuilder.build());
            bustersAmount++;
        }

        WildBusterPlugin.log(" - Found " + bustersAmount + " chunk-busters in config.yml.");
        WildBusterPlugin.log("Loading configuration done (Took " + (System.currentTimeMillis() - startTime) + "ms)");
    }

    public static void reload(){
        WildBusterPlugin plugin = WildBusterPlugin.getPlugin();
        plugin.setSettings(new SettingsHandler(plugin));
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
