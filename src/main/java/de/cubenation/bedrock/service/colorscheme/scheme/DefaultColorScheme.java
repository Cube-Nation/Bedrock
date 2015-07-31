package de.cubenation.bedrock.service.colorscheme.scheme;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.service.colorscheme.ColorScheme;
import net.md_5.bungee.api.ChatColor;

public class DefaultColorScheme extends ColorScheme {

    public DefaultColorScheme(BasePlugin plugin) {
        super(ColorSchemeName.DEFAULT, ChatColor.BLUE, ChatColor.AQUA, ChatColor.GRAY, ChatColor.WHITE);
    }

}