package com.bgsoftware.wildbuster.command.commands;

import org.bukkit.command.CommandSender;
import com.bgsoftware.wildbuster.Locale;
import com.bgsoftware.wildbuster.WildBusterPlugin;
import com.bgsoftware.wildbuster.command.ICommand;

import java.util.ArrayList;
import java.util.List;

public final class CommandSave implements ICommand {

    @Override
    public String getLabel() {
        return "save";
    }

    @Override
    public String getUsage() {
        return "buster save";
    }

    @Override
    public String getPermission() {
        return "wildbuster.save";
    }

    @Override
    public String getDescription() {
        return "Save all chunk-busters to files.";
    }

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public int getMaxArgs() {
        return 1;
    }

    @Override
    public void perform(WildBusterPlugin plugin, CommandSender sender, String[] args) {
        new Thread(() -> {
            plugin.getDataHandler().saveBusters();
            Locale.SAVE_SUCCESS.send(sender);
        }).start();
    }

    @Override
    public List<String> tabComplete(WildBusterPlugin plugin, CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
