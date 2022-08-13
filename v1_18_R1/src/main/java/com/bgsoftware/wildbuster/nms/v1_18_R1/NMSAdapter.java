package com.bgsoftware.wildbuster.nms.v1_18_R1;

import com.bgsoftware.common.remaps.Remap;
import com.bgsoftware.wildbuster.WildBusterPlugin;
import com.bgsoftware.wildbuster.api.objects.BlockData;
import com.bgsoftware.wildbuster.nms.v1_18_R1.mappings.net.minecraft.core.BlockPosition;
import com.bgsoftware.wildbuster.nms.v1_18_R1.mappings.net.minecraft.nbt.NBTTagCompound;
import com.bgsoftware.wildbuster.nms.v1_18_R1.mappings.net.minecraft.server.level.ChunkProviderServer;
import com.bgsoftware.wildbuster.nms.v1_18_R1.mappings.net.minecraft.world.item.ItemStack;
import com.bgsoftware.wildbuster.nms.v1_18_R1.mappings.net.minecraft.world.level.World;
import com.bgsoftware.wildbuster.nms.v1_18_R1.mappings.net.minecraft.world.level.block.Block;
import com.bgsoftware.wildbuster.nms.v1_18_R1.mappings.net.minecraft.world.level.block.state.IBlockData;
import com.bgsoftware.wildbuster.nms.v1_18_R1.mappings.net.minecraft.world.level.chunk.Chunk;
import com.bgsoftware.wildbuster.nms.v1_18_R1.mappings.net.minecraft.world.level.chunk.ChunkSection;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.core.SectionPosition;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.level.block.entity.TileEntityHopper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.WorldBorder;
import org.bukkit.craftbukkit.v1_18_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_18_R1.util.CraftMagicNumbers;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings({"unused", "ConstantConditions"})
public final class NMSAdapter implements com.bgsoftware.wildbuster.nms.NMSAdapter {

    @Override
    public String getMappingsHash() {
        return ((CraftMagicNumbers) CraftMagicNumbers.INSTANCE).getMappingsVersion();
    }

    @Override
    public String getVersion() {
        return "v1_18_R1";
    }

    @Override
    public void setFastBlock(Location location, BlockData blockData) {
        BlockPosition blockPosition = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        World worldServer = new World(((CraftWorld) location.getWorld()).getHandle());
        Chunk chunk = worldServer.getChunkAt(blockPosition);
        int indexY = chunk.getSectionIndex(blockPosition.getY());

        net.minecraft.world.level.chunk.ChunkSection[] chunkSections = chunk.getSections();

        net.minecraft.world.level.chunk.ChunkSection nmsChunkSection = chunkSections[indexY];

        if (nmsChunkSection == null) {
            int yOffset = SectionPosition.a(blockPosition.getY());
            nmsChunkSection = chunkSections[indexY] = new net.minecraft.world.level.chunk.ChunkSection(yOffset, chunk.getBiomeRegistry());
        }

        ChunkSection chunkSection = new ChunkSection(nmsChunkSection);

        IBlockData oldBlockData = chunkSection.setBlockState(blockPosition.getX() & 15,
                blockPosition.getY() & 15, blockPosition.getZ() & 15,
                Block.getByCombinedId(blockData.getCombinedId()), false);

        if (oldBlockData.isTileEntity()) {
            worldServer.removeTileEntity(blockPosition);
        }

        worldServer.getLightEngine().a(blockPosition.getHandle());
        worldServer.getChunkSource().blockChanged(blockPosition);
    }

    @Override
    public void refreshChunk(org.bukkit.Chunk bukkitChunk, List<Location> blocksList, List<Player> playerList) {
        Chunk chunk = new Chunk(((CraftChunk) bukkitChunk).getHandle());
        World worldServer = chunk.getLevel();
        Map<Integer, Set<Short>> blocks = new HashMap<>();

        ChunkProviderServer chunkProviderServer = worldServer.getChunkSource();

        for (Location location : blocksList) {
            BlockPosition blockPosition = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
            chunkProviderServer.getHandle().a(blockPosition.getHandle());
        }
    }

    @Override
    public void refreshLight(org.bukkit.Chunk chunk) {

    }

