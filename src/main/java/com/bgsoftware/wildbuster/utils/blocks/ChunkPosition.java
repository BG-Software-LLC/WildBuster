package com.bgsoftware.wildbuster.utils.blocks;

import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.Objects;

public final class ChunkPosition {

    private String world;
    private int x, z;

    private ChunkPosition(String world, int x, int z){
        this.world = world;
        this.x = x;
        this.z = z;
    }

    public String getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChunkPosition that = (ChunkPosition) o;
        return x == that.x &&
                z == that.z &&
                Objects.equals(world, that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, x, z);
    }

    public static ChunkPosition of(Chunk chunk){
        return new ChunkPosition(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    public static ChunkPosition of(Location location){
        return new ChunkPosition(location.getWorld().getName(), location.getBlockX() >> 4, location.getBlockZ() >> 4);
    }

}
