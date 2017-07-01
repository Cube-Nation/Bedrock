package de.cubenation.bedrock.core.service.colorscheme.scheme;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.service.colorscheme.ColorScheme;
import net.md_5.bungee.api.ChatColor;

public class DefaultColorScheme extends ColorScheme {

    public DefaultColorScheme(FoundationPlugin plugin) {
        super(ColorSchemeName.DEFAULT, ChatColor.BLUE, ChatColor.AQUA, ChatColor.GRAY, ChatColor.WHITE);
    }

}
