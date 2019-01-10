package xyz.wildseries.wildbuster.nms;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import xyz.wildseries.wildbuster.api.objects.BlockData;

import java.util.List;

public interface NMSAdapter {

    String getVersion();

    void setFastBlock(Location loc, BlockData blockData);

    void refreshChunks(World bukkitWorld, List<Chunk> chunksList);

    void sendActionBar(Player pl, String message);

    int getMaterialId(Material type);

    int getMaterialData(Block block);

    int getCombinedId(Block block);

    Object getBlockData(int combined);

}
