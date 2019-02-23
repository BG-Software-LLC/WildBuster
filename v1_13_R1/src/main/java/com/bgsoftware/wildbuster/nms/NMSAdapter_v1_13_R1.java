package com.bgsoftware.wildbuster.nms;

import net.minecraft.server.v1_13_R1.Block;
import net.minecraft.server.v1_13_R1.BlockPosition;
import net.minecraft.server.v1_13_R1.ChatMessageType;
import net.minecraft.server.v1_13_R1.Chunk;
import net.minecraft.server.v1_13_R1.EntityHuman;
import net.minecraft.server.v1_13_R1.EntityPlayer;
import net.minecraft.server.v1_13_R1.IBlockData;
import net.minecraft.server.v1_13_R1.IChatBaseComponent;
import net.minecraft.server.v1_13_R1.PacketPlayOutChat;
import net.minecraft.server.v1_13_R1.PacketPlayOutMapChunk;
import net.minecraft.server.v1_13_R1.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import com.bgsoftware.wildbuster.api.objects.BlockData;

import java.util.List;

@SuppressWarnings("unused")
public final class NMSAdapter_v1_13_R1 implements NMSAdapter {

    @Override
    public String getVersion() {
        return "v1_13_R1";
    }

    @Override
    public void setFastBlock(Location loc, BlockData blockData) {
        World world = ((CraftWorld) loc.getWorld()).getHandle();
        Chunk chunk = world.getChunkAt(loc.getChunk().getX(), loc.getChunk().getZ());
        BlockPosition blockPosition = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        chunk.a(blockPosition, Block.getByCombinedId(blockData.getCombinedId()), false);
    }

    @Override
    public void refreshChunks(org.bukkit.World bukkitWorld, List<org.bukkit.Chunk> chunksList) {
        World world = ((CraftWorld) bukkitWorld).getHandle();
        for(org.bukkit.Chunk bukkitChunk : chunksList){
            Chunk chunk = ((CraftChunk) bukkitChunk).getHandle();
            for(EntityHuman entityHuman : world.players)
                ((EntityPlayer) entityHuman).playerConnection.sendPacket(new PacketPlayOutMapChunk(chunk,65535));
        }
    }

    @Override
    public void sendActionBar(Player pl, String message) {
        IChatBaseComponent chatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + message + "\"}");
        PacketPlayOutChat packet = new PacketPlayOutChat(chatBaseComponent, ChatMessageType.GAME_INFO);
        ((CraftPlayer) pl).getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public int getMaterialId(Material type) {
        throw new RuntimeException("You cannot run getMaterialId in 1.13.X!");
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
}
