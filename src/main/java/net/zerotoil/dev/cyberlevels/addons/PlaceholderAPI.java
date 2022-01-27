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

        if (identifier.startsWith("leaderboard_name_")) {
            if (!main.levelCache().isLeaderboardEnabled()) return null;
            int place = Integer.parseInt(identifier.substring(17));
            if (place > 10 || place < 1) return null;
            LeaderboardPlayer lPlayer = main.levelCache().getLeaderboard().getTopPlayer(place);
            if (lPlayer == null) return ChatColor.translateAlternateColorCodes('&', main.files().getConfig("lang")
                    .getString("leaderboard-placeholders.loading-name", "&6Loading..."));

            String playerName = main.files().getConfig("lang").getString("leaderboard-placeholders.no-player-name", "&c-");
            if (lPlayer.getPlayer() != null) playerName = lPlayer.getPlayer().getName();
            if (!(player instanceof Player)) return ChatColor.translateAlternateColorCodes('&', playerName);
            return main.langUtils().parse((Player) player, playerName);
        }

        if (identifier.startsWith("leaderboard_level_")) {
            if (!main.levelCache().isLeaderboardEnabled()) return null;
            int place = Integer.parseInt(identifier.substring(18));
            if (place > 10 || place < 1) return null;
            LeaderboardPlayer lPlayer = main.levelCache().getLeaderboard().getTopPlayer(place);
            if (lPlayer == null) return ChatColor.translateAlternateColorCodes('&', main.files().getConfig("lang")
                    .getString("leaderboard-placeholders.loading-level", "&6-"));

            String playerLevel = main.files().getConfig("lang").getString("leaderboard-placeholders.no-player-level", "&c-");
            if (lPlayer.getPlayer() != null) playerLevel = lPlayer.getLevel() + "";
            if (!(player instanceof Player)) return ChatColor.translateAlternateColorCodes('&', playerLevel);
            return main.langUtils().parse((Player) player, playerLevel);
        }

        if (identifier.startsWith("leaderboard_exp_")) {
            if (!main.levelCache().isLeaderboardEnabled()) return null;
            int place = Integer.parseInt(identifier.substring(16));
            if (place > 10 || place < 1) return null;
            LeaderboardPlayer lPlayer = main.levelCache().getLeaderboard().getTopPlayer(place);
            if (lPlayer == null) return ChatColor.translateAlternateColorCodes('&', main.files().getConfig("lang")
                    .getString("leaderboard-placeholders.loading-exp", "&6-"));

            String playerEXP = main.files().getConfig("lang").getString("leaderboard-placeholders.no-player-level", "&c-");
            if (lPlayer.getPlayer() != null) playerEXP = main.levelUtils().roundDecimal(lPlayer.getExp()) + "";

            if (!(player instanceof Player)) return ChatColor.translateAlternateColorCodes('&', playerEXP);
            return main.langUtils().parse((Player) player, playerEXP);
        }

        LevelObject playerLevel = main.levelCache().playerLevels().get(player);
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






















}
