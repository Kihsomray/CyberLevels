package net.zerotoil.dev.cyberlevels.objects.files;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class YAMLFile {

    private final CyberLevels main;

    private File configFile;
    private FileConfiguration dataConfig;

    private String location;
    private String folder = null;

    public YAMLFile(CyberLevels main, String name) {
        this.main = main;
        this.location = name + ".yml";

        saveDefaultConfig();
        dataConfig = YamlConfiguration.loadConfiguration(getFile());
    }

    public YAMLFile(CyberLevels main, String name, String folder) {
        this.main = main;
        this.location = name + ".yml";
        this.folder = folder;

        saveDefaultConfig();
        dataConfig = YamlConfiguration.loadConfiguration(getFile());
    }

    private File getFile() {
        if (folder != null) {
            File file = new File(main.getDataFolder(), folder);
            if (!file.exists()) file.mkdirs();
            return new File(file, location);
        }
        return new File(main.getDataFolder(), location);
    }

    public FileConfiguration getConfig() {
        return dataConfig;
    }

    public void saveConfig() throws IOException {
        if (!((this.dataConfig == null) || (this.configFile == null))) {
            this.getConfig().save(this.configFile);
        }
    }

    public void updateConfig() {
        try {
            ConfigUpdater.update(main, location, getFile(), null);
            if (main.serverVersion() < 13) ConfigUpdater.update(main, location, getFile(), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        reloadConfig();
    }

    public void reloadConfig() {
        dataConfig = YamlConfiguration.loadConfiguration(getFile());
    }

    public void saveDefaultConfig() {
        if (configFile == null) configFile = getFile();
        if (configFile.exists()) return;

        if (folder != null) location = folder + File.separator + location;
        main.saveResource(location, false);
    }

}
