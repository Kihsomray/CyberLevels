package net.zerotoil.dev.cyberlevels.objects.levels;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import net.zerotoil.dev.cyberlevels.objects.RewardObject;
import net.zerotoil.dev.cyberlevels.objects.leaderboard.Leaderboard;
import net.zerotoil.dev.cyberlevels.objects.leaderboard.LeaderboardPlayer;
import net.zerotoil.dev.iridiumapi.IridiumAPI;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.List;

public class LevelObject {

    private final CyberLevels main;
    private Player player;
    private Long level;
    private Double exp;


    public LevelObject(CyberLevels main, Player player) {
        this.main = main;
        level = main.levelCache().startLevel();
        exp = main.levelCache().startExp();
        this.player = player;
    }

    public void addLevel(long amount) {
        long levelCounter = level;
        long newLevel = Math.min(level + Math.max(amount, 0), main.levelCache().maxLevel());
        if (nextExpRequirement() == 0) exp = 0.0;

        if (main.levelCache().addLevelReward() && levelCounter < newLevel)
            for (long i = levelCounter + 1; i <= newLevel; i++) {
                level++;
                sendLevelReward(i);
            }
        else level = newLevel;

        levelCounter = level - levelCounter;
        if (levelCounter > 0)
            main.langUtils().sendMessage(player, player,"gained-levels", true, true, new String[]{"{gainedLevels}"}, new String[]{levelCounter + ""});
        checkLeaderboard();
    }

    public void setLevel(long amount, boolean sendMessage) {
        long levelCounter = level;
        if (amount < main.levelCache().startLevel()) exp = 0.0;
        else if (amount >= main.levelCache().maxLevel()) exp = 0.0;
        level = Math.max(Math.min(amount, main.levelCache().maxLevel()), main.levelCache().startLevel());
        levelCounter -= level;
        try {
            exp = Math.min(exp, nextExpRequirement());
        } catch (Exception e) {
            // nothing, too lazy to exclude the startup setLevel error lol
        }
        if (!sendMessage) return;
        if (levelCounter > 0) main.langUtils().sendMessage(player, player,"lost-levels", true, true, new String[]{"{lostLevels}"}, new String[]{Math.abs(levelCounter) + ""});
        else if (levelCounter < 0) main.langUtils().sendMessage(player, player,"gained-levels", true, true, new String[]{"{gainedLevels}"}, new String[]{Math.abs(levelCounter) + ""});
        checkLeaderboard();
    }

    public void removeLevel(long amount) {
        if (level - amount < main.levelCache().startLevel()) exp = 0.0;
        long levelCounter = level;
        level = Math.max(level - Math.max(amount, 0), main.levelCache().startLevel());
        levelCounter -= level;
        if (levelCounter > 0)
            main.langUtils().sendMessage(player, player,"lost-levels", true, true, new String[]{"{lostLevels}"}, new String[]{levelCounter + ""});
        checkLeaderboard();
    }

    public void addExp(double amount, boolean doMultiplier) {
        addExp(amount, 0, true, doMultiplier);
    }

    public void addExp(double amount, double difference, boolean sendMessage, boolean doMultiplier) {
        amount = Math.max(amount, 0);

        // does player have a multiplier permission?
        if (doMultiplier && main.playerUtils().hasParentPerm(player, "CyberLevels.player.multiplier.", false))
            amount *= main.playerUtils().getMultiplier(player);

        long levelCounter = 0;
        // current exp + exp increase > required exp to next level
        while (exp + amount >= nextExpRequirement()) {
            if (level.equals(main.levelCache().maxLevel())) return;
            amount = (amount - nextExpRequirement()) + exp;
            exp = 0.0;
            level++;
            levelCounter++;
            sendLevelReward();
        }
        exp += amount;
        if (sendMessage && (amount - difference) > 0)
            main.langUtils().sendMessage(player, player,"gained-exp", true, true, new String[]{"{gainedEXP}"}, new String[]{main.levelUtils().roundStringDecimal(amount - difference)});
        else if (sendMessage && (amount - difference) < 0)
            main.langUtils().sendMessage(player, player,"lost-exp", true, true, new String[]{"{lostEXP}"}, new String[]{main.levelUtils().roundStringDecimal(difference - amount)});
        if (levelCounter > 0 && sendMessage) main.langUtils().sendMessage(player, player,"gained-levels", true, true, new String[]{"{gainedLevels}"}, new String[]{levelCounter + ""});
        if (sendMessage) checkLeaderboard();
    }

