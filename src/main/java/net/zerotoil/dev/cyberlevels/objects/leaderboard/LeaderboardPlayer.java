package net.zerotoil.dev.cyberlevels.objects.leaderboard;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LeaderboardPlayer implements Comparable {

    private final CyberLevels main;
    private final String uuid;
    private Player player;
    private long level;
    private double exp;

    public LeaderboardPlayer(CyberLevels main, String uuid, long level, double exp) {
        this.main = main;
        this.uuid = uuid;
        this.level = level;
        this.exp = exp;
    }

    public OfflinePlayer getPlayer() {
        if (uuid == null) return null;
        return Bukkit.getOfflinePlayer(UUID.fromString(uuid));
    }

    public int compareTo(Object other) {
        LeaderboardPlayer otherPlayer = (LeaderboardPlayer) other;
        if (this.level > otherPlayer.level) return 1;
        else if (this.level < otherPlayer.level) return -1;
        else return Double.compare(this.exp, otherPlayer.exp);
    }


    public long getLevel() {
        OfflinePlayer player = getPlayer();
        if (player != null && player.isOnline()) return main.levelCache().playerLevels().get((Player) player).getLevel();
        return level;
    }
    public double getExp() {
        OfflinePlayer player = getPlayer();
        if (player != null && player.isOnline()) return main.levelCache().playerLevels().get((Player) player).getExp();
        return exp;
    }

}
