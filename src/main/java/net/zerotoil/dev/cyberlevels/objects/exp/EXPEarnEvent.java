package net.zerotoil.dev.cyberlevels.objects.exp;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public class EXPEarnEvent {

    private CyberLevels main;
    private Boolean enabled = false;
    private String category, name;
    private double minEXP, maxEXP;
    private boolean includedEnabled = false, whitelist = false;
    private List<String> list = new ArrayList<>();

    private boolean specificEnabled = false;
    private HashMap<String, Double> specificMin = new HashMap<>(), specificMax = new HashMap<>();

    private static Random random = new Random();

    protected EXPEarnEvent(CyberLevels main) {
        this.main = main;
    }

    public EXPEarnEvent(CyberLevels main, String category, String name) {
        this.main = main;
        this.category = category;
        this.name = name;
        ConfigurationSection config = main.files().getConfig("earn-exp").getConfigurationSection("earn-exp." + category);
        loadGeneral(config);
        loadSpecific(config);
    }

    protected void loadGeneral(ConfigurationSection config) {
        if (config == null) return;
        config = config.getConfigurationSection("general");     // general section
        if (config == null) return;                             // if no general section

        try { // since it can make a null pointer exception
            enabled = config.getBoolean("enabled");
            if (!enabled) return;
        } catch (Exception e) {
            return;
        }

        String exp = config.getString("exp");
        if (exp == null) {            // if exp isn't set, disable it
            enabled = false;
            return;
        }

        // correctly parses the exp values
        if (exp.contains(",")) {
            String[] string = exp.replace(" ", "").split(",", 2);
            double tempMin = Math.min(Double.parseDouble(string[0]), Double.parseDouble(string[1]));
            double tempMax = Math.max(Double.parseDouble(string[0]), Double.parseDouble(string[1]));
            minEXP = tempMin;
            maxEXP = tempMax;
        } else maxEXP = minEXP = Double.parseDouble(exp);

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

    protected void loadSpecific(ConfigurationSection config) {
        if (config == null) return;
        config = config.getConfigurationSection("specific-" + name);
        if (config == null) return;
        try { // since it can make a null pointer exception
            specificEnabled = config.getBoolean("enabled");
            if (!specificEnabled) return;
        } catch (Exception e) {
            return;
        }

        if (config.get(name) == null) return;

        for (String s : main.langUtils().convertList(main.files().getConfig("earn-exp"), "earn-exp." + category + ".specific-" + name + "." + name)) {
            s = s.replace(" ", "");
            String val = s.split(":", 2)[1];
            s = s.split(":", 2)[0];
            if (val.contains(",")) {
                String[] string = val.split(",", 2);
                double tempMin = Math.min(Double.parseDouble(string[0]), Double.parseDouble(string[1]));
                double tempMax = Math.max(Double.parseDouble(string[0]), Double.parseDouble(string[1]));
                specificMin.put(s.toUpperCase(), tempMin);
                specificMax.put(s.toUpperCase(), tempMax);
            } else {
                specificMin.put(s.toUpperCase(), Double.parseDouble(val));
                specificMax.put(s.toUpperCase(), Double.parseDouble(val));
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
        double tempExp = minEXP + (maxEXP - minEXP) * random.nextDouble();
        if (main.expCache().roundExp()) tempExp = main.levelUtils().roundDecimal(tempExp);
        if (main.expCache().useDouble()) tempExp = Math.round(tempExp);
        return tempExp;
    }

    public double getSpecificExp(String string) {
        if (!isInSpecificList(string)) return 0.0;
        double tempExp = specificMin.get(string.toUpperCase()) + (specificMax.get(string.toUpperCase()) - specificMin.get(string.toUpperCase())) * random.nextDouble();
        if (main.expCache().roundExp()) tempExp = main.levelUtils().roundDecimal(tempExp);
        if (main.expCache().useDouble()) tempExp = Math.round(tempExp);
        return tempExp;

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

    public boolean hasPartialMatches(String string, boolean generalList) {
        if (generalList) {
            if (!includedEnabled) return true;
            else if (whitelist) for (String s : list) if (string.contains(s.toUpperCase())) return true;
            for (String s : list) if (!string.contains(s.toUpperCase())) return true;
        } else {
            if (!specificEnabled) return true;
            for (String s : specificMin.keySet()) if (string.contains(s.toUpperCase())) return true;
        }
        return false;
    }

    public boolean hasPermission(Player player) {
        if (!isSpecificEnabled()) return false;
        for (String s : getSpecificMin().keySet())
            if (player.hasPermission(s)) return true;
        return false;
    }

    public double getPartialMatchesExp(String string, boolean generalList) {

        double amount = 0.0;
        string = string.toUpperCase();

        while (hasPartialMatches(string, generalList)) {
            if (generalList) {
                if (!includedEnabled) return getGeneralExp();
                if (whitelist) {
                    for (String s : list) {
                        if (string.contains(s.toUpperCase())) {
                            string = string.replace(s, "");
                            amount += getGeneralExp();
                            System.out.println(string + ", " + s);
                        }
                    }
                } else {
                    for (String s : list) {
                        if (!string.contains(s.toUpperCase())) {
                            string = string.replace(s, "");
                            amount += getGeneralExp();
                            System.out.println(string + ", " + s);
                        }
                    }
                }
            } else {
                if (!specificEnabled) return 0.0;
                for (String s : specificMin.keySet()) {
                    if (string.contains(s.toUpperCase())) {
                        string = string.replace(s, "");
                        amount += getSpecificExp(s);
                        System.out.println(string + ", " + s);
                    }
                }
            }
        }

        return amount;
    }

    protected void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    protected void setCategory(String category) {
        this.category = category;
    }
    protected void setName(String name) {
        this.name = name;
    }
    protected void setMinEXP(double minEXP) {
        this.minEXP = minEXP;
    }
    protected void setMaxEXP(double maxEXP) {
        this.maxEXP = maxEXP;
    }
    protected void setIncludedEnabled(boolean includedEnabled) {
        this.includedEnabled = includedEnabled;
    }
    protected void setWhitelist(boolean whitelist) {
        this.whitelist = whitelist;
    }
    protected void setList(List<String> list) {
        this.list = list;
    }
    protected void setSpecificEnabled(boolean specificEnabled) {
        this.specificEnabled = specificEnabled;
    }
    protected void setSpecificMin(HashMap<String, Double> specificMin) {
        this.specificMin = specificMin;
    }
    protected void setSpecificMax(HashMap<String, Double> specificMax) {
        this.specificMax = specificMax;
    }
    protected static void setRandom(Random random) {
        EXPEarnEvent.random = random;
    }

    public Boolean getEnabled() {
        return enabled;
    }
    public String getCategory() {
        return category;
    }
    public String getName() {
        return name;
    }
    public double getMinEXP() {
        return minEXP;
    }
    public double getMaxEXP() {
        return maxEXP;
    }
    public boolean isIncludedEnabled() {
        return includedEnabled;
    }
    public boolean isWhitelist() {
        return whitelist;
    }
    public List<String> getList() {
        return list;
    }
    public HashMap<String, Double> getSpecificMin() {
        return specificMin;
    }
    public HashMap<String, Double> getSpecificMax() {
        return specificMax;
    }
    public static Random getRandom() {
        return random;
    }
}
