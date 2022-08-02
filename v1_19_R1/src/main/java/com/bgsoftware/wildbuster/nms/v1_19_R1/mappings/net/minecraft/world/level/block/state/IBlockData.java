package com.bgsoftware.wildbuster.nms.v1_19_R1.mappings.net.minecraft.world.level.block.state;

import com.bgsoftware.wildbuster.nms.v1_19_R1.mappings.MappedObject;

public class IBlockData extends MappedObject<net.minecraft.world.level.block.state.IBlockData> {

    public IBlockData(net.minecraft.world.level.block.state.IBlockData handle) {
        super(handle);
    }

    public boolean isTileEntity() {
        return handle.o();
    }

}
