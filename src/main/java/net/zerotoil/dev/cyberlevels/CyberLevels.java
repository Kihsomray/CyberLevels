package net.zerotoil.dev.cyberlevels;

import net.zerotoil.dev.cyberlevels.commands.CLVCommand;
import net.zerotoil.dev.cyberlevels.events.OnJoin;
import net.zerotoil.dev.cyberlevels.objects.LevelObject;
import net.zerotoil.dev.cyberlevels.objects.files.Files;
import net.zerotoil.dev.cyberlevels.utilities.LangUtils;
import net.zerotoil.dev.cyberlevels.utilities.LevelUtils;
import net.zerotoil.dev.cyberlevels.utilities.Logger;
import net.zerotoil.dev.cyberlevels.utilities.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

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
        // stuff
    }

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
