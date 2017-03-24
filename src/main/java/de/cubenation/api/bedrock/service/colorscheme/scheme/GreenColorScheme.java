package de.cubenation.api.bedrock.service.colorscheme.scheme;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.service.colorscheme.ColorScheme;
import net.md_5.bungee.api.ChatColor;

@SuppressWarnings("unused")
public class GreenColorScheme extends ColorScheme {

    public GreenColorScheme(BasePlugin plugin) {
        super(ColorSchemeName.GREEN, ChatColor.GREEN, ChatColor.DARK_GREEN, ChatColor.GRAY, ChatColor.WHITE);
    }

}