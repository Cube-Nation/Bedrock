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

package de.cubenation.bedrock.core;

import de.cubenation.bedrock.core.config.BedrockDefaults;
import de.cubenation.bedrock.core.service.ServiceManager;
import de.cubenation.bedrock.core.service.colorscheme.ColorSchemeService;
import de.cubenation.bedrock.core.service.command.CommandService;
import de.cubenation.bedrock.core.service.config.ConfigService;
import de.cubenation.bedrock.core.service.inventory.InventoryService;
import de.cubenation.bedrock.core.service.localization.LocalizationService;
import de.cubenation.bedrock.core.service.permission.PermissionService;
import de.cubenation.bedrock.core.service.settings.CustomSettingsFile;
import de.cubenation.bedrock.core.service.settings.SettingsService;

import java.util.ArrayList;

/**
 * Represents a Bedrock-based plugin.
 *
 * @author Cube-Nation
 * @version 2.0
 */
public interface BedrockBasePlugin extends BasePlugin, DatabasePlugin {

    /**
     * Access to the ServiceManager instance and it's functions
     *
     * @return The ServiceManager instance
     */
    ServiceManager getServiceManager();

    /**
     * Returns the BedrockDefaults ConfigurationFile
     * @return The BedrockDefaults Config
     * @see BedrockDefaults
     */
    BedrockDefaults getBedrockDefaults();

    /**
     * Returns the Bedrock ConfigService object instance.
     * If the ConfigService is not ready, <code>null</code> is returned.
     *
     * @return The Bedrock ConfigService
     * @see ConfigService
     */
    ConfigService getConfigService();

    /**
     * Returns the Bedrock ColorSchemeService object instance.
     * If the ColorSchemeService is not ready, <code>null</code> is returned.
     *
     * @return The Bedrock ColorSchemeService
     * @see ColorSchemeService
     */
    ColorSchemeService getColorSchemeService();

    /**
     * Returns the Bedrock CommandService object instance.
     * If the CommandService is not ready, <code>null</code> is returned.
     *
     * @return The Bedrock CommandService
     * @see CommandService
     */
    CommandService getCommandService();

    /**
     * Returns the Bedrock PermissionService object instance.
     * If the PermissionService is not ready, <code>null</code> is returned.
     *
     * @return The Bedrock PermissionService
     * @see PermissionService
     */
    PermissionService getPermissionService();

    /**
     * Returns the Bedrock LocalizationService object instance.
     * If the LocalizationService is not ready, <code>null</code> is returned.
     *
     * @return The Bedrock LocalizationService
     * @see LocalizationService
     */
    LocalizationService getLocalizationService();

    /**
     * Returns the Bedrock InventoryService object instance.
     * If the InventoryService is not ready, <code>null</code> is returned.
     *
     * @return The Bedrock InventoryService
     * @see InventoryService
     */
    @SuppressWarnings("unused")
    InventoryService getInventoryService();

    /**
     * Returns the Bedrock SettingsService object instance.
     * If the InventoryService is not ready, <code>null</code> is returned.
     *
     * @return The Bedrock SettingsService
     * @see SettingsService
     */
    SettingsService getSettingService();

    /**
     * Returns a list of CustomSettingsFile classes
     *
     * @return An ArrayList of classes
     * @see CustomSettingsFile
     */
    ArrayList<Class<?>> getCustomSettingsFiles();
}
