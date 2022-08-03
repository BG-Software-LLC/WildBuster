package com.bgsoftware.wildbuster.nms.v1_18_R2.mappings.net.minecraft.world.level.block.state;

import com.bgsoftware.wildbuster.nms.mapping.Remap;
import com.bgsoftware.wildbuster.nms.v1_18_R2.mappings.MappedObject;

public class IBlockData extends MappedObject<net.minecraft.world.level.block.state.IBlockData> {

    public IBlockData(net.minecraft.world.level.block.state.IBlockData handle) {
        super(handle);
    }

    @Remap(classPath = "net.minecraft.world.level.block.state.BlockBehaviour$BlockStateBase",
            name = "hasBlockEntity",
            type = Remap.Type.METHOD,
            remappedName = "n")
    public boolean isTileEntity() {
        return handle.n();
    }

}
