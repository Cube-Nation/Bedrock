package de.cubenation.bedrock.style.scheme;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.style.ColorScheme;
import net.md_5.bungee.api.ChatColor;

@SuppressWarnings("unused")
public class RedColorScheme extends ColorScheme {

    public RedColorScheme(BasePlugin plugin) {
        super(ColorSchemeName.RED, ChatColor.RED, ChatColor.DARK_RED, ChatColor.GRAY, ChatColor.WHITE);
    }

}