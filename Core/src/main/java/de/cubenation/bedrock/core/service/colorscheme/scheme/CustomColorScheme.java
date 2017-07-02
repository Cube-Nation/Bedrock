/*
 * Bedrock
 *
 * Copyright (c) 2017 Cube-Nation (Benedikt Hruschka, Tristan Cebulla)
 *
 * Permission is hereby granted, free of charge,
 * to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.cubenation.bedrock.core.service.colorscheme.scheme;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.service.colorscheme.ColorScheme;
import net.md_5.bungee.api.ChatColor;

/**
 * @author Cube-Nation
 * @version 1.0
 */
@SuppressWarnings("unused")
public class CustomColorScheme extends ColorScheme {

    public CustomColorScheme(FoundationPlugin plugin, ChatColor primary, ChatColor secondary, ChatColor flag, ChatColor text) {
        super(
                ColorSchemeName.CUSTOM,
                primary, secondary, flag, text
        );
    }

    @SuppressWarnings("deprecation")
    public CustomColorScheme(FoundationPlugin plugin) {
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
