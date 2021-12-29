package net.zerotoil.dev.cyberlevels.objects;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RewardObject {

    private final CyberLevels main;
    private final String rewardName;
    private List<String> commands;
    private List<String> messages;
    private List<Long> levels;

    public RewardObject(CyberLevels main, String rewardName) {
        this.main = main;
        this.rewardName = rewardName;
        commands = main.langUtils().convertList(main.files().getConfig("rewards"), "rewards." + rewardName + ".commands");
        messages = main.langUtils().convertList(main.files().getConfig("rewards"), "rewards." + rewardName + ".messages");
        levels = new ArrayList<>();
        for (String s : main.langUtils().convertList(main.files().getConfig("rewards"), "rewards." + rewardName + ".levels")) {
            levels.add(Long.parseLong(s));
            main.levelCache().levelData().get(Long.parseLong(s)).addReward(this);
        }
    }

    public void giveReward(Player player) {

        sendCommands(player);
        sendMessage(player);




    }

    public void sendCommands(Player player) {
        if (commands == null) return;
        for (String command : commands) {

            while (command.charAt(0) == ' ') command = command.substring(1);

            if (!command.startsWith("[") || command.toLowerCase().startsWith("[console]")) {
                if (command.toLowerCase().startsWith("[console]")) command = command.substring(9);
                while (command.charAt(0) == ' ') command = command.substring(1);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), main.levelUtils().getPlaceholders(command, player, true));
                continue;
            }

            if (command.toLowerCase().startsWith("[player]")) {
                command = command.substring(8);
                while (command.charAt(0) == ' ') command = command.substring(1);
                Bukkit.dispatchCommand(player, main.levelUtils().getPlaceholders(command, player, true));
            }

        }
    }

    public void sendMessage(Player player) {

        if (messages == null) return;
        for (String message : messages) {

            player.sendMessage(main.levelUtils().getPlaceholders(ChatColor.translateAlternateColorCodes('&', message), player, true));

        }



    }

}
