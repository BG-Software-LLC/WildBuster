package com.bgsoftware.wildbuster.nms.v1_18_R1.mappings.net.minecraft.server.level;

import com.bgsoftware.wildbuster.nms.mapping.Remap;
import com.bgsoftware.wildbuster.nms.v1_18_R1.mappings.MappedObject;
import com.bgsoftware.wildbuster.nms.v1_18_R1.mappings.net.minecraft.core.BlockPosition;

public class ChunkProviderServer extends MappedObject<net.minecraft.server.level.ChunkProviderServer> {

    public ChunkProviderServer(net.minecraft.server.level.ChunkProviderServer handle) {
        super(handle);
    }

    @Remap(classPath = "net.minecraft.server.level.ServerChunkCache",
            name = "blockChanged",
            type = Remap.Type.METHOD,
            remappedName = "a")
    public void blockChanged(BlockPosition blockPosition) {
        handle.a(blockPosition.getHandle());
    }

}
