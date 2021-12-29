package net.zerotoil.dev.cyberlevels.utilities;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class LevelUtils {

    private final CyberLevels main;
    private DecimalFormat decimalFormat;

    private String bar;
    private String barCompleteColor;
    private String barIncompleteColor;

    public LevelUtils(CyberLevels main) {
        this.main = main;
        if (main.files().getConfig("config").isConfigurationSection("config.round-evaluation") &&
                main.files().getConfig("config").getBoolean("config.round-evaluation.enabled")) {
            String decimalFormat = "#.";
            for (int i = 0; i < main.files().getConfig("config").getLong("config.round-evaluation.digits"); i++) decimalFormat += "#";

            this.decimalFormat = new DecimalFormat(decimalFormat);
            this.decimalFormat.setRoundingMode(RoundingMode.CEILING);

        } else {
            decimalFormat = null;
        }
        bar = main.files().getConfig("lang").getString("messages.progress.bar");
        barCompleteColor = main.files().getConfig("lang").getString("messages.progress.complete-color");
        barIncompleteColor = main.files().getConfig("lang").getString("messages.progress.incomplete-color");
    }

    public Configuration levelsYML() {
        return main.files().getConfig("levels");
    }

    public long startLevel() {
        return levelsYML().getLong("levels.starting.level");
    }
    public double startEXP() {
        return levelsYML().getDouble("levels.starting.experience");
    }
    public long maxLevel() {
        return levelsYML().getLong("levels.maximum.level");
    }
    public String generalFormula() {
        return levelsYML().getString("levels.experience.general-formula");
    }
    public String levelFormula(long level) {
        if (levelsYML().isSet("levels.experience.level." + level))
            return levelsYML().getString("levels.experience.level." + level);
        return null;
    }
    public double roundDecimal(double value) {
        if (decimalFormat == null) return value;
        return Double.parseDouble(decimalFormat.format(value));
    }

    public String progressBar(Double exp, Double requiredExp) {
        if (requiredExp == 0) return barCompleteColor + bar + barIncompleteColor;
        int completion = (int) ((exp / requiredExp) * bar.length());
        return barCompleteColor + bar.substring(0, completion) + barIncompleteColor + bar.substring(completion);
    }

    public String getPlaceholders(String string, Player player, boolean playerPlaceholder) {
        string = string
                .replace("{level}", (main.levelCache().playerLevels().get(player).getLevel() - 1) + "")
                .replace("{playerEXP}", main.levelCache().playerLevels().get(player).getExp() + "")
                .replace("{nextLevel}", main.levelCache().playerLevels().get(player).getLevel() + "")
                .replace("{maxLevel}", main.levelCache().maxLevel() + "")
                .replace("{minLevel}", main.levelCache().startLevel() + "")
                .replace("{minEXP}", main.levelCache().startExp() + "");
        if (playerPlaceholder) string = string
                .replace("{player}", player.getName())
                .replace("{playerDisplayName}", player.getDisplayName())
                .replace("{playerUUID}", player.getUniqueId().toString());
        return string;
    }

}
