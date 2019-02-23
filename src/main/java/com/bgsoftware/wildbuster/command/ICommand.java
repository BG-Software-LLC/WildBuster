package com.bgsoftware.wildbuster.command;

import org.bukkit.command.CommandSender;
import com.bgsoftware.wildbuster.WildBusterPlugin;

import java.util.List;

public interface ICommand {

    String getLabel();

    String getUsage();

    String getPermission();

    String getDescription();

    int getMinArgs();

    int getMaxArgs();

    void perform(WildBusterPlugin plugin, CommandSender sender, String[] args);

    List<String> tabComplete(WildBusterPlugin plugin, CommandSender sender, String[] args);

}
