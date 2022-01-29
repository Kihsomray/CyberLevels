package net.zerotoil.dev.cyberlevels.addons;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.zerotoil.dev.cyberlevels.CyberLevels;
import net.zerotoil.dev.cyberlevels.objects.leaderboard.LeaderboardPlayer;
import net.zerotoil.dev.cyberlevels.objects.levels.LevelObject;
import net.zerotoil.dev.iridiumapi.IridiumAPI;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPI extends PlaceholderExpansion {

    private final CyberLevels main;

    public PlaceholderAPI(CyberLevels main) {
        this.main = main;
    }

    @Override
    public @NotNull String getAuthor() {
        return main.getAuthors();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "clv";
    }

    @Override
    public @NotNull String getVersion() {
        return main.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {
        if (!player.isOnline()) return null;

        if (identifier.equalsIgnoreCase("level_maximum"))
            return main.levelCache().maxLevel() + "";

        if (identifier.equalsIgnoreCase("level_minimum"))
            return main.levelCache().startLevel() + "";

        if (identifier.equalsIgnoreCase("exp_minimum"))
            return main.levelCache().startLevel() + "";

        if (identifier.startsWith("leaderboard_displayname_"))
            return getLeaderboard(player, "displayname", identifier.substring(24));

        if (identifier.startsWith("leaderboard_name_"))
            return getLeaderboard(player, "name", identifier.substring(17));

        if (identifier.startsWith("leaderboard_level_"))
            return getLeaderboard(player, "level", identifier.substring(18));

        if (identifier.startsWith("leaderboard_exp_"))
            return getLeaderboard(player, "exp", identifier.substring(16));


        LevelObject playerLevel = main.levelCache().playerLevels().get((Player) player);
        if (playerLevel == null) return null;

        if (identifier.equalsIgnoreCase("player_level"))
            return playerLevel.getLevel() + "";

        if (identifier.equalsIgnoreCase("player_level_next"))
            return Math.min(playerLevel.getLevel() + 1, main.levelCache().maxLevel()) + "";

        if (identifier.equalsIgnoreCase("player_exp"))
            return main.levelUtils().roundDecimal(playerLevel.getExp()) + "";

        if (identifier.equalsIgnoreCase("player_exp_required"))
            return main.levelUtils().roundDecimal(playerLevel.nextExpRequirement()) + "";

        if (identifier.equalsIgnoreCase("player_exp_remaining"))
            return main.levelUtils().roundDecimal(playerLevel.nextExpRequirement() -
                    playerLevel.getExp()) + "";

        if (identifier.equalsIgnoreCase("player_exp_progress_bar"))
            return IridiumAPI.process(main.levelUtils().progressBar(playerLevel.getExp(),
                    playerLevel.nextExpRequirement()));

        if (identifier.equalsIgnoreCase("player_exp_percent"))
            return main.levelUtils().getPercent(playerLevel.getExp(),
                    playerLevel.nextExpRequirement());

        return null;
    }


    private String getLeaderboard(OfflinePlayer player, String type, String position) {
        if (!main.levelCache().isLeaderboardEnabled()) return null;
        int place;
        try {
            place = Integer.parseInt(position);
        } catch (Exception e) {
            return null;
        }
        if (place > 10 || place < 1) return null;
        LeaderboardPlayer lPlayer = main.levelCache().getLeaderboard().getTopPlayer(place);
        if (lPlayer == null) return ChatColor.translateAlternateColorCodes('&', main.files().getConfig("lang")
                .getString("leaderboard-placeholders.loading-" + type.replace("display", ""), "&c-"));

        String value = main.files().getConfig("lang").getString("leaderboard-placeholders.no-player-" + type.replace("display", ""), "&c-");
        if (lPlayer.getPlayer() != null) {
            if (type.equalsIgnoreCase("name")) value = lPlayer.getPlayer().getName();
            else if (type.equalsIgnoreCase("displayname")) {
                try {
                    value = lPlayer.getPlayer().getPlayer().getDisplayName();
                } catch (Exception e) {
                    value = lPlayer.getPlayer().getName();
                }
            }
            else if (type.equalsIgnoreCase("level")) value = lPlayer.getLevel() + "";
            else if (type.equalsIgnoreCase("exp")) value = main.levelUtils().roundStringDecimal(lPlayer.getExp());
        }
        if (!(player instanceof Player)) return ChatColor.translateAlternateColorCodes('&', value);
        return main.langUtils().parse((Player) player, value);
    }

}
