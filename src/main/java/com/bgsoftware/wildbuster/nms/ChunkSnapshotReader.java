package com.bgsoftware.wildbuster.nms;

import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;

public interface ChunkSnapshotReader {

    ChunkSnapshot getHandle();

    Material getBlockType(int x, int y, int z);

    byte getBlockData(int x, int y, int z);

    int getCombinedId(int x, int y, int z);

}
