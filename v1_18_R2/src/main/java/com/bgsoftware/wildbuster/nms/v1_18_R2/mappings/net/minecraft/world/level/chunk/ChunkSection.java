package com.bgsoftware.wildbuster.nms.v1_18_R2.mappings.net.minecraft.world.level.chunk;

import com.bgsoftware.common.remaps.Remap;
import com.bgsoftware.wildbuster.nms.v1_18_R2.mappings.MappedObject;
import com.bgsoftware.wildbuster.nms.v1_18_R2.mappings.net.minecraft.world.level.block.state.IBlockData;

public class ChunkSection extends MappedObject<net.minecraft.world.level.chunk.ChunkSection> {

    public ChunkSection(net.minecraft.world.level.chunk.ChunkSection handle) {
        super(handle);
    }

    @Remap(classPath = "net.minecraft.world.level.chunk.LevelChunkSection",
            name = "setBlockState",
            type = Remap.Type.METHOD,
            remappedName = "a")
    public IBlockData setBlockState(int x, int y, int z, net.minecraft.world.level.block.state.IBlockData state, boolean lock) {
        return new IBlockData(handle.a(x, y, z, state, lock));
    }

}
