package xyz.wildseries.wildbuster;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class Locale {

    private static Map<String, Locale> localeMap = new HashMap<>();

    public static final Locale ACTIONBAR_BUSTER_MESSAGE = new Locale("ACTIONBAR_BUSTER_MESSAGE");
    public static final Locale ACTIONBAR_CANCEL_MESSAGE = new Locale("ACTIONBAR_CANCEL_MESSAGE");
    public static final Locale BELOW_MINIMUM_CANCEL = new Locale("BELOW_MINIMUM_CANCEL");
    public static final Locale BUSTER_ALREADY_CANCELLED = new Locale("BUSTER_ALREADY_CANCELLED");
    public static final Locale BUSTER_FINISHED = new Locale("BUSTER_FINISHED");
    public static final Locale CANCELLED_BUSTER = new Locale("CANCELLED_BUSTER");
    public static final Locale CANCELLED_BUSTER_OTHER = new Locale("CANCELLED_BUSTER_OTHER");
    public static final Locale CHUNK_ALREADY_BUSTED = new Locale("CHUNK_ALREADY_BUSTED");
    public static final Locale COMMAND_USAGE = new Locale("COMMAND_USAGE");
    public static final Locale GIVE_SUCCESS = new Locale("GIVE_SUCCESS");
    public static final Locale HELP_COMMAND_FOOTER = new Locale("HELP_COMMAND_FOOTER");
    public static final Locale HELP_COMMAND_HEADER = new Locale("HELP_COMMAND_HEADER");
    public static final Locale HELP_COMMAND_LINE = new Locale("HELP_COMMAND_LINE");
    public static final Locale INVALID_BUSTER_LOCATION = new Locale("INVALID_BUSTER_LOCATION");
    public static final Locale INVALID_BUSTER_PLAYER = new Locale("INVALID_BUSTER_PLAYER");
    public static final Locale INVALID_BUSTER_ITEM = new Locale("INVALID_BUSTER_ITEM");
    public static final Locale INVALID_NUMBER = new Locale("INVALID_NUMBER");
    public static final Locale INVALID_PLAYER = new Locale("INVALID_PLAYER");
    public static final Locale MAX_BUSTERS_AMOUNT = new Locale("MAX_BUSTERS_AMOUNT");
    public static final Locale MUST_PLACE_IN_CLAIM = new Locale("MUST_PLACE_IN_CLAIM");
    public static final Locale NO_PERMISSION = new Locale("NO_PERMISSION");
    public static final Locale NO_PERMISSION_PLACE = new Locale("NO_PERMISSION_PLACE");
    public static final Locale PLACED_BUSTER = new Locale("PLACED_BUSTER");
    public static final Locale RELOAD_SUCCESS = new Locale("RELOAD_SUCCESS");
    public static final Locale SAVE_SUCCESS = new Locale("SAVE_SUCCESS");

    private String message;

    private Locale(String identifier){
        localeMap.put(identifier, this);
    }

    public String getMessage(Object... objects){
        if(message != null && !message.equals("")) {
            String msg = message;

            for (int i = 0; i < objects.length; i++)
                msg = msg.replace("{" + i + "}", objects[i].toString());

            return msg;
        }

        return null;
    }

    public void send(CommandSender sender, Object... objects){
        String message = getMessage(objects);
        if(message != null && sender != null)
            sender.sendMessage(message);
    }

    private void setMessage(String message){
        this.message = message;
    }

    public static void reload(){
        WildBusterPlugin.log("Loading messages started...");
        long startTime = System.currentTimeMillis();
        int messagesAmount = 0;
        File file = new File(WildBusterPlugin.getPlugin().getDataFolder(), "lang.yml");

        if(!file.exists())
            WildBusterPlugin.getPlugin().saveResource("lang.yml", false);

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        for(String identifier : localeMap.keySet()){
            localeMap.get(identifier).setMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString(identifier, "")));
            messagesAmount++;
        }

        WildBusterPlugin.log(" - Found " + messagesAmount + " messages in lang.yml.");
        WildBusterPlugin.log("Loading messages done (Took " + (System.currentTimeMillis() - startTime) + "ms)");
    }

}
