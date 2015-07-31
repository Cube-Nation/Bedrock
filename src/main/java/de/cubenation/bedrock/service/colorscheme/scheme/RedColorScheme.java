package de.cubenation.bedrock.service.colorscheme.scheme;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.service.colorscheme.ColorScheme;
import net.md_5.bungee.api.ChatColor;

@SuppressWarnings("unused")
public class RedColorScheme extends ColorScheme {

    public RedColorScheme(BasePlugin plugin) {
        super(ColorSchemeName.RED, ChatColor.RED, ChatColor.DARK_RED, ChatColor.GRAY, ChatColor.WHITE);
    }

}