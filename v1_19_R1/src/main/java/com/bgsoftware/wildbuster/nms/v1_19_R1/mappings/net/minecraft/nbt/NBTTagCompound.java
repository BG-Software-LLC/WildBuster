package com.bgsoftware.wildbuster.nms.v1_19_R1.mappings.net.minecraft.nbt;

import com.bgsoftware.wildbuster.nms.v1_19_R1.mappings.MappedObject;
import net.minecraft.nbt.NBTBase;

public class NBTTagCompound extends MappedObject<net.minecraft.nbt.NBTTagCompound> {

    public static NBTTagCompound ofNullable(net.minecraft.nbt.NBTTagCompound handle) {
        return handle == null ? null : new NBTTagCompound(handle);
    }

    public NBTTagCompound() {
        this(new net.minecraft.nbt.NBTTagCompound());
    }

    public NBTTagCompound(net.minecraft.nbt.NBTTagCompound handle) {
        super(handle);
    }

    public boolean contains(String key) {
        return handle.e(key);
    }

    public NBTTagCompound getCompound(String key) {
        return NBTTagCompound.ofNullable(handle.p(key));
    }

    public void putString(String key, String value) {
        handle.a(key, value);
    }

    public void put(String key, NBTBase nbtBase) {
        handle.a(key, nbtBase);
    }

}
