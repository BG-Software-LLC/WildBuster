package com.bgsoftware.wildbuster.nms.v1_19_R1.mappings.net.minecraft.world.level.chunk;

import com.bgsoftware.wildbuster.nms.v1_19_R1.mappings.MappedObject;
import com.bgsoftware.wildbuster.nms.v1_19_R1.mappings.net.minecraft.world.level.block.state.IBlockData;

public class ChunkSection extends MappedObject<net.minecraft.world.level.chunk.ChunkSection> {

    public ChunkSection(net.minecraft.world.level.chunk.ChunkSection handle) {
        super(handle);
    }

    public IBlockData setBlockState(int x, int y, int z, net.minecraft.world.level.block.state.IBlockData state, boolean lock) {
        return new IBlockData(handle.a(x, y, z, state, lock));
    }

}
