package com.bgsoftware.wildbuster.nms.v1_16_R3;

import com.bgsoftware.wildbuster.WildBusterPlugin;
import com.bgsoftware.wildbuster.api.objects.BlockData;
import net.minecraft.server.v1_16_R3.Block;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.ChatMessage;
import net.minecraft.server.v1_16_R3.ChatMessageType;
import net.minecraft.server.v1_16_R3.Chunk;
import net.minecraft.server.v1_16_R3.ChunkProviderServer;
import net.minecraft.server.v1_16_R3.ChunkSection;
import net.minecraft.server.v1_16_R3.IBlockData;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.ItemStack;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.NBTTagList;
import net.minecraft.server.v1_16_R3.PacketPlayOutChat;
import net.minecraft.server.v1_16_R3.PacketPlayOutMultiBlockChange;
import net.minecraft.server.v1_16_R3.SectionPosition;
import net.minecraft.server.v1_16_R3.SystemUtils;
import net.minecraft.server.v1_16_R3.TileEntityHopper;
import net.minecraft.server.v1_16_R3.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.WorldBorder;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.shorts.ShortArraySet;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.shorts.ShortSet;
import org.bukkit.craftbukkit.v1_16_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftMagicNumbers;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings({"unused", "ConstantConditions"})
public final class NMSAdapter implements com.bgsoftware.wildbuster.nms.NMSAdapter {

    private static Class<?> SHORT_ARRAY_SET_CLASS = null;
    private static Constructor<?> MULTI_BLOCK_CHANGE_CONSTRUCTOR = null;

