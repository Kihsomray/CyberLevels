package net.zerotoil.dev.cyberlevels.objects.levels;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import net.zerotoil.dev.cyberlevels.objects.RewardObject;
import net.zerotoil.dev.iridiumapi.IridiumAPI;
import org.bukkit.entity.Player;

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
        level = Math.min(level + Math.max(amount, 0), main.levelCache().maxLevel());
        if (nextExpRequirement() == 0) exp = 0.0;
        levelCounter = level = levelCounter;
        if (levelCounter > 0)
            main.langUtils().sendMessage(player, player,"gained-levels", true, true, new String[]{"{gainedLevels}"}, new String[]{levelCounter + ""});
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
    }

    public void removeLevel(long amount) {
        if (level - amount < main.levelCache().startLevel()) exp = 0.0;
        long levelCounter = level;
        level = Math.max(level - Math.max(amount, 0), main.levelCache().startLevel());
        levelCounter -= level;
        if (levelCounter > 0)
            main.langUtils().sendMessage(player, player,"lost-levels", true, true, new String[]{"{lostLevels}"}, new String[]{levelCounter + ""});
    }

    public void addExp(double amount) {
        addExp(amount, 0, true);
    }

    public void addExp(double amount, double difference, boolean sendMessage) {
        amount = Math.max(amount, 0);
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

    }

    public void setExp(double amount, boolean checkLevel, boolean sendMessage) {
        amount = Math.abs(amount);
        if (checkLevel) {
            double exp = this.exp;
            this.exp = 0.0;
            addExp(amount, exp, sendMessage);
        } else exp = amount;
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

    }

    @Override
    public String toString() {
        return "level: " + level + ", exp: " + exp + ", progress: " +
                IridiumAPI.process(main.levelUtils().progressBar(exp, nextExpRequirement())) +
                " [" + (int) (100 * (exp / nextExpRequirement())) + "%]";
    }

    private void sendLevelReward() {
        for (RewardObject rewardObject : main.levelCache().levelData().get(level).getRewards()) rewardObject.giveReward(player);
    }

    public double nextExpRequirement() {
        if (main.levelCache().levelData().get(level + 1) == null) return 0.0;
        return main.levelCache().levelData().get(level + 1).getRequiredExp(player);
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
