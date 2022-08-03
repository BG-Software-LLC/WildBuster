package com.bgsoftware.wildbuster.nms.v1_19_R1.mappings.net.minecraft.world.level;

import com.bgsoftware.wildbuster.nms.mapping.Remap;
import com.bgsoftware.wildbuster.nms.v1_19_R1.mappings.MappedObject;
import com.bgsoftware.wildbuster.nms.v1_19_R1.mappings.net.minecraft.core.BlockPosition;
import com.bgsoftware.wildbuster.nms.v1_19_R1.mappings.net.minecraft.server.level.ChunkProviderServer;
import com.bgsoftware.wildbuster.nms.v1_19_R1.mappings.net.minecraft.world.level.chunk.Chunk;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.lighting.LightEngine;

public class World extends MappedObject<net.minecraft.world.level.World> {

    public World(net.minecraft.world.level.World handle) {
        super(handle);
    }

    @Remap(classPath = "net.minecraft.server.level.ServerLevel",
            name = "getChunkSource",
            type = Remap.Type.METHOD,
            remappedName = "k")
    public ChunkProviderServer getChunkSource() {
        return new ChunkProviderServer(((WorldServer) handle).k());
    }

    @Remap(classPath = "net.minecraft.server.level.WorldGenRegion",
            name = "getLightEngine",
            type = Remap.Type.METHOD,
            remappedName = "l_")
    public LightEngine getLightEngine() {
        return handle.l_();
    }

    @Remap(classPath = "net.minecraft.world.level.Level",
            name = "getChunkAt",
            type = Remap.Type.METHOD,
            remappedName = "l")
    public Chunk getChunkAt(BlockPosition blockPosition) {
        return new Chunk(handle.l(blockPosition.getHandle()));
    }

    @Remap(classPath = "net.minecraft.world.level.Level",
            name = "getBlockState",
            type = Remap.Type.METHOD,
            remappedName = "a_")
    public IBlockData getBlockState(BlockPosition blockPosition) {
        return handle.a_(blockPosition.getHandle());
    }

    @Remap(classPath = "net.minecraft.world.level.Level",
            name = "removeBlockEntity",
            type = Remap.Type.METHOD,
            remappedName = "n")
    public void removeTileEntity(BlockPosition blockPosition) {
        handle.n(blockPosition.getHandle());
    }

}
