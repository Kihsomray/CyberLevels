package net.zerotoil.dev.cyberlevels;

import net.zerotoil.dev.cyberlevels.addons.Metrics;
import net.zerotoil.dev.cyberlevels.addons.PlaceholderAPI;
import net.zerotoil.dev.cyberlevels.commands.CLVCommand;
import net.zerotoil.dev.cyberlevels.commands.CLVTabComplete;
import net.zerotoil.dev.cyberlevels.events.OnJoin;
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

    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();
        logger = new Logger(this);
        if (!SystemUtils.OS_NAME.contains("Windows")) {
            logger("&d―――――――――――――――――――――――――――――――――――――――――――――――",
                    "&d╭━━━╮&7╱╱╱&d╭╮&7╱╱╱╱╱╱&d╭╮&7╱╱╱╱╱╱╱╱╱╱╱&d╭╮",
                    "&d┃╭━╮┃&7╱╱╱&d┃┃&7╱╱╱╱╱╱&d┃┃&7╱╱╱╱╱╱╱╱╱╱╱&d┃┃",
                    "&d┃┃&7╱&d╰╋╮&7╱&d╭┫╰━┳━━┳━┫┃&7╱╱&d╭━━┳╮╭┳━━┫┃╭━━╮",
                    "&d┃┃&7╱&d╭┫┃&7╱&d┃┃╭╮┃┃━┫╭┫┃&7╱&d╭┫┃━┫╰╯┃┃━┫┃┃━━┫",
                    "&d┃╰━╯┃╰━╯┃╰╯┃┃━┫┃┃╰━╯┃┃━╋╮╭┫┃━┫╰╋━━┃",
                    "&d╰━━━┻━╮╭┻━━┻━━┻╯╰━━━┻━━╯╰╯╰━━┻━┻━━╯",
                    "&7╱╱╱╱&d╭━╯┃  &7Authors: &f" + getAuthors(),
                    "&7╱╱╱╱&d╰━━╯  &7Version: &f" + getDescription().getVersion(),
                    "&d―――――――――――――――――――――――――――――――――――――――――――――――", ""
            );
        } else
            logger("-----------------------------------------------",
                    "_________ .____ ____   ____",
                    "\\_   ___ \\|    |\\   \\ /   /",
                    "/    \\  \\/|    | \\   Y   / ",
                    "\\     \\___|    |__\\     /  ",
                    " \\______  /_______ \\___/  ",
                    "        \\/        \\/",
                    "Authors: " + getAuthors(),
                    "Version: " + getDescription().getVersion(),
                    "-----------------------------------------------", ""
            );


        files = new Files(this);

        langUtils = new LangUtils(this);
        levelUtils = new LevelUtils(this);
        levelCache = new LevelCache(this);

        levelCache.loadLevelData();
        levelCache.loadOnlinePlayers();
        levelCache.loadRewards();

        playerUtils = new PlayerUtils(this);
        //testPlayer = new LevelObject(this);
        new CLVCommand(this);
        new CLVTabComplete(this);
        new OnJoin(this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) new PlaceholderAPI(this).register();

        new Metrics(this, 13782, this);
        logger("&7Loaded &dCWR v" + getDescription().getVersion() + "&7 in &a" +
                (System.currentTimeMillis() - startTime) + "ms&7.");
        if (SystemUtils.OS_NAME.contains("Windows")) logger("-----------------------------------------------");
        else logger("&d―――――――――――――――――――――――――――――――――――――――――――――――");

    }

    @Override
    public void onDisable() {
        levelCache.saveOnlinePlayers(true);
        levelCache.clearLevelData();
        levelCache.cancelAutoSave();
        // stuff

        if (levelCache.getMySQL() != null) levelCache.getMySQL().disconnect();
    }

    public String getAuthors() { return String.join(", ", getDescription().getAuthors()); }

    public String serverFork() { return Bukkit.getVersion().split("-")[1]; }

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

    public LangUtils langUtils() {
        return langUtils;
    }

}
