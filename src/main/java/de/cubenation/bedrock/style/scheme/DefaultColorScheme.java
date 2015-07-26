package de.cubenation.bedrock.style.scheme;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.style.ColorScheme;
import net.md_5.bungee.api.ChatColor;

@SuppressWarnings("unused")
public class DefaultColorScheme extends ColorScheme {

    public DefaultColorScheme(BasePlugin plugin) {
        super(ColorSchemeName.DEFAULT, ChatColor.AQUA, ChatColor.WHITE, ChatColor.GRAY, ChatColor.WHITE);
    }

}