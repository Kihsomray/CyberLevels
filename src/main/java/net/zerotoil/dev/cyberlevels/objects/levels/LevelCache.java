package net.zerotoil.dev.cyberlevels.objects.levels;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import net.zerotoil.dev.cyberlevels.objects.MySQL;
import net.zerotoil.dev.cyberlevels.objects.RewardObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class LevelCache {

    private final CyberLevels main;

    private Long startLevel;
    private Double startExp;
    private Long maxLevel;

    private BukkitTask autoSave;

    private Map<Player, LevelObject> playerLevels;
    private Map<Long, LevelData> levelData;

    private MySQL mySQL;

    public LevelCache(CyberLevels main) {
        this.main = main;
        startLevel = main.levelUtils().levelsYML().getLong("levels.starting.level");
        startExp = main.levelUtils().levelsYML().getDouble("levels.starting.experience");
        maxLevel = main.levelUtils().levelsYML().getLong("levels.maximum.level");
        playerLevels = new HashMap<>();
        clearLevelData();
        startAutoSave();
        Configuration config = main.files().getConfig("config");
        if (config.getBoolean("config.mysql.enabled")) {
            mySQL = new MySQL(main, new String[]{
                    config.getString("config.mysql.host"),
                    config.getString("config.mysql.port"),
                    config.getString("config.mysql.database"),
                    config.getString("config.mysql.username"),
                    config.getString("config.mysql.password"),
                    config.getString("config.mysql.table")
            },
                    config.getBoolean("config.mysql.ssl"));
        }
    }

    public void loadLevelData() {

        long startTime = System.currentTimeMillis();

        ConfigurationSection levelSection = main.files().getConfig("levels").getConfigurationSection("levels.experience.level");
        if (levelSection == null) return;
        Set<String> levels = levelSection.getKeys(false);

        long l = startLevel;

        while (l <= maxLevel) {
            levelData.put(l, new LevelData(main, l));
            levels.remove(l + "");
            l++;
        }

        main.logger("&7Loaded &d" + (l - startLevel) + " &7levels in &a" + (System.currentTimeMillis() - startTime) + "ms&7.", "");

    }

    public void loadRewards() {
        if (main.files().getConfig("rewards").isConfigurationSection("rewards"))
            for (String s : main.files().getConfig("rewards").getConfigurationSection("rewards").getKeys(false)) new RewardObject(main, s);
    }

    public void cancelAutoSave() {
        if (autoSave == null) return;
        autoSave.cancel();
        autoSave = null;
    }

    public void startAutoSave() {
        if (!main.files().getConfig("config").getBoolean("config.auto-save.enabled")) return;
        autoSave = (new BukkitRunnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                saveOnlinePlayers(false);
                main.langUtils().sendMixed(null, main.files().getConfig("lang").getString("messages.auto-save")
                        .replace("{ms}", (System.currentTimeMillis() - startTime) + ""));
                startAutoSave();
            }
        }).runTaskLater(main, 20L * Math.max(1, main.files().getConfig("config").getLong("config.auto-save.interval")));
    }

    public void clearLevelData() {
        levelData = new HashMap<>();
    }

    public void loadPlayer(Player player) {
        LevelObject levelObject;
        String uuid = player.getUniqueId().toString();

        if (mySQL == null) {
            levelObject = new LevelObject(main, player);
            File playerFile = new File(main.getDataFolder() + File.separator + "player_data", uuid + ".clv");
            try {
                if (!playerFile.exists()) {
                    playerFile.createNewFile();
                    String content = levelObject.getLevel() + "\n" + main.levelUtils().roundStringDecimal(levelObject.getExp());
                    BufferedWriter writer = Files.newBufferedWriter(Paths.get(main.getDataFolder().getAbsolutePath() + File.separator + "player_data" + File.separator + uuid + ".clv"));
                    writer.write(content);
                    writer.close();
                }
                else {
                    Scanner scanner = new Scanner(playerFile);
                    levelObject.setLevel(Long.parseLong(scanner.nextLine()), false);
                    levelObject.setExp(Double.parseDouble(scanner.nextLine()), false, false);
                }

            } catch (Exception e) {
                e.printStackTrace();
                main.logger("&cFailed to make file for " + player.getName());
            }
        }
        else levelObject = mySQL.getPlayerData(player);
        playerLevels.put(player, levelObject);
    }

    public void savePlayer(Player player, boolean clearData) {

        LevelObject levelObject = playerLevels.get(player);
        String uuid = player.getUniqueId().toString();
        if (mySQL == null) {
            try {
                String content = levelObject.getLevel() + "\n" + main.levelUtils().roundStringDecimal(levelObject.getExp());
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(main.getDataFolder().getAbsolutePath() + File.separator + "player_data" + File.separator + uuid + ".clv"));
                writer.write(content);
                writer.close();
            } catch (Exception e) {
                main.logger("&cFailed to save data for " + player.getName());
            }
        }
        else mySQL.updatePlayer(player);
        if (clearData) playerLevels.remove(player);
    }

    public void loadOnlinePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) loadPlayer(player);
    }

    public void saveOnlinePlayers(boolean clearData) {
        for (Player player : Bukkit.getOnlinePlayers()) savePlayer(player, clearData);
    }

    public Long startLevel() {
        return startLevel;
    }

    public Double startExp() {
        return startExp;
    }

    public Long maxLevel() {
        return maxLevel;
    }

    public Map<Player, LevelObject> playerLevels() {
        return playerLevels;
    }

    public Map<Long, LevelData> levelData() {
        return levelData;
    }

    public MySQL getMySQL() {
        return mySQL;
    }
}
