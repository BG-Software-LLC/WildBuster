package xyz.wildseries.wildbuster.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import xyz.wildseries.wildbuster.Locale;
import xyz.wildseries.wildbuster.WildBusterPlugin;
import xyz.wildseries.wildbuster.command.commands.CommandCancel;
import xyz.wildseries.wildbuster.command.commands.CommandGive;
import xyz.wildseries.wildbuster.command.commands.CommandReload;
import xyz.wildseries.wildbuster.command.commands.CommandSave;

import java.util.ArrayList;
import java.util.List;

public final class CommandsHandler implements CommandExecutor, TabCompleter {

    private List<ICommand> subCommands = new ArrayList<>();
    private WildBusterPlugin plugin;

    public CommandsHandler(WildBusterPlugin plugin){
        this.plugin = plugin;
        subCommands.add(new CommandGive());
        subCommands.add(new CommandCancel());
        subCommands.add(new CommandReload());
        subCommands.add(new CommandSave());
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if(args.length > 0){
            for(ICommand subCommand : subCommands) {
                if (subCommand.getLabel().equalsIgnoreCase(args[0])){
                    if(subCommand.getPermission() != null && !sender.hasPermission(subCommand.getPermission())){
                        Locale.NO_PERMISSION.send(sender);
                        return false;
                    }
                    if(args.length < subCommand.getMinArgs() || args.length > subCommand.getMaxArgs()){
                        Locale.COMMAND_USAGE.send(sender, subCommand.getUsage());
                        return false;
                    }
                    subCommand.perform(plugin, sender, args);
                    return true;
                }
            }
        }


        Locale.HELP_COMMAND_HEADER.send(sender);
        for(ICommand subCommand : subCommands)
            if(subCommand.getPermission() == null || sender.hasPermission(subCommand.getPermission()))
                Locale.HELP_COMMAND_LINE.send(sender, subCommand.getUsage(), subCommand.getDescription());
        Locale.HELP_COMMAND_FOOTER.send(sender);

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if(args.length > 0){
            for(ICommand subCommand : subCommands) {
                if (subCommand.getLabel().equalsIgnoreCase(args[0])){
                    if(subCommand.getPermission() != null && !sender.hasPermission(subCommand.getPermission())){
                        return new ArrayList<>();
                    }
                    return subCommand.tabComplete(plugin, sender, args);
                }
            }
        }

        List<String> list = new ArrayList<>();

        for(ICommand subCommand : subCommands)
            if(subCommand.getPermission() == null || sender.hasPermission(subCommand.getPermission()))
                if(subCommand.getLabel().startsWith(args[0]))
                    list.add(subCommand.getLabel());

        return list;
    }
}
