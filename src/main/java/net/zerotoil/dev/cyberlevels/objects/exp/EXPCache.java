package net.zerotoil.dev.cyberlevels.objects.exp;

import net.zerotoil.dev.cyberlevels.CyberLevels;

import java.util.HashMap;
import java.util.Map;

public class EXPCache {

    private final CyberLevels main;
    private Map<String, EXPEarnEvent> expEarnEvents = new HashMap<>();
    private boolean useDouble, roundExp;
    private long counter = 0;

    public EXPCache(CyberLevels main) {
        this.main = main;
        useDouble = main.files().getConfig("config").getBoolean("config.earn-exp.integer-only");
        roundExp = main.files().getConfig("config").getBoolean("config.round-evaluation.round-earn-exp");
        loadExpEvents();
    }

    public void loadExpEvents() {

        long startTime = System.currentTimeMillis();
        addEvent("placing", "blocks");
        addEvent("breaking", "blocks");
        addEvent("crafting", "items");
        addEvent("fishing", "fish");
        main.logger("Loaded " + counter + " exp earn events in " + (System.currentTimeMillis() - startTime) + "ms.");

    }

    private void addEvent(String category, String name) {
        expEarnEvents.put(category, new EXPEarnEvent(main, category, name));
        counter++;
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
