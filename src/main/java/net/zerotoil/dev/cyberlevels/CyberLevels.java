package net.zerotoil.dev.cyberlevels;

import net.zerotoil.dev.cyberlevels.addons.Metrics;
import net.zerotoil.dev.cyberlevels.addons.PlaceholderAPI;
import net.zerotoil.dev.cyberlevels.commands.CLVCommand;
import net.zerotoil.dev.cyberlevels.commands.CLVTabComplete;
import net.zerotoil.dev.cyberlevels.listeners.AntiAbuseListeners;
import net.zerotoil.dev.cyberlevels.listeners.EXPListeners;
import net.zerotoil.dev.cyberlevels.listeners.JoinListener;
import net.zerotoil.dev.cyberlevels.listeners.hooks.RivalHarvesterHoesHook;
import net.zerotoil.dev.cyberlevels.objects.exp.EXPCache;
import net.zerotoil.dev.cyberlevels.objects.levels.LevelCache;
import net.zerotoil.dev.cyberlevels.objects.files.Files;
import net.zerotoil.dev.cyberlevels.utilities.LangUtils;
import net.zerotoil.dev.cyberlevels.utilities.LevelUtils;
import net.zerotoil.dev.cyberlevels.utilities.Logger;
import net.zerotoil.dev.cyberlevels.utilities.PlayerUtils;
import org.apache.commons.lang.SystemUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class CyberLevels extends JavaPlugin {

    private Logger logger;
    private Files files;

    private LangUtils langUtils;
    private LevelUtils levelUtils;
    private PlayerUtils playerUtils;

    private LevelCache levelCache;
    private EXPCache expCache;

    private EXPListeners expListeners;

    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();
        logger = new Logger(this);

        reloadClasses(false);
        playerUtils = new PlayerUtils(this);
        expListeners = new EXPListeners(this);
        new AntiAbuseListeners(this);

        new CLVCommand(this);
        new CLVTabComplete(this);
        new JoinListener(this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            new PlaceholderAPI(this).register();

        if (Bukkit.getPluginManager().getPlugin("RivalHarvesterHoes") != null) {
            logger("&7Hooked into &bRivalHarvesterHoes&7.");
            new RivalHarvesterHoesHook(this);
        }

        new Metrics(this, 13782, this);
        logger("&7Loaded &dCLV v" + getDescription().getVersion() + "&7 in &a" +
                (System.currentTimeMillis() - startTime) + "ms&7.");

        if (SystemUtils.OS_NAME.contains("Windows"))
            logger("&8-----------------------------------------------");
        else logger("&d―――――――――――――――――――――――――――――――――――――――――――――――");
    }

    public void reloadClasses(boolean footer) {
        String author = "&7Authors: &f" + getAuthors();
        String version = "&7Version: &f" + getDescription().getVersion();

        if (!SystemUtils.OS_NAME.contains("Windows"))
            logger("&d―――――――――――――――――――――――――――――――――――――――――――――――",
                    "&d╭━━━╮&7╱╱╱&d╭╮&7╱╱╱╱╱╱&d╭╮&7╱╱╱╱╱╱╱╱╱╱╱&d╭╮",
                    "&d┃╭━╮┃&7╱╱╱&d┃┃&7╱╱╱╱╱╱&d┃┃&7╱╱╱╱╱╱╱╱╱╱╱&d┃┃",
                    "&d┃┃&7╱&d╰╋╮&7╱&d╭┫╰━┳━━┳━┫┃&7╱╱&d╭━━┳╮╭┳━━┫┃╭━━╮",
                    "&d┃┃&7╱&d╭┫┃&7╱&d┃┃╭╮┃┃━┫╭┫┃&7╱&d╭┫┃━┫╰╯┃┃━┫┃┃━━┫",
                    "&d┃╰━╯┃╰━╯┃╰╯┃┃━┫┃┃╰━╯┃┃━╋╮╭┫┃━┫╰╋━━┃",
                    "&d╰━━━┻━╮╭┻━━┻━━┻╯╰━━━┻━━╯╰╯╰━━┻━┻━━╯",
                    "&7╱╱╱╱&d╭━╯┃  " + author,
                    "&7╱╱╱╱&d╰━━╯  " + version,
                    "&d―――――――――――――――――――――――――――――――――――――――――――――――", ""
            );
        else
            logger("&8-----------------------------------------------",
                    "&d_________ .____ ____   ____",
                    "&d\\_   ___ \\|    |\\   \\ /   /",
                    "&d/    \\  \\/|    | \\   Y   / ",
                    "&d\\     \\___|    |__\\     /  ",
                    "&d \\______  /_______ \\___/  ",
                    "&d        \\/        \\/",
                    author,
                    version,
                    "&8-----------------------------------------------", ""
            );

        long startTime = System.currentTimeMillis();
        if (expCache != null) {
            expCache.cancelTimedEXP();
            expCache.cancelAntiAbuseTimers();
        }
        files = new Files(this);
        langUtils = new LangUtils(this);
        levelUtils = new LevelUtils(this);
        levelCache = new LevelCache(this);
        expCache = new EXPCache(this);

        levelCache.loadLevelData();
        levelCache.loadOnlinePlayers();
        levelCache.loadRewards();
        levelCache.loadLeaderboard();

        if (footer) {
            logger("&7Reloaded &dCLV v" + getDescription().getVersion() + "&7 in &a" +
                    (System.currentTimeMillis() - startTime) + "ms&7.");
            if (SystemUtils.OS_NAME.contains("Windows"))
                logger("&8-----------------------------------------------");
            else logger("&d―――――――――――――――――――――――――――――――――――――――――――――――");
        }

    }

    @Override
    public void onDisable() {
        levelCache.saveOnlinePlayers(true);
        levelCache.clearLevelData();
        levelCache.cancelAutoSave();
        // stuff

        if (levelCache.getMySQL() != null) levelCache.getMySQL().disconnect();
    }

    public String getAuthors() {
        return String.join(", ", getDescription().getAuthors());
    }

    public String serverFork() {
        String[] fork = Bukkit.getVersion().split("-");
        return fork[fork.length > 1 ? 1 : 0];
    }

    public int serverVersion() {
        return Integer.parseInt(Bukkit.getBukkitVersion().split("-")[0].split("\\.")[1]);
    }

    public void logger(String... messages) {
        logger.log(messages);
    }

    public Files files() {
        return files;
    }
    public LevelUtils levelUtils() {
        return levelUtils;
    }
    public PlayerUtils playerUtils() {
        return playerUtils;
    }

    public LevelCache levelCache() {
        return levelCache;
    }
    public EXPCache expCache() {
        return expCache;
    }

    public LangUtils langUtils() {
        return langUtils;
    }

    public EXPListeners expListeners() {
        return expListeners;
    }
}
