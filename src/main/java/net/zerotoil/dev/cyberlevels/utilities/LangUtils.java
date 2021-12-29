package net.zerotoil.dev.cyberlevels.utilities;

import me.clip.placeholderapi.PlaceholderAPI;
import net.zerotoil.dev.cyberlevels.CyberLevels;
import net.zerotoil.dev.cyberlevels.terminals.ActionBar;
import net.zerotoil.dev.cyberlevels.terminals.Title;
import net.zerotoil.dev.iridiumapi.IridiumAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class LangUtils {

    private final ActionBar actionBar;
    private final Title title;


    private final PAPI papi;

    // Initializer for PAPI
    public interface PAPI {
        String parsePAPI(Player player, String message);
    }

    public LangUtils(CyberLevels main) {
        papi =  (p, line) ->
                Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null ? line :
                (p != null ? PlaceholderAPI.setPlaceholders(p, line) : line);
        actionBar = new ActionBar(main);
        title = new Title(main);
    }

    // converts message to list
    public List<String> convertList(Configuration config, String path) {
        return  !config.isList(path) ?
                Collections.singletonList(config.getString(path)) :
                config.getStringList(path);
    }

    public String parsePAPI(Player player, String message) {
        return papi.parsePAPI(player, message);
    }

    public String parse(Player player, String message) {
        return IridiumAPI.process(parsePAPI(player, message));
    }

    public void sendCentered(Player player, String message) {
        message = parse(player, message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') previousCode = true;
            else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            } else {
                FontInfo dFI = FontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ?
                        dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = 154 - halvedMessageSize;
        int spaceLength = FontInfo.SPACE.getLength() + 1;
        int compensated = 0;

        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }

        player.sendMessage(sb + message);
    }

    public void sendMixed(Player player, String message) {
        if (player == null) Bukkit.getLogger().info(ChatColor.translateAlternateColorCodes('&', message));
        else if (!message.startsWith("[C]")) player.sendMessage(parse(player, message));
        else sendCentered(player, message.replace("[C]", ""));
    }

    public void actionBar(Player player, String message) {
        actionBar.getMethod().send(player, message);
    }

    private boolean checkInts(String[] array) {
        if (array == null) return false;
        for (String integer : array)
            if (!integer.matches("-?\\d+")) return false;
        return true;
    }

    private int[] intArray(String[] array) {
        int[] ints = new int[array.length];
        for (int i = 0; i < array.length; i++)
            ints[i] = Integer.parseInt(array[i]);
        return ints;
    }

    public void title(Player player, String[] message, String[] times) {
        if (message.length == 0 || message.length > 2) return;
        String subtitle = message.length == 1 ? "" : message[1];
        int[] i = checkInts(times) ? intArray(times) : new int[]{10, 50, 10};
        title.getMethod().send(player, message[0], subtitle, i[0], i[1], i[2]);
    }
}
