package net.zerotoil.dev.cyberlevels.objects.files;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import org.bukkit.configuration.Configuration;

import java.io.File;
import java.util.HashMap;

public class Files {

    private final CyberLevels main;
    private HashMap<String, YAMLFile> files = new HashMap<>();
    private int counter = 0;

    public Files(CyberLevels main) {
        this.main = main;
        loadFiles();
    }

    public void loadFiles() {
        if (!files.isEmpty()) files.clear();
        main.logger("&dLoading YAML files...");
        long startTime = System.currentTimeMillis();

        // front end
        addFile("config");
        addFile("lang");
        addFile("levels");
        addFile("rewards");
        addFile("earn-exp");
        addFile("anti-abuse");

        addFile("levelled-mobs", "addons");

        if (updateFile("config")) get("config").updateConfig();
        if (updateFile("lang")) get("lang").updateConfig();
        if (updateFile("earn-exp")) get("earn-exp").updateConfig();

        if (updateFile("levelled-mobs")) get("levelled-mobs").updateConfig();

        // back end
        File playerData = new File(main.getDataFolder(),"player_data");
        if (!playerData.exists()) playerData.mkdirs();

        main.logger("&7Loaded &e" + counter + "&7 files in &a" +
                (System.currentTimeMillis() - startTime) + "ms&7.", ""
        );
    }

    private boolean updateFile(String name) {
        return getConfig("config").getBoolean("config.auto-update." + name, false);
    }

    private void addFile(String file) {
        counter++;
        files.put(file, new YAMLFile(main, file));
        files.get(file).reloadConfig();
        main.logger("&7Loaded file &e" + file + ".yml&7.");
    }

    public void addFile(String file, String folder) {
        counter++;
        files.put(file, new YAMLFile(main, file, folder));
        files.get(file).reloadConfig();
        main.logger("&7Loaded file &e" + file + ".yml&7 in &e" + folder + "&7 folder.");
    }

    public HashMap<String, YAMLFile> getFiles() { return this.files; }
    public YAMLFile get(String file){  return files.get(file); }
    public Configuration getConfig(String file) { return files.get(file).getConfig(); }
}
