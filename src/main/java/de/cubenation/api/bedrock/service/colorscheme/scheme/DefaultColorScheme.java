package de.cubenation.api.bedrock.service.colorscheme.scheme;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.service.colorscheme.ColorScheme;
import net.md_5.bungee.api.ChatColor;

public class DefaultColorScheme extends ColorScheme {

    public DefaultColorScheme(BasePlugin plugin) {
        super(ColorSchemeName.DEFAULT, ChatColor.BLUE, ChatColor.AQUA, ChatColor.GRAY, ChatColor.WHITE);
    }

}