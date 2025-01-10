package com.bgsoftware.wildbuster.nms.v1_21_4;

import com.bgsoftware.common.reflection.ReflectField;
import com.bgsoftware.wildbuster.WildBusterPlugin;
import com.bgsoftware.wildbuster.api.objects.BlockData;
import com.bgsoftware.wildbuster.nms.ChunkSnapshotReader;
import com.bgsoftware.wildbuster.nms.NMSAdapter;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.legacy.CraftLegacy;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class NMSAdapterImpl implements NMSAdapter {

    private static final ReflectField<Map<NamespacedKey, Enchantment>> REGISTRY_CACHE =
            new ReflectField<>(CraftRegistry.class, Map.class, "cache");

    @Override
    public String getVersion() {
        return "v1_21_4";
    }

    @Override
    public void loadLegacy() {
        // Load legacy by accessing the CraftLegacy class.
        CraftLegacy.fromLegacy(Material.ACACIA_BOAT);
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
    public ChunkSnapshotReader createChunkSnapshotReader(ChunkSnapshot chunkSnapshot) {
        return new ChunkSnapshotReaderImpl(chunkSnapshot);
    }

    @Override
    public Object getBlockData(int combined) {
        return CraftBlockData.fromData(Block.stateById(combined));
    }

    @Override
    public org.bukkit.inventory.ItemStack getPlayerSkull(org.bukkit.inventory.ItemStack bukkitItem, String texture) {
        ItemStack itemStack = CraftItemStack.asNMSCopy(bukkitItem);

        PropertyMap propertyMap = new PropertyMap();
        propertyMap.put("textures", new Property("textures", texture));

        ResolvableProfile resolvableProfile = new ResolvableProfile(Optional.empty(), Optional.empty(), propertyMap);

        itemStack.set(DataComponents.PROFILE, resolvableProfile);

        return CraftItemStack.asBukkitCopy(itemStack);
    }

    @Override
    public boolean isInsideBorder(Location location) {
        World bukkitWorld = location.getWorld();

        WorldBorder worldBorder = bukkitWorld.getWorldBorder();
        int blockY = location.getBlockY();

        return worldBorder.isInside(location) &&
                blockY >= bukkitWorld.getMinHeight() && blockY <= bukkitWorld.getMaxHeight();
    }

    @Override
    public Enchantment getGlowEnchant() {
        return new GlowEnchantment();
    }

    @Override
    public Enchantment createGlowEnchantment() {
        Enchantment enchantment = getGlowEnchant();

        Registry<Enchantment> registry = Registry.ENCHANTMENT;
        try {
            if (registry instanceof io.papermc.paper.registry.legacy.DelayedRegistry) {
                registry = ((io.papermc.paper.registry.legacy.DelayedRegistry) registry).delegate();
            }
        } catch (Throwable ignored) {
        }

        Map<NamespacedKey, Enchantment> registryCache = REGISTRY_CACHE.get(registry);

        registryCache.put(enchantment.getKey(), enchantment);

        return enchantment;
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
    public void handleChunkUnload(World world, List<org.bukkit.Chunk> chunks, WildBusterPlugin plugin, boolean unload) {
        if (unload)
            chunks.forEach(chunk -> world.removePluginChunkTicket(chunk.getX(), chunk.getZ(), plugin));
        else
            chunks.forEach(chunk -> world.addPluginChunkTicket(chunk.getX(), chunk.getZ(), plugin));
    }

    @Override
    public int getWorldMinHeight(World world) {
        return world.getMinHeight();
    }

}
