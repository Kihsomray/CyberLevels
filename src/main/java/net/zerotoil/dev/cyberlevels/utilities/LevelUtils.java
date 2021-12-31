package net.zerotoil.dev.cyberlevels.utilities;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class LevelUtils {

    private final CyberLevels main;
    private DecimalFormat decimalFormat;

    private String bar;
    private String completeStart;
    private String completeEnd;
    private String incompleteStart;
    private String incompleteEnd;

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

        }
        else decimalFormat = null;

        bar = langYML().getString("messages.progress.bar");
        completeStart = langYML().getString("messages.progress.complete-color.start", "");
        completeEnd = langYML().getString("messages.progress.complete-color.end", "");
        incompleteStart = langYML().getString("messages.progress.incomplete-color.start", "");
        incompleteEnd = langYML().getString("messages.progress.incomplete-color.end", "");
    }

    public Configuration levelsYML() {
        return main.files().getConfig("levels");
    }

    public Configuration langYML() {
        return main.files().getConfig("lang");
    }

    public String generalFormula() {
        return levelsYML().getString("levels.experience.general-formula");
    }

    @Nullable
    public String levelFormula(long level) {
        return levelsYML().getString("levels.experience.level." + level);
    }

    public double roundDecimal(double value) {
        if (decimalFormat == null) return value;
        return Double.parseDouble(decimalFormat.format(value));
    }

    public String progressBar(Double exp, Double requiredExp) {
        if (requiredExp == 0) return incompleteStart + bar + incompleteEnd;
        int completion = Math.min((int) ((exp / requiredExp) * bar.length()), bar.length() - 1);

        String end = incompleteStart + bar.substring(completion) + incompleteEnd;
        return completeStart + bar.substring(0, completion) + completeEnd + end;
    }

    public String getPlaceholders(String string, Player player, boolean playerPlaceholder) {
        return getPlaceholders(string, player, playerPlaceholder, false);
    }

    public String getPlaceholders(String string, Player player, boolean playerPlaceholder, boolean expRequirement) {
        String[] keys = {"{level}", "{playerEXP}", "{nextLevel}",
                "{maxLevel}", "{minLevel}", "{minEXP}"};
        String[] values = {
                main.levelCache().playerLevels().get(player).getLevel() + "",
                roundDecimal(main.levelCache().playerLevels().get(player).getExp()) + "",
                main.levelCache().playerLevels().get(player).getLevel() + 1 + "",
                main.levelCache().maxLevel() + "", main.levelCache().startLevel() + "",
                main.levelCache().startExp() + "",
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

    public String getPercent(Double exp, Double requiredExp) {
        if (requiredExp.equals(exp)) return "100";
        return (int) (100 * (exp / requiredExp)) + "";
    }

}
