package com.bgsoftware.wildbuster.menu;

import com.bgsoftware.wildbuster.Locale;
import com.bgsoftware.wildbuster.api.objects.PlayerBuster;
import com.bgsoftware.wildbuster.utils.items.ItemBuilder;
import com.bgsoftware.wildbuster.utils.legacy.Materials;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PlayersBustersMenu extends WildMenu {

    private final OfflinePlayer player;
    private Inventory inventory;

    private PlayersBustersMenu(OfflinePlayer player){
        this.player = player;
    }

    @Override
    public void onButtonClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)
            return;

        //Buster item
        if(e.getRawSlot() < 36){
            //"Running at " chunk.getWorld().getName() + "," + chunk.getX() + "," + chunk.getZ()
            String chunkString = e.getCurrentItem().getItemMeta().getLore().get(0).split(" ")[2];
            String[] chunkSections = chunkString.split(",");
            Chunk chunk = Bukkit.getWorld(chunkSections[0]).getChunkAt(Integer.parseInt(chunkSections[1]), Integer.parseInt(chunkSections[2]));
            PlayerBuster playerBuster = plugin.getBustersManager().getPlayerBuster(chunk);

            if(playerBuster != null) {
                playerBuster.performCancel(player);
            }
            else{
                Locale.INVALID_BUSTER_LOCATION.send(player, chunkString);
            }
        }
        //Previous or Next page
        else if(e.getRawSlot() == 38 || e.getRawSlot() == 42){
            String firstLoreLine = ChatColor.stripColor(e.getInventory().getItem(e.getRawSlot()).getItemMeta().getLore().get(0));
            Matcher matcher;
            if((matcher = Pattern.compile("Page (\\d)").matcher(firstLoreLine)).matches()){
                int newPage = Integer.parseInt(matcher.group(1));
                PlayersBustersMenu.open((Player) e.getWhoClicked(), this.player, newPage - 1);
            }
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private Inventory buildInventory(int page){
        List<PlayerBuster> playerBusters = plugin.getBustersManager().getPlayerBusters(player);

        inventory = Bukkit.createInventory(this, 9 * 5, ChatColor.BOLD + "Player's Active Busters");

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
            inventory.setItem(slot, new ItemBuilder(Materials.BLACK_STAINED_GLASS_PANE.parseItem()).setDisplayName(ChatColor.WHITE + "").build());
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

    public static void open(Player player, OfflinePlayer targetPlayer){
        open(player, targetPlayer, 0);
    }

    public static void open(Player player, OfflinePlayer targetPlayer, int page){
        if(Bukkit.isPrimaryThread()){
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> open(player, targetPlayer, page));
            return;
        }

        PlayersBustersMenu bustersCancelMenu = new PlayersBustersMenu(targetPlayer);
        Inventory inventory = bustersCancelMenu.buildInventory(page);

        Bukkit.getScheduler().runTask(plugin, () -> player.openInventory(inventory));
    }

}
