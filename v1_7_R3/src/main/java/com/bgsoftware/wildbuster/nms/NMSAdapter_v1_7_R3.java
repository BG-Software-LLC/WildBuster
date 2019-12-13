package com.bgsoftware.wildbuster.nms;

import com.bgsoftware.wildbuster.api.objects.BlockData;
import net.minecraft.server.v1_7_R3.Block;
import net.minecraft.server.v1_7_R3.Chunk;
import net.minecraft.server.v1_7_R3.ChunkPosition;
import net.minecraft.server.v1_7_R3.ChunkSection;
import net.minecraft.server.v1_7_R3.ItemStack;
import net.minecraft.server.v1_7_R3.NBTTagCompound;
import net.minecraft.server.v1_7_R3.NBTTagList;
import net.minecraft.server.v1_7_R3.PacketPlayOutMapChunk;
import net.minecraft.server.v1_7_R3.TileEntity;
import net.minecraft.server.v1_7_R3.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_7_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
public final class NMSAdapter_v1_7_R3 implements NMSAdapter {

    @Override
    public String getVersion() {
        return "v1_7_R3";
    }

    @Override
    public void setFastBlock(Location location, BlockData blockData) {
        Chunk chunk = ((CraftWorld) location.getWorld()).getHandle().getChunkAt(location.getBlockX() >> 4, location.getBlockZ() >> 4);
        int indexY = location.getBlockY() >> 4;
        ChunkSection chunkSection = chunk.i()[indexY];

        if(chunkSection == null)
            chunkSection = chunk.i()[indexY] = new ChunkSection(indexY << 4, !chunk.world.worldProvider.g);

        int blockX = location.getBlockX() & 15, blockY = location.getBlockY() & 15, blockZ = location.getBlockZ() & 15;

        chunkSection.setTypeId(blockX, blockY, blockZ, Block.e(blockData.getCombinedId()));
        chunkSection.setData(blockX, blockY, blockZ, blockData.getData());
    }

    @Override
    public void refreshChunk(List<Player> playerList, org.bukkit.Chunk bukkitChunk) {
        Chunk chunk = ((CraftChunk) bukkitChunk).getHandle();
        for(Player player : playerList)
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutMapChunk(chunk, true, 65535));
    }

    @Override
    public void clearTileEntities(org.bukkit.Chunk bukkitChunk, List<Location> tileEntities) {
        Chunk chunk = ((CraftChunk) bukkitChunk).getHandle();
        //noinspection unchecked
        new HashMap<>((Map<ChunkPosition, TileEntity>) chunk.tileEntities).forEach(((chunkPosition, tileEntity) -> {
            Location location = new Location(bukkitChunk.getWorld(), chunkPosition.x, chunkPosition.y, chunkPosition.z);
            if(tileEntities.contains(location))
                chunk.tileEntities.remove(chunkPosition);
        }));
    }

    @Override
    public void refreshLight(org.bukkit.Chunk chunk) {
        ((CraftChunk) chunk).getHandle().initLighting();
    }

    @Override
    public void sendActionBar(Player pl, String message) {
        //No action bar in 1.7
    }

    @Override
    public int getMaterialId(Material type) {
        return Block.b(CraftMagicNumbers.getBlock(type));
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
}
