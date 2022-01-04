package net.zerotoil.dev.cyberlevels.commands;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CLVCommand implements CommandExecutor {

    private final CyberLevels main;
    private List<String> consoleCmds;

    public CLVCommand(CyberLevels main) {
        this.main = main;
        main.getCommand("clv").setExecutor(this);
        consoleCmds = Arrays.asList("about", "reload", "addexp", "setexp", "removeexp", "addlevel", "setlevel", "removelevel");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player player;
        String uuid;

        // console check
        if (!(sender instanceof Player)) {
            if (!consoleCmds.contains(args[0])) {
                main.logger("&cConsole cannot use this command!");
                return true;
            }
            player = null;
            uuid = null;
        } else {
            player = (Player) sender;
            uuid = player.getUniqueId().toString();
        }

        if (args.length == 1) {

            switch (args[0].toLowerCase()) {
                case "about":

                    if (noPlayerPerm(player, "player.about")) return true;
                    main.langUtils().sendMixed(player, " &d&lCyber&f&lLevels &fv" + main.getDescription().getVersion() + " &7(&7&nhttps://bit.ly/2YSlqYq&7).");
                    main.langUtils().sendMixed(player, " &fDeveloped by &d" + main.getAuthors() + "&f.");
                    main.langUtils().sendMixed(player, " A leveling system plugin with MySQL support and custom events.");
                    return true;

                case "reload":
                    if (noPlayerPerm(player, "admin.reload")) return true;
                    main.langUtils().sendMessage(player, "reloading", true, false);

                    // unload
                    main.onDisable();

                    // load
                    main.reloadClasses();

                    main.langUtils().sendMessage(player, "reloaded", true, false);
                    return true;

                case "info":
                    if (noPlayerPerm(player, "player.info")) return true;
                    main.langUtils().sendMessage(player, "level-info", false);
                    return true;

            }
        }

        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "info":
                    Player target = getPlayer(args[1]);
                    if (target == null) {
                        main.langUtils().sendMessage(player, player, "player-offline", true, false, new String[]{"{player}"}, new String[]{args[1]});
                        return true;
                    }
                    if (noPlayerPerm(player, "admin.info")) return true;
                    main.langUtils().sendMessage(player, target, "level-info", false, true, null, null);
                    return true;
            }
        }

        if (player == null && args.length != 3) {
            main.logger("&cYou need to specify a player!");
            return true;
        }
        if (args.length == 3 || args.length == 2) {

            Player target;
            if (args.length == 3) {
                target = getPlayer(args[2]);
                if (target == null) {
                    main.langUtils().sendMessage(player, player, "player-offline", true, false, new String[]{"{player}"}, new String[]{args[2]});
                    return true;
                }
            } else target = player;

            switch (args[0].toLowerCase()) {
                case "addexp":
                    if (noPlayerPerm(player, "admin.levels.exp.add")) return true;
                    if (notDouble(player, args[1])) return true;
                    main.levelCache().playerLevels().get(target).addExp(Math.abs(Double.parseDouble(args[1])));
                    main.langUtils().sendMessage(player, target,"added-exp", true, true, new String[]{"{addedEXP}"}, new String[]{args[1]});
                    return true;

                case "setexp":
                    if (noPlayerPerm(player, "admin.levels.exp.set")) return true;
                    if (notDouble(player, args[1])) return true;
                    main.levelCache().playerLevels().get(target).setExp(Math.abs(Double.parseDouble(args[1])), true, true);
                    main.langUtils().sendMessage(player, target, "set-exp", true, true, new String[]{"{setEXP}"}, new String[]{args[1]});
                    return true;

                case "removeexp":
                    if (noPlayerPerm(player, "admin.levels.exp.remove")) return true;
                    if (notDouble(player, args[1])) return true;
                    main.levelCache().playerLevels().get(target).removeExp(Math.abs(Double.parseDouble(args[1])));
                    main.langUtils().sendMessage(player, target, "removed-exp", true, true, new String[]{"{removedEXP}"}, new String[]{args[1]});
                    return true;

                case "addlevel":
                    if (noPlayerPerm(player, "admin.levels.level.add")) return true;
                    if (notLong(player, args[1])) return true;
                    main.levelCache().playerLevels().get(target).addLevel(Math.abs(Long.parseLong(args[1])));
                    main.langUtils().sendMessage(player, target, "added-levels", true, true, new String[]{"{addedLevels}"}, new String[]{args[1]});
                    return true;

                case "setlevel":
                    if (noPlayerPerm(player, "admin.levels.level.set")) return true;
                    if (notLong(player, args[1])) return true;
                    main.levelCache().playerLevels().get(target).setLevel(Math.abs(Long.parseLong(args[1])), true);
                    main.langUtils().sendMessage(player, target, "set-level", true, true, new String[]{"{setLevel}"}, new String[]{args[1]});
                    return true;

                case "removelevel":
                    if (noPlayerPerm(player, "admin.levels.level.remove")) return true;
                    if (notLong(player, args[1])) return true;
                    main.levelCache().playerLevels().get(target).removeLevel(Math.abs(Long.parseLong(args[1])));
                    main.langUtils().sendMessage(player, target, "removed-levels", true, true, new String[]{"{removedLevels}"}, new String[]{args[1]});
                    return true;

            }

        }

        // final outcome, if command does not exist:
        if (player.hasPermission("CyberLevels.admin.help")) main.langUtils().sendHelp(player, true);
        else if (player.hasPermission("CyberLevels.player.help")) main.langUtils().sendHelp(player, false);
        else main.langUtils().sendMessage(player, "no-permission", true, false);
        return true;


    }

    private boolean noPlayerPerm(Player player, String permissionKey) {
        if (player == null) return false;
        if (!player.hasPermission("CyberLevels." + permissionKey)) {
            main.langUtils().sendMessage(player, "no-permission", true, false);
            return true;
        }
        return false;
    }

    private Player getPlayer(String player) {
        for (Player p : Bukkit.getOnlinePlayers()) if (p.getName().equalsIgnoreCase(player)) return p;
        return null;
    }

    private boolean notLong(Player player, String arg) {
        try {
            Long.parseLong(arg);
            return false;
        } catch (Exception e) {
            main.langUtils().sendMessage(player, "not-number");
            return true;
        }
    }
    private boolean notDouble(Player player, String arg) {
        try {
            Double.parseDouble(arg);
            return false;
        } catch (Exception e) {
            main.langUtils().sendMessage(player, "not-number");
            return true;
        }
    }

}
