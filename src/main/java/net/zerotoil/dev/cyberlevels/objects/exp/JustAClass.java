package net.zerotoil.dev.cyberlevels.objects.exp;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import org.bukkit.configuration.ConfigurationSection;

public class JustAClass extends EXPEarnEvent {

    public JustAClass(CyberLevels main, String category, String name) {
        super(main);
        setCategory(category);
        setName(name);
        ConfigurationSection config = main.files().getConfig("levelled-mobs").getConfigurationSection("earn-exp." + category);
        loadGeneral(config);
        loadSpecific(config);
    }
}
