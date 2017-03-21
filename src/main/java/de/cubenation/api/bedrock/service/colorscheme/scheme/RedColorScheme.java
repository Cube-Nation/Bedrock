package de.cubenation.api.bedrock.service.colorscheme.scheme;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.service.colorscheme.ColorScheme;
import net.md_5.bungee.api.ChatColor;

@SuppressWarnings("unused")
public class RedColorScheme extends ColorScheme {

    public RedColorScheme(BasePlugin plugin) {
        super(ColorSchemeName.RED, ChatColor.RED, ChatColor.DARK_RED, ChatColor.GRAY, ChatColor.WHITE);
    }

}