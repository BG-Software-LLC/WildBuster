package com.bgsoftware.wildbuster.menu;

import com.bgsoftware.wildbuster.api.objects.ChunkBuster;
import com.bgsoftware.wildbuster.scheduler.Scheduler;
import com.bgsoftware.wildbuster.utils.items.ItemBuilder;
import com.bgsoftware.wildbuster.utils.items.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public final class BustersConfirmMenu extends WildMenu {

    private final ChunkBuster chunkBuster;
    private final Location location;
    private Inventory inventory;

    private BustersConfirmMenu(ChunkBuster chunkBuster, Location location){
        this.chunkBuster = chunkBuster;
        this.location = location;
    }

    @Override
    public void onButtonClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        if(e.getRawSlot() == 1){
            player.closeInventory();
            plugin.getBustersManager().handleBusterPlacement(player, location, chunkBuster);
        }

        else if(e.getRawSlot() == 3){
            player.closeInventory();
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private Inventory buildInventory(){
        inventory = createInventory(InventoryType.HOPPER, 0, ChatColor.BOLD + "Confirm Menu");

        //Confirm Button
        inventory.setItem(1, new ItemBuilder(ItemUtils.getWool(DyeColor.LIME))
                .setDisplayName(ChatColor.GREEN + "Confirm").setLore(ChatColor.GRAY + "Click to confirm busting the chunk.").build());

        //Cancel Button
        inventory.setItem(3, new ItemBuilder(ItemUtils.getWool(DyeColor.RED))
                .setDisplayName(ChatColor.RED + "Cancel").setLore(ChatColor.GRAY + "Click to cancel placement of buster.").build());

        return inventory;
    }

    public static void open(Player player, Location location, ChunkBuster chunkBuster){
        if(Bukkit.isPrimaryThread()){
            Scheduler.runTaskAsync(() -> open(player, location, chunkBuster));
            return;
        }

        BustersConfirmMenu bustersCancelMenu = new BustersConfirmMenu(chunkBuster, location);
        Inventory inventory = bustersCancelMenu.buildInventory();

        Scheduler.runTask(player, () -> player.openInventory(inventory));
    }

}
