package com.bgsoftware.wildbuster.nms.v1_19_R1.mappings.net.minecraft.server.level;

import com.bgsoftware.wildbuster.nms.v1_19_R1.mappings.MappedObject;
import com.bgsoftware.wildbuster.nms.v1_19_R1.mappings.net.minecraft.core.BlockPosition;
import com.bgsoftware.wildbuster.nms.v1_19_R1.mappings.net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.lighting.LightEngine;

public class WorldServer extends MappedObject<net.minecraft.server.level.WorldServer> {

    public WorldServer(net.minecraft.server.level.WorldServer handle) {
        super(handle);
    }

    public int getSectionIndex(int y) {
        return handle.e(y);
    }

    public ChunkProviderServer getChunkSource() {
        return new ChunkProviderServer(handle.k());
    }

    public LightEngine getLightEngine() {
        return handle.l_();
    }

    public Chunk getChunkAt(BlockPosition blockPosition) {
        return new Chunk(handle.l(blockPosition.getHandle()));
    }

    public IBlockData getBlockState(BlockPosition blockPosition) {
        return handle.a_(blockPosition.getHandle());
    }

    public void removeTileEntity(BlockPosition blockPosition) {
        handle.m(blockPosition.getHandle());
    }

}
