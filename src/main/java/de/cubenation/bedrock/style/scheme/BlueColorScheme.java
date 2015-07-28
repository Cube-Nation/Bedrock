package de.cubenation.bedrock.style.scheme;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.style.ColorScheme;
import net.md_5.bungee.api.ChatColor;

@SuppressWarnings("unused")
public class BlueColorScheme extends ColorScheme {

    public BlueColorScheme(BasePlugin plugin) {
        super(ColorSchemeName.BLUE, ChatColor.BLUE, ChatColor.DARK_BLUE, ChatColor.GRAY, ChatColor.WHITE);
    }

}