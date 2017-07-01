package de.cubenation.bedrock.core.service.colorscheme.scheme;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.service.colorscheme.ColorScheme;
import net.md_5.bungee.api.ChatColor;

@SuppressWarnings("unused")
public class GreenColorScheme extends ColorScheme {

    public GreenColorScheme(FoundationPlugin plugin) {
        super(ColorSchemeName.GREEN, ChatColor.GREEN, ChatColor.DARK_GREEN, ChatColor.GRAY, ChatColor.WHITE);
    }

}
