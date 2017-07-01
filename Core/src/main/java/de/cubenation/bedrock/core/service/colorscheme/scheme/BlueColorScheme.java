package de.cubenation.bedrock.core.service.colorscheme.scheme;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.service.colorscheme.ColorScheme;
import net.md_5.bungee.api.ChatColor;

@SuppressWarnings("unused")
public class BlueColorScheme extends ColorScheme {

    public BlueColorScheme(FoundationPlugin plugin) {
        super(ColorSchemeName.BLUE, ChatColor.AQUA, ChatColor.BLUE, ChatColor.GRAY, ChatColor.WHITE);
    }

}
