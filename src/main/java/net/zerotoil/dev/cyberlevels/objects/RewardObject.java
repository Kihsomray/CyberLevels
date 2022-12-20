package net.zerotoil.dev.cyberlevels.objects;

import me.clip.placeholderapi.PlaceholderAPI;
import net.zerotoil.dev.cyberlevels.CyberLevels;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.ArrayList;
import java.util.List;

public class RewardObject {

    private final CyberLevels main;

    private final String soundName;
    private final float volume;
    private final float pitch;

    private final List<String> commands, messages;
    private final List<Long> levels;

    public RewardObject(CyberLevels main, String rewardName) {
        this.main = main;

        soundName = rewardsYML().getString("rewards." + rewardName + ".sound.sound-effect", "");
        volume = rewardsYML().getInt("rewards." + rewardName + ".sound.volume", 1);
        pitch = rewardsYML().getInt("rewards." + rewardName + ".sound.pitch", 1);

        commands = main.langUtils().convertList(rewardsYML(), "rewards." + rewardName + ".commands");
        messages = main.langUtils().convertList(rewardsYML(), "rewards." + rewardName + ".messages");
        levels = new ArrayList<>();

        for (String s : main.langUtils().convertList(rewardsYML(), "rewards." + rewardName + ".levels")) {

            //parse compact level syntax ex: "1-100"
            if (s.contains("-")) {
                final String[] split = s.split("-");
                addLevel(Long.parseLong(split[0]), Long.parseLong(split[1]));
                continue;
            }

            //parse comma separated level syntax ex: "1,2,3,4" and "1,100"
            if (s.contains(",")) {
                final String[] split = s.replace(" ", "").split(",");

                if (split.length == 2) {
                    addLevel(Long.parseLong(split[0]), Long.parseLong(split[1]));
                    continue;
                }

                for (String levelString : split) {
                    addLevel(Long.parseLong(levelString));
                }

                continue;
            }

            //defaults to adding singular level ex: "1"
            addLevel(Long.parseLong(s));

        }
    }

    private void addLevel(long start, long end) {
        for (long i = start; i <= end; i++) {
            addLevel(i);
        }
    }

    private void addLevel(long level) {
        if (this.levels.contains(level)) {
            return;
        }

        this.levels.add(level);

        if (main.levelCache().levelData().get(level) != null) {
            main.levelCache().levelData().get(level).addReward(this);
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

            if (command == null || command.isEmpty()) continue;

            while (command.charAt(0) == ' ') command = command.substring(1);

            if (!command.startsWith("[") || command.toLowerCase().startsWith("[console]")) {
                command = main.langUtils().parseFormat("[console]", command);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(player, main.levelUtils().getPlaceholders(command, player, true)));
                continue;
            }

            if (command.toLowerCase().startsWith("[player]")) {
                command = main.langUtils().parseFormat("[player]", command);
                Bukkit.dispatchCommand(player, PlaceholderAPI.setPlaceholders(player, main.levelUtils().getPlaceholders(command, player, true)));
            }

        }
    }

    public void sendMessage(Player player) {
        if (messages == null) return;
        for (String message : messages) {

            if (message == null || message.equals("")) continue;
            while (message.charAt(0) == ' ') message = message.substring(1);

            message = message.replace("[global]", "");
            message = main.levelUtils().getPlaceholders(message, player, true);

            if (message.toLowerCase().startsWith("[player]")) {
                if (isSuppressed(player, "player")) continue;
                main.langUtils().typeMessage(player, main.langUtils().parseFormat("[player]", message));
            } else {
                if (isSuppressed(player, "global")) continue;
                String result = message;
                Bukkit.getOnlinePlayers().forEach(p -> main.langUtils().typeMessage(p, result));
            }
        }
    }

    private boolean isSuppressed(Player player, String type) {
        if (!player.hasPermission("CyberLevels.suppress.levelup." + type)) return false;
        boolean skip = false;
        for (PermissionAttachmentInfo perm : player.getEffectivePermissions()) {
            if (!perm.getPermission().toLowerCase().startsWith("cyberlevels.suppress")) continue;
            if (perm.getValue() && (perm.getPermission().equalsIgnoreCase("CyberLevels.suppress.levelup." + type) ||
                    perm.getPermission().equalsIgnoreCase("CyberLevels.suppress.levelup.*") ||
                    perm.getPermission().equalsIgnoreCase("CyberLevels.suppress.*"))) skip = true;
        }
        return skip;
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
