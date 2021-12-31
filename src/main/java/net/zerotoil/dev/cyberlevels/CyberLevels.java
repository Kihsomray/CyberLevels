package net.zerotoil.dev.cyberlevels;

import net.zerotoil.dev.cyberlevels.commands.CLVCommand;
import net.zerotoil.dev.cyberlevels.events.OnJoin;
import net.zerotoil.dev.cyberlevels.objects.levels.LevelCache;
import net.zerotoil.dev.cyberlevels.objects.files.Files;
import net.zerotoil.dev.cyberlevels.utilities.LangUtils;
import net.zerotoil.dev.cyberlevels.utilities.LevelUtils;
import net.zerotoil.dev.cyberlevels.utilities.Logger;
import net.zerotoil.dev.cyberlevels.utilities.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.StringJoiner;

public final class CyberLevels extends JavaPlugin {

    private Logger logger;
    private Files files;

    private LangUtils langUtils;
    private LevelUtils levelUtils;
    private PlayerUtils playerUtils;

    private LevelCache levelCache;

    //private Map<Player, LevelObject> playerLevels = new HashMap<>();

    @Override
    public void onEnable() {
        logger = new Logger(this);
        files = new Files(this);

        langUtils = new LangUtils(this);
        levelUtils = new LevelUtils(this);
        levelCache = new LevelCache(this);

        logger("" +
                "&b―――――――――――――――――――――――――――――――――――――――――――――――",
                "_________ .____ ____   ____",
                "\\_   ___ \\|    |\\   \\ /   /",
                "/    \\  \\/|    | \\   Y   / ",
                "\\     \\___|    |__\\     /  ",
                " \\______  /_______ \\___/  ",
                "        \\/        \\/",
                "Authors: " + getAuthors(),
                "Edition: " + getDescription().getVersion(),
                "&b―――――――――――――――――――――――――――――――――――――――――――――――"
        );

        levelCache.loadLevelData();
        levelCache.loadOnlinePlayers();
        levelCache.loadRewards();

        playerUtils = new PlayerUtils(this);
        //testPlayer = new LevelObject(this);
        new CLVCommand(this);
        new OnJoin(this);

    }

    @Override
    public void onDisable() {
        levelCache.saveOnlinePlayers(true);
        levelCache.clearLevelData();
        levelCache.cancelAutoSave();
        // stuff

        if (levelCache.getMySQL() != null) levelCache.getMySQL().disconnect();
    }

    private String getAuthors() { return String.join(", ", getDescription().getAuthors()); }

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
