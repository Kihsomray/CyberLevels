package net.zerotoil.dev.cyberlevels.objects.exp;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class EXPEarnEvent {

    private final CyberLevels main;
    private Boolean enabled = false;
    private String category, name;
    private long minEXP, maxEXP;
    private boolean includedEnabled = false, whitelist = false;
    private List<String> list = new ArrayList<>();

    private boolean specificEnabled = false;
    private HashMap<String, Long> specificMin = new HashMap<>(), specificMax = new HashMap<>();

    private static Random random = new Random();


    public EXPEarnEvent(CyberLevels main, String category, String name) {
        this.main = main;
        this.category = category;
        this.name = name;
        ConfigurationSection config = main.files().getConfig("earn-exp").getConfigurationSection("earn-exp." + category);
        loadGeneral(config);
        loadSpecific(config);
    }

    private void loadGeneral(ConfigurationSection config) {
        if (config == null) return;
        config = config.getConfigurationSection("general");     // general section
        if (config == null) return;                             // if no general section

        try { // since it can make a null pointer exception
            enabled = config.getBoolean("enabled");
            if (!enabled) return;
        } catch (Exception e) {
            return;
        }

        if (config.getString("exp") == null) {            // if exp isn't set, disable it
            enabled = false;
            return;
        }

        // correctly parses the exp values
        if (config.getString("exp").contains("-")) {
            String[] string = config.getString("exp").split("-", 2);
            minEXP = Long.parseLong(string[0]);
            maxEXP = Long.parseLong(string[1]);
        } else maxEXP = minEXP = config.getLong("exp");

        config = config.getConfigurationSection("includes");
        if (config == null) return;                             // if no section with includes, just stop

        try { // since it can make a null pointer exception
            includedEnabled = config.getBoolean("enabled");
            if (!includedEnabled) return;
        } catch (Exception e) {
            return;                                             // return if not enabled, the rest isn't important
        }
        try {
            whitelist = config.getBoolean("whitelist");   // if whitelist isn't in config, set it to false
        } catch (Exception e) {
            whitelist = false;
        }

        for (String s : config.getStringList("list"))     // if gets to the end, get the whitelist/blacklist
            list.add(s.toUpperCase());

    }

    private void loadSpecific(ConfigurationSection config) {
        if (config == null) return;
        config = config.getConfigurationSection("specific-" + name);
        if (config == null) return;
        try { // since it can make a null pointer exception
            specificEnabled = config.getBoolean("enabled");
            if (!specificEnabled) return;
        } catch (Exception e) {
            return;
        }

        config = config.getConfigurationSection(name);
        if (config == null) return;

        for (String s : config.getKeys(false)) {
            if (config.getString(s).contains("-")) {
                String[] string = config.getString(s).split("-", 2);
                specificMin.put(s.toUpperCase(), Long.parseLong(string[0]));
                specificMax.put(s.toUpperCase(), Long.parseLong(string[1]));
            } else {
                specificMin.put(s.toUpperCase(), config.getLong(s));
                specificMax.put(s.toUpperCase(), config.getLong(s));
            }
        }

    }

    public boolean isEnabled() {
        return enabled;
    }
    public boolean isSpecificEnabled() {
        return specificEnabled;
    }

    public double getGeneralExp() {
        return random.nextInt(Math.round((maxEXP - minEXP) + 1)) + minEXP;
    }

    public double getSpecificExp(String string) {
        if (!isInSpecificList(string)) return 0.0;
        return random.nextInt(Math.round((specificMax.get(string.toUpperCase()) - specificMin.get(string.toUpperCase())) + 1)) + specificMin.get(string.toUpperCase());
    }

    public boolean isInGeneralList(String string) {
        if (includedEnabled) {
            if (whitelist) return list.contains(string.toUpperCase());
            else return !list.contains(string.toUpperCase());
        } else return true;
    }

    public boolean isInSpecificList(String string) {
        if (!specificEnabled) return false;
        return specificMin.containsKey(string);
    }

}
