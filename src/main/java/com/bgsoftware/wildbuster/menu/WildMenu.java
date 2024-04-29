package com.bgsoftware.wildbuster.menu;

import com.bgsoftware.common.reflection.ClassInfo;
import com.bgsoftware.common.reflection.ReflectField;
import com.bgsoftware.wildbuster.WildBusterPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class WildMenu implements InventoryHolder {

    private static final ReflectField<Object> CRAFT_INVENTORY_HANDLE = new ReflectField<>(
            new ClassInfo("inventory.CraftInventory", ClassInfo.PackageType.CRAFTBUKKIT),
            Object.class, "inventory").removeFinal();

    protected static final WildBusterPlugin plugin = WildBusterPlugin.getPlugin();

    public abstract void onButtonClick(InventoryClickEvent e);

    public void onMenuClose(InventoryCloseEvent e) {

    }

    protected Inventory createInventory(InventoryType inventoryType, int size, String title) {
        Inventory inventory;

        if (inventoryType != InventoryType.CHEST) {
            inventory = Bukkit.createInventory(this, inventoryType, title);
        } else {
            inventory = Bukkit.createInventory(this, size, title);
        }

        if (inventory.getHolder() == null) {
            CRAFT_INVENTORY_HANDLE.set(inventory, plugin.getNMSAdapter().getCustomHolder(inventoryType, this, title));
        }

        return inventory;
    }

}
