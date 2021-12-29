package net.zerotoil.dev.cyberlevels.utilities;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import org.bukkit.configuration.Configuration;

import java.util.ArrayList;
import java.util.List;

public class LangUtils {

    private final CyberLevels main;

    public LangUtils(CyberLevels main) {
        this.main = main;
    }

    // converts message to list
    public List<String> convertList(Configuration config, String path) {

        // if already list
        if (config.isList(path)) return config.getStringList(path);

        // if single string
        List<String> list = new ArrayList<>();
        list.add(config.getString(path));
        return list;

    }

}
