package de.cubenation.bedrock.service.colorscheme.scheme;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.service.colorscheme.ColorScheme;
import net.md_5.bungee.api.ChatColor;

@SuppressWarnings("unused")
public class YellowColorScheme extends ColorScheme {

    public YellowColorScheme(BasePlugin plugin) {
        super(ColorSchemeName.YELLOW, ChatColor.YELLOW, ChatColor.GOLD, ChatColor.GRAY, ChatColor.WHITE);
    }

}