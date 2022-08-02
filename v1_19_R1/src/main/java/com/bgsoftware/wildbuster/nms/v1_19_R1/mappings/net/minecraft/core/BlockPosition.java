package com.bgsoftware.wildbuster.nms.v1_19_R1.mappings.net.minecraft.core;

import com.bgsoftware.wildbuster.nms.v1_19_R1.mappings.MappedObject;

public class BlockPosition extends MappedObject<net.minecraft.core.BlockPosition> {

    public static BlockPosition ZERO = new BlockPosition(net.minecraft.core.BlockPosition.b);

    public BlockPosition(int x, int y, int z) {
        this(new net.minecraft.core.BlockPosition(x, y, z));
    }

    public BlockPosition(net.minecraft.core.BlockPosition handle) {
        super(handle);
    }

    public int getX() {
        return handle.u();
    }

    public int getY() {
        return handle.v();
    }

    public int getZ() {
        return handle.w();
    }

}
