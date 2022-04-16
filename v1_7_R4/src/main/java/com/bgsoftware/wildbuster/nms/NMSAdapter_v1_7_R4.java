package com.bgsoftware.wildbuster.nms;

import com.bgsoftware.wildbuster.api.objects.BlockData;
import net.minecraft.server.v1_7_R4.Block;
import net.minecraft.server.v1_7_R4.Chunk;
import net.minecraft.server.v1_7_R4.ChunkPosition;
import net.minecraft.server.v1_7_R4.ChunkSection;
import net.minecraft.server.v1_7_R4.ItemStack;
import net.minecraft.server.v1_7_R4.NBTTagCompound;
import net.minecraft.server.v1_7_R4.NBTTagList;
import net.minecraft.server.v1_7_R4.PacketPlayOutMultiBlockChange;
import net.minecraft.server.v1_7_R4.TileEntity;
import net.minecraft.server.v1_7_R4.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.CraftChunk;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_7_R4.util.CraftMagicNumbers;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
public final class NMSAdapter_v1_7_R4 implements NMSAdapter {

    @Override
    public String getVersion() {
        return "v1_7_R4";
    }

    @Override
    public void setFastBlock(Location location, BlockData blockData) {
        Chunk chunk = ((CraftWorld) location.getWorld()).getHandle().getChunkAt(location.getBlockX() >> 4, location.getBlockZ() >> 4);
        int indexY = location.getBlockY() >> 4;
        ChunkSection chunkSection = chunk.getSections()[indexY];

        if (chunkSection == null)
            chunkSection = chunk.getSections()[indexY] = new ChunkSection(indexY << 4, !chunk.world.worldProvider.g);

        int blockX = location.getBlockX() & 15;
        int blockY = location.getBlockY() & 15;
        int blockZ = location.getBlockZ() & 15;


        Block oldBlock = chunkSection.getTypeId(blockX, blockY, blockZ);
        chunkSection.setTypeId(blockX, blockY, blockZ, Block.getById(blockData.getCombinedId()));
        chunkSection.setData(blockX, blockY, blockZ, blockData.getData());

        if (oldBlock.isTileEntity()) {
            chunk.world.p(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        }
    }

    @Override
    public void refreshChunk(org.bukkit.Chunk bukkitChunk, List<Location> blocksList, List<Player> playerList) {
        Chunk chunk = ((CraftChunk) bukkitChunk).getHandle();
        int blocksAmount = blocksList.size();
        short[] values = new short[blocksAmount];

        Location firstLocation = null;

        int counter = 0;
        for (Location location : blocksList) {
            if (firstLocation == null)
                firstLocation = location;

            values[counter++] = (short) ((location.getBlockX() & 15) << 12 | (location.getBlockZ() & 15) << 8 | location.getBlockY());
        }

        PacketPlayOutMultiBlockChange multiBlockChange = new PacketPlayOutMultiBlockChange(blocksAmount, values, chunk);

        for (Player player : playerList)
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(multiBlockChange);
    }

    @Override
    public void refreshLight(org.bukkit.Chunk chunk) {
        ((CraftChunk) chunk).getHandle().initLighting();
    }

    @Override
    public void clearTileEntities(org.bukkit.Chunk bukkitChunk, List<Location> tileEntities) {
        Chunk chunk = ((CraftChunk) bukkitChunk).getHandle();
        //noinspection unchecked
        new HashMap<>((Map<ChunkPosition, TileEntity>) chunk.tileEntities).forEach(((chunkPosition, tileEntity) -> {
            Location location = new Location(bukkitChunk.getWorld(), chunkPosition.x, chunkPosition.y, chunkPosition.z);
            if (tileEntities.contains(location))
                chunk.tileEntities.remove(chunkPosition);
        }));
    }

    @Override
    public void sendActionBar(Player pl, String message) {
        //No action bar in 1.7
    }

    @Override
    public int getMaterialId(Material type) {
        return Block.getId(CraftMagicNumbers.getBlock(type));
    }

    @Override
    public int getMaterialData(org.bukkit.block.Block block) {
        World world = ((CraftWorld) block.getWorld()).getHandle();
        return world.getData(block.getX(), block.getY(), block.getZ());
    }

    @Override
    public int getCombinedId(org.bukkit.block.Block block) {
        return getMaterialId(block.getType()) + (getMaterialData(block) << 12);
    }

    @Override
    public Object getBlockData(int combined) {
        return null;
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
        return true;
    }

    @Override
    public Enchantment getGlowEnchant() {
        return new Enchantment(201) {
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
        };
    }

}
