package com.bgsoftware.wildbuster.nms.v1_16_R3;

import com.bgsoftware.common.reflection.ReflectField;
import com.bgsoftware.wildbuster.nms.ChunkSnapshotReader;
import net.minecraft.server.v1_16_R3.Block;
import net.minecraft.server.v1_16_R3.DataPaletteBlock;
import net.minecraft.server.v1_16_R3.IBlockData;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.CraftChunkSnapshot;

import java.lang.ref.WeakReference;

public class ChunkSnapshotReaderImpl implements ChunkSnapshotReader {

    private static final ReflectField<DataPaletteBlock<IBlockData>[]> CHUNK_SNAPSHOT_BLOCK_IDS =
            new ReflectField<>(CraftChunkSnapshot.class, DataPaletteBlock[].class, "blockids");

    private final CraftChunkSnapshot chunkSnapshot;
    private final WeakReference<DataPaletteBlock<IBlockData>[]> blockIds;

    public ChunkSnapshotReaderImpl(ChunkSnapshot chunkSnapshot) {
        this.chunkSnapshot = (CraftChunkSnapshot) chunkSnapshot;
        this.blockIds = new WeakReference<>(CHUNK_SNAPSHOT_BLOCK_IDS.get(chunkSnapshot));
    }

    @Override
    public ChunkSnapshot getHandle() {
        return this.chunkSnapshot;
    }

    @Override
    public Material getBlockType(int x, int y, int z) {
        return this.chunkSnapshot.getBlockType(x, y, z);
    }

    @Override
    public byte getBlockData(int x, int y, int z) {
        return 0;
    }

    @Override
    public int getCombinedId(int x, int y, int z) {
        IBlockData blockData = getBlockIds()[y >> 4].a(x, y & 0xF, z);
        return Block.getCombinedId(blockData);
    }

    private DataPaletteBlock<IBlockData>[] getBlockIds() {
        DataPaletteBlock<IBlockData>[] blockIds = this.blockIds.get();
        if (blockIds == null)
            throw new IllegalStateException("blockIds is null");
        return blockIds;
    }

}
