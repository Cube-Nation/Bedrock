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
        data.put("version",                         "%plugin_prefix%&RESET& &TEXT&Version &PRIMARY&%version%");

        data.put("reload.complete",                 "%plugin_prefix%&RESET& Plugin reloaded");
        data.put("reload.failed",                   "%plugin_prefix%&RESET& Could not reload plugin");

        data.put("permission.list.header",          "%plugin_prefix%&RESET& &SECONDARY&All Permissions:");
        data.put("permission.list.role",            "&PRIMARY&%role%&WHITE&:");
        data.put("permission.list.permission",      " - &FLAG&%permission%");
        data.put("permission.no_permissions",       "%plugin_prefix%&RESET& &RED&Warning: &SECONDARY&This plugin does use any permissions");

        data.put("permission.insufficient",         "%plugin_prefix%&RESET& &RED&You do not have enough permissions");

        data.put("command.invalid",                 "%plugin_prefix%&RESET& &RED&Invalid command");

        /*
         * Help messages
         */
        data.put("help.header",                     "&FLAG&==== &PRIMARY&%plugin% Help &FLAG&====");

        data.put("help.command.command",            "&PRIMARY&/%label%&RESET& &SECONDARY&%commands%&RESET& %args%&RESET&");
        data.put("help.command.divider",            "&PRIMARY&|&SECONDARY&");
        data.put("help.command.description",        "&TEXT&%description%&RESET&");
        data.put("help.command.args.needed",        "&GRAY&&ITALIC&%argument%&RESET&");
        data.put("help.command.args.optional",      "&GRAY&&ITALIC&[%argument%]&RESET&");
        data.put("help.command.args.description",   "&TEXT& - &GRAY&%description%&RESET&");

        data.put("help.plugin",                     "&TEXT&The plugin help");
        data.put("help.version",                    "&TEXT&Shows the plugin version");
        data.put("help.reload",                     "&TEXT&Reloads the plugin");
        data.put("help.permission.list",            "&TEXT&Shows all plugin permissions");

        return data;
    }

}
