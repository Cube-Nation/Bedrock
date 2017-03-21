package de.cubenation.api.bedrock.service.colorscheme.scheme;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.service.colorscheme.ColorScheme;
import net.md_5.bungee.api.ChatColor;

@SuppressWarnings("unused")
public class BlueColorScheme extends ColorScheme {

    public BlueColorScheme(BasePlugin plugin) {
        super(ColorSchemeName.BLUE, ChatColor.AQUA, ChatColor.BLUE, ChatColor.GRAY, ChatColor.WHITE);
    }

}