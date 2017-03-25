package de.cubenation.api.bedrock.config.locale;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.service.config.CustomConfigurationFile;
import net.cubespace.Yamler.Config.Path;

import java.io.File;

@SuppressWarnings({"unused", "WeakerAccess"})
public class de_DE extends CustomConfigurationFile {

    public static String getFilename() {
        return "locale" + File.separator + "de_DE.yml";
    }

    public de_DE(BasePlugin plugin) {
        CONFIG_FILE = new File(plugin.getDataFolder(), getFilename());
    }


    @Path("version")
    private String version = "{\"text\":\"%plugin_prefix%\",\"color\":\"white\",\"extra\":[{\"text\":\" \",\"color\":\"white\"},{\"text\":\"Version \",\"color\":\"&TEXT&\"},{\"text\":\"%version%\",\"color\":\"&PRIMARY&\"}]}";


    /**
     * Plugin reload
     */

    @Path("reload.complete")
    private String reload_complete = "{\"text\":\"%plugin_prefix%\",\"color\":\"white\",\"extra\":[{\"text\":\" \",\"color\":\"white\"},{\"text\":\"Plugin neu geladen\",\"color\":\"white\"}]}";

    @Path("reload.failed")
    private String reload_failed = "{\"text\":\"%plugin_prefix%\",\"color\":\"white\",\"extra\":[{\"text\":\" \",\"color\":\"white\"},{\"text\":\"Plugin konnte nicht neu geladen werden\",\"color\":\"white\"}]}";


    /**
     * Permission stuff
     */


    @Path("permission.list.header")
    private String permission_list_header = "{\"text\":\"%plugin_prefix%\",\"color\":\"white\",\"extra\":[{\"text\":\" \",\"color\":\"white\"},{\"text\":\"Alle Berechtigungen:\",\"color\":\"&SECONDARY&\"}]}";

    @Path("permission.list.role")
    private String permission_list_role = "{\"text\":\"%role%\",\"color\":\"&PRIMARY&\",\"extra\":[{\"text\":\":\",\"color\":\"white\"}]}";

    @Path("permission.list.permission")
    private String permission_list_permission = "{\"text\":\" - \",\"color\":\"&FLAG&\",\"extra\":[{\"text\":\"%permission%\",\"color\":\"&SECONDARY&\"}]}";

    @Path("permission.insufficient")
    private String permission_insufficient = "{\"text\":\"%plugin_prefix%\",\"color\":\"white\",\"extra\":[{\"text\":\" \",\"color\":\"white\"},{\"text\":\"Für diesen Befehl hast Du keine Berechtigung!\",\"color\":\"red\"}]}";

    @Path("command.invalid")
    private String command_invalid = "{\"text\":\"%plugin_prefix%\",\"color\":\"white\",\"extra\":[{\"text\":\" \",\"color\":\"white\"},{\"text\":\"Ungültiger Befehl\",\"color\":\"red\"}]}";

    @Path("must_be_player")
    private String must_be_player = "{\"text\":\"%plugin_prefix%\",\"color\":\"white\",\"extra\":[{\"text\":\" \",\"color\":\"white\"},{\"text\":\"Um diesen Befehl zu verwenden musst Du im Spiel sein\",\"color\":\"red\"}]}";

    @Path("no_such_setting")
    private String no_such_setting = "{\"text\":\"%plugin_prefix%\",\"color\":\"white\",\"extra\":[{\"text\":\" \",\"color\":\"white\"},{\"text\":\"Einstellungen für \",\"color\":\"red\"},{\"text\":\"%key% \",\"color\":\"&SECONDARY&\"},{\"text\":\"wurden nicht gefunden\",\"color\":\"red\"}]}";


    @Path("no_such_player.specific")
    private String no_such_player_specific = "{\"text\":\"%plugin_prefix%\",\"color\":\"white\",\"extra\":[{\"text\":\" \",\"color\":\"white\"},{\"text\":\"Spieler \",\"color\":\"red\"},{\"text\":\"%player% \",\"color\":\"&SECONDARY&\"},{\"text\":\"wurde nicht gefunden\",\"color\":\"red\"}]}";

    @Path("no_such_player.default")
    private String no_such_player_default = "{\"text\":\"%plugin_prefix%\",\"color\":\"white\",\"extra\":[{\"text\":\" \",\"color\":\"white\"},{\"text\":\"Spieler wurde nicht gefunden\",\"color\":\"red\"}]}";

