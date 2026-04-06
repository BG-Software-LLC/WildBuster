package com.bgsoftware.wildbuster.nms.v1_21_9;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.PlayerSkin;
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

public class NMSAdapterImpl extends com.bgsoftware.wildbuster.nms.v1_21_9.AbstractNMSAdapter {

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
        Multimap<String, Property> properties = HashMultimap.create();
        properties.put("textures", new Property("textures", texture));

        ResolvableProfile.Partial partialProfile = new ResolvableProfile.Partial(
                Optional.empty(), Optional.empty(), new PropertyMap(properties));
        ResolvableProfile resolvableProfile = new ResolvableProfile.Static(Either.right(partialProfile), PlayerSkin.Patch.EMPTY);

        itemStack.set(DataComponents.PROFILE, resolvableProfile);
    }

    @Override
    public void makeItemGlow(ItemMeta itemMeta) {
        itemMeta.setEnchantmentGlintOverride(true);
    }

}
