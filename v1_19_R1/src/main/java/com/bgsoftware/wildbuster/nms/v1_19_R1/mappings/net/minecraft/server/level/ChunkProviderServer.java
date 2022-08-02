package com.bgsoftware.wildbuster.nms.v1_19_R1.mappings.net.minecraft.server.level;

import com.bgsoftware.wildbuster.nms.v1_19_R1.mappings.MappedObject;
import com.bgsoftware.wildbuster.nms.v1_19_R1.mappings.net.minecraft.core.BlockPosition;

public class ChunkProviderServer extends MappedObject<net.minecraft.server.level.ChunkProviderServer> {

    public ChunkProviderServer(net.minecraft.server.level.ChunkProviderServer handle) {
        super(handle);
    }

    public void blockChanged(BlockPosition blockPosition) {
        handle.a(blockPosition.getHandle());
    }

}
