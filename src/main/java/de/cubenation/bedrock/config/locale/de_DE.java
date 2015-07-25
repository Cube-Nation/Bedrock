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
        HashMap<String,Object> data		= new HashMap<String,Object>();


        data.put("version",                     "Version &PRIMARY&%version%");

        /*
         * Help messages
         */
        data.put("help.version",                "Zeigt die Version des Plugins an");

        return data;
    }

}
