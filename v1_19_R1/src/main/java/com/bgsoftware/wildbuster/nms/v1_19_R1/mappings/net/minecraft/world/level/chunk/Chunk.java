package com.bgsoftware.wildbuster.nms.v1_19_R1.mappings.net.minecraft.world.level.chunk;

import com.bgsoftware.common.remaps.Remap;
import com.bgsoftware.wildbuster.nms.v1_19_R1.mappings.MappedObject;
import com.bgsoftware.wildbuster.nms.v1_19_R1.mappings.net.minecraft.world.level.World;
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

    @Remap(classPath = "net.minecraft.world.level.LevelHeightAccessor",
            name = "getSectionIndex",
            type = Remap.Type.METHOD,
            remappedName = "e")
    public int getSectionIndex(int y) {
        return handle.e(y);
    }

    @Remap(classPath = "net.minecraft.world.level.chunk.ChunkAccess",
            name = "getSections",
            type = Remap.Type.METHOD,
            remappedName = "d")
    public ChunkSection[] getSections() {
        return handle.d();
    }

    @Remap(classPath = "net.minecraft.world.level.chunk.LevelChunk",
            name = "getBlockEntities",
            type = Remap.Type.METHOD,
            remappedName = "E")
    public Map<BlockPosition, TileEntity> getBlockEntities() {
        return handle.E();
    }

    @Remap(classPath = "net.minecraft.world.level.chunk.LevelChunk",
            name = "getLevel",
            type = Remap.Type.METHOD,
            remappedName = "D")
    public World getLevel() {
        return new World(handle.D());
    }

    public IRegistry<BiomeBase> getBiomeRegistry() {
        return handle.biomeRegistry;
    }

}