    @Path("no_such_world")
    private String no_such_world = "{\"text\":\"%plugin_prefix%\",\"color\":\"white\",\"extra\":[{\"text\":\" \",\"color\":\"white\"},{\"text\":\"Welt \",\"color\":\"red\"},{\"text\":\"%world%\",\"color\":\"&SECONDARY&\"},{\"text\":\"wurde nicht gefunden\",\"color\":\"red\"}]}";

    @Path("no_such_world_empty")
    private String no_such_world_empty = "{\"text\":\"%plugin_prefix%\",\"color\":\"white\",\"extra\":[{\"text\":\" \",\"color\":\"white\"},{\"text\":\"Die Welt wurde nicht gefunden\",\"color\":\"red\"}]}";


    /*
     * Help messages
     */

    @Path("help.header")
    private String help_header = "%plugin% Hilfe";

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



    @Path("help.subcommand.alias.desc")
    private String help_subcommand_alias_desc = "Alternativ kannst du folgendes nutzen:";

    @Path("help.subcommand.alias.value")
    private String help_subcommand_alias_value = " - %alias%";


    @Path("help.info.arg.required.symbol")
    private String string2 = "-";

    @Path("help.info.arg.optional.symbol")
    private String string3 = "*";

    @Path("help.info.arg.required.desc")
    private String string1 = "Bedeutet dass dieses Argument benötigt wird.";

    @Path("help.info.arg.optional.desc")
    private String string4 = "Bedeutet dass dieses Argument optional ist.";

    /*
     * PageDesignHelper Messages
    */
    @Path("page.design.header")
    private String page_design_header = "&FLAG&======= &PRIMARY&%from%/%to% &FLAG&=======";

    @Path("json.page.design.header")
    private String json_page_design_header = "{\"text\":\"\",\"extra\":[{\"color\":\"&FLAG&\",\"strikethrough\":true,\"text\":\"--------\"},{\"color\":\"white\",\"text\":\" \"},{\"color\":\"&SECONDARY&\",\"text\":\"%pageheader%\"},{\"color\":\"white\",\"text\":\" \"},{\"color\":\"&SECONDARY&\",\"text\":\"(%currentpagecount%\\/%totalpagecount%)\"},{\"color\":\"white\",\"text\":\" \"},{\"color\":\"&FLAG&\",\"strikethrough\":true,\"text\":\"--------\"}]}";

    @Path("json.page.notexsist")
    private String json_page_not_exists = "{text:\"%plugin_prefix% \",extra:[{text:\"Diese Seite gibt es nicht.\",color:\"&SECONDARY&\"}]}";


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

    @Path("command.bedrock.settings.info.desc")
    private String help_settings_info = "Zeigt Infos zu Settings an";

    @Path("command.bedrock.permissions.desc")
    private String help_permission_other = "Zeigt Plugin-Berechtigungen eines Spielers an";

    @Path("command.bedrock.reload.desc")
    private String help_reload = "Lädt das Plugin neu";

    @Path("command.bedrock.version.desc")
    private String help_version = "Zeigt die Version des Plugins an";

    @Path("command.bedrock.regeneratelocale.desc")
    private String help_regenerate_locale = "Erstellt die Datei für das aktuelle Locale neu";


    /**
     * Args
     */


    @Path("command.bedrock.username_uuid.desc")
    private String help_args_username_uuid_description = "Username/UUID eines Spielers";

    @Path("command.bedrock.username_uuid.ph")
    private String help_args_username_uuid_placeholder = "Username/UUID";

    @Path("command.bedrock.key.desc")
    private String help_args_key_description = "Eindeutiger Key";

    @Path("command.bedrock.key.ph")
    private String help_args_key_placeholder = "Key";

    // CommandList

    @Path("plugin.command.list.header")
    private String plugin_commands_header = "{\"text\":\"[\",\"color\":\"dark_gray\",\"extra\":[{\"text\":\"Bedrock\",\"color\":\"gold\"},{\"text\":\"]\",\"color\":\"dark_gray\"},{\"text\":\" \",\"color\":\"white\"},{\"text\":\"Verfügbare Commands\",\"color\":\"&PRIMARY&\"}]}";

    @Path("plugin.command.list.entry")
    private String plugin_command_list = "{\"text\":\"[\",\"color\":\"dark_gray\",\"extra\":[{\"text\":\"Bedrock\",\"color\":\"gold\"},{\"text\":\"]\",\"color\":\"dark_gray\"},{\"text\":\" \",\"color\":\"white\"},{\"text\":\"*\",\"color\":\"&PRIMARY&\"},{\"text\":\" \",\"color\":\"white\"},{\"text\":\"%command%\",\"color\":\"&SECONDARY&\"},{\"text\":\" \",\"color\":\"white\"},{\"text\":\"%description%\",\"color\":\"&TEXT&\"}]}";


}
