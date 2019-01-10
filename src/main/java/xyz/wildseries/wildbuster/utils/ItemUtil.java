package xyz.wildseries.wildbuster.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.wildseries.wildbuster.WildBusterPlugin;
import xyz.wildseries.wildbuster.api.objects.PlayerBuster;
import xyz.wildseries.wildbuster.objects.WMaterial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class ItemUtil {

    private static WildBusterPlugin plugin = WildBusterPlugin.getPlugin();

    public static void saveContents(ItemStack[] contents, ConfigurationSection destination) {
        // Save every element in the list
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];

            // Don't store NULL entries
            if (item != null && item.getType() != Material.AIR) {
                destination.set(Integer.toString(i), item);
            }
        }
    }

    public static ItemStack[] loadContents(ConfigurationSection source) {
        List<ItemStack> stacks = new ArrayList<>();

        // Try to parse this inventory
        for (String key : source.getKeys(false)) {
            int number = Integer.parseInt(key);

            // Size should always be bigger
            while (stacks.size() <= number) {
                stacks.add(null);
            }

            stacks.set(number, (ItemStack) source.get(key));
        }

        // Return result
        return stacks.toArray(new ItemStack[0]);
    }

    public static void addItem(ItemStack itemStack, Inventory inventory, Location location){
        HashMap<Integer, ItemStack> additionalItems = inventory.addItem(itemStack);
        if(location != null && !additionalItems.isEmpty()){
            for(ItemStack additional : additionalItems.values())
                location.getWorld().dropItemNaturally(location, additional);
        }
    }

    public static Inventory getCancelMenu(int page){
        Inventory inventory = Bukkit.createInventory(null, 9 * 5, ChatColor.BOLD + "Cancelling Menu");

        List<OfflinePlayer> players = new ArrayList<>();
        plugin.getBustersManager().getPlayerBusters().forEach(playerBuster -> {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerBuster.getUniqueID());
            if(!players.contains(offlinePlayer))
                players.add(offlinePlayer);
        });

        //Set player heads
        for(int slot = 0; slot < 36 && (page * 36 + slot) < players.size(); slot++){
            OfflinePlayer player = players.get(page * 36 + slot);
            int bustersAmount = plugin.getBustersManager().getPlayerBusters(player).size();
            inventory.setItem(slot, new ItemBuilder(WMaterial.PLAYER_HEAD.parseItem())
                    .setDisplayName(ChatColor.GREEN + player.getName())
                    .setLore(ChatColor.GRAY + player.getName() + " has " + bustersAmount + " running busters.", ChatColor.GRAY + player.getUniqueId().toString())
                    .setOwner(player.getName())
                    .build());
        }

        //Set glass panes
        for(int slot = 36; slot < 45; slot++){
            //inventory.setItem(slot, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 15).setDisplayName(ChatColor.WHITE + "").build());
            inventory.setItem(slot, new ItemBuilder(WMaterial.BLACK_STAINED_GLASS_PANE.parseItem()).setDisplayName(ChatColor.WHITE + "").build());
        }

        //Set next page
        int maxPages = players.size() % 36 == 0 ? players.size() / 36 : (players.size() / 36) + 1;
        String nextLore = page + 2 <= maxPages ? (ChatColor.GRAY + "Page " + (page + 2)) : (ChatColor.GRAY + "Current page");
        inventory.setItem(42, new ItemBuilder(Material.PAPER).setDisplayName(ChatColor.GREEN + "Next Page").setLore(nextLore).build());

        //Set previous page
        String previousLore = page > 0 ? (ChatColor.GRAY + "Page " + page) : (ChatColor.GRAY + "Current page");
        inventory.setItem(38, new ItemBuilder(Material.PAPER).setDisplayName(ChatColor.GREEN + "Previous Page").setLore(previousLore).build());

        return inventory;
    }

    public static Inventory getPlayerMenu(OfflinePlayer player, int page){
        List<PlayerBuster> playerBusters = plugin.getBustersManager().getPlayerBusters(player);

        Inventory inventory = Bukkit.createInventory(null, 9 * 5, ChatColor.BOLD + "Player's Active Busters");

        //Set busters
        for(int slot = 0; slot < 36 && (page * 36 + slot) < playerBusters.size(); slot++){
            PlayerBuster playerBuster = playerBusters.get(page * 36 + slot);
            Chunk chunk = playerBuster.getChunks().get(0);
            ItemStack busterItem = playerBuster.getChunkBuster().getBusterItem();
            inventory.setItem(slot, new ItemBuilder(busterItem.getType(), busterItem.getDurability())
                    .setDisplayName(ChatColor.GREEN + "Active Buster #" + (page * 36 + slot + 1))
                    .setLore(ChatColor.GRAY + "Running at " + chunk.getWorld().getName() + "," + chunk.getX() + "," + chunk.getZ())
                    .build());
        }

        //Set glass panes
        for(int slot = 36; slot < 45; slot++){
            //inventory.setItem(slot, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 15).setDisplayName(ChatColor.WHITE + "").build());
            inventory.setItem(slot, new ItemBuilder(WMaterial.BLACK_STAINED_GLASS_PANE.parseItem()).setDisplayName(ChatColor.WHITE + "").build());
        }

        //Set next page
        int maxPages = playerBusters.size() % 36 == 0 ? playerBusters.size() / 36 : (playerBusters.size() / 36) + 1;
        String nextLore = page + 2 <= maxPages ? (ChatColor.GRAY + "Page " + (page + 2)) : (ChatColor.GRAY + "Current page");
        inventory.setItem(42, new ItemBuilder(Material.PAPER).setDisplayName(ChatColor.GREEN + "Next Page").setLore(nextLore).build());

        //Set previous page
        String previousLore = page > 0 ? (ChatColor.GRAY + "Page " + page) : (ChatColor.GRAY + "Current page");
        inventory.setItem(38, new ItemBuilder(Material.PAPER).setDisplayName(ChatColor.GREEN + "Previous Page").setLore(previousLore).build());

        //Set player info
        inventory.setItem(40, new ItemBuilder(Material.PAPER).setDisplayName(ChatColor.GREEN + "Player Info")
                .setLore(ChatColor.GRAY + "Name: " + player.getName(), ChatColor.GRAY + "UUID: " + player.getUniqueId()).build());

        return inventory;
    }

}