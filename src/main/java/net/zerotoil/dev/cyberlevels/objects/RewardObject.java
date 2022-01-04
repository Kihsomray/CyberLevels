package net.zerotoil.dev.cyberlevels.objects;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RewardObject {

    private final CyberLevels main;

    private final String rewardName;
    private String soundName;
    private List<String> commands;
    private List<String> messages;
    private List<Long> levels;

    public RewardObject(CyberLevels main, String rewardName) {
        this.main = main;
        this.rewardName = rewardName;

        soundName = main.files().getConfig("rewards").getString("rewards." + rewardName + ".sound", "");
        commands = main.langUtils().convertList(main.files().getConfig("rewards"), "rewards." + rewardName + ".commands");
        messages = main.langUtils().convertList(main.files().getConfig("rewards"), "rewards." + rewardName + ".messages");
        levels = new ArrayList<>();
        for (String s : main.langUtils().convertList(main.files().getConfig("rewards"), "rewards." + rewardName + ".levels")) {
            levels.add(Long.parseLong(s));
            if (main.levelCache().levelData().get(Long.parseLong(s)) != null)
                main.levelCache().levelData().get(Long.parseLong(s)).addReward(this);
        }
    }

    public void giveReward(Player player) {
        sendCommands(player);
        sendMessage(player);
        playSound(player);
    }

    private void sendCommands(Player player) {
        if (commands == null) return;
        for (String command : commands) {

            while (command.charAt(0) == ' ') command = command.substring(1);

            if (!command.startsWith("[") || command.toLowerCase().startsWith("[console]")) {
                command = main.langUtils().parseFormat("[console]", command);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), main.levelUtils().getPlaceholders(command, player, true));
                continue;
            }

            if (command.toLowerCase().startsWith("[player]")) {
                command = main.langUtils().parseFormat("[player]", command);
                Bukkit.dispatchCommand(player, main.levelUtils().getPlaceholders(command, player, true));
            }

        }
    }

    public void sendMessage(Player player) {
        if (messages == null) return;
        for (String message : messages) {

            if (message == null || message.equals("")) continue;
            while (message.charAt(0) == ' ') message = message.substring(1);

            message = main.levelUtils().getPlaceholders(message, player, true);

            if (message.toLowerCase().startsWith("[player]"))
                main.langUtils().typeMessage(player, main.langUtils().parseFormat("[player]", message));
            else {
                String result = message;
                Bukkit.getOnlinePlayers().forEach(p -> main.langUtils().typeMessage(p, result));
            }
        }
    }

    private void playSound(Player player) {
        Sound sound;
        if (soundName.equals("")) return;

        try {
            Enum.valueOf(Sound.class, soundName);
            sound = Sound.valueOf(soundName);
        }
        catch (Exception e) {
            return;
        }

        player.playSound(player.getLocation(), sound, 1, 1);
    }
}
