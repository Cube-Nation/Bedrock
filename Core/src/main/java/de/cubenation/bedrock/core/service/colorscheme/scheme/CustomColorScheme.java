package de.cubenation.bedrock.core.service.colorscheme.scheme;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.exception.EqualFallbackPluginException;
import de.cubenation.bedrock.core.service.colorscheme.ColorScheme;
import net.md_5.bungee.api.ChatColor;

@SuppressWarnings("unused")
public class CustomColorScheme extends ColorScheme {

    public CustomColorScheme(FoundationPlugin plugin, ChatColor primary, ChatColor secondary, ChatColor flag, ChatColor text) {
        super(
                ColorSchemeName.CUSTOM,
                primary, secondary, flag, text
        );
    }

    @SuppressWarnings("deprecation")
    public CustomColorScheme(FoundationPlugin plugin) throws EqualFallbackPluginException {
        super(
                ColorSchemeName.CUSTOM,
                ChatColor.valueOf(
                        plugin.getConfigService().getReadOnlyConfig().getString("service.colorscheme.primary",
                                plugin.getFallbackBedrockPlugin().getColorSchemeService().getColorScheme().getPrimary().toString())

                ),
                ChatColor.valueOf(
                        plugin.getConfigService().getReadOnlyConfig().getString("service.colorscheme.secondary",
                                plugin.getFallbackBedrockPlugin().getColorSchemeService().getColorScheme().getSecondary().toString()
                        )
                ),
                ChatColor.valueOf(
                        plugin.getConfigService().getReadOnlyConfig().getString("service.colorscheme.flag",
                                plugin.getFallbackBedrockPlugin().getColorSchemeService().getColorScheme().getFlag().toString()
                        )
                ),
                ChatColor.valueOf(
                        plugin.getConfigService().getReadOnlyConfig().getString("service.colorscheme.text",
                                plugin.getFallbackBedrockPlugin().getColorSchemeService().getColorScheme().getText().toString()
                        )
                )
        );
    }

}
