package com.bgsoftware.wildbuster.menu;

import com.bgsoftware.wildbuster.utils.items.ItemBuilder;
import com.bgsoftware.wildbuster.utils.legacy.Materials;
import com.bgsoftware.wildbuster.utils.threads.Executor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BustersCancelMenu extends WildMenu {

    private Inventory inventory;

    private BustersCancelMenu(){
    }

    @Override
    public void onButtonClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)
            return;

        //Player head
        if(e.getRawSlot() < 36){
            UUID playerUUID = UUID.fromString(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getLore().get(1)));
            PlayersBustersMenu.open(player, Bukkit.getOfflinePlayer(playerUUID));
        }

        //Previous or Next page
        else if(e.getRawSlot() == 38 || e.getRawSlot() == 42){
            String firstLoreLine = ChatColor.stripColor(e.getInventory().getItem(e.getRawSlot()).getItemMeta().getLore().get(0));
            Matcher matcher;
            if((matcher = Pattern.compile("Page (\\d)").matcher(firstLoreLine)).matches()){
                int newPage = Integer.parseInt(matcher.group(1));
                BustersCancelMenu.open(player, newPage - 1);
            }
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private Inventory buildInventory(int page){
        inventory = createInventory(InventoryType.CHEST, 9 * 5, ChatColor.BOLD + "Cancelling Menu");

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
            inventory.setItem(slot, new ItemBuilder(Materials.PLAYER_HEAD.parseItem())
                    .setDisplayName(ChatColor.GREEN + player.getName())
                    .setLore(ChatColor.GRAY + player.getName() + " has " + bustersAmount + " running busters.", ChatColor.GRAY + player.getUniqueId().toString())
                    .setOwner(player.getName())
                    .build());
        }

        //Set glass panes
        for(int slot = 36; slot < 45; slot++){
            //inventory.setItem(slot, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 15).setDisplayName(ChatColor.WHITE + "").build());
            inventory.setItem(slot, new ItemBuilder(Materials.BLACK_STAINED_GLASS_PANE.parseItem()).setDisplayName(ChatColor.WHITE + "").build());
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

    public static void open(Player player){
        open(player, 0);
    }

    public static void open(Player player, int page){
        if(Bukkit.isPrimaryThread()){
            Executor.async(() -> open(player, page));
            return;
        }

        BustersCancelMenu bustersCancelMenu = new BustersCancelMenu();
        Inventory inventory = bustersCancelMenu.buildInventory(page);

        Executor.sync(() -> player.openInventory(inventory));
    }

}
