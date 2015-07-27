package de.cubenation.bedrock.config.locale;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.service.customconfigurationfile.CustomConfigurationFile;

import java.io.IOException;
import java.util.HashMap;

public class en_US extends CustomConfigurationFile {

    public en_US(BasePlugin plugin) throws IOException {
        super(
                plugin,
                "locale" + System.getProperty("file.separator") + "en_US.yml",
                en_US.data()
        );
    }

    private static HashMap<String,Object> data() {
        HashMap<String,Object> data		= new HashMap<>();

        /*
         * Generic Messages
         */
        data.put("version",                     "Version &PRIMARY&%version%");

        data.put("reload.complete",             "Plugin reload complete");
        data.put("reload.failed",               "Plugin reload failed");

        data.put("permission.list.header",      "&SECONDARY&All Permissions:");
        data.put("permission.list.role",        "&PRIMARY&%role%&WHITE&:");
        data.put("permission.list.permission",  " - &FLAG&%permission%");
        data.put("permission.no_permissions",   "&RED&Warning: &SECONDARY&This plugin does not support permissions");

        data.put("permission.reload.complete",  "Permissions reloaded successfully");
        data.put("permission.reload.failed",    "Reloading permissions failed");

        data.put("permission.insufficient",     "&RED&You do not have permissions for this command");

        data.put("command.invalid",             "&RED&Invalid command");

        /*
         * Help messages
         */
        data.put("help.header",                 "&FLAG&==== &PRIMARY&%plugin% Help &FLAG&====");
        data.put("help.plugin",                 "Plugin commands help");
        data.put("help.version",                "Shows the plugin version");
        data.put("help.reload",                 "Reload the plugin");
        data.put("help.permission.reload",      "Reload all permissions");
        data.put("help.permission.list",        "Show all permissions");

        return data;
    }

}