    public void setExp(double amount, boolean checkLevel, boolean sendMessage) {
        amount = Math.abs(amount);
        if (checkLevel) {
            double exp = this.exp;
            this.exp = 0.0;
            addExp(amount, exp, sendMessage, false);
        } else exp = amount;
        checkLeaderboard();
    }

    public void removeExp(double amount) {
        amount = Math.max(amount, 0);
        long levelsLost = 0;
        if (amount > exp) {
            if (level.equals(main.levelCache().startLevel())) {
                exp = 0.0;
                return;
            }

            amount -= exp;
            level--;
            levelsLost++;
            exp = nextExpRequirement();

            while (amount > exp) {
                if (level.equals(main.levelCache().startLevel())) {
                    exp = Math.max(0, exp - amount);
                    amount = 0;
                } else {
                    amount -= nextExpRequirement();
                    level--;
                    levelsLost++;
                    exp = nextExpRequirement();
                }
            }
        }
        double expTemp = exp;
        exp -= amount;
        if (levelsLost > 0) main.langUtils().sendMessage(player, player,"lost-levels", true, true, new String[]{"{lostLevels}"}, new String[]{levelsLost + ""});
        else if (amount > 0) main.langUtils().sendMessage(player, player,"lost-exp", true, true, new String[]{"{lostEXP}"}, new String[]{main.levelUtils().roundStringDecimal(amount)});

        // makes sure the level doesn't go down below the start level
        level = Math.max(main.levelCache().startLevel(), level);
        exp = Math.max(0, exp);
        checkLeaderboard();
    }

    @Override
    public String toString() {
        return "level: " + level + ", exp: " + exp + ", progress: " +
                IridiumAPI.process(main.levelUtils().progressBar(exp, nextExpRequirement())) +
                " [" + (int) (100 * (exp / nextExpRequirement())) + "%]";
    }

    private void sendLevelReward(long level) {
        for (RewardObject rewardObject : main.levelCache().levelData().get(level).getRewards()) rewardObject.giveReward(player);
    }

    private void sendLevelReward() {
        for (RewardObject rewardObject : main.levelCache().levelData().get(level).getRewards()) rewardObject.giveReward(player);
    }

    public double nextExpRequirement() {
        if (main.levelCache().levelData().get(level + 1) == null) return 0.0;
        return main.levelCache().levelData().get(level + 1).getRequiredExp(player);
    }

    private void checkLeaderboard() {
        if (!main.levelCache().isLeaderboardInstantUpdate()) return;

        // checks if player is promoted
        int startFrom = main.levelCache().getLeaderboard().checkFrom(player);
        List<LeaderboardPlayer> topPlayers = main.levelCache().getLeaderboard().getTopTenPlayers();
        boolean movedUp = false;
        for (int i = startFrom; i >= 1; i--) {
            LeaderboardPlayer lbp = main.levelCache().getLeaderboard().getTopPlayer(i);
            if (lbp.getUUID() == null || lbp.getUUID().equals(player.getUniqueId().toString())) continue;
            if (this.level < lbp.getLevel()) break;
            if (this.level == lbp.getLevel() && this.exp < lbp.getExp()) break;
            LeaderboardPlayer cp = new LeaderboardPlayer(main, player.getUniqueId().toString(), level, exp);
            if (topPlayers.size() > i)
                topPlayers.set(i, topPlayers.get(i - 1)); // puts the above player down
            topPlayers.set(i - 1, cp);
            movedUp = true;
        }
        if (movedUp) return;

        // checks if player is demoted
        for (int i = startFrom; i <= 10; i++) {
            LeaderboardPlayer lbp = main.levelCache().getLeaderboard().getTopPlayer(i);
            if (lbp.getUUID() == null) break;
            if (lbp.getUUID().equals(player.getUniqueId().toString())) continue;
            if (this.level > lbp.getLevel()) break;
            if (this.level == lbp.getLevel() && this.exp > lbp.getExp()) break;
            LeaderboardPlayer cp = new LeaderboardPlayer(main, player.getUniqueId().toString(), level, exp);
            topPlayers.set(i - 2, topPlayers.get(i - 1));
            if (topPlayers.size() > i)
                topPlayers.set(i - 1, cp);
            if (topPlayers.size() == i) main.levelCache().getLeaderboard().updateLeaderboard();
        }

    }

    public Player getPlayer() {
        return player;
    }
    public Long getLevel() {
        return level;
    }
    public Double getExp() {
        return exp;
    }

}
