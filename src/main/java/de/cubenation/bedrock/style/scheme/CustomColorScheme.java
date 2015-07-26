package de.cubenation.bedrock.style.scheme;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.BedrockPlugin;
import de.cubenation.bedrock.style.ColorScheme;
import net.md_5.bungee.api.ChatColor;

@SuppressWarnings("unused")
public class CustomColorScheme extends ColorScheme {

    public CustomColorScheme(BasePlugin plugin) {
        super(
                ColorSchemeName.CUSTOM,
                ChatColor.valueOf(
                        plugin.getConfig().getString("scheme.primary",
                        BedrockPlugin.getInstance().getColorScheme().getPrimary().toString())
                ),
                ChatColor.valueOf(
                        plugin.getConfig().getString("scheme.secondary",
                        BedrockPlugin.getInstance().getColorScheme().getSecondary().toString())
                ),
                ChatColor.valueOf(
                        plugin.getConfig().getString("scheme.flag",
                        BedrockPlugin.getInstance().getColorScheme().getFlag().toString())
                ),
                ChatColor.valueOf(
                        plugin.getConfig().getString("scheme.text",
                        BedrockPlugin.getInstance().getColorScheme().getText().toString())
                )
        );
    }

}