package net.zerotoil.dev.cyberlevels.objects.exp;

import net.zerotoil.dev.cyberlevels.CyberLevels;

import java.util.HashMap;
import java.util.Map;

public class EXPCache {

    private final CyberLevels main;
    private Map<String, EXPEarnEvent> expEarnEvents = new HashMap<>();

    public EXPCache(CyberLevels main) {
        this.main = main;
        loadExpEvents();
    }

    public void loadExpEvents() {

        addEvent("placing", "blocks");
        addEvent("breaking", "blocks");
        addEvent("crafting", "items");
        addEvent("fishing", "fish");

    }

    private void addEvent(String category, String name) {
        expEarnEvents.put(category, new EXPEarnEvent(main, category, name));
    }

    public Map<String, EXPEarnEvent> expEarnEvents() {
        return expEarnEvents;
    }
}
