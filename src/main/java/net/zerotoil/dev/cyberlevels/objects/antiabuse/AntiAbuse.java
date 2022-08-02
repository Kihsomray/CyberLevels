package net.zerotoil.dev.cyberlevels.objects.antiabuse;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private boolean worldsEnabled;
    private boolean worldsWhitelist;
    private List<String> worlds;

    private Map<Player, Long> playerCooldowns = new HashMap<>();
    private Map<Player, Long> playerLimiters = new HashMap<>();

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
        cooldownTime = section.getLong("cooldown.time", 5L) * 1000;

        limiterEnabled = section.getBoolean("limiter.enabled", false);
        limiterAmount = section.getLong("limiter.amount", 250L);
        limiterTimer = section.getString("limiter.timer", "");

        worldsEnabled = section.getBoolean("worlds.enabled", false);
        worldsWhitelist = section.getBoolean("worlds.whitelist", false);
        worlds = section.getStringList("worlds.list");

        if (limiterEnabled) abuseReset = new TimedAbuseReset(main, this, limiterTimer);

    }

    public void cancelTimer() {
        if (abuseReset != null) abuseReset.cancelTimer();
    }

    public boolean isCoolingDown(Player player, String event) {
        if (!expEvents.contains(event)) return false;
        if (!cooldownEnabled) return false;
        if (!playerCooldowns.containsKey(player) || System.currentTimeMillis() - playerCooldowns.get(player) >= cooldownTime) {
            playerCooldowns.put(player, System.currentTimeMillis());
            return false;
        } else return true;
    }

    public void resetCooldowns() {
        playerCooldowns = new HashMap<>();
    }

    public void resetCooldown(Player player) {
        playerCooldowns.remove(player);
    }

    public boolean isLimited(Player player, String event) {
        if (!expEvents.contains(event)) return false;
        if (!limiterEnabled) return false;
        if (!playerLimiters.containsKey(player)) playerLimiters.put(player, limiterAmount - 1);
        else if (playerLimiters.get(player).compareTo(0L) > 0) playerLimiters.put(player, playerLimiters.get(player) - 1);
        else return true;
        return false;
    }

    public void resetLimiters() {
        playerLimiters = new HashMap<>();
    }

    public void resetLimiter(Player player) {
        playerLimiters.remove(player);
    }

    public long getPlayerCooldown(Player player) {
        if (!playerCooldowns.containsKey(player)) return 0;
        if (System.currentTimeMillis() - playerCooldowns.get(player) >= cooldownTime) return 0;
        else return System.currentTimeMillis() - playerCooldowns.get(player);
    }

    public long getPlayerLimiter(Player player) {
        if (!playerLimiters.containsKey(player)) return limiterAmount;
        return playerLimiters.get(player);
    }

    public boolean isWorldLimited(Player player, String event) {
        if (!expEvents.contains(event)) return false;
        if (!worldsEnabled) return false;

        String world = player.getWorld().getName();
        if (!worldsWhitelist && worlds.contains(world)) return true;
        return (worldsWhitelist && !worlds.contains(world));
    }


}
