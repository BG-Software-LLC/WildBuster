package xyz.wildseries.wildbuster.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.wildseries.wildbuster.Locale;
import xyz.wildseries.wildbuster.WildBusterPlugin;
import xyz.wildseries.wildbuster.api.objects.ChunkBuster;
import xyz.wildseries.wildbuster.command.ICommand;
import xyz.wildseries.wildbuster.utils.ItemUtil;

import java.util.ArrayList;
import java.util.List;

public final class CommandGive implements ICommand {

    @Override
    public String getLabel() {
        return "give";
    }

    @Override
    public String getUsage() {
        return "buster give <player-name> <buster-name> [amount]";
    }

    @Override
    public String getPermission() {
        return "wildbuster.give";
    }

    @Override
    public String getDescription() {
        return "Give a chunk-buster item to a specific player.";
    }

    @Override
    public int getMinArgs() {
        return 3;
    }

    @Override
    public int getMaxArgs() {
        return 4;
    }

    @Override
    public void perform(WildBusterPlugin plugin, CommandSender sender, String[] args) {
        Player target = Bukkit.getPlayer(args[1]);

        if(target == null){
            Locale.INVALID_PLAYER.send(sender, args[1]);
            return;
        }

        ChunkBuster buster = plugin.getBustersManager().getChunkBuster(args[2]);

        if(buster == null){
            Locale.INVALID_BUSTER_ITEM.send(sender, args[2]);
            return;
        }

        if(!target.equals(sender) && !sender.hasPermission(getPermission() + ".other")){
            Locale.NO_PERMISSION.send(sender);
            return;
        }

        if(target.equals(sender) && !(sender instanceof Player)){
            sender.sendMessage(ChatColor.RED + "You must give a chunk-buster item to a valid player.");
            return;
        }

        int amount = 1;

        if(args.length == 4){
            try{
                amount = Integer.valueOf(args[3]);
            } catch (IllegalArgumentException e){
                Locale.INVALID_NUMBER.send(sender, args[3]);
                return;
            }
        }

        ItemStack item = buster.getBusterItem();
        item.setAmount(amount);

        ItemUtil.addItem(item, target.getInventory(), null);

        Locale.GIVE_SUCCESS.send(sender, amount, buster.getName(), target.getName());
    }

    @Override
    public List<String> tabComplete(WildBusterPlugin plugin, CommandSender sender, String[] args) {
        if(!sender.hasPermission(getPermission()))
            return new ArrayList<>();

        if (args.length == 3) {
            List<String> list = new ArrayList<>();
            for(ChunkBuster chunkBuster : plugin.getBustersManager().getChunkBusters())
                if(chunkBuster.getName().startsWith(args[2]))
                    list.add(chunkBuster.getName());
            return list;
        }

        if (args.length >= 4) {
            return new ArrayList<>();
        }

        return null;
    }
}
