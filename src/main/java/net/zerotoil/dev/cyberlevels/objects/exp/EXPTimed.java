package net.zerotoil.dev.cyberlevels.objects.exp;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import org.bukkit.entity.Player;

public class EXPTimed extends EXPEarnEvent {

    public EXPTimed(CyberLevels main, String category, String name) {
        super(main, category, name);
    }

    public boolean hasPermission(Player player) {
        if (!isSpecificEnabled()) return false;
        for (String s : getSpecificMin().keySet())
            if (player.hasPermission(s)) return true;
        return false;
    }

}
