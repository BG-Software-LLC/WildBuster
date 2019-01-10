package xyz.wildseries.wildbuster.nms;

import net.minecraft.server.v1_7_R3.Block;
import net.minecraft.server.v1_7_R3.Chunk;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.PacketPlayOutMapChunk;
import net.minecraft.server.v1_7_R3.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import xyz.wildseries.wildbuster.api.objects.BlockData;

import java.util.List;

@SuppressWarnings("unused")
public final class NMSAdapter_v1_7_R3 implements NMSAdapter {

    @Override
    public String getVersion() {
        return "v1_7_R3";
    }

    @Override
    public void setFastBlock(Location loc, BlockData blockData) {
        World world = ((CraftWorld) loc.getWorld()).getHandle();
        Chunk chunk = world.getChunkAt(loc.getChunk().getX(), loc.getChunk().getZ());
        chunk.a(loc.getBlockX() & 0x0f, loc.getBlockY(), loc.getBlockZ() & 0x0f, Block.e(blockData.getTypeId()), blockData.getData());
    }


    @Override
    public void refreshChunks(org.bukkit.World bukkitWorld, List<org.bukkit.Chunk> chunksList) {
        World world = ((CraftWorld) bukkitWorld).getHandle();
        for(org.bukkit.Chunk bukkitChunk : chunksList){
            Chunk chunk = ((CraftChunk) bukkitChunk).getHandle();
            for(Object object : world.players)
                ((EntityPlayer) object).playerConnection.sendPacket(new PacketPlayOutMapChunk(chunk, true, 65535));
        }
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
}
