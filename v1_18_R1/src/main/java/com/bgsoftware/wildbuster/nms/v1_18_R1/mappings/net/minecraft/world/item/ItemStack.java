package com.bgsoftware.wildbuster.nms.v1_18_R1.mappings.net.minecraft.world.item;

import com.bgsoftware.wildbuster.nms.mapping.Remap;
import com.bgsoftware.wildbuster.nms.v1_18_R1.mappings.MappedObject;
import com.bgsoftware.wildbuster.nms.v1_18_R1.mappings.net.minecraft.nbt.NBTTagCompound;

public class ItemStack extends MappedObject<net.minecraft.world.item.ItemStack> {

    public ItemStack(net.minecraft.world.item.ItemStack handle) {
        super(handle);
    }

    @Remap(classPath = "net.minecraft.world.item.ItemStack",
            name = "getOrCreateTag",
            type = Remap.Type.METHOD,
            remappedName = "t")
    public NBTTagCompound getOrCreateTag() {
        return new NBTTagCompound(handle.t());
    }

}
