package net.zerotoil.dev.cyberlevels.commands;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CLVTabComplete implements TabCompleter {

    private CyberLevels main;

    public CLVTabComplete(CyberLevels main) {
        this.main = main;
        main.getCommand("clv").setTabCompleter(this);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) return null;
        Player player = (Player) sender;

        List<String> args0 = new ArrayList<>();
        List<String> args0Comp = new ArrayList<>();
        String pPrefix = "CyberLevels.player.";
        String aPrefix = "CyberLevels.admin.";

        if (player.hasPermission(pPrefix + "about")) args0.add("about");
        if (player.hasPermission(pPrefix + "info")) args0.add("info");
        if (player.hasPermission(pPrefix + "help")) args0.add("help");

        if (player.hasPermission(aPrefix + "reload")) args0.add("reload");
        if (player.hasPermission(aPrefix + "list")) args0.add("list");
        if (player.hasPermission(aPrefix + "exp.add")) args0.add("addExp");
        if (player.hasPermission(aPrefix + "exp.set")) args0.add("setExp");
        if (player.hasPermission(aPrefix + "exp.remove")) args0.add("removeExp");

        if (player.hasPermission(aPrefix + "levels.add")) args0.add("addLevel");
        if (player.hasPermission(aPrefix + "levels.set")) args0.add("setLevel");
        if (player.hasPermission(aPrefix + "levels.remove")) args0.add("removeLevel");

        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], args0, args0Comp);
            Collections.sort(args0Comp);
            return args0Comp;
        }

        List<String> args1 = new ArrayList<>();
        List<String> args1Comp = new ArrayList<>();

        if (args.length == 2) {

            if (cmdReq(args[0], args0, "info") && player.hasPermission(aPrefix + "list"))
                for (Player p : Bukkit.getOnlinePlayers()) args1.add(p.getName());

            if (cmdReq(args[0], args0, "addExp") || cmdReq(args[0], args0, "setExp") || cmdReq(args[0], args0, "removeExp")) {
                args1.add("<amount>");
                args1.add("5");
                args1.add("100");
                args1.add("250");
                args1.add("1000");
            }

            if (cmdReq(args[0], args0, "addLevel") || cmdReq(args[0], args0, "setLevel") || cmdReq(args[0], args0, "removeLevel")) {
                args1.add("<amount>");
                args1.add("1");
                args1.add("2");
                args1.add("5");
            }

            StringUtil.copyPartialMatches(args[1], args1, args1Comp);

        }

        if (args.length == 3) {

            if (cmdReq(args[0], args0, "addExp") || cmdReq(args[0], args0, "setExp") || cmdReq(args[0], args0, "removeExp") ||
                    cmdReq(args[0], args0, "addLevel") || cmdReq(args[0], args0, "setLevel") || cmdReq(args[0], args0, "removeLevel")) {
                args1.add("[<player>]");
                for (Player p : Bukkit.getOnlinePlayers()) args1.add(p.getName());

            }

            StringUtil.copyPartialMatches(args[2], args1, args1Comp);

        }

        Collections.sort(args1Comp);
        return args1Comp;

    }

    private boolean cmdReq(String arg0, List<String> args0, String command) {
        return (arg0.equalsIgnoreCase(command) && args0.contains(command));
    }

}
