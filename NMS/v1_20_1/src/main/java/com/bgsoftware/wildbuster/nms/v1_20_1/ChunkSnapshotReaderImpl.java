package com.bgsoftware.wildbuster.nms.v1_20_1;

import com.bgsoftware.common.reflection.ReflectField;
import com.bgsoftware.wildbuster.nms.ChunkSnapshotReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R1.CraftChunkSnapshot;

import java.lang.ref.WeakReference;

public class ChunkSnapshotReaderImpl implements ChunkSnapshotReader {

    private static final ReflectField<PalettedContainer<BlockState>[]> CHUNK_SNAPSHOT_BLOCK_IDS =
            new ReflectField<>(CraftChunkSnapshot.class, PalettedContainer[].class, "blockids");
    private static final ReflectField<Integer> CHUNK_SNAPSHOT_MIN_HEIGHT =
            new ReflectField<>(CraftChunkSnapshot.class, int.class, "minHeight");

    private final CraftChunkSnapshot chunkSnapshot;
    private final WeakReference<PalettedContainer<BlockState>[]> blockIds;
    private final int minHeight;

    public ChunkSnapshotReaderImpl(ChunkSnapshot chunkSnapshot) {
        this.chunkSnapshot = (CraftChunkSnapshot) chunkSnapshot;
        this.blockIds = new WeakReference<>(CHUNK_SNAPSHOT_BLOCK_IDS.get(chunkSnapshot));
        this.minHeight = CHUNK_SNAPSHOT_MIN_HEIGHT.get(chunkSnapshot);
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
        BlockState blockState = getBlockIds()[this.getSectionIndex(y)].get(x, y & 0xF, z);
        return Block.getId(blockState);
    }

    private PalettedContainer<BlockState>[] getBlockIds() {
        PalettedContainer<BlockState>[] blockIds = this.blockIds.get();
        if (blockIds == null)
            throw new IllegalStateException("blockIds is null");
        return blockIds;
    }

    private int getSectionIndex(int y) {
        return (y - this.minHeight) >> 4;
    }

}
