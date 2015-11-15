package de.cubenation.bedrock.config.locale;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.service.config.CustomConfigurationFile;
import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.Path;

import java.io.File;

@SuppressWarnings("unused")
public class de_DE extends CustomConfigurationFile {

    public static String getFilename() {
        return "locale" + File.separator + "de_DE.yml";
    }

    public de_DE(BasePlugin plugin) {
        CONFIG_FILE = new File(plugin.getDataFolder(), getFilename());
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

    @Path("must_be_player")
    private String must_be_player                   = "%plugin_prefix%&RESET& &RED&Um diesen Befehl zu verwenden musst Du im Spiel sein";

    @Path("no_such_player")
    private String no_such_player                   = "%plugin_prefix%&RESET& &RED&Spieler &SECONDARY&%player% &RED&nicht gefunden";

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

    @Path("help.command.args.username_uuid.description")
    private String help_args_username_uuid_description  = "Username/UUID eines Spielers";

    @Path("help.command.args.username_uuid.placeholder")
    private String help_args_username_uuid_placeholder  = "Username/UUID";

    @Path("help.plugin")
    private String help_plugin                      = "&TEXT&Hilfe zu allen Plugin Befehlen";

    @Path("help.version")
    private String help_version                     = "&TEXT&Zeigt die Version des Plugins an";

    @Path("help.reload")
    private String help_reload                      = "&TEXT&Lädt das Plugin neu";

    @Path("help.permissions.list")
    private String help_permission_list             = "&TEXT&Zeigt alle Plugin-Berechtigungen an";

    @Path("help.permissions.other")
    private String help_permission_other            = "&TEXT&Zeigt Plugin-Berechtigungen eines Spielers an";


    /*
     * PageDesignHelper Messages
    */
    @Path("page.design.header")
    private String page_design_header               = "&FLAG&======= &PRIMARY&%from%/%to% &FLAG&=======";




    /*
     * Bedrock Commands
     */

    // Toggle NameChange

    @Path("command.namechange.toggle.desc")
    private String command_namechange_toggle_desc = "Schaltet die Namensänderungs-Meldung an oder aus";

    @Path("command.namechange.toggle.args.toggle")
    private String command_namechange_toggle_args_toggle = "Ein oder Aus";


    // Messages

    @Path("namechange.notify.message")
    private String namechange_notify_message = "%plugin_prefix%&RESET& &PRIMARY&%oldName% &SECONDARY&heißt jetzt &PRIMARY&%newName%";

    @Path("namechange.toggle.message")
    private String namechange_toggle_message = "%plugin_prefix%&RESET& &TEXT&Nachrichten über Namensänderungen wurden &SECONDARY&%toggle%";

    @Path("namechange.toggle.enabled")
    private String namechange_toggle_on = "aktiviert";

    @Path("namechange.toggle.disabled")
    private String namechange_toggle_off = "deaktiviert";

    /**
     * Commands
     */

    // CommandList

    @Path("plugin.commands.header")
    private String plugin_commands_header = "%plugin_prefix% Verfügbare Commands:";

    @Path("plugin.commands.list")
    private String plugin_command_list = "&PRIMARY&%command%: &SECONDARY&%description%";

}