    static {
        try {
            SHORT_ARRAY_SET_CLASS = Class.forName("it.unimi.dsi.fastutil.shorts.ShortArraySet");
            Class<?> shortSetClass = Class.forName("it.unimi.dsi.fastutil.shorts.ShortSet");
            for (Constructor<?> constructor : PacketPlayOutMultiBlockChange.class.getConstructors()) {
                if (constructor.getParameterCount() > 0)
                    MULTI_BLOCK_CHANGE_CONSTRUCTOR = constructor;
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public String getVersion() {
        return "v1_16_R3";
    }

    @Override
    public void setFastBlock(Location location, BlockData blockData) {
        BlockPosition blockPosition = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        Chunk chunk = ((CraftWorld) location.getWorld()).getHandle().getChunkAtWorldCoords(blockPosition);
        int indexY = blockPosition.getY() >> 4;
        ChunkSection chunkSection = chunk.getSections()[indexY];

        if (chunkSection == null)
            chunkSection = chunk.getSections()[indexY] = new ChunkSection(indexY << 4);

        IBlockData oldBlockData = chunkSection.setType(blockPosition.getX() & 15, blockPosition.getY() & 15, blockPosition.getZ() & 15,
                Block.getByCombinedId(blockData.getCombinedId()), false);

        if (oldBlockData.getBlock().isTileEntity()) {
            chunk.world.removeTileEntity(blockPosition);
        }

        ChunkProviderServer chunkProviderServer = chunk.world.getChunkProvider();
        chunkProviderServer.getLightEngine().a(blockPosition);
        chunkProviderServer.flagDirty(blockPosition);
    }

    @Override
    public void refreshChunk(org.bukkit.Chunk bukkitChunk, List<Location> blocksList, List<Player> playerList) {
        Chunk chunk = ((CraftChunk) bukkitChunk).getHandle();
        Map<Integer, Set<Short>> blocks = new HashMap<>();

        for (Location location : blocksList) {
            Set<Short> shortSet = blocks.computeIfAbsent(location.getBlockY() >> 4, i -> createShortSet());
            shortSet.add((short) ((location.getBlockX() & 15) << 8 | (location.getBlockZ() & 15) << 4 | (location.getBlockY() & 15)));
        }

        Set<PacketPlayOutMultiBlockChange> packetsToSend = new HashSet<>();

        for (Map.Entry<Integer, Set<Short>> entry : blocks.entrySet()) {
            PacketPlayOutMultiBlockChange packetPlayOutMultiBlockChange = createMultiBlockChangePacket(
                    SectionPosition.a(chunk.getPos(), entry.getKey()), entry.getValue(), chunk.getSections()[entry.getKey()]);
            if (packetPlayOutMultiBlockChange != null)
                packetsToSend.add(packetPlayOutMultiBlockChange);
        }

        for (Player player : playerList)
            packetsToSend.forEach(((CraftPlayer) player).getHandle().playerConnection::sendPacket);
    }

    @SuppressWarnings("all")
    private static Set<Short> createShortSet() {
        if (SHORT_ARRAY_SET_CLASS == null)
            return new ShortArraySet();

        try {
            return (Set<Short>) SHORT_ARRAY_SET_CLASS.newInstance();
        } catch (Throwable ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static PacketPlayOutMultiBlockChange createMultiBlockChangePacket(SectionPosition sectionPosition, Set<Short> shortSet, ChunkSection chunkSection) {
        if (MULTI_BLOCK_CHANGE_CONSTRUCTOR == null) {
            return new PacketPlayOutMultiBlockChange(
                    sectionPosition,
                    (ShortSet) shortSet,
                    chunkSection,
                    true
            );
        }

        try {
            return (PacketPlayOutMultiBlockChange) MULTI_BLOCK_CHANGE_CONSTRUCTOR.newInstance(sectionPosition, shortSet, chunkSection, true);
        } catch (Throwable ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public void refreshLight(org.bukkit.Chunk chunk) {

    }

    @Override
    public void clearTileEntities(org.bukkit.Chunk bukkitChunk, List<Location> tileEntities) {
        Chunk chunk = ((CraftChunk) bukkitChunk).getHandle();
        new HashMap<>(chunk.tileEntities).forEach(((blockPosition, tileEntity) -> {
            Location location = new Location(bukkitChunk.getWorld(), blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
            if (tileEntities.contains(location))
                chunk.tileEntities.remove(blockPosition);
        }));
    }

    @Override
    public void sendActionBar(Player pl, String message) {
        IChatBaseComponent chatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + message + "\"}");
        PacketPlayOutChat packet = new PacketPlayOutChat(chatBaseComponent, ChatMessageType.GAME_INFO, SystemUtils.b);
        ((CraftPlayer) pl).getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public int getMaterialId(Material type) {
        throw new RuntimeException("You cannot run getMaterialId in 1.14.X!");
    }

    @Override
    public int getMaterialData(org.bukkit.block.Block block) {
        World world = ((CraftWorld) block.getWorld()).getHandle();
        IBlockData blockData = world.getType(new BlockPosition(block.getX(), block.getY(), block.getZ()));
        return CraftMagicNumbers.toLegacyData(blockData);
    }

    @Override
    public int getCombinedId(org.bukkit.block.Block block) {
        World world = ((CraftWorld) block.getWorld()).getHandle();
        BlockPosition blockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());
        return Block.getCombinedId(world.getType(blockPosition));
    }

    @Override
    public Object getBlockData(int combined) {
        return CraftBlockData.fromData(Block.getByCombinedId(combined));
    }

    @Override
    public org.bukkit.inventory.ItemStack getPlayerSkull(org.bukkit.inventory.ItemStack itemStack, String texture) {
        ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);

        NBTTagCompound nbtTagCompound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();

        NBTTagCompound skullOwner = nbtTagCompound.hasKey("SkullOwner") ? nbtTagCompound.getCompound("SkullOwner") : new NBTTagCompound();

        NBTTagCompound properties = new NBTTagCompound();

        NBTTagList textures = new NBTTagList();
        NBTTagCompound signature = new NBTTagCompound();
        signature.setString("Value", texture);
        textures.add(signature);

        properties.set("textures", textures);

        skullOwner.set("Properties", properties);
        skullOwner.setString("Id", UUID.randomUUID().toString());

        nbtTagCompound.set("SkullOwner", skullOwner);

        nmsItem.setTag(nbtTagCompound);

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
        switch (type) {
            case SUNFLOWER:
            case LILAC:
            case TALL_GRASS:
            case LARGE_FERN:
            case ROSE_BUSH:
            case PEONY:
            case TALL_SEAGRASS:
                return true;
            default:
                return false;
        }
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

    private static class CustomTileEntityHopper extends TileEntityHopper {

        private final InventoryHolder holder;

        CustomTileEntityHopper(InventoryHolder holder, String title) {
            this.holder = holder;
            this.setCustomName(new ChatMessage(title));
        }

        @Override
        public InventoryHolder getOwner() {
            return holder;
        }
    }

}
