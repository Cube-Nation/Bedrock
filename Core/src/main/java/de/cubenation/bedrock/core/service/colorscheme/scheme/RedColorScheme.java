package de.cubenation.bedrock.core.service.colorscheme.scheme;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.service.colorscheme.ColorScheme;
import net.md_5.bungee.api.ChatColor;

@SuppressWarnings("unused")
public class RedColorScheme extends ColorScheme {

    public RedColorScheme(FoundationPlugin plugin) {
        super(ColorSchemeName.RED, ChatColor.RED, ChatColor.DARK_RED, ChatColor.GRAY, ChatColor.WHITE);
    }

}
