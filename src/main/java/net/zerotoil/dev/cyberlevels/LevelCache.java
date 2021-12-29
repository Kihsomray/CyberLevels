package net.zerotoil.dev.cyberlevels;

import net.zerotoil.dev.cyberlevels.objects.LevelData;
import net.zerotoil.dev.cyberlevels.objects.LevelObject;
import net.zerotoil.dev.cyberlevels.objects.RewardObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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

    private Map<Player, LevelObject> playerLevels;
    private Map<Long, LevelData> levelData;

    public LevelCache(CyberLevels main) {
        this.main = main;
        startLevel = main.levelUtils().levelsYML().getLong("levels.starting.level");
        startExp = main.levelUtils().levelsYML().getDouble("levels.starting.experience");
        maxLevel = main.levelUtils().levelsYML().getLong("levels.maximum.level");
        playerLevels = new HashMap<>();
        clearLevelData();
    }

    public void loadLevelData() {
        Set<String> levels = main.files().getConfig("levels").getConfigurationSection("levels.experience.level").getKeys(false);

        long l = startLevel;

        while (!levels.isEmpty()) {

            levelData.put(l, new LevelData(main, l));
            levels.remove(l + "");
            l++;
        }
    }

    public void loadRewards() {

        if (main.files().getConfig("rewards").isConfigurationSection("rewards")) {

            for (String s : main.files().getConfig("rewards").getConfigurationSection("rewards").getKeys(false)) {
                new RewardObject(main, s);
            }

        }

    }

    public void clearLevelData() {
        levelData = new HashMap<>();
    }

    public void loadPlayer(Player player) {

        LevelObject levelObject = new LevelObject(main, player);
        String uuid = player.getUniqueId().toString();
        File playerFile = new File(main.getDataFolder() + File.separator + "player_data", uuid + ".clv");
        try {
            if (!playerFile.exists()) {
                playerFile.createNewFile();
                String content = levelObject.getLevel() + "\n" + levelObject.getExp();
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(main.getDataFolder().getAbsolutePath() + File.separator + "player_data" + File.separator + uuid + ".clv"));
                writer.write(content);
                writer.close();
            } else {

                Scanner scanner = new Scanner(playerFile);
                levelObject.setLevel(Long.parseLong(scanner.nextLine()));
                levelObject.setExp(Double.parseDouble(scanner.nextLine()));

            }

        } catch (Exception e) {
            main.logger("&cFailed to make file for " + player.getName());
        }
        playerLevels.put(player, levelObject);

    }

    public void savePlayer(Player player, boolean clearData) {

        LevelObject levelObject = playerLevels.get(player);
        if (clearData) playerLevels.remove(player);
        String uuid = player.getUniqueId().toString();
        try {
            String content = levelObject.getLevel() + "\n" + levelObject.getExp();
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(main.getDataFolder().getAbsolutePath() + File.separator + "player_data" + File.separator + uuid + ".clv"));
            writer.write(content);
            writer.close();
        } catch (Exception e) {
            main.logger("&cFailed to save data for " + player.getName());
        }
    }

    public void loadOnlinePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            loadPlayer(player);
        }
    }

    public void saveOnlinePlayers(boolean clearData) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            savePlayer(player, clearData);
        }
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
}
