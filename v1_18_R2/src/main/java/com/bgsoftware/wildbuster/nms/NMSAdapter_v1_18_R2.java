package com.bgsoftware.wildbuster.nms;

import com.bgsoftware.wildbuster.WildBusterPlugin;
import com.bgsoftware.wildbuster.api.objects.BlockData;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutChat;
import net.minecraft.server.level.ChunkProviderServer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityHopper;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkSection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.WorldBorder;
import org.bukkit.craftbukkit.v1_18_R2.CraftChunk;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_18_R2.util.CraftMagicNumbers;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.bgsoftware.wildbuster.nms.NMSMappings_v1_18_R2.*;

@SuppressWarnings({"unused", "ConstantConditions"})
public final class NMSAdapter_v1_18_R2 implements NMSAdapter {

    @Override
    public String getVersion() {
        return "v1_18_R2";
    }

    @Override
    public void setFastBlock(Location location, BlockData blockData) {
        BlockPosition blockPosition = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        Chunk chunk = getChunkAt(((CraftWorld) location.getWorld()).getHandle(), blockPosition);
        int indexY = getSectionIndex(chunk, getY(blockPosition));

        ChunkSection[] chunkSections = getSections(chunk);

        ChunkSection chunkSection = chunkSections[indexY];

        if (chunkSection == null) {
            int yOffset = SectionPosition.a(getY(blockPosition));
            chunkSection = chunkSections[indexY] = new ChunkSection(yOffset, chunk.biomeRegistry);
        }

        IBlockData oldBlockData = setBlockState(chunkSection, getX(blockPosition) & 15, getY(blockPosition) & 15, getZ(blockPosition) & 15,
                getByCombinedId(blockData.getCombinedId()), false);

        if(isTileEntity(oldBlockData)) {
            removeTileEntity(getLevel(chunk), blockPosition);
        }

        ChunkProviderServer chunkProviderServer = getChunkSource(getLevel(chunk));
        getLightEngine(getLevel(chunk)).a(blockPosition);
        blockChanged(chunkProviderServer, blockPosition);
    }

    @Override
    public void refreshChunk(org.bukkit.Chunk bukkitChunk, List<Location> blocksList, List<Player> playerList) {
        Chunk chunk = ((CraftChunk) bukkitChunk).getHandle();
        Map<Integer, Set<Short>> blocks = new HashMap<>();
        WorldServer worldServer = getLevel(chunk);

        ChunkProviderServer chunkProviderServer = getChunkSource(worldServer);

        for(Location location : blocksList) {
            BlockPosition blockPosition = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
            chunkProviderServer.a(blockPosition);
        }
    }

    @Override
    public void refreshLight(org.bukkit.Chunk chunk) {

    }

    @Override
    public void clearTileEntities(org.bukkit.Chunk bukkitChunk, List<Location> tileEntities) {
        Chunk chunk = ((CraftChunk) bukkitChunk).getHandle();

        Map<BlockPosition, TileEntity> chunkTileEntities = getBlockEntities(chunk);

        new HashMap<>(chunkTileEntities).forEach(((blockPosition, tileEntity) -> {
            Location location = new Location(bukkitChunk.getWorld(), getX(blockPosition), getY(blockPosition), getZ(blockPosition));
            if (tileEntities.contains(location))
                chunkTileEntities.remove(blockPosition);
        }));
    }

    @Override
    public void sendActionBar(Player pl, String message) {
        IChatBaseComponent chatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + message + "\"}");
        PacketPlayOutChat packet = new PacketPlayOutChat(chatBaseComponent, ChatMessageType.c, SystemUtils.c);
        send(((CraftPlayer) pl).getHandle().b, packet);
    }

    @Override
    public int getMaterialId(Material type) {
        throw new RuntimeException("You cannot run getMaterialId in 1.14.X!");
    }

    @Override
    public int getMaterialData(org.bukkit.block.Block block) {
        World world = ((CraftWorld) block.getWorld()).getHandle();
        IBlockData blockData = getBlockState(world, new BlockPosition(block.getX(), block.getY(), block.getZ()));
        return CraftMagicNumbers.toLegacyData(blockData);
    }

    @Override
    public int getCombinedId(org.bukkit.block.Block block) {
        World world = ((CraftWorld) block.getWorld()).getHandle();
        BlockPosition blockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());
        return NMSMappings_v1_18_R2.getId(getBlockState(world, blockPosition));
    }

    @Override
    public Object getBlockData(int combined) {
        return CraftBlockData.fromData(getByCombinedId(combined));
    }

    @Override
    public org.bukkit.inventory.ItemStack getPlayerSkull(org.bukkit.inventory.ItemStack itemStack, String texture) {
        ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);

        NBTTagCompound nbtTagCompound = getOrCreateTag(nmsItem);

        NBTTagCompound skullOwner = contains(nbtTagCompound, "SkullOwner") ?
                getCompound(nbtTagCompound, "SkullOwner") : new NBTTagCompound();

        NBTTagCompound properties = new NBTTagCompound();

        NBTTagList textures = new NBTTagList();
        NBTTagCompound signature = new NBTTagCompound();
        putString(signature, "Value", texture);
        textures.add(signature);

        put(properties,"textures", textures);

        put(skullOwner, "Properties", properties);
        putString(skullOwner, "Id", UUID.randomUUID().toString());

        put(nbtTagCompound, "SkullOwner", skullOwner);

        return CraftItemStack.asBukkitCopy(nmsItem);
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

        CustomTileEntityHopper(InventoryHolder holder, String title) {
            super(BlockPosition.b, NMSMappings_v1_18_R2.getBlockData(Blocks.a));
            this.holder = holder;
            this.a(new ChatMessage(title));
        }

        @Override
        public InventoryHolder getOwner() {
            return holder;
        }
    }

}
