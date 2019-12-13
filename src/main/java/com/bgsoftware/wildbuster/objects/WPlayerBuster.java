package com.bgsoftware.wildbuster.objects;

import com.bgsoftware.wildbuster.Locale;
import com.bgsoftware.wildbuster.WildBusterPlugin;
import com.bgsoftware.wildbuster.api.events.ChunkBusterCancelEvent;
import com.bgsoftware.wildbuster.api.events.ChunkBusterFinishEvent;
import com.bgsoftware.wildbuster.api.objects.BlockData;
import com.bgsoftware.wildbuster.api.objects.ChunkBuster;
import com.bgsoftware.wildbuster.api.objects.PlayerBuster;
import com.bgsoftware.wildbuster.utils.PlayerUtils;
import com.bgsoftware.wildbuster.utils.TimerUtils;
import com.bgsoftware.wildbuster.utils.blocks.MultiBlockTask;
import com.bgsoftware.wildbuster.utils.items.ItemUtils;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.UUID;
import java.util.stream.Collectors;

public final class WPlayerBuster implements PlayerBuster {

    private static WildBusterPlugin plugin = WildBusterPlugin.getPlugin();

    private final String busterName;
    private final UUID uuid;
    private final World world;
    private final Chunk originalChunk;
    private final List<Chunk> chunks;
    private final Set<Location> tileEntities;

    private List<BlockData> removedBlocks;

    private Timer timer = null;
    private int currentLevel;
    private boolean cancelStatus;

    private Map<Location, InventoryHolder> blockStateMap = new HashMap<>();

    public WPlayerBuster(Player player, Location placedLocation, ChunkBuster buster){
        this(buster.getName(), player.getUniqueId(), placedLocation.getWorld(), false, true, plugin.getSettings().startingLevel,
                getChunks(player, placedLocation.getChunk(), buster.getRadius()), new ArrayList<>(), placedLocation.getChunk());
    }

    public WPlayerBuster(String busterName, UUID uuid, World world, boolean cancelStatus, boolean notifyStatus, int currentLevel,
                         List<Chunk> chunksList, List<BlockData> removedBlocks, Chunk originalChunk){
        this.busterName = busterName;
        this.uuid = uuid;
        this.world = world;
        this.cancelStatus = cancelStatus;
        this.currentLevel = currentLevel;
        this.chunks = Collections.unmodifiableList(chunksList);
        this.originalChunk = originalChunk == null ? getOriginalChunk() : originalChunk;
        this.tileEntities = loadTileEntities();
        this.removedBlocks = new ArrayList<>(removedBlocks);

        chunksList.forEach(chunk ->
            Arrays.stream(chunk.getTileEntities()).filter(blockState -> blockState instanceof InventoryHolder)
                    .forEach(blockState -> blockStateMap.put(blockState.getLocation(), (InventoryHolder) blockState)));

        if(notifyStatus)
            setNotify();

        if(cancelStatus){
            runCancelTask();
        }

        else{
            Bukkit.getScheduler().runTaskLater(plugin, this::runRegularTask, plugin.getSettings().timeBeforeRunning);
        }
    }

    private static List<Chunk> getChunks(Player player, Chunk origin, int radius) {
        List<Chunk> chunks = new ArrayList<>();

        origin = origin.getWorld().getChunkAt(origin.getX() - (radius / 2), origin.getZ() - (radius / 2));

        for (int x = 0; x < radius; x++) {
            for (int z = 0; z < radius; z++) {
                Chunk ch = origin.getWorld().getChunkAt(origin.getX() + x, origin.getZ() + z);
                if (PlayerUtils.canBustChunk(player, ch) && plugin.getBustersManager().getPlayerBuster(ch) == null)
                    chunks.add(ch);
            }
        }

        return chunks;
    }

    private Chunk getOriginalChunk(){
        int radius = (int) Math.sqrt(chunks.size());
        Chunk minimumChunk = chunks.stream().min((o1, o2) -> {
            if(o1.getX() < o2.getX() && o1.getZ() < o2.getZ())
                return -1;
            else if(o1.getX() == o2.getX() && o1.getZ() == o2.getZ())
                return 0;
            else
                return 1;
        }).orElse(chunks.get(0));

        return minimumChunk.getWorld().getChunkAt(minimumChunk.getX() + radius, minimumChunk.getZ() + radius);
    }

