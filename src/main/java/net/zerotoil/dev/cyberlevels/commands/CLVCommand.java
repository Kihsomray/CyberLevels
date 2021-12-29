package net.zerotoil.dev.cyberlevels.commands;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CLVCommand implements CommandExecutor {

    private final CyberLevels main;

    public CLVCommand(CyberLevels main) {
        this.main = main;
        main.getCommand("clv").setExecutor(this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (args[0].equalsIgnoreCase("addEXP")) {
            main.levelCache().playerLevels().get((Player) sender).addExp(Double.parseDouble(args[1]));
        }

        if (args[0].equalsIgnoreCase("addLevel")) {
            main.levelCache().playerLevels().get((Player) sender).addLevel(Long.parseLong(args[1]));
        }

        if (args[0].equalsIgnoreCase("setEXP")) {
            main.levelCache().playerLevels().get((Player) sender).setExp(Double.parseDouble(args[1]), true);
        }

        if (args[0].equalsIgnoreCase("setLevel")) {
            main.levelCache().playerLevels().get((Player) sender).setLevel(Long.parseLong(args[1]));
        }

        if (args[0].equalsIgnoreCase("removeEXP")) {
            main.levelCache().playerLevels().get((Player) sender).removeExp(Double.parseDouble(args[1]));
        }

        if (args[0].equalsIgnoreCase("removeLevel")) {
            main.levelCache().playerLevels().get((Player) sender).removeLevel(Long.parseLong(args[1]));
        }

        if (args[0].equalsIgnoreCase("info")) {
            sender.sendMessage(main.levelCache().playerLevels().get((Player) sender).toString());
        }

        return true;

    }

}
