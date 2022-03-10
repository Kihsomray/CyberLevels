package net.zerotoil.dev.cyberlevels.objects.levels;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import net.zerotoil.dev.cyberlevels.objects.MySQL;
import net.zerotoil.dev.cyberlevels.objects.RewardObject;
import net.zerotoil.dev.cyberlevels.objects.leaderboard.Leaderboard;
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

    private final Long startLevel;
    private final Double startExp;
    private final Long maxLevel;

    private BukkitTask autoSave;

    private final Map<Player, PlayerData> playerLevels;
    private Map<Long, LevelData> levelData;
    private Leaderboard leaderboard;

    private final boolean doCommandMultiplier;
    private final boolean doEventMultiplier;

    private final boolean addLevelReward;

    private final boolean leaderboardEnabled;
    private final boolean syncLeaderboardAutoSave;
    private final boolean leaderboardInstantUpdate;

    private final boolean preventDuplicateRewards;

    private MySQL mySQL;

    public LevelCache(CyberLevels main) {
        this.main = main;
        startLevel = main.levelUtils().levelsYML().getLong("levels.starting.level");
        startExp = main.levelUtils().levelsYML().getDouble("levels.starting.experience");
        maxLevel = main.levelUtils().levelsYML().getLong("levels.maximum.level");
        doCommandMultiplier = main.files().getConfig("config").getBoolean("config.multipliers.commands", false);
        doEventMultiplier = main.files().getConfig("config").getBoolean("config.multipliers.events", true);
        addLevelReward = main.files().getConfig("config").getBoolean("config.add-level-reward", false);
        leaderboardEnabled = main.files().getConfig("config").getBoolean("config.leaderboard.enabled", false);
        syncLeaderboardAutoSave = main.files().getConfig("config").getBoolean("config.leaderboard.sync-on-auto-save", true) && leaderboardEnabled;
        leaderboardInstantUpdate = main.files().getConfig("config").getBoolean("config.leaderboard.instant-update", true) && leaderboardEnabled;
        preventDuplicateRewards = main.files().getConfig("config").getBoolean("config.prevent-duplicate-rewards", true);

        playerLevels = new HashMap<>();
        clearLevelData();
        startAutoSave();
        Configuration config = main.files().getConfig("config");
        if (config.getBoolean("config.mysql.enabled")) {
            try {
                mySQL = new MySQL(main, new String[]{
                        config.getString("config.mysql.host"),
                        config.getString("config.mysql.port"),
                        config.getString("config.mysql.database"),
                        config.getString("config.mysql.username"),
                        config.getString("config.mysql.password"),
                        config.getString("config.mysql.table")},
                        config.getBoolean("config.mysql.ssl"));
            } catch (Exception e) {
                mySQL = null;
                main.logger("&dSwitched to flat-file storage.", "");
            }
        }
    }

    public void loadLevelData() {

        main.logger("&dLoading level data...");
        long startTime = System.currentTimeMillis();

        ConfigurationSection levelSection = main.files().getConfig("levels").getConfigurationSection("levels.experience.level");
        Set<String> levels = new HashSet<>();
        if (levelSection != null) levels = levelSection.getKeys(false);

        long l = startLevel;

        while (l <= maxLevel) {
            levelData.put(l, new LevelData(main, l));
            levels.remove(l + "");
            l++;
        }

        main.logger("&7Loaded &d" + (l - startLevel) + " &7level(s) in &a" + (System.currentTimeMillis() - startTime) + "ms&7.", "");
        if (leaderboardEnabled) loadLeaderboard();

    }

    public void loadLeaderboard() {
        main.logger("&dLoading leaderboard data...");
        long startTime = System.currentTimeMillis();

        leaderboard = new Leaderboard(main);

        main.logger("&7Loaded &d10 &7players in &a" + (System.currentTimeMillis() - startTime) + "ms&7.", "");
    }

    public void loadRewards() {
        if (!main.files().getConfig("rewards").isConfigurationSection("rewards")) return;
        main.logger("&dLoading reward data...");
        long startTime = System.currentTimeMillis(), counter = 0;
        for (String s : main.files().getConfig("rewards").getConfigurationSection("rewards").getKeys(false)) {
            new RewardObject(main, s);
            counter++;
        }
        main.logger("&7Loaded &d" + counter + " &7reward(s) in &a" + (System.currentTimeMillis() - startTime) + "ms&7.", "");

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
                if (syncLeaderboardAutoSave) leaderboard.updateLeaderboard();
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
        PlayerData playerData;
        String uuid = player.getUniqueId().toString();

        if (mySQL == null) {
            playerData = new PlayerData(main, player);
            File playerFile = new File(main.getDataFolder() + File.separator + "player_data", uuid + ".clv");
            try {
                if (!playerFile.exists()) {
                    playerFile.createNewFile();
                    String content = playerData.getLevel() + "\n" + main.levelUtils().roundStringDecimal(playerData.getExp()) + "\n" + playerData.getMaxLevel();
                    BufferedWriter writer = Files.newBufferedWriter(Paths.get(main.getDataFolder().getAbsolutePath() + File.separator + "player_data" + File.separator + uuid + ".clv"));
                    writer.write(content);
                    writer.close();
                } else {
                    Scanner scanner = new Scanner(playerFile);
                    playerData.setLevel(Long.parseLong(scanner.nextLine()), false);
                    playerData.setExp(Double.parseDouble(scanner.nextLine()), false, false);
                    if (scanner.hasNext()) playerData.setMaxLevel(Long.parseLong(scanner.nextLine()));
                }

            } catch (Exception e) {
                e.printStackTrace();
                main.logger("&cFailed to make file for " + player.getName() + ".");
            }
        }
        else playerData = mySQL.getPlayerData(player);
        playerLevels.put(player, playerData);
    }

    public void savePlayer(Player player, boolean clearData) {

        PlayerData playerData = playerLevels.get(player);
        String uuid = player.getUniqueId().toString();
        if (mySQL == null) {
            try {
                String content = playerData.getLevel() + "\n" + main.levelUtils().roundStringDecimal(playerData.getExp()) + "\n" + playerData.getMaxLevel();
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(main.getDataFolder().getAbsolutePath() + File.separator + "player_data" + File.separator + uuid + ".clv"));
                writer.write(content);
                writer.close();
            } catch (Exception e) {
                main.logger("&cFailed to save data for " + player.getName() + ".");
            }
        }
        else mySQL.updatePlayer(player);
        if (clearData) playerLevels.remove(player);
    }

    public void loadOnlinePlayers() {
        if (Bukkit.getOnlinePlayers().isEmpty()) return;
        main.logger("&dLoading data for online players...");
        long startTime = System.currentTimeMillis(), counter = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            loadPlayer(player);
            counter++;
        }
        main.logger("&7Loaded data for &e" + counter + " &7online player(s) in &a" + (System.currentTimeMillis() - startTime) + "ms&7.", "");
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

    public Map<Player, PlayerData> playerLevels() {
        return playerLevels;
    }

    public Map<Long, LevelData> levelData() {
        return levelData;
    }

    public MySQL getMySQL() {
        return mySQL;
    }

    public boolean doCommandMultiplier() {
        return doCommandMultiplier;
    }

    public boolean doEventMultiplier() {
        return doEventMultiplier;
    }

    public boolean addLevelReward() {
        return addLevelReward;
    }

    public Leaderboard getLeaderboard() {
        return leaderboard;
    }

    public boolean isLeaderboardEnabled() {
        return leaderboardEnabled;
    }

    public boolean isLeaderboardInstantUpdate() {
        return leaderboardInstantUpdate;
    }

    public boolean isPreventDuplicateRewards() {
        return preventDuplicateRewards;
    }
}
