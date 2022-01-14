package net.zerotoil.dev.cyberlevels.objects.antiabuse;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class AntiAbuse {

    private final CyberLevels main;
    private final String location;

    private List<String> expEvents; // = new ArrayList<>();

    private boolean cooldownEnabled;
    private long cooldownTime;

    private boolean limiterEnabled;
    private long limiterAmount;
    private String limiterTimer;
    private TimedAbuseReset abuseReset;

    public AntiAbuse(CyberLevels main, String location) {
        this.main = main;
        this.location = location;
        initiateAntiAbuse();
    }

    private void initiateAntiAbuse() {

        ConfigurationSection section = main.files().getConfig("anti-abuse").getConfigurationSection("anti-abuse." + location);
        if (section == null) return;

        expEvents = section.getStringList("exp-events");
        if (expEvents.isEmpty()) return;

        cooldownEnabled = section.getBoolean("cooldown.enabled", false);
        cooldownTime = section.getLong("cooldown.time", 5L);

        limiterEnabled = section.getBoolean("limiter.enabled", false);
        limiterAmount = section.getLong("limiter.amount", 250L);
        limiterTimer = section.getString("limiter.timer", "");

        if (limiterEnabled) abuseReset = new TimedAbuseReset(main, this, limiterTimer);

    }

    public void cancelTimer() {
        if (abuseReset != null) abuseReset.cancelTimer();
    }


}
