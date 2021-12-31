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
        main.logger("&bLoading YAML files...");
        long startTime = System.currentTimeMillis();

        // front end
        addFile("config");
        addFile("lang");
        addFile("levels");
        addFile("rewards");

        if (getConfig("config").getBoolean("config.auto-update.config")) get("config").updateConfig();
        if (getConfig("config").getBoolean("config.auto-update.lang")) get("lang").updateConfig();

        // back end
        File playerData = new File(main.getDataFolder(),"player_data");
        if (!playerData.exists()) playerData.mkdirs();

        main.logger("&7Loaded &e" + counter + "&7 files in &a" +
                (System.currentTimeMillis() - startTime) + "ms&7.", ""
        );
    }

    private void addFile(String file) {
        counter++;
        files.put(file, new YAMLFile(main, file + ".yml"));
        files.get(file).reloadConfig();
        main.logger("&7Loaded file &e" + file + ".yml&7.");
    }

    public HashMap<String, YAMLFile> getFiles() { return this.files; }
    private YAMLFile get(String file){  return files.get(file); }
    public Configuration getConfig(String file) { return files.get(file).getConfig(); }

}
