package com.bgsoftware.wildbuster.nms.v1_19;

import com.bgsoftware.wildbuster.nms.algorithms.PaperGlowEnchantment;
import com.bgsoftware.wildbuster.nms.algorithms.SpigotGlowEnchantment;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.util.UUID;

public class NMSAdapterImpl extends com.bgsoftware.wildbuster.nms.v1_19.AbstractNMSAdapter {

    private static final Enchantment GLOW_ENCHANT = initializeGlowEnchantment();

    @Override
    protected LevelChunkSection getChunkSectionForY(LevelChunk levelChunk, int y) {
        LevelChunkSection[] chunkSections = levelChunk.getSections();
        int index = levelChunk.getSectionIndex(y);

        LevelChunkSection chunkSection = chunkSections[index];

        if (chunkSection == null) {
            int yOffset = SectionPos.blockToSectionCoord(y);
            chunkSection = chunkSections[index] = new LevelChunkSection(yOffset, levelChunk.biomeRegistry);
        }

        return chunkSection;
    }

    @Override
    protected void setProfileForItem(ItemStack itemStack, String texture) {
        CompoundTag compoundTag = itemStack.getOrCreateTag();

        CompoundTag skullOwner = compoundTag.contains("SkullOwner") ?
                compoundTag.getCompound("SkullOwner") : new CompoundTag();

        CompoundTag properties = new CompoundTag();
        ListTag textures = new ListTag();
        CompoundTag signature = new CompoundTag();

        signature.putString("Value", texture);
        textures.add(signature);

        properties.put("textures", textures);

        skullOwner.put("Properties", properties);
        skullOwner.putString("Id", UUID.randomUUID().toString());

        compoundTag.put("SkullOwner", skullOwner);
    }

    @Override
    public void makeItemGlow(ItemMeta itemMeta) {
        itemMeta.addEnchant(GLOW_ENCHANT, 1, true);
    }

    private static Enchantment initializeGlowEnchantment() {
        Enchantment glowEnchant;

        try {
            glowEnchant = new PaperGlowEnchantment("wildbuster_glowing_enchant");
        } catch (Throwable error) {
            glowEnchant = new SpigotGlowEnchantment("wildbuster_glowing_enchant");
        }

        try {
            Field field = Enchantment.class.getDeclaredField("acceptingNew");
            field.setAccessible(true);
            field.set(null, true);
            field.setAccessible(false);
        } catch (Exception ignored) {
        }

        try {
            Enchantment.registerEnchantment(glowEnchant);
        } catch (Exception ignored) {
        }

        return glowEnchant;
    }

}
