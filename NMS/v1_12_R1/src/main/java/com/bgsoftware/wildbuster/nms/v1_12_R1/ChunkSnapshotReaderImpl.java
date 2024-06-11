package com.bgsoftware.wildbuster.nms.v1_12_R1;

import com.bgsoftware.wildbuster.nms.ChunkSnapshotReader;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.CraftChunkSnapshot;

public class ChunkSnapshotReaderImpl implements ChunkSnapshotReader {

    private final CraftChunkSnapshot chunkSnapshot;

    public ChunkSnapshotReaderImpl(ChunkSnapshot chunkSnapshot) {
        this.chunkSnapshot = (CraftChunkSnapshot) chunkSnapshot;
    }

    @Override
    public ChunkSnapshot getHandle() {
        return this.chunkSnapshot;
    }

    @Override
    public Material getBlockType(int x, int y, int z) {
        int blockId = this.chunkSnapshot.getBlockTypeId(x, y, z);
        return Material.getMaterial(blockId);
    }

    @Override
    public byte getBlockData(int x, int y, int z) {
        return (byte) this.chunkSnapshot.getBlockData(x, y, z);
    }

    @Override
    public int getCombinedId(int x, int y, int z) {
        int blockId = this.chunkSnapshot.getBlockTypeId(x, y, z);
        byte blockData = getBlockData(x, y, z);
        return blockId + (blockData << 12);
    }

}
