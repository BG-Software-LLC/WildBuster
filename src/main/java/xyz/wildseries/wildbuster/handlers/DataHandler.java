package xyz.wildseries.wildbuster.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.wildseries.wildbuster.WildBusterPlugin;
import xyz.wildseries.wildbuster.api.objects.BlockData;
import xyz.wildseries.wildbuster.api.objects.PlayerBuster;
import xyz.wildseries.wildbuster.objects.WBlockData;
import xyz.wildseries.wildbuster.utils.ItemUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("all")
public final class DataHandler {

    private WildBusterPlugin plugin;

    public DataHandler(WildBusterPlugin plugin){
        this.plugin = plugin;
        loadBusters();
    }

    public void saveBusters(){
        WildBusterPlugin.log("Saving database started...");
        long startTime = System.currentTimeMillis();
        int bustersAmount = 0;
        File dir = new File(plugin.getDataFolder(), "data");

        if(dir.exists()){
            for(File file : dir.listFiles())
                file.delete();
        }

        for(PlayerBuster playerBuster : plugin.getBustersManager().getPlayerBusters()){
            int fileIndex = 0;
            File file;

            do{
                fileIndex++;
                file = new File(plugin.getDataFolder(), "data/" + fileIndex + ".yml");
            }while(file.exists());

            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

            cfg.set("buster-name", playerBuster.getBusterName());
            cfg.set("uuid", playerBuster.getUniqueID().toString());
            cfg.set("cancel-status", playerBuster.isCancelled());
            cfg.set("notify-status", playerBuster.isNotify());
            cfg.set("current-level", playerBuster.getCurrentLevel());
            cfg.set("world", playerBuster.getWorld().getName());

            for(int index = 0; index < playerBuster.getChunks().size(); index++) {
                cfg.set("chunks." + index + ".x", playerBuster.getChunks().get(index).getX());
                cfg.set("chunks." + index + ".z", playerBuster.getChunks().get(index).getZ());
            }

            for(BlockData blockData : playerBuster.getRemovedBlocks()){
                String path = "blocks." + blockData.getX() + "," + blockData.getY() + "," + blockData.getZ();
                cfg.set(path + ".type", blockData.getType().name());
                cfg.set(path + ".data", blockData.getData());
                cfg.set(path + ".combined", blockData.getCombinedId());

                if(blockData.hasContents())
                    ItemUtil.saveContents(blockData.getContents(), cfg.createSection(path + ".inventory"));
            }

            try{
                cfg.save(file);
                bustersAmount++;
            }catch(IOException ex){
                ex.printStackTrace();
            }

        }

        WildBusterPlugin.log(" - Saved " + bustersAmount + " player-busters to files.");
        WildBusterPlugin.log("Saving database done (Took " + (System.currentTimeMillis() - startTime) + "ms)");
    }

    public void loadBusters(){
        WildBusterPlugin.log("Loading database started...");
        long startTime = System.currentTimeMillis();
        int bustersAmount = 0;

        File dir = new File(plugin.getDataFolder(), "data");
        if(!dir.exists())
            dir.mkdirs();

        for(File file : dir.listFiles()){
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

            //OLD DATA IS NOT SUPPORTED ANYMORE!
            if(cfg.contains("chunk-buster"))
                continue;

            String busterName = cfg.getString("buster-name");
            UUID uuid = UUID.fromString(cfg.getString("uuid"));
            boolean cancelStatus = cfg.getBoolean("cancel-status");
            boolean notifyStatus = cfg.getBoolean("notify-status");
            int currentLevel = cfg.getInt("current-level");
            World world = Bukkit.getWorld(cfg.getString("world"));

            List<Chunk> chunksList = new ArrayList<>();

            for(String index : cfg.getConfigurationSection("chunks").getKeys(false)){
                int chunkX = cfg.getInt("chunks." + index + ".x");
                int chunkZ = cfg.getInt("chunks." + index + ".z");
                chunksList.add(world.getChunkAt(chunkX, chunkZ));
            }

            List<BlockData> removedBlocks = new ArrayList<>();

            if(cfg.contains("blocks")){
                for(String block : cfg.getConfigurationSection("blocks").getKeys(false)){
                    String[] blockSections = block.split(",");
                    Material type = Material.valueOf(cfg.getString("blocks." + block + ".type"));
                    byte data = (byte) cfg.getInt("blocks." + block + ".data");

                    if(!cfg.contains("blocks." + block + ".combined") && Bukkit.getBukkitVersion().contains("1.13")){
                        WildBusterPlugin.log("Couldn't find a combined value for " + block + " on a 1.13 server.");
                        WildBusterPlugin.log("If you want to migrate into 1.13, please run /wbmigrate.");
                        WildBusterPlugin.log("Backing up your busters data files.");
                        backupFolder("plugins/WildBuster/data", "plugins/WildBuster/data-backup");
                        return;
                    }

                    int combinedId = cfg.getInt("blocks." + block + ".combined");
                    BlockData blockData = new WBlockData(type, data, combinedId, world, Integer.valueOf(blockSections[0]),
                            Integer.valueOf(blockSections[1]), Integer.valueOf(blockSections[2]));

                    if(cfg.contains("blocks." + block + ".inventory")) {
                        blockData.setContents(ItemUtil.loadContents(cfg.getConfigurationSection("blocks." + block + ".inventory")));
                    }

                    removedBlocks.add(blockData);
                }
            }

            //Load player-buster to database

            plugin.getBustersManager().loadPlayerBuster(busterName, uuid, world, cancelStatus, notifyStatus, currentLevel, chunksList, removedBlocks);
            bustersAmount++;
        }

        WildBusterPlugin.log(" - Found " + bustersAmount + " player-busters in files.");
        WildBusterPlugin.log("Loading database done (Took " + (System.currentTimeMillis() - startTime) + "ms)");
    }

    private void backupFolder(String oldFolderPath, String newFolderPath){
        File oldFolder = new File(oldFolderPath), newFolder = new File(newFolderPath);

        for(File file : oldFolder.listFiles()){
            File newFile = new File(newFolderPath, file.getName());

            try{
                YamlConfiguration.loadConfiguration(file).save(newFile);
            }catch(IOException ex){
                ex.printStackTrace();
            }
        }
    }

}
