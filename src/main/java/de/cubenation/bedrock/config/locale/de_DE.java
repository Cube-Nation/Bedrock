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


    @Deprecated
    @Path("version")
    private String version = "%plugin_prefix%&RESET& &TEXT&Version &PRIMARY&%version%";

    @Path("json.version")
    private String jsonVersion = "{text:\"%plugin_prefix%\",color:\"white\",extra:[{text:\" Version \",color:\"&TEXT&\"},{text:\"%version%\",color:\"&PRIMARY&\"}]}";


    /**
     * Plugin reload
     */

    @Deprecated
    @Path("reload.complete")
    @Comment("Plugin reload operations")
    private String reload_complete = "%plugin_prefix%&RESET& Plugin neu geladen";

    @Deprecated
    @Path("reload.failed")
    private String reload_failed = "%plugin_prefix%&RESET& Plugin konnte nicht neu geladen werden";


    @Path("json.reload.complete")
    @Comment("Plugin reload operations")
    private String json_reload_complete = "{text:\"%plugin_prefix%\",color:\"white\",extra:[{text:\" Plugin neu geladen\",color:\"white\"}]}";

    @Path("json.reload.failed")
    private String json_reload_failed = "{text:\"%plugin_prefix%\",color:\"white\",extra:[{text:\"Plugin konnte nicht neu geladen werden\",color:\"white\"}]}";


    /**
     * Permission stuff
     */

    @Deprecated
    @Path("permission.list.header")
    private String permission_list_header = "%plugin_prefix%&RESET& &SECONDARY&Alle Berechtigungen:";

    @Deprecated
    @Path("permission.list.role")
    private String permission_list_role = "&PRIMARY&%role%&WHITE&:";

    @Deprecated
    @Path("permission.list.permission")
    private String permission_list_permission = "&FLAG& - &SECONDARY&%permission%";

    @Deprecated
    @Path("permission.no_permissions")
    private String permission_no_permissions = "%plugin_prefix%&RESET& &RED&Hinweis: &SECONDARY&Dieses Plugin nutzt keine Berechtigungen";

    @Deprecated
    @Path("permission.insufficient")
    private String permission_insufficient = "%plugin_prefix%&RESET& &RED&Für diesen Befehl hast Du keine Berechtigung";

    @Deprecated
    @Path("command.invalid")
    private String command_invalid = "%plugin_prefix%&RESET& &RED&Ungültiger Befehl";

    @Deprecated
    @Path("must_be_player")
    private String must_be_player = "%plugin_prefix%&RESET& &RED&Um diesen Befehl zu verwenden musst Du im Spiel sein";

    @Deprecated
    @Path("no_such_player")
    private String no_such_player = "%plugin_prefix%&RESET& &RED&Spieler &SECONDARY&%player% &RED&nicht gefunden";


    @Path("json.permission.list.header")
    private String json_permission_list_header = "{text:\"%plugin_prefix% \",color:\"white\",extra:[{text:\"Alle Berechtigungen:\",color:\"&SECONDARY&\"}]}";

    @Path("json.permission.list.role")
    private String json_permission_list_role = "{text:\"%role%\",color:\"&PRIMARY&\",extra:[{text:\":\",color:\"white\"}]}";

    @Path("json.permission.list.permission")
    private String json_permission_list_permission = "{text:\" - \",color:\"&FLAG&\",extra:[{text:\"%permission%\",color:\"&SECONDARY&\"}]}%";

    @Path("json.permission.no_permissions")
    private String json_permission_no_permissions = "{text:\"%plugin_prefix%\",color:\"white\",extra:[{text:\" Hinweis: \",color:\"red\"},{text:\"Dieses Plugin nutzt keine Berechtigungen\",color:\"%SECONDARY%\"}]}";

    @Path("json.permission.insufficient")
    private String json_permission_insufficient = "{text:\"%plugin_prefix% \",color:\"white\",extra:[{text:\"Für diesen Befehl hast Du keine Berechtigung!\",color:\"red\"}]}";

    @Path("json.command.invalid")
    private String json_command_invalid = "{text:\"%plugin_prefix% \",color:\"white\",extra:[{text:\"Ungültiger Befehl\",color:\"red\"}]}";

    @Path("json.must_be_player")
    private String json_must_be_player = "{text:\"%plugin_prefix% \",color:\"white\",extra:[{text:\"Um diesen Befehl zu verwenden musst Du im Spiel sein\",color:\"red\"}]}";

    @Path("json.no_such_player")
    private String json_no_such_player = "{text:\"%plugin_prefix% \",color:\"white\",extra:[{text:\"Spieler \",color:\"red\"},{text:\"%player% \",color:\"&SECONDARY&\"},{text:\"nicht gefunden\",color:\"red\"}]}";

    /*
     * Help messages
     */

    @Path("help.header")
    private String help_header = "&FLAG&==== &PRIMARY&%plugin% Hilfe &FLAG&====";

    @Path("help.command.command")
    private String help_command_command = "&PRIMARY&/%label%&RESET& &SECONDARY&%commands%&RESET& %args%&RESET&";

    @Path("help.command.divider")
    private String help_command_divider = "&PRIMARY&|&SECONDARY&";

    @Path("help.command.description")
    private String help_command_description = "&TEXT&%description%&RESET&";

    @Path("help.command.args.key")
    private String help_command_args_key = "&SECONDARY&%key%&RESET&";

    @Path("help.command.args.needed")
    private String help_command_args_needed = "&GRAY&&ITALIC&%argument%&RESET&";

    @Path("help.command.args.optional")
    private String help_command_args_optional = "&GRAY&&ITALIC&[%argument%]&RESET&";

    @Path("help.command.args.description")
    private String help_command_args_description = "&TEXT& - %description%&RESET&";


    /*
     * PageDesignHelper Messages
    */
    @Path("page.design.header")
    private String page_design_header = "&FLAG&======= &PRIMARY&%from%/%to% &FLAG&=======";


    /*
     * Facing Directions
     */

    @Path("direction.south")
    private String south = "Süden";

    @Path("direction.southwest")
    private String southwest = "Südwesten";

    @Path("direction.west")
    private String west = "Westen";

    @Path("direction.nothwest")
    private String northwest = "Nordwesten";

    @Path("direction.north")
    private String north = "Norden";

    @Path("direction.northeast")
    private String northeast = "Nordosten";

    @Path("direction.east")
    private String east = "Osten";

    @Path("direction.southeast")
    private String southeast = "Südosten";

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

    @Path("command.bedrock.help.desc")
    private String help_plugin = "Hilfe zu allen Plugin Befehlen";

    @Path("command.bedrock.cmd.list.desc")
    private String string = "Gibt dir eine Liste aller Haupt-Commands, welche dieses Plugin bietet";

    @Path("command.bedrock.permissions.list.desc")
    private String help_permission_list = "Zeigt alle Plugin-Berechtigungen an";

    @Path("command.bedrock.permissions.desc")
    private String help_permission_other = "Zeigt Plugin-Berechtigungen eines Spielers an";

    @Path("command.bedrock.reload.desc")
    private String help_reload = "Lädt das Plugin neu";

    @Path("command.bedrock.version.desc")
    private String help_version = "Zeigt die Version des Plugins an";


    /**
     * Args
     */


    @Path("command.bedrock.username_uuid.desc")
    private String help_args_username_uuid_description = "Username/UUID eines Spielers";

    @Path("command.bedrock.username_uuid.ph")
    private String help_args_username_uuid_placeholder = "Username/UUID";

    // CommandList

    @Path("plugin.commands.header")
    private String plugin_commands_header = "%plugin_prefix% Verfügbare Commands:";

    @Path("plugin.commands.list")
    private String plugin_command_list = "&PRIMARY&%command%: &SECONDARY&%description%";


}
