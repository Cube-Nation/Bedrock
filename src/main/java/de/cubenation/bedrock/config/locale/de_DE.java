package de.cubenation.bedrock.config.locale;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.service.customconfigurationfile.CustomConfigurationFile;

import java.io.IOException;
import java.util.HashMap;

public class de_DE extends CustomConfigurationFile {

    public de_DE(BasePlugin plugin) throws IOException {
        super(
                plugin,
                "locale" + java.lang.System.getProperty("file.separator") + "de_DE.yml",
                de_DE.data()
        );
    }

    private static HashMap<String,Object> data() {
        HashMap<String,Object> data		= new HashMap<>();

        /*
         * Generic Messages
         */
        data.put("version",                         "%plugin_prefix%&RESET& &TEXT&Version &PRIMARY&%version%");

        data.put("reload.complete",                 "%plugin_prefix%&RESET& Plugin neu geladen");
        data.put("reload.failed",                   "%plugin_prefix%&RESET& Plugin konnte nicht neu geladen werden");

        data.put("permission.list.header",          "%plugin_prefix%&RESET& &SECONDARY&Alle Berechtigungen:");
        data.put("permission.list.role",            "&PRIMARY&%role%&WHITE&:");
        data.put("permission.list.permission",      " - &FLAG&%permission%");
        data.put("permission.no_permissions",       "%plugin_prefix%&RESET& &RED&Hinweis: &SECONDARY&Dieses Plugin nutzt keine Berechtigungen");

        data.put("permission.insufficient",         "%plugin_prefix%&RESET& &RED&Für diesen Befehl hast Du keine Berechtigung");

        data.put("command.invalid",                 "%plugin_prefix%&RESET& &RED&Ungültiger Befehl");

        /*
         * Help messages
         */
        data.put("help.header",                     "&FLAG&==== &PRIMARY&%plugin% Hilfe &FLAG&====");

        data.put("help.command.command",            "&PRIMARY&/%label%&RESET& &SECONDARY&%commands%&RESET& %args%&RESET&");
        data.put("help.command.divider",            "&PRIMARY&|&SECONDARY&");
        data.put("help.command.description",        "&TEXT&%description%&RESET&");
        data.put("help.command.args.needed",        "&GRAY&&ITALIC&%argument%&RESET&");
        data.put("help.command.args.optional",      "&GRAY&&ITALIC&[%argument%]&RESET&");
        data.put("help.command.args.description",   "&TEXT& - &GRAY&%description%&RESET&");


        data.put("help.plugin",                     "&TEXT&Hilfe zu allen Plugin Befehlen");
        data.put("help.version",                    "&TEXT&Zeigt die Version des Plugins an");
        data.put("help.reload",                     "&TEXT&Lädt das Plugin neu");
        data.put("help.permission.list",            "&TEXT&Zeigt alle Plugin-Berechtigungen an");

        return data;
    }

}
