package com.bgsoftware.wildbuster.nms.v1_20_3;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import org.bukkit.inventory.InventoryHolder;

public class CustomHopperBlockEntity extends HopperBlockEntity {

    private final InventoryHolder holder;

    public CustomHopperBlockEntity(InventoryHolder holder, String title) {
        super(BlockPos.ZERO, Blocks.AIR.defaultBlockState());
        this.holder = holder;
        this.setCustomName(Component.nullToEmpty(title));
    }

    @Override
    public InventoryHolder getOwner() {
        return holder;
    }

}
