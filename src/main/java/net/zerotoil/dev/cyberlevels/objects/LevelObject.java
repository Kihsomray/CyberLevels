package net.zerotoil.dev.cyberlevels.objects;

import net.objecthunter.exp4j.ExpressionBuilder;
import net.zerotoil.dev.cyberlevels.CyberLevels;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LevelObject {

    private final CyberLevels main;
    private Player player;
    private Long level;
    private Double exp;


    public LevelObject(CyberLevels main, Player player) {
        this.main = main;
        level = main.levelCache().startLevel();
        exp = 0.0;
        this.player = player;
    }

    public void addLevel(long amount) {
        level = Math.min(level + Math.max(amount, 0), main.levelCache().maxLevel());
        if (nextExpRequirement() == 0) exp = 0.0;
    }

    public void setLevel(long amount) {
        if (amount < main.levelCache().startLevel()) exp = 0.0;
        else if (amount >= main.levelCache().maxLevel()) exp = 0.0;
        level = Math.max(Math.min(amount, main.levelCache().maxLevel()), main.levelCache().startLevel());
    }

    public void removeLevel(long amount) {
        if (level - amount < main.levelCache().startLevel()) exp = 0.0;
        level = Math.max(level - Math.max(amount, 0), main.levelCache().startLevel());

    }

    public void addExp(double amount) {
        amount = Math.max(amount, 0);

        // current exp + exp increase > required exp to next level
        while (exp + amount >= nextExpRequirement()) {
            amount = (amount - nextExpRequirement()) + exp;
            exp = 0.0;
            level++;
            sendLevelReward();
            if (level.equals(main.levelCache().maxLevel())) return;
        }
        exp += amount;

    }

    public void setExp(double amount) {
        exp = 0.0;
        addExp(amount);
    }

    public void removeExp(double amount) {
        amount = Math.max(amount, 0); // 60

        if (amount > exp) {

            if (level.equals(main.levelCache().startLevel())) {
                exp = 0.0;
                return;
            }

            amount -= exp;
            level--;
            exp = nextExpRequirement();

            while (amount > exp) {
                if (level.equals(main.levelCache().startLevel())) {
                    exp = Math.max(0, exp - amount);
                    amount = 0;
                } else {
                    amount -= nextExpRequirement();
                    level--;
                    exp = nextExpRequirement();
                }
            }
        }
        exp -= amount;

        // makes sure the level doesn't go down below the start level
        level = Math.max(main.levelCache().startLevel(), level);
        exp = Math.max(0, exp);

    }

    public String toString() {
        return "level: " + level + ", exp: " + exp + ", progress: " + ChatColor.translateAlternateColorCodes('&', main.levelUtils().progressBar(exp, nextExpRequirement()));
    }

    private void sendLevelReward() {
        for (RewardObject reward : main.levelCache().levelData().get(level).getRewards()) reward.giveReward(player);
    }

    private double nextExpRequirement() {
        if (main.levelCache().levelData().get(level + 1) == null) return 0.0;
        return main.levelCache().levelData().get(level + 1).getRequiredExp(player);
    }

    /*private double getExpRequired(Long level) {
        String formula = main.levelsUtils().levelFormula(level);
        if (formula == null) formula = main.levelsUtils().generalFormula();
        formula = formula
                .replace("{level}", level + "")
                .replace("{playerEXP}", exp + "")
                .replace("{maxLevel}", main.levelsUtils().maxLevel() + "")
                .replace("{minLevel}", main.levelsUtils().startLevel() + "")
                .replace("{minEXP}", main.levelsUtils().startEXP() + "");
        return (new ExpressionBuilder(formula).build().evaluate());
    }*/

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
