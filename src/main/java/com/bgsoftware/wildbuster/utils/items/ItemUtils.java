package com.bgsoftware.wildbuster.utils.items;

import com.bgsoftware.wildbuster.utils.ServerVersion;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class ItemUtils {

    private static final int OFFHAND_SLOT = 40;

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

    public static void addItem(ItemStack itemStack, Inventory inventory, Location location) {
        HashMap<Integer, ItemStack> additionalItems = inventory.addItem(itemStack);
        if (location != null && !additionalItems.isEmpty()) {
            for (ItemStack additional : additionalItems.values())
                location.getWorld().dropItemNaturally(location, additional);
        }
    }

    public static void removeItem(Inventory inventory, ItemStack itemStack) {
        HashMap<Integer, ItemStack> additionalItems = inventory.removeItem(itemStack);
        if (!additionalItems.isEmpty() && ServerVersion.isAtLeast(ServerVersion.v1_9)) {
            ItemStack offHandItem = inventory.getItem(OFFHAND_SLOT);
            if (itemStack.isSimilar(offHandItem)) {
                if(itemStack.getAmount() >= offHandItem.getAmount()) {
                    inventory.setItem(OFFHAND_SLOT, new ItemStack(Material.AIR));
                }
                else {
                    offHandItem.setAmount(offHandItem.getAmount() - itemStack.getAmount());
                }
            }
        }
    }

    public static ItemStack getWool(DyeColor dyeColor) {
        try {
            return new ItemStack(Material.valueOf(dyeColor.name() + "_WOOL"));
        } catch (Exception ex) {
            //noinspection deprecation
            return new ItemStack(Material.WOOL, 1, dyeColor.getWoolData());
        }
    }

}