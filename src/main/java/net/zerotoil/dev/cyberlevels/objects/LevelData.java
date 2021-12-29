package net.zerotoil.dev.cyberlevels.objects;

import net.objecthunter.exp4j.ExpressionBuilder;
import net.zerotoil.dev.cyberlevels.CyberLevels;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LevelData {

    private final CyberLevels main;
    private Long level;
    private String expFormula;
    private List<RewardObject> rewards;

    public LevelData(CyberLevels main, Long level) {
        this.main = main;
        setLevel(level);

    }

    public void setLevel(Long level) {
        this.level = level;
        String formula = main.levelUtils().levelFormula(level);
        if (formula == null) formula = main.levelUtils().generalFormula();
        expFormula = formula;
        clearRewards();

    }
    public void setRewards(List<RewardObject> rewards) {
        this.rewards = rewards;
    }

    public void addReward(RewardObject reward) {
        rewards.add(reward);
    }

    public void clearRewards() {
        rewards = new ArrayList<>();
    }

    public Double getRequiredExp(Player player) {
        String formula = expFormula;
        formula = main.levelUtils().getPlaceholders(formula, player, false);
        return (new ExpressionBuilder(formula).build().evaluate());
    }

    public List<RewardObject> getRewards() {
        return rewards;
    }
}
