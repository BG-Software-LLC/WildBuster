package com.bgsoftware.wildbuster.nms.v1_20_1;

import com.bgsoftware.wildbuster.WildBusterPlugin;
import com.bgsoftware.wildbuster.api.objects.BlockData;
import com.bgsoftware.wildbuster.nms.algorithms.PaperGlowEnchantment;
import com.bgsoftware.wildbuster.nms.algorithms.SpigotGlowEnchantment;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.craftbukkit.v1_20_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftMagicNumbers;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public final class NMSAdapter implements com.bgsoftware.wildbuster.nms.NMSAdapter {

    @Override
    public String getVersion() {
        return "v1_20_R1";
    }

    @Override
    public void setFastBlock(Location location, BlockData blockData) {
        BlockPos blockPos = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        ServerLevel serverLevel = ((CraftWorld) location.getWorld()).getHandle();
        LevelChunk levelChunk = serverLevel.getChunkAt(blockPos);

        LevelChunkSection chunkSection = levelChunk.getSection(levelChunk.getSectionIndex(blockPos.getY()));

        BlockState oldBlockState = chunkSection.setBlockState(blockPos.getX() & 15,
                blockPos.getY() & 15, blockPos.getZ() & 15,
                Block.stateById(blockData.getCombinedId()), false);

        if (oldBlockState.hasBlockEntity()) {
            serverLevel.removeBlockEntity(blockPos);
        }

        serverLevel.getLightEngine().checkBlock(blockPos);
        serverLevel.getChunkSource().blockChanged(blockPos);
    }

    @Override
    public void refreshChunk(org.bukkit.Chunk bukkitChunk, List<Location> blocksList, List<Player> playerList) {
        ServerLevel serverLevel = ((CraftChunk) bukkitChunk).getCraftWorld().getHandle();
        LevelChunk levelChunk = serverLevel.getChunk(bukkitChunk.getX(), bukkitChunk.getZ());
        ServerChunkCache chunkCache = levelChunk.level.getChunkSource();
        blocksList.forEach(location -> {
            BlockPos blockPos = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
            chunkCache.blockChanged(blockPos);
        });
    }

    @Override
    public void refreshLight(org.bukkit.Chunk chunk) {
        // Do nothing.
    }

    @Override
    public void clearTileEntities(org.bukkit.Chunk bukkitChunk, List<Location> tileEntities) {
        ServerLevel serverLevel = ((CraftChunk) bukkitChunk).getCraftWorld().getHandle();
        LevelChunk levelChunk = serverLevel.getChunk(bukkitChunk.getX(), bukkitChunk.getZ());
        Iterator<BlockPos> blockEntitiesIterator = levelChunk.getBlockEntities().keySet().iterator();
        while (blockEntitiesIterator.hasNext()) {
            BlockPos blockPos = blockEntitiesIterator.next();
            Location location = new Location(bukkitChunk.getWorld(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
            if (tileEntities.contains(location))
                blockEntitiesIterator.remove();
        }
    }

    @Override
    public void sendActionBar(Player pl, String message) {
        pl.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

    @Override
    public int getMaterialId(Material type) {
        throw new RuntimeException("You cannot run getMaterialId in 1.18!");
    }

    @Override
    public int getMaterialData(org.bukkit.block.Block block) {
        BlockState blockState = ((CraftBlock) block).getNMS();
        return CraftMagicNumbers.toLegacyData(blockState);
    }

    @Override
    public int getCombinedId(org.bukkit.block.Block block) {
        BlockState blockState = ((CraftBlock) block).getNMS();
        return Block.getId(blockState);
    }

    @Override
    public Object getBlockData(int combined) {
        return CraftBlockData.fromData(Block.stateById(combined));
    }

    @Override
    public org.bukkit.inventory.ItemStack getPlayerSkull(org.bukkit.inventory.ItemStack bukkitItem, String texture) {
        ItemStack itemStack = CraftItemStack.asNMSCopy(bukkitItem);

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

        return CraftItemStack.asBukkitCopy(itemStack);
    }

    @Override
    public boolean isInsideBorder(Location location) {
        WorldBorder worldBorder = location.getWorld().getWorldBorder();
        Location center = worldBorder.getCenter();
        int radius = (int) worldBorder.getSize() / 2;
        return location.getBlockX() <= (center.getBlockX() + radius) && location.getBlockX() >= (center.getBlockX() - radius) &&
                location.getBlockZ() <= (center.getBlockZ() + radius) && location.getBlockZ() >= (center.getBlockZ() - radius);
    }

    @Override
    public Enchantment getGlowEnchant() {
        try {
            return new PaperGlowEnchantment("wildbuster_glowing_enchant");
        } catch (Throwable error) {
            return new SpigotGlowEnchantment("wildbuster_glowing_enchant");
        }
    }

    @Override
    public boolean isTallGrass(Material type) {
        return switch (type) {
            case SUNFLOWER, LILAC, TALL_GRASS, LARGE_FERN, ROSE_BUSH, PEONY, TALL_SEAGRASS -> true;
            default -> false;
        };
    }

    @Override
    public Object getCustomHolder(InventoryType inventoryType, InventoryHolder defaultHolder, String title) {
        return new CustomHopperBlockEntity(defaultHolder, title);
    }

    @Override
    public void handleChunkUnload(org.bukkit.World world, List<org.bukkit.Chunk> chunks, WildBusterPlugin plugin, boolean unload) {
        if (unload)
            chunks.forEach(chunk -> world.removePluginChunkTicket(chunk.getX(), chunk.getZ(), plugin));
        else
            chunks.forEach(chunk -> world.addPluginChunkTicket(chunk.getX(), chunk.getZ(), plugin));
    }

    @Override
    public int getWorldMinHeight(org.bukkit.World world) {
        return world.getMinHeight();
    }

}
