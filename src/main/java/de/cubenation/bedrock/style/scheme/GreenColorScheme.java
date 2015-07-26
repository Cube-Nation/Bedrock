package de.cubenation.bedrock.style.scheme;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.style.ColorScheme;
import net.md_5.bungee.api.ChatColor;

@SuppressWarnings("unused")
public class GreenColorScheme extends ColorScheme {

    public GreenColorScheme(BasePlugin plugin) {
        super(ColorSchemeName.GREEN, ChatColor.GREEN, ChatColor.WHITE, ChatColor.GRAY, ChatColor.WHITE);
    }

}