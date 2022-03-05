package com.bgsoftware.wildbuster.nms;

import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ChunkProviderServer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkSection;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.lighting.LightEngine;

import java.util.Map;

public final class NMSMappings_v1_18_R2 {

    private NMSMappings_v1_18_R2() {

    }

    public static Chunk getChunkAtWorldCoords(World world, BlockPosition blockPosition) {
        return world.l(blockPosition);
    }

    public static int getX(BaseBlockPosition baseBlockPosition) {
        return baseBlockPosition.u();
    }

    public static int getY(BaseBlockPosition baseBlockPosition) {
        return baseBlockPosition.v();
    }

    public static int getZ(BaseBlockPosition baseBlockPosition) {
        return baseBlockPosition.w();
    }

    public static int getSectionIndex(LevelHeightAccessor levelHeightAccessor, int y) {
        return levelHeightAccessor.e(y);
    }

    public static ChunkSection[] getSections(IChunkAccess chunkAccess) {
        return chunkAccess.d();
    }

    public static void setType(ChunkSection chunkSection, int x, int y, int z, IBlockData state, boolean lock) {
        chunkSection.a(x, y, z, state, lock);
    }

    public static IBlockData getByCombinedId(int combinedId) {
        return Block.a(combinedId);
    }

    public static WorldServer getWorld(Chunk chunk) {
        return chunk.q;
    }

    public static ChunkProviderServer getChunkProvider(WorldServer worldServer) {
        return worldServer.k();
    }

    public static LightEngine getLightEngine(WorldServer worldServer) {
        return worldServer.l_();
    }

    public static void flagDirty(ChunkProviderServer chunkProviderServer, BlockPosition blockPosition) {
        chunkProviderServer.a(blockPosition);
    }

    public static ChunkCoordIntPair getPos(IChunkAccess chunk) {
        return chunk.f();
    }

    public static void sendPacket(PlayerConnection playerConnection, Packet<?> packet) {
        playerConnection.a(packet);
    }

    public static Map<BlockPosition, TileEntity> getTileEntities(IChunkAccess chunkAccess) {
        return chunkAccess.i;
    }

    public static IBlockData getType(World world, BlockPosition blockPosition) {
        return world.a_(blockPosition);
    }

    public static int getCombinedId(IBlockData blockData) {
        return Block.i(blockData);
    }

    public static NBTTagCompound getOrCreateTag(ItemStack itemStack) {
        return itemStack.t();
    }

    public static boolean hasKey(NBTTagCompound nbtTagCompound, String key) {
        return nbtTagCompound.e(key);
    }

    public static NBTTagCompound getCompound(NBTTagCompound nbtTagCompound, String key) {
        return nbtTagCompound.p(key);
    }

    public static void setString(NBTTagCompound nbtTagCompound, String key, String value) {
        nbtTagCompound.a(key, value);
    }

    public static void set(NBTTagCompound nbtTagCompound, String key, NBTBase nbtBase) {
        nbtTagCompound.a(key, nbtBase);
    }

    public static IBlockData getBlockData(Block block) {
        return block.n();
    }

}
