package de.cubenation.bedrock.config.locale;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.service.config.CustomConfigurationFile;
import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.Path;

import java.io.File;

@SuppressWarnings("unused")
public class de_DE extends CustomConfigurationFile {

    public de_DE(BasePlugin plugin) {
        this.setFilename(plugin);
        CONFIG_FILE = new File(plugin.getDataFolder(), this.getFilename());
    }

    @Override
    public void setFilename(BasePlugin plugin) {
        this.filename = "locale" + System.getProperty("file.separator") + "de_DE.yml";
    }


    @Path("version")
    private String version                          = "%plugin_prefix%&RESET& &TEXT&Version &PRIMARY&%version%";


    /**
     * Plugin reload
     */

    @Path("reload.complete")
    @Comment("Plugin reload operations")
    private String reload_complete                  = "%plugin_prefix%&RESET& Plugin neu geladen";

    @Path("reload.failed")
    private String reload_failed                    = "%plugin_prefix%&RESET& Plugin konnte nicht neu geladen werden";


    /**
     * Permission stuff
     */
    @Path("permission.list.header")
    private String permission_list_header           = "%plugin_prefix%&RESET& &SECONDARY&Alle Berechtigungen:";

    @Path("permission.list.role")
    private String permission_list_role             = "&PRIMARY&%role%&WHITE&:";

    @Path("permission.list.permission")
    private String permission_list_permission       = " - &FLAG&%permission%";

    @Path("permission.no_permissions")
    private String permission_no_permissions        = "%plugin_prefix%&RESET& &RED&Hinweis: &SECONDARY&Dieses Plugin nutzt keine Berechtigungen";

    @Path("permission.insufficient")
    private String permission_insufficient          = "%plugin_prefix%&RESET& &RED&Für diesen Befehl hast Du keine Berechtigung";



    @Path("command.invalid")
    private String command_invalid                  = "%plugin_prefix%&RESET& &RED&Ungültiger Befehl";


    /*
     * Help messages
     */

    @Path("help.header")
    private String help_header                      = "&FLAG&==== &PRIMARY&%plugin% Hilfe &FLAG&====";

    @Path("help.command.command")
    private String help_command_command             = "&PRIMARY&/%label%&RESET& &SECONDARY&%commands%&RESET& %args%&RESET&";

    @Path("help.command.divider")
    private String help_command_divider             = "&PRIMARY&|&SECONDARY&";

    @Path("help.command.description")
    private String help_command_description         = "&TEXT&%description%&RESET&";

    @Path("help.command.args.key")
    private String help_command_args_key            = "&SECONDARY&%key%&RESET&";

    @Path("help.command.args.needed")
    private String help_command_args_needed         = "&GRAY&&ITALIC&%argument%&RESET&";

    @Path("help.command.args.optional")
    private String help_command_args_optional       = "&GRAY&&ITALIC&[%argument%]&RESET&";

    @Path("help.command.args.description")
    private String help_command_args_description    = "&TEXT& - %description%&RESET&";

    @Path("help.plugin")
    private String help_plugin                      = "&TEXT&Hilfe zu allen Plugin Befehlen";

    @Path("help.version")
    private String help_version                     = "&TEXT&Zeigt die Version des Plugins an";

    @Path("help.reload")
    private String help_reload                      = "&TEXT&Lädt das Plugin neu";

    @Path("help.permission.list")
    private String help_permission_list             = "&TEXT&Zeigt alle Plugin-Berechtigungen an";


    /*
     * PageDesignHelper Messages
    */
    @Path("page.design.header")
    private String page_design_header               = "&FLAG&======= &PRIMARY&%from%/%to% &FLAG&=======";

}
