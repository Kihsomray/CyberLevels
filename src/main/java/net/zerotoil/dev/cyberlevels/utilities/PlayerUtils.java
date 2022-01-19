package net.zerotoil.dev.cyberlevels.utilities;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class PlayerUtils {

    private final CyberLevels main;

    public PlayerUtils(CyberLevels main) {
        this.main = main;
    }

    public boolean hasParentPerm(Player player, String permission, boolean checkOp) {
        if (checkOp && player.isOp()) return true;
        for (PermissionAttachmentInfo permissionNode : player.getEffectivePermissions()) {
            if (!permissionNode.getValue()) continue;
            if (permissionNode.getPermission().toLowerCase().startsWith(permission.toLowerCase())) return true;
        }
        return false;
    }

    public double getMultiplier(Player player) {
        double multiplier = 0;
        for (PermissionAttachmentInfo perm : player.getEffectivePermissions()) {
            if (!perm.getValue()) continue;
            String s = perm.getPermission().toLowerCase();
            if (!s.startsWith("cyberlevels.player.multiplier.")) continue;
            try {
                double currentMultiplier = Double.parseDouble(s.substring(30));
                if (currentMultiplier > multiplier) multiplier = currentMultiplier;
            } catch (Exception e) {
                // nothing
            }
        }
        if (multiplier == 0) return 1;
        return multiplier;
    }

}
