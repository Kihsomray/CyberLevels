package net.zerotoil.dev.cyberlevels.objects;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RewardObject {

    private final CyberLevels main;

    private String soundName;
    private float volume;
    private float pitch;

    private List<String> commands;
    private List<String> messages;
    private List<Long> levels;

    public RewardObject(CyberLevels main, String rewardName) {
        this.main = main;

        soundName = rewardsYML().getString("rewards." + rewardName + ".sound.sound-effect", "");
        volume = rewardsYML().getInt("rewards." + rewardName + ".sound.volume", 1);
        pitch = rewardsYML().getInt("rewards." + rewardName + ".sound.pitch", 1);

        commands = main.langUtils().convertList(rewardsYML(), "rewards." + rewardName + ".commands");
        messages = main.langUtils().convertList(rewardsYML(), "rewards." + rewardName + ".messages");
        levels = new ArrayList<>();

        List<String> tempList = main.langUtils().convertList(rewardsYML(), "rewards." + rewardName + ".levels");

        if (tempList.size() == 1 && tempList.get(0).contains(",")) {
            String start = tempList.get(0).split(",")[0].replace(" ", "");
            String end = tempList.get(0).split(",")[1].replace(" ", "");

            System.out.println(start + ", " + end);

            for (long i = Long.parseLong(start); i <= Long.parseLong(end); i++) {
                levels.add(i);
                if (main.levelCache().levelData().get(i) != null)
                    main.levelCache().levelData().get(i).addReward(this);
            }

        } else {
            for (String s : main.langUtils().convertList(rewardsYML(), "rewards." + rewardName + ".levels")) {
                levels.add(Long.parseLong(s));
                if (main.levelCache().levelData().get(Long.parseLong(s)) != null)
                    main.levelCache().levelData().get(Long.parseLong(s)).addReward(this);
            }
        }
    }

    private Configuration rewardsYML() {
        return main.files().getConfig("rewards");
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

        player.playSound(player.getLocation(), sound, volume, pitch);
    }
}
