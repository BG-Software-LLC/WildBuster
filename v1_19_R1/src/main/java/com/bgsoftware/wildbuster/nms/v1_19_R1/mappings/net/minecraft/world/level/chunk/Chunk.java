package com.bgsoftware.wildbuster.nms.v1_19_R1.mappings.net.minecraft.world.level.chunk;

import com.bgsoftware.wildbuster.nms.v1_19_R1.mappings.MappedObject;
import com.bgsoftware.wildbuster.nms.v1_19_R1.mappings.net.minecraft.server.level.WorldServer;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.chunk.ChunkSection;

import java.util.Map;

public class Chunk extends MappedObject<net.minecraft.world.level.chunk.Chunk> {

    public Chunk(net.minecraft.world.level.chunk.Chunk handle) {
        super(handle);
    }

    public int getSectionIndex(int y) {
        return handle.e(y);
    }

    public ChunkSection[] getSections() {
        return handle.d();
    }

    public Map<BlockPosition, TileEntity> getBlockEntities() {
        return handle.i;
    }

    public WorldServer getLevel() {
        return new WorldServer(handle.q);
    }

    public IRegistry<BiomeBase> getBiomeRegistry() {
        return handle.biomeRegistry;
    }

}
