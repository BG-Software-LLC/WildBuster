package com.bgsoftware.wildbuster.nms.v1_20_4;

import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;

public class NMSAdapterImpl extends com.bgsoftware.wildbuster.nms.v1_20_4.AbstractNMSAdapter {

    @Override
    protected LevelChunkSection getChunkSectionForY(LevelChunk levelChunk, int y) {
        return levelChunk.getSection(levelChunk.getSectionIndex(y));
    }

    @Override
    protected BlockState getBlockState(org.bukkit.block.Block bukkitBlock) {
        return ((CraftBlock) bukkitBlock).getNMS();
    }

    @Override
    public Object getBlockData(int combined) {
        return CraftBlockData.fromData(Block.stateById(combined));
    }

    @Override
    protected void setProfileForItem(ItemStack itemStack, String texture) {
        PropertyMap propertyMap = new PropertyMap();
        propertyMap.put("textures", new Property("textures", texture));

        ResolvableProfile resolvableProfile = new ResolvableProfile(Optional.empty(), Optional.empty(), propertyMap);

        itemStack.set(DataComponents.PROFILE, resolvableProfile);
    }

    @Override
    public void makeItemGlow(ItemMeta itemMeta) {
        itemMeta.setEnchantmentGlintOverride(true);
    }

}
