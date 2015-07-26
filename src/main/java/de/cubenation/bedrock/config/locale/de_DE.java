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
        data.put("version",                     "Version &PRIMARY&%version%");

        data.put("reload.complete",             "Plugin neu geladen");
        data.put("reload.failed",               "Plugin konnte nicht neu geladen werden");

        data.put("permission.list.header",      "&SECONDARY&Alle Berechtigungen:");
        data.put("permission.list.role",        "&PRIMARY&%role%&WHITE&:");
        data.put("permission.list.permission",  " - &FLAG&%permission%");
        data.put("permission.no_permissions",   "&RED&Hinweis: &SECONDARY&Dieses Plugin nutzt keine Berechtigungen");

        data.put("permission.reload.complete",  "Berechtigungen neu geladen");
        data.put("permission.reload.failed",    "Berechtigungen konnten nicht neu geladen werden");

        data.put("permission.insufficient",     "&RED&F�r diesen Befehl hast Du keine Berechtigung");

        data.put("command.invalid",             "&RED&Ung�ltiger Befehl");

        /*
         * Help messages
         */
        data.put("help.header",                 "&FLAG&==== &PRIMARY&%plugin% Hilfe &FLAG&====");
        data.put("help.plugin",                 "Hilfe zu allen Plugin Befehlen");
        data.put("help.version",                "Zeigt die Version des Plugins an");
        data.put("help.reload",                 "L�dt das Plugin neu");
        data.put("help.permission.reload",      "L�dt die Berechtigungen neu");
        data.put("help.permission.list",        "Zeigt alle Plugin-Berechtigungen an");

        return data;
    }

}
