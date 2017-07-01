package de.cubenation.bedrock.core.service.colorscheme.scheme;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.service.colorscheme.ColorScheme;
import net.md_5.bungee.api.ChatColor;

@SuppressWarnings("unused")
public class YellowColorScheme extends ColorScheme {

    public YellowColorScheme(FoundationPlugin plugin) {
        super(ColorSchemeName.YELLOW, ChatColor.YELLOW, ChatColor.GOLD, ChatColor.GRAY, ChatColor.WHITE);
    }

}
