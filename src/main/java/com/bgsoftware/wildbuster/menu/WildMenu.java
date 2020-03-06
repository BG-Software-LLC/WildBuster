package com.bgsoftware.wildbuster.menu;

import com.bgsoftware.wildbuster.WildBusterPlugin;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

public abstract class WildMenu implements InventoryHolder {

    protected static final WildBusterPlugin plugin = WildBusterPlugin.getPlugin();

    public abstract void onButtonClick(InventoryClickEvent e);

    public void onMenuClose(InventoryCloseEvent e){

    }

}