    @Override
    public void clearTileEntities(org.bukkit.Chunk bukkitChunk, List<Location> tileEntities) {
        Chunk chunk = new Chunk(((CraftChunk) bukkitChunk).getHandle());

        Iterator<net.minecraft.core.BlockPosition> iterator = chunk.getBlockEntities().keySet().iterator();

        while (iterator.hasNext()) {
            BlockPosition blockPosition = new BlockPosition(iterator.next());
            Location location = new Location(bukkitChunk.getWorld(), blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
            if (tileEntities.contains(location))
                iterator.remove();
        }
    }

    @Override
    public void sendActionBar(Player pl, String message) {
        pl.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

    @Override
    public int getMaterialId(Material type) {
        throw new RuntimeException("You cannot run getMaterialId in 1.14.X!");
    }

    @Override
    public int getMaterialData(org.bukkit.block.Block block) {
        World worldServer = new World(((CraftWorld) block.getWorld()).getHandle());
        BlockPosition blockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());
        net.minecraft.world.level.block.state.IBlockData blockData = worldServer.getBlockState(blockPosition);
        return CraftMagicNumbers.toLegacyData(blockData);
    }

    @Override
    public int getCombinedId(org.bukkit.block.Block block) {
        World worldServer = new World(((CraftWorld) block.getWorld()).getHandle());
        BlockPosition blockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());
        return Block.getId(worldServer.getBlockState(blockPosition));
    }

    @Override
    public Object getBlockData(int combined) {
        return CraftBlockData.fromData(Block.getByCombinedId(combined));
    }

    @Override
    public org.bukkit.inventory.ItemStack getPlayerSkull(org.bukkit.inventory.ItemStack itemStack, String texture) {
        ItemStack nmsItem = new ItemStack(CraftItemStack.asNMSCopy(itemStack));

        NBTTagCompound nbtTagCompound = nmsItem.getOrCreateTag();

        NBTTagCompound skullOwner = nbtTagCompound.contains("SkullOwner") ?
                nbtTagCompound.getCompound("SkullOwner") : new NBTTagCompound();

        NBTTagCompound properties = new NBTTagCompound();

        NBTTagList textures = new NBTTagList();
        NBTTagCompound signature = new NBTTagCompound();
        signature.putString("Value", texture);
        textures.add(signature.getHandle());

        properties.put("textures", textures);

        skullOwner.put("Properties", properties.getHandle());
        skullOwner.putString("Id", UUID.randomUUID().toString());

        nbtTagCompound.put("SkullOwner", skullOwner.getHandle());

        return CraftItemStack.asBukkitCopy(nmsItem.getHandle());
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
        //noinspection NullableProblems
        return new Enchantment(NamespacedKey.minecraft("wb_glowing_enchant")) {
            @Override
            public String getName() {
                return "WildBusterGlow";
            }

            @Override
            public int getMaxLevel() {
                return 1;
            }

            @Override
            public int getStartLevel() {
                return 0;
            }

            @Override
            public EnchantmentTarget getItemTarget() {
                return null;
            }

            @Override
            public boolean conflictsWith(Enchantment enchantment) {
                return false;
            }

            @Override
            public boolean canEnchantItem(org.bukkit.inventory.ItemStack itemStack) {
                return true;
            }

            @Override
            public boolean isTreasure() {
                return false;
            }

            @Override
            public boolean isCursed() {
                return false;
            }
        };
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
        return new CustomTileEntityHopper(defaultHolder, title);
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

    private static class CustomTileEntityHopper extends TileEntityHopper {

        private final InventoryHolder holder;

        @Remap(classPath = "net.minecraft.world.level.block.entity.BaseContainerBlockEntity",
                name = "setCustomName",
                type = Remap.Type.METHOD,
                remappedName = "a")
        @Remap(classPath = "net.minecraft.network.chat.Component",
                name = "nullToEmpty",
                type = Remap.Type.METHOD,
                remappedName = "a")
        CustomTileEntityHopper(InventoryHolder holder, String title) {
            super(BlockPosition.ZERO.getHandle(), Block.AIR.getBlockData());
            this.holder = holder;
            this.a(IChatBaseComponent.a(title));
        }

        @Override
        public InventoryHolder getOwner() {
            return holder;
        }
    }

}