    private Set<Location> loadTileEntities(){
        Set<Location> tileEntities = new HashSet<>();

        chunks.forEach(chunk ->
            tileEntities.addAll(Arrays.stream(chunk.getTileEntities()).map(BlockState::getLocation).collect(Collectors.toList())));

        return tileEntities;
    }

    @Override
    public String getBusterName() {
        return busterName;
    }

    @Override
    public ChunkBuster getChunkBuster(){
        return plugin.getBustersManager().getChunkBuster(busterName);
    }

    @Override
    public UUID getUniqueID() {
        return uuid;
    }

    @Override
    public List<Chunk> getChunks() {
        return chunks;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public int getCurrentLevel() {
        return currentLevel;
    }

    @Override
    public int getTaskID() {
        return -1;
    }

    @Override
    public Timer getBusterTimer() {
        return timer;
    }

    @Override
    public List<BlockData> getRemovedBlocks() {
        return new ArrayList<>(removedBlocks);
    }

    @Override
    public boolean isCancelled() {
        return cancelStatus;
    }

    @Override
    public boolean isNotify() {
        PlayerBuster notify = plugin.getBustersManager().getNotifyBuster(uuid);
        return notify != null && notify.equals(this);
    }

    @Override
    public void setNotify() {
        plugin.getBustersManager().setNotifyBuster(this);
    }

    @Override
    public void runRegularTask() {
        if(cancelStatus)
            return;

        timer = new Timer();

        List<ChunkSnapshot> chunkSnapshots = new ArrayList<>();
        chunks.forEach(chunk -> chunkSnapshots.add(chunk.getChunkSnapshot(true, false, false)));

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if(plugin.getSettings().skipAirLevels) {
                int startingLevel = 0;

                for (ChunkSnapshot chunkSnapshot : chunkSnapshots) {
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            startingLevel = Math.max(startingLevel, chunkSnapshot.getHighestBlockYAt(x, z));
                        }
                    }
                }

                currentLevel = Math.min(startingLevel, currentLevel);
            }

