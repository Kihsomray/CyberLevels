package net.zerotoil.dev.cyberlevels.utilities;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import net.zerotoil.dev.cyberlevels.LevelCache;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class LevelUtils {

    private final CyberLevels main;
    private final LevelCache cache;
    private DecimalFormat decimalFormat;

    private String bar;
    private String barCompleteColor;
    private String barIncompleteColor;

    public LevelUtils(CyberLevels main) {
        this.main = main;
        this.cache = main.levelCache();
        initUtils();
    }

    private Configuration Config() { return main.getFiles().getFile("config"); }
    private Configuration Lang() { return main.getFiles().getFile("lang"); }
    public Configuration Levels() { return main.getFiles().getFile("levels"); }

    private void initUtils() {
        if (Config().isConfigurationSection("config.round-evaluation") &&
                Config().getBoolean("config.round-evaluation.enabled")) {
            StringBuilder decimalFormat = new StringBuilder("#.");
            for (int i = 0; i < Config().getLong("config.round-evaluation.digits"); i++) decimalFormat.append("#");

            this.decimalFormat = new DecimalFormat(decimalFormat.toString());
            this.decimalFormat.setRoundingMode(RoundingMode.CEILING);

        }
        else decimalFormat = null;

        bar = Lang().getString("messages.progress.bar");
        barCompleteColor = Lang().getString("messages.progress.complete-color");
        barIncompleteColor = Lang().getString("messages.progress.incomplete-color");
    }

    public long startLevel() { return Levels().getLong("levels.starting.level"); }
    public double startEXP() { return Levels().getDouble("levels.starting.experience"); }
    public long maxLevel() { return Levels().getLong("levels.maximum.level"); }

    public String generalFormula() {
        return Levels().getString("levels.experience.general-formula");
    }

    public String levelFormula(long level) {
        if (Levels().isSet("levels.experience.level." + level))
            return Levels().getString("levels.experience.level." + level);
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

    public String getPlaceholders(String string, Player player, boolean isPlayer) {
        String[] keys = {"{level}", "{playerEXP}", "{nextLevel}",
                "{maxLevel}", "{minLevel}", "{minEXP}"};
        String[] values = {
                cache.playerLevels().get(player).getLevel() - 1 + "",
                cache.playerLevels().get(player).getExp() + "",
                cache.playerLevels().get(player).getLevel() + "",
                cache.maxLevel() + "", cache.startLevel() + "", cache.startExp() + ""
        };
        string = StringUtils.replaceEach(string, keys, values);

        if (isPlayer) {
            String[] keys1 = {"{player}", "{playerDisplayName}", "{playerUUID}"};
            String[] values1 = {
                    player.getName(), player.getDisplayName(),
                    player.getUniqueId().toString()
            };
            string = StringUtils.replaceEach(string, keys1, values1);
        }

        return string;
    }

}
