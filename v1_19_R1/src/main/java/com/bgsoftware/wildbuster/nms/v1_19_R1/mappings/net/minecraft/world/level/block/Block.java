package com.bgsoftware.wildbuster.nms.v1_19_R1.mappings.net.minecraft.world.level.block;

import com.bgsoftware.wildbuster.nms.v1_19_R1.mappings.MappedObject;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class Block extends MappedObject<net.minecraft.world.level.block.Block> {

    public static Block AIR = new Block(Blocks.a);

    public Block(net.minecraft.world.level.block.Block handle) {
        super(handle);
    }

    public IBlockData getBlockData() {
        return handle.m();
    }

    public static IBlockData getByCombinedId(int combinedId) {
        return net.minecraft.world.level.block.Block.a(combinedId);
    }

    public static int getId(IBlockData blockData) {
        return net.minecraft.world.level.block.Block.i(blockData);
    }

}
