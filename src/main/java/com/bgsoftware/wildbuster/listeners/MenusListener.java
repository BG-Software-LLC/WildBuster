package com.bgsoftware.wildbuster.listeners;

import com.bgsoftware.wildbuster.menu.WildMenu;
import com.bgsoftware.wildbuster.scheduler.Scheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
public final class MenusListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMenuClick(InventoryClickEvent e){
        Inventory topInventory = e.getView().getTopInventory();
        if(topInventory != null && topInventory.getHolder() instanceof WildMenu){
            WildMenu wildMenu = (WildMenu) topInventory.getHolder();
            e.setCancelled(true);
            wildMenu.onButtonClick(e);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMenuClose(InventoryCloseEvent e){
        Inventory topInventory = e.getView().getTopInventory();
        if(topInventory != null && topInventory.getHolder() instanceof WildMenu){
            WildMenu wildMenu = (WildMenu) topInventory.getHolder();
            wildMenu.onMenuClose(e);
        }
    }

    /**
     * The following two events are here for patching a dupe glitch caused
     * by shift clicking and closing the inventory in the same time.
     */

    private Map<UUID, ItemStack> latestClickedItem = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMenuClickMonitor(InventoryClickEvent e){
        Inventory topInventory = e.getView().getTopInventory();
        if(e.getCurrentItem() != null && e.isCancelled() && topInventory != null && topInventory.getHolder() instanceof WildMenu){
            latestClickedItem.put(e.getWhoClicked().getUniqueId(), e.getCurrentItem());
            Scheduler.runTask(e.getWhoClicked(), () -> latestClickedItem.remove(e.getWhoClicked().getUniqueId()), 20L);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMenuCloseMonitor(InventoryCloseEvent e){
        if(latestClickedItem.containsKey(e.getPlayer().getUniqueId())){
            ItemStack clickedItem = latestClickedItem.get(e.getPlayer().getUniqueId());
            Scheduler.runTask(e.getPlayer(), () -> {
                e.getPlayer().getInventory().removeItem(clickedItem);
                //noinspection deprecation
                ((Player) e.getPlayer()).updateInventory();
            }, 1L);
        }
    }

}
