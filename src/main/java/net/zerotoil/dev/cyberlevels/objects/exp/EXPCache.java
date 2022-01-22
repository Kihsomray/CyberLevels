package net.zerotoil.dev.cyberlevels.objects.exp;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import net.zerotoil.dev.cyberlevels.objects.antiabuse.AntiAbuse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class EXPCache {

    private final CyberLevels main;
    private Map<String, EXPEarnEvent> expEarnEvents = new HashMap<>();
    private Map<String, AntiAbuse> antiAbuse = new HashMap<>();
    private boolean useDouble, roundExp;

    private boolean preventSilkTouchAbuse;
    private boolean onlyNaturalBlocks;

    private BukkitTask timedEXP;

    public EXPCache(CyberLevels main) {
        cancelTimedEXP();
        cancelAntiAbuseTimers();
        this.main = main;
        useDouble = main.files().getConfig("config").getBoolean("config.earn-exp.integer-only");
        roundExp = main.files().getConfig("config").getBoolean("config.round-evaluation.round-earn-exp");
        loadExpEvents();
        loadAntiAbuse();
        startTimedEXP();
    }

    public void loadExpEvents() {

        main.logger("&dLoading exp earning events...");
        if (!expEarnEvents.isEmpty()) expEarnEvents.clear();
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
        addEvent("breeding", "animals");

        expEarnEvents.put("timed-giving", new EXPTimed(main, "timed-giving", "permissions"));

        //addEvent("chatting", "words");

        long counter = 0;
        for (EXPEarnEvent event : expEarnEvents.values()) if (event.isEnabled() || event.isSpecificEnabled()) counter++;

        main.logger("&7Loaded &e" + counter + " &7exp earn events in &a" + (System.currentTimeMillis() - startTime) + "ms&7.", "");

    }

    public void loadAntiAbuse() {
        main.logger("&dLoading anti-abuse...");
        preventSilkTouchAbuse = !main.files().getConfig("anti-abuse").getBoolean("anti-abuse.general.silk-touch-reward", true);
        onlyNaturalBlocks = main.files().getConfig("anti-abuse").getBoolean("anti-abuse.general.only-natural-blocks", false);
        if (!antiAbuse.isEmpty()) antiAbuse.clear();
        long startTime = System.currentTimeMillis();
        long counter = 0;
        if (main.files().getConfig("anti-abuse").isConfigurationSection("anti-abuse")) {
            for (String s : main.files().getConfig("anti-abuse").getConfigurationSection("anti-abuse").getKeys(false)) {
                if (s.equalsIgnoreCase("general")) continue;
                antiAbuse.put(s, new AntiAbuse(main, s));
                counter++;
            }
        }

        main.logger("&7Loaded &e" + counter + " &7anti-abuse settings in &a" + (System.currentTimeMillis() - startTime) + "ms&7.", "");
    }

    public boolean isAntiAbuse(Player player, String event) {
        for (AntiAbuse a : antiAbuse.values()) {
            if (a.isCoolingDown(player, event)) return true;
            else if (a.isLimited(player, event)) return true;
        }
        return false;
    }

    public void cancelAntiAbuseTimers() {
        for (AntiAbuse a : antiAbuse.values()) a.cancelTimer();
    }

    public void cancelTimedEXP() {
        if (timedEXP == null) return;
        timedEXP.cancel();
        timedEXP = null;
    }

    public void startTimedEXP() {
        if (!main.files().getConfig("earn-exp").getBoolean("earn-exp.timed-giving.general.enabled") &&
                !main.files().getConfig("earn-exp").getBoolean("earn-exp.timed-giving.specific-permissions.enabled")) return;
        //if (timedEXP != null && !timedEXP.isCancelled()) return;
        timedEXP = (new BukkitRunnable() {
            @Override
            public void run() {
                EXPEarnEvent expEarnEvent = expEarnEvents.get("timed-giving");

                for (Player p : Bukkit.getOnlinePlayers()) {
                    double counter = 0;

                    if (expEarnEvent.isEnabled() && expEarnEvent.isInGeneralList(p.getName()))
                        counter += expEarnEvent.getGeneralExp();

                    if (expEarnEvent.isSpecificEnabled() && ((EXPTimed) expEarnEvent).hasPermission(p))
                        for (String s : expEarnEvent.getSpecificMin().keySet())
                            if (p.hasPermission(s)) counter += expEarnEvent.getSpecificExp(s);

                    if (counter > 0) main.levelCache().playerLevels().get(p).addExp(counter, main.levelCache().doEventMultiplier());
                    else if (counter < 0) main.levelCache().playerLevels().get(p).removeExp(Math.abs(counter));

                }

            }
        }).runTaskTimer(main, 0L, Math.max(20L * Math.max(1, main.files().getConfig("earn-exp").getLong("earn-exp.timed-giving.general.interval")), 1));
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

    public boolean isPreventSilkTouchAbuse() {
        return preventSilkTouchAbuse;
    }
    public boolean isOnlyNaturalBlocks() {
        return onlyNaturalBlocks;
    }

}
