package com.bgsoftware.wildbuster.command.commands;

import com.bgsoftware.wildbuster.Locale;
import com.bgsoftware.wildbuster.WildBusterPlugin;
import com.bgsoftware.wildbuster.api.objects.PlayerBuster;
import com.bgsoftware.wildbuster.command.ICommand;
import com.bgsoftware.wildbuster.menu.BustersCancelMenu;
import com.bgsoftware.wildbuster.menu.PlayersBustersMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public final class CommandCancel implements ICommand {

    @Override
    public String getLabel() {
        return "cancel";
    }

    @Override
    public String getUsage() {
        return "buster cancel [player-name OR world,x,z]";
    }

    @Override
    public String getPermission() {
        return "wildbuster.cancel";
    }

    @Override
    public String getDescription() {
        return "Cancel yours or others active chunk-busters.";
    }

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public int getMaxArgs() {
        return 2;
    }

    @Override
    public void perform(WildBusterPlugin plugin, CommandSender sender, String[] args) {
        Object targetObject = null;

        if(args.length == 1){
            if(!(sender instanceof Player)){
                sender.sendMessage(ChatColor.RED + "You cannot cancel CONSOLE's buster.");
                return;
            }

            if(plugin.getSettings().cancelGUI){
                if(!sender.hasPermission(getPermission() + ".other"))
                    PlayersBustersMenu.open((Player) sender, (Player) sender);
                else
                    BustersCancelMenu.open((Player) sender);
                return;
            }

            targetObject = sender;
        }

        if(args.length == 2) {
            if (args[1].contains(",") || args[1].matches("(.*),((-)?\\d),((-)?\\d)")) {
                String[] chunkSections = args[1].split(",");

                World world = Bukkit.getWorld(chunkSections[0]);

                if (world == null) {
                    Locale.INVALID_BUSTER_LOCATION.send(sender, args[1]);
                    return;
                }

                String invalidNumber = "";
                int chunkX, chunkZ;

                try {
                    invalidNumber = chunkSections[1];
                    chunkX = Integer.parseInt(chunkSections[1]);
                    invalidNumber = chunkSections[2];
                    chunkZ = Integer.parseInt(chunkSections[2]);
                } catch (NumberFormatException ex) {
                    Locale.INVALID_NUMBER.send(sender, invalidNumber);
                    return;
                } catch(ArrayIndexOutOfBoundsException ex){
                    Locale.COMMAND_USAGE.send(sender, getUsage());
                    return;
                }

                targetObject = world.getChunkAt(chunkX, chunkZ);
            }
            else{
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);

                //We want to cancel all the active busters
                if(args[1].equals("*") && sender.hasPermission(getPermission() + ".other")){
                    for(PlayerBuster playerBuster : plugin.getBustersManager().getPlayerBusters())
                        playerBuster.performCancel(sender);
                    return;
                }

                if (offlinePlayer == null) {
                    Locale.INVALID_PLAYER.send(sender, args[1]);
                    return;
                }

                targetObject = offlinePlayer;
            }
        }

        PlayerBuster playerBuster;

        if(targetObject instanceof OfflinePlayer){
            playerBuster = plugin.getBustersManager().getNotifyBuster(((OfflinePlayer) targetObject).getUniqueId());

            if(playerBuster == null){
                Locale.INVALID_BUSTER_PLAYER.send(sender, ((OfflinePlayer) targetObject).getName());
                return;
            }
        }
        else{
            playerBuster = plugin.getBustersManager().getPlayerBuster((Chunk) targetObject);

            if(playerBuster == null){
                Locale.INVALID_BUSTER_LOCATION.send(sender, args[1]);
                return;
            }
        }

        playerBuster.performCancel(sender);
    }

    @Override
    public List<String> tabComplete(WildBusterPlugin plugin, CommandSender sender, String[] args) {
        if (sender.hasPermission(getPermission()) && args.length == 2) {
            List<String> list = new ArrayList<>();
            if(!sender.hasPermission(getPermission() + ".other")) {
                list.add(sender.getName());
            }else return null;
            return list;
        }

        return new ArrayList<>();
    }
}