            TimerUtils.runTimer(timer, () -> {
                if(cancelStatus)
                    return;

                //Counter for skipped levels
                int levelsAmount = plugin.getSettings().bustingLevelsAmount, stopLevel = plugin.getSettings().stoppingLevel;
                List<String> blockedMaterials = plugin.getSettings().blockedMaterials;

                MultiBlockTask multiBlockTask = new MultiBlockTask(plugin, Bukkit.getOfflinePlayer(uuid), this, true);

                for (int y = 0; y < levelsAmount; y++) {
                    //Making sure the buster hasn't reached the stop level
                    if (currentLevel - y >= stopLevel) {
                        for(Chunk chunk : chunks) {
                            for (int x = 0; x < 16; x++) {
                                for (int z = 0; z < 16; z++) {
                                    Block block = chunk.getBlock(x, (currentLevel - y), z);

                                    if (block.getType() == Material.AIR || blockedMaterials.contains(block.getType().name()) ||
                                            !plugin.getNMSAdapter().isInsideBorder(block.getLocation()) ||
                                            !plugin.getBlockBreakProvider().canBuild(Bukkit.getPlayer(uuid), block))
                                        continue;

                                    InventoryHolder inventoryHolder = blockStateMap.get(block.getLocation());

                                    BlockData blockData = new WBlockData(block, inventoryHolder);

                                    if (plugin.getSettings().reverseMode)
                                        removedBlocks.add(blockData);

                                    multiBlockTask.setBlock(block.getLocation(), WBlockData.AIR, tileEntities.contains(block.getLocation()));
                                }
                            }
                        }
                    }
                }

                int currentLevelToNotify = currentLevel;

                multiBlockTask.submitUpdate(() -> {
                    if (isNotify())
                        PlayerUtils.sendActionBar(Bukkit.getPlayer(uuid), Locale.ACTIONBAR_BUSTER_MESSAGE, currentLevelToNotify);
                });

                currentLevel -= levelsAmount;

                if(currentLevel < stopLevel){
                    ChunkBusterFinishEvent event = new ChunkBusterFinishEvent(this, ChunkBusterFinishEvent.FinishReason.BUSTER_FINISH);
                    Bukkit.getPluginManager().callEvent(event);
                    deleteBuster(false);
                    Locale.BUSTER_FINISHED.send(Bukkit.getPlayer(uuid));
                }
            }, plugin.getSettings().bustingInterval);

        });
    }

    @Override
    public void performCancel(CommandSender sender){
        OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);

        if(!sender.hasPermission("wildbuster.cancel.other") && !uuid.equals(((Player) sender).getUniqueId())){
            Locale.NO_PERMISSION.send(sender);
            return;
        }

        if(cancelStatus){
            Locale.BUSTER_ALREADY_CANCELLED.send(sender);
            return;
        }

        if(currentLevel < plugin.getSettings().minimumCancelLevel){
            Locale.BELOW_MINIMUM_CANCEL.send(sender);
            return;
        }

        if(sender instanceof Player) {
            ChunkBusterCancelEvent event = new ChunkBusterCancelEvent((Player) sender, this);
            Bukkit.getPluginManager().callEvent(event);

            if (event.isCancelled())
                return;
        }

        runCancelTask();

        Locale.CANCELLED_BUSTER.send(sender, target.getName());

        if(sender instanceof Player && target.isOnline() && !target.getUniqueId().equals(((Player) sender).getUniqueId()))
            Locale.CANCELLED_BUSTER_OTHER.send(target.getPlayer(), sender.getName());
    }

    @Override
    public void runCancelTask() {
        if(timer != null)
            timer.cancel();

        cancelStatus = true;

        final int levelsAmount = plugin.getSettings().bustingLevelsAmount;
        removedBlocks = Lists.reverse(removedBlocks);

        timer = new Timer();

        TimerUtils.runTimer(timer, () -> {
            MultiBlockTask multiBlockTask = new MultiBlockTask(plugin, Bukkit.getOfflinePlayer(uuid), this, false);

            for(int index = 0; index < chunks.size() * 16 * 16 * levelsAmount; index++){
                if(removedBlocks.isEmpty()) {
                    ChunkBusterFinishEvent event = new ChunkBusterFinishEvent(this, ChunkBusterFinishEvent.FinishReason.CANCEL_FINISH);
                    Bukkit.getPluginManager().callEvent(event);
                    deleteBuster(true);
                    break;
                }

                BlockData blockData = removedBlocks.get(0);
                multiBlockTask.setBlock(blockData.getLocation(), blockData, tileEntities.contains(blockData.getLocation()));

                currentLevel = blockData.getY();
                removedBlocks.remove(0);
            }

            int currentLevelToNotify = currentLevel;

            multiBlockTask.submitUpdate(() -> {
                if(isNotify())
                    PlayerUtils.sendActionBar(Bukkit.getPlayer(uuid), Locale.ACTIONBAR_CANCEL_MESSAGE, currentLevelToNotify);
            });
        }, plugin.getSettings().bustingInterval);
    }

    @Override
    public void deleteBuster(boolean giveBusterItem) {
        Player pl = Bukkit.getPlayer(uuid);
        if(giveBusterItem && pl != null)
            ItemUtils.addItem(plugin.getBustersManager().getChunkBuster(busterName).getBusterItem(), pl.getInventory(), pl.getLocation());

        List<Player> playerList = getNearbyPlayers();

        //Refreshing the chunks
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                chunks.forEach(chunk -> plugin.getNMSAdapter().refreshChunk(playerList, chunk)), plugin.getSettings().bustingInterval);

        timer.cancel();
        plugin.getBustersManager().removePlayerBuster(this);
    }

    @Override
    public List<Player> getNearbyPlayers() {
        return world.getPlayers().stream().filter(player -> PlayerUtils.isCloseEnough(player.getLocation(), originalChunk)).collect(Collectors.toList());
    }
}
