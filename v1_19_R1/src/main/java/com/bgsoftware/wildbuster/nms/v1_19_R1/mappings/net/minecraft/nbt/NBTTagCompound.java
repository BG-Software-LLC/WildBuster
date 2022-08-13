package com.bgsoftware.wildbuster.nms.v1_19_R1.mappings.net.minecraft.nbt;

import com.bgsoftware.common.remaps.Remap;
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

    @Remap(classPath = "net.minecraft.nbt.CompoundTag",
            name = "contains",
            type = Remap.Type.METHOD,
            remappedName = "e")
    public boolean contains(String key) {
        return handle.e(key);
    }

    @Remap(classPath = "net.minecraft.nbt.CompoundTag",
            name = "getCompound",
            type = Remap.Type.METHOD,
            remappedName = "p")
    public NBTTagCompound getCompound(String key) {
        return NBTTagCompound.ofNullable(handle.p(key));
    }

    @Remap(classPath = "net.minecraft.nbt.CompoundTag",
            name = "putString",
            type = Remap.Type.METHOD,
            remappedName = "a")
    public void putString(String key, String value) {
        handle.a(key, value);
    }

    @Remap(classPath = "net.minecraft.nbt.CompoundTag",
            name = "put",
            type = Remap.Type.METHOD,
            remappedName = "a")
    public void put(String key, NBTBase nbtBase) {
        handle.a(key, nbtBase);
    }

}
