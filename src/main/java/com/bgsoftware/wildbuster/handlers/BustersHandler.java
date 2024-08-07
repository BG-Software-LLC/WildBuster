package com.bgsoftware.wildbuster.handlers;

import com.bgsoftware.wildbuster.Locale;
import com.bgsoftware.wildbuster.WildBusterPlugin;
import com.bgsoftware.wildbuster.api.events.ChunkBusterPlaceEvent;
import com.bgsoftware.wildbuster.api.handlers.BustersManager;
import com.bgsoftware.wildbuster.api.objects.BlockData;
import com.bgsoftware.wildbuster.api.objects.ChunkBuster;
import com.bgsoftware.wildbuster.api.objects.PlayerBuster;
import com.bgsoftware.wildbuster.objects.WBlockData;
import com.bgsoftware.wildbuster.objects.WChunkBuster;
import com.bgsoftware.wildbuster.objects.WPlayerBuster;
import com.bgsoftware.wildbuster.scheduler.Scheduler;
import com.bgsoftware.wildbuster.utils.blocks.ChunkPosition;
import com.bgsoftware.wildbuster.utils.items.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class BustersHandler implements BustersManager {

    private final Map<String, ChunkBuster> chunkBusters = new HashMap<>();
    private final Set<PlayerBuster> playerBusters = new HashSet<>();
    private final Map<ChunkPosition, PlayerBuster> chunksToPlayerBusters = new HashMap<>();
    private final Map<UUID, PlayerBuster> notifyBusters = new HashMap<>();
    private final WildBusterPlugin plugin;

    public BustersHandler(WildBusterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public ChunkBuster getChunkBuster(String name) {
        return chunkBusters.get(name.toLowerCase());
    }

    @Override
    public ChunkBuster getChunkBuster(ItemStack item) {
        return chunkBusters.values().stream().filter(chunkBuster -> chunkBuster.getBusterItem().isSimilar(item)).findFirst().orElse(null);
    }

    @Override
    public List<ChunkBuster> getChunkBusters() {
        return new ArrayList<>(chunkBusters.values());
    }

    @Override
    public PlayerBuster getPlayerBuster(Chunk chunk) {
        return chunksToPlayerBusters.get(ChunkPosition.of(chunk));
    }

    @Override
    public boolean isChunkBusted(Chunk chunk) {
        return getPlayerBuster(chunk) != null;
    }

    @Override
    public List<PlayerBuster> getPlayerBusters() {
        return new ArrayList<>(playerBusters);
    }

    @Override
    public List<PlayerBuster> getPlayerBusters(OfflinePlayer player) {
        return playerBusters.stream()
                .filter(buster -> buster.getUniqueID().equals(player.getUniqueId()))
                .collect(Collectors.toList());
    }

    @Override
    public ChunkBuster createChunkBuster(String name, int radius, ItemStack busterItem) {
        ChunkBuster chunkBuster = new WChunkBuster(name, radius, busterItem);
        chunkBusters.put(name.toLowerCase(), chunkBuster);
        return chunkBuster;
    }

    @Override
    public void removeChunkBusters() {
        chunkBusters.clear();
    }

    @Override
    public PlayerBuster createPlayerBuster(Player player, Location placedLocation, ChunkBuster buster) {
        PlayerBuster playerBuster = new WPlayerBuster(player, placedLocation, buster);
        playerBusters.add(playerBuster);
        playerBuster.getChunks().forEach(chunk -> chunksToPlayerBusters.put(ChunkPosition.of(chunk), playerBuster));
        plugin.getNMSAdapter().handleChunkUnload(placedLocation.getWorld(), playerBuster.getChunks(), plugin, false);
        return playerBuster;
    }

    @Override
    public PlayerBuster loadPlayerBuster(String busterName, UUID uuid, World world, boolean cancelStatus, boolean notifyStatus, int currentLevel, List<Chunk> chunksList, List<BlockData> removedBlocks) {
        PlayerBuster playerBuster = new WPlayerBuster(busterName, uuid, world, cancelStatus, notifyStatus, currentLevel, chunksList, removedBlocks, null);
        playerBusters.add(playerBuster);
        chunksList.forEach(chunk -> chunksToPlayerBusters.put(ChunkPosition.of(chunk), playerBuster));
        plugin.getNMSAdapter().handleChunkUnload(world, playerBuster.getChunks(), plugin, false);
        return playerBuster;
    }

    @Override
    public void removePlayerBuster(PlayerBuster playerBuster) {
        playerBusters.remove(playerBuster);
        notifyBusters.remove(playerBuster.getUniqueID());

        if (Scheduler.isRegionScheduler()) {
            playerBuster.getChunks().forEach(chunk -> {
                chunksToPlayerBusters.remove(ChunkPosition.of(chunk));
                Scheduler.runTask(chunk, () -> plugin.getNMSAdapter().handleChunkUnload(
                        playerBuster.getWorld(), Collections.singletonList(chunk), plugin, true));
            });
        } else {
            Scheduler.runTask(() ->
                    playerBuster.getChunks().forEach(chunk -> chunksToPlayerBusters.remove(ChunkPosition.of(chunk))));
        }
    }

    @Override
    public void setNotifyBuster(PlayerBuster buster) {
        notifyBusters.put(buster.getUniqueID(), buster);
    }

    @Override
    public PlayerBuster getNotifyBuster(UUID uuid) {
        return notifyBusters.get(uuid);
    }

    @Override
    public BlockData getBlockData(Block block) {
        return new WBlockData(block, block.getState() instanceof InventoryHolder ? (InventoryHolder) block.getState() : null);
    }

    @Override
    public void handleBusterPlacement(Player player, Location location, ChunkBuster chunkBuster) {
        ChunkBusterPlaceEvent event = new ChunkBusterPlaceEvent(location, player, chunkBuster);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled())
            return;

        //Remove the item from the inventory of the player
        if (player.getGameMode() != GameMode.CREATIVE)
            ItemUtils.removeItem(player.getInventory(), chunkBuster.getBusterItem());

        //Register the player-buster in the system
        createPlayerBuster(player, location, chunkBuster);

        Locale.PLACED_BUSTER.send(player, plugin.getSettings().timeBeforeRunning / 20);
    }
}
