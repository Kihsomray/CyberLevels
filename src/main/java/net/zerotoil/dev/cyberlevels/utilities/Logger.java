package net.zerotoil.dev.cyberlevels.utilities;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import net.zerotoil.dev.iridiumapi.IridiumAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class Logger {

    private final CyberLevels main;

    public Logger(CyberLevels main) {
        this.main = main;
    }

    private String parseColor(String line) {
        String isBukkit = main.serverFork().split(" ")[0];
        return (main.serverVersion() >= 12 && !isBukkit.matches("(?i)Spigot")) ?
                IridiumAPI.process(line) : IridiumAPI.stripAll(line);
    }

    public void playerLog(Player player, String... lines) {
        Arrays.asList(lines).forEach(s -> player.sendMessage(IridiumAPI.process(s)));
    }

    public void rawLog(String... lines) {
        Arrays.asList(lines).forEach(s -> main.getServer().getLogger().info(parseColor(s)));
    }

    public void log(CommandSender sender, String... lines) {
        Arrays.asList(lines).forEach(s -> {
            if (sender instanceof Player) playerLog((Player) sender, s);
            main.getLogger().info(parseColor(s));
        });
    }

    public void log(String... lines) { log(null, lines); }
}
