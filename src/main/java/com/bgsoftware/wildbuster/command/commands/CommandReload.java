package com.bgsoftware.wildbuster.command.commands;

import org.bukkit.command.CommandSender;
import com.bgsoftware.wildbuster.Locale;
import com.bgsoftware.wildbuster.WildBusterPlugin;
import com.bgsoftware.wildbuster.command.ICommand;
import com.bgsoftware.wildbuster.handlers.SettingsHandler;

import java.util.ArrayList;
import java.util.List;

public final class CommandReload implements ICommand {

    @Override
    public String getLabel() {
        return "reload";
    }

    @Override
    public String getUsage() {
        return "buster reload";
    }

    @Override
    public String getPermission() {
        return "wildbuster.reload";
    }

    @Override
    public String getDescription() {
        return "Reload the settings and the language files.";
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
            WildBusterPlugin.log("******** RELOAD START ********");
            SettingsHandler.reload();
            Locale.reload();
            WildBusterPlugin.log("******** RELOAD DONE ********");
            Locale.RELOAD_SUCCESS.send(sender);
        }).start();
    }

    @Override
    public List<String> tabComplete(WildBusterPlugin plugin, CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
