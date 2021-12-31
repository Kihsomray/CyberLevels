package net.zerotoil.dev.cyberlevels.utilities;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import org.apache.commons.lang.StringUtils;
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
        loadUtility();
    }

    private void loadUtility() {
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
        return getPlaceholders(string, player, playerPlaceholder, false);
    }
    public String getPlaceholders(String string, Player player, boolean playerPlaceholder, boolean expRequirement) {
        String[] keys = {"{level}", "{playerEXP}", "{nextLevel}",
                "{maxLevel}", "{minLevel}", "{minEXP}"};
        String[] values = {
                main.levelCache().playerLevels().get(player).getLevel() + "",
                main.levelCache().playerLevels().get(player).getExp() + "",
                main.levelCache().playerLevels().get(player).getLevel() + "",
                main.levelCache().maxLevel() + "", main.levelCache().startLevel() + "", main.levelCache().startExp() + "",
        };
        string = StringUtils.replaceEach(string, keys, values);

        if (!expRequirement) {
            String[] keys1 = {"{requiredEXP}", "{percent}", "{progressBar}"};
            String[] values1 = {
                    main.levelCache().playerLevels().get(player).nextExpRequirement() + "",
                    getPercent(main.levelCache().playerLevels().get(player).getExp(), main.levelCache().playerLevels().get(player).nextExpRequirement()),
                    progressBar(main.levelCache().playerLevels().get(player).getExp(), main.levelCache().playerLevels().get(player).nextExpRequirement())
            };
            string = StringUtils.replaceEach(string, keys1, values1);
        }

        if (playerPlaceholder) {
            String[] keys1 = {"{player}", "{playerDisplayName}", "{playerUUID}"};
            String[] values1 = {
                    player.getName(), player.getDisplayName(),
                    player.getUniqueId().toString()
            };
            string = StringUtils.replaceEach(string, keys1, values1);
        }
        return string;
    }

    private String getPercent(Double exp, Double requiredExp) {
        if (requiredExp.equals(exp)) return "100";
        return (int) (100 * (exp / requiredExp)) + "";
    }

}
