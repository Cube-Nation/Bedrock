package de.cubenation.api.bedrock.service.colorscheme.scheme;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.BedrockPlugin;
import de.cubenation.api.bedrock.service.colorscheme.ColorScheme;
import net.md_5.bungee.api.ChatColor;

@SuppressWarnings("unused")
public class CustomColorScheme extends ColorScheme {

    public CustomColorScheme(BasePlugin plugin, ChatColor primary, ChatColor secondary, ChatColor flag, ChatColor text) {
        super(
                ColorSchemeName.CUSTOM,
                primary, secondary, flag, text
        );
    }

    @SuppressWarnings("deprecation")
    public CustomColorScheme(BasePlugin plugin) {
        super(
                ColorSchemeName.CUSTOM,
                ChatColor.valueOf(
                        plugin.getConfigService().getReadOnlyConfig().getString("service.colorscheme.primary",
                                BedrockPlugin.getInstance().getColorSchemeService().getColorScheme().getPrimary().toString())

                ),
                ChatColor.valueOf(
                        plugin.getConfigService().getReadOnlyConfig().getString("service.colorscheme.secondary",
                                BedrockPlugin.getInstance().getColorSchemeService().getColorScheme().getSecondary().toString()
                        )
                ),
                ChatColor.valueOf(
                        plugin.getConfigService().getReadOnlyConfig().getString("service.colorscheme.flag",
                                BedrockPlugin.getInstance().getColorSchemeService().getColorScheme().getFlag().toString()
                        )
                ),
                ChatColor.valueOf(
                        plugin.getConfigService().getReadOnlyConfig().getString("service.colorscheme.text",
                                BedrockPlugin.getInstance().getColorSchemeService().getColorScheme().getText().toString()
                        )
                )
        );
    }

}