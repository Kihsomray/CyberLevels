package net.zerotoil.dev.cyberlevels;

import net.zerotoil.dev.cyberlevels.objects.LevelData;
import net.zerotoil.dev.cyberlevels.objects.LevelObject;
import net.zerotoil.dev.cyberlevels.objects.RewardObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class LevelCache {

    private final CyberLevels main;

    private final String I = File.separator;

    private Long startLevel, maxLevel;
    private Double startExp;

    private Map<Player, LevelObject> playerLevels;
    private Map<Long, LevelData> levelData;

    public LevelCache(CyberLevels main) {
        this.main = main;
        startLevel = main.levelUtils().Levels().getLong("levels.starting.level");
        startExp = main.levelUtils().Levels().getDouble("levels.starting.experience");
        maxLevel = main.levelUtils().Levels().getLong("levels.maximum.level");
        playerLevels = new HashMap<>();
        clearLevelData();
    }

    private Configuration Rewards() { return main.getFiles().getFile("levels"); }

    public void loadLevelData() {
        ConfigurationSection levelSection = Rewards().getConfigurationSection("rewards");
        if (levelSection == null) return;

        Set<String> levels = levelSection.getKeys(false);
        long l = startLevel;

        while (!levels.isEmpty()) {
            levelData.put(l, new LevelData(main, l));
            levels.remove(l + "");
            l++;
        }
    }

    public void loadRewards() {
        ConfigurationSection rewards = Rewards().getConfigurationSection("rewards");
        if (rewards == null) return;
        for (String s : rewards.getKeys(false)) new RewardObject(main, s);
    }

    public void clearLevelData() {
        levelData = new HashMap<>();
    }

    public void loadPlayer(Player player) {
        LevelObject levelObject = new LevelObject(main, player);
        String uuid = player.getUniqueId().toString();
        File playerFile = new File(main.getDataFolder() + I + "player_data", uuid + ".clv");

        try {
            if (!playerFile.exists()) {
                playerFile.createNewFile();
                String content = levelObject.getLevel() + "\n" + levelObject.getExp();
                String path = main.getDataFolder().getAbsolutePath() + I + "player_data" + I + uuid + ".clv";
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(path));
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
            String path = main.getDataFolder().getAbsolutePath() + I + "player_data" + I + uuid + ".clv";
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(path));
            writer.write(content);
            writer.close();
        } catch (Exception e) {
            main.logger("&cFailed to save data for " + player.getName());
        }
    }

    public void loadOnlinePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) loadPlayer(player);
    }

    public void saveOnlinePlayers(boolean clearData) {
        for (Player player : Bukkit.getOnlinePlayers()) savePlayer(player, clearData);
    }

    public Long startLevel() { return startLevel; }
    public Double startExp() { return startExp; }
    public Long maxLevel() { return maxLevel; }

    public Map<Player, LevelObject> playerLevels() {
        return playerLevels;
    }

    public Map<Long, LevelData> levelData() {
        return levelData;
    }
}
