package com.bgsoftware.wildbuster.menu;

import com.bgsoftware.wildbuster.WildBusterPlugin;
import com.bgsoftware.wildbuster.utils.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.lang.reflect.Field;

public abstract class WildMenu implements InventoryHolder {

    private static Field inventoryField;

    static {
        try{
            Class<?> craftInventoryClass = ReflectionUtils.getClass("org.bukkit.craftbukkit.VERSION.inventory.CraftInventory");
            inventoryField = craftInventoryClass.getDeclaredField("inventory");
            inventoryField.setAccessible(true);
        }catch(Exception ignored){}
    }

    protected static final WildBusterPlugin plugin = WildBusterPlugin.getPlugin();

    public abstract void onButtonClick(InventoryClickEvent e);

    public void onMenuClose(InventoryCloseEvent e){

    }

    protected Inventory createInventory(InventoryType inventoryType, int size, String title){
        Inventory inventory;

        if(inventoryType != InventoryType.CHEST){
            inventory = Bukkit.createInventory(this, inventoryType, title);
        }
        else{
            inventory = Bukkit.createInventory(this, size, title);
        }

        if(inventory.getHolder() == null) {
            try {
                inventoryField.set(inventory, plugin.getNMSAdapter().getCustomHolder(inventoryType, this, title));
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }

        return inventory;
    }

}
