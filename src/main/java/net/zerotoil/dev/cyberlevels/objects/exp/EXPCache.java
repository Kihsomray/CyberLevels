package net.zerotoil.dev.cyberlevels.objects.exp;

import net.zerotoil.dev.cyberlevels.CyberLevels;

import java.util.HashMap;
import java.util.Map;

public class EXPCache {

    private final CyberLevels main;
    private Map<String, EXPEarnEvent> expEarnEvents = new HashMap<>();
    private boolean useDouble, roundExp;

    public EXPCache(CyberLevels main) {
        this.main = main;
        useDouble = main.files().getConfig("config").getBoolean("config.earn-exp.integer-only");
        roundExp = main.files().getConfig("config").getBoolean("config.round-evaluation.round-earn-exp");
        loadExpEvents();
    }

    public void loadExpEvents() {

        main.logger("&dLoading exp earning events...");
        long startTime = System.currentTimeMillis();
        addEvent("damaging-players", "players");
        addEvent("damaging-animals", "animals");
        addEvent("damaging-monsters", "monsters");

        addEvent("killing-players", "players");
        addEvent("killing-animals", "animals");
        addEvent("killing-monsters", "monsters");

        addEvent("placing", "blocks");
        addEvent("breaking", "blocks");
        addEvent("crafting", "items");
        addEvent("fishing", "fish");

        long counter = 0;
        for (EXPEarnEvent event : expEarnEvents.values()) if (event.isEnabled() || event.isSpecificEnabled()) counter++;

        main.logger("&7Loaded &e" + counter + " &7exp earn events in &a" + (System.currentTimeMillis() - startTime) + "ms&7.", "");

    }

    private void addEvent(String category, String name) {
        expEarnEvents.put(category, new EXPEarnEvent(main, category, name));
    }

    public Map<String, EXPEarnEvent> expEarnEvents() {
        return expEarnEvents;
    }

    public boolean useDouble() {
        return useDouble;
    }
    public boolean roundExp() {
        return roundExp;
    }

}
