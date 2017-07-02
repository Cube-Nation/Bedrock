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

package de.cubenation.bedrock.core.service.settings;

import de.cubenation.bedrock.core.FoundationPlugin;
import net.cubespace.Yamler.Config.YamlConfig;

import java.io.File;

/**
 * @author Cube-Nation
 * @version 1.0
 */
@SuppressWarnings({"WeakerAccess", "ResultOfMethodCallIgnored", "unused"})
public abstract class CustomSettingsFile extends YamlConfig {

    private final FoundationPlugin plugin;

    public CustomSettingsFile(FoundationPlugin plugin) {
        this(plugin, getFilename());
    }

    public CustomSettingsFile(FoundationPlugin plugin, String name) {
        this.plugin = plugin;
        setConfigFile(name);
    }

    private void setConfigFile(String name) {
        File settingsDir = new File(this.plugin.getDataFolder(), SettingsService.SETTINGSDIR);
        if (!settingsDir.exists()) {
            settingsDir.mkdir();
        }

        File file = new File(settingsDir, getSettingsName());
        if (!file.exists()) {
            file.mkdir();
        }

        CONFIG_FILE = new File(file, name);
    }

    private static String getFilename() {
        return "_default.yml";
    }

    public abstract String getSettingsName();

    public abstract String info();

}
