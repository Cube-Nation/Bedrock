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

package de.cubenation.bedrock.core.config.locale;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.service.config.CustomConfigurationFile;
import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.Path;

import java.io.File;
import java.util.HashMap;

/**
 * @author Cube-Nation
 * @version 1.0
 */
@SuppressWarnings("unused")
public class en_US extends CustomConfigurationFile {

    public static String getFilename() {
        return "locale" + File.separator + "en_US.yml";
    }

    public en_US(FoundationPlugin plugin) {
        CONFIG_FILE = new File(plugin.getDataFolder(), getFilename());
    }

    @Deprecated
    @Path("version")
    private String version                          = "%plugin_prefix%&RESET& &TEXT&Version &PRIMARY&%version%";

    @Path("json.version")
    private String jsonVersion                          = "{text:\"%plugin_prefix%\",color:\"white\",extra:[{text:\" Version \",color:\"&TEXT&\"},{text:\"%version%\",color:\"&PRIMARY&\"}]}";


    /**
     * Plugin reload
     */

    @Deprecated
    @Path("reload.complete")
    @Comment("Plugin reload operations")
    private String reload_complete                  = "%plugin_prefix%&RESET& Plugin reloaded successfully";

    @Deprecated
    @Path("reload.failed")
    private String reload_failed                    = "%plugin_prefix%&RESET& Could not reload plugin";

    @Path("json.reload.complete")
    @Comment("Plugin reload operations")
    private String json_reload_complete = "{text:\"%plugin_prefix%\",color:\"white\",extra:[{text:\" Plugin reloaded successfully\",color:\"white\"}]}";

    @Path("json.reload.failed")
    private String json_reload_failed = "{text:\"%plugin_prefix%\",color:\"white\",extra:[{text:\"Could not reload plugin\",color:\"white\"}]}";


    /**
     * Permission stuff
     */

    @Deprecated
    @Path("permission.list.header")
    private String permission_list_header           = "%plugin_prefix%&RESET& &SECONDARY&All permissions:";

    @Deprecated
    @Path("permission.list.role")
    private String permission_list_role             = "&PRIMARY&%role%&WHITE&:";

    @Deprecated
    @Path("permission.list.permission")
    private String permission_list_permission       = "&FLAG& - &SECONDARY&%permission%";

    @Deprecated
    @Path("permission.no_permissions")
    private String permission_no_permissions        = "%plugin_prefix%&RESET& &RED&Note: &SECONDARY&This plugin has no permissions";

    @Deprecated
    @Path("permission.insufficient")
    private String permission_insufficient          = "%plugin_prefix%&RESET& &RED&You do not have enough permission for this command";

    @Deprecated
    @Path("command.invalid")
    private String command_invalid                  = "%plugin_prefix%&RESET& &RED&Unknown command";

    @Deprecated
    @Path("must_be_player")
    private String must_be_player                   = "%plugin_prefix%&RESET& &RED&You must be ingame to use this command";

    @Deprecated
    @Path("no_such_player")
    private String no_such_player                   = "%plugin_prefix%&RESET& &RED&Player &SECONDARY&%player% &RED&not found";

    @Path("no_valid_int")
    private String no_valid_int = "{\"text\":\"%plugin_prefix%\",\"color\":\"white\",\"extra\":[{\"text\":\" \",\"color\":\"white\"},{\"text\":\"%input%\",\"color\":\"&SECONDARY&\"},{\"text\":\" is not a valid integer.\",\"color\":\"white\"}]}";

    @Path("no_valid_float")
    private String no_valid_float = "{\"text\":\"%plugin_prefix%\",\"color\":\"white\",\"extra\":[{\"text\":\" \",\"color\":\"white\"},{\"text\":\"%input%\",\"color\":\"&SECONDARY&\"},{\"text\":\" is not a valid floating point number.\",\"color\":\"white\"}]}";

    @Path("no_valid_uuid")
    private String no_valid_uuid = "{\"text\":\"%plugin_prefix%\",\"color\":\"white\",\"extra\":[{\"text\":\" \",\"color\":\"white\"},{\"text\":\"%input%\",\"color\":\"&SECONDARY&\"},{\"text\":\" is not a valid UUID.\",\"color\":\"white\"}]}";

    @Path("no_valid_enum_constant")
    private String no_valid_enum_constant = "{\"text\":\"%plugin_prefix%\",\"color\":\"white\",\"extra\":[{\"text\":\" \",\"color\":\"white\"},{\"text\":\"%input%\",\"color\":\"&SECONDARY&\"},{\"text\":\" is not a valid option. Use one of the following:\",\"color\":\"white\"},{\"text\":\" &%constants%\",\"color\":\"&SECONDARY&\"}]}";


    @Path("json.permission.list.header")
    private String json_permission_list_header = "{text:\"%plugin_prefix% \",color:\"white\",extra:[{text:\"All permissions:\",color:\"&SECONDARY&\"}]}";

    @Path("json.permission.list.role")
    private String json_permission_list_role = "{text:\"%role%\",color:\"&PRIMARY&\",extra:[{text:\":\",color:\"white\"}]}";

    @Path("json.permission.list.permission")
    private String json_permission_list_permission = "{text:\" - \",color:\"&FLAG&\",extra:[{text:\"%permission%\",color:\"&SECONDARY&\"}]}%";

    @Path("json.permission.no_permissions")
    private String json_permission_no_permissions = "{text:\"%plugin_prefix%\",color:\"white\",extra:[{text:\" Note: \",color:\"red\"},{text:\"This plugin has no permissions\",color:\"%SECONDARY%\"}]}";

    @Path("json.permission.insufficient")
    private String json_permission_insufficient = "{text:\"%plugin_prefix% \",color:\"white\",extra:[{text:\"You do not have enough permission for this command!\",color:\"red\"}]}";

    @Path("json.command.invalid")
    private String json_command_invalid = "{text:\"%plugin_prefix% \",color:\"white\",extra:[{text:\"Unknown command\",color:\"red\"}]}";

    @Path("json.must_be_player")
    private String json_must_be_player = "{text:\"%plugin_prefix% \",color:\"white\",extra:[{text:\"You must be ingame to use this command\",color:\"red\"}]}";

    @Path("json.no_such_player.specific")
    private String json_no_such_player_specific = "{text:\"%plugin_prefix% \",color:\"white\",extra:[{text:\"Player \",color:\"red\"},{text:\"%player% \",color:\"white\"},{text:\"not found\",color:\"red\"}]}";

    @Path("json.no_such_player.default")
    private String json_no_such_player_default = "{text:\"%plugin_prefix% \",color:\"white\",extra:[{text:\"Player not found\",color:\"red\"}]}";

    /*
     * Help messages
     */

    @Path("help.header")
    private String help_header                      = "&FLAG&==== &PRIMARY&%plugin% Help &FLAG&====";

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


    @Path("help.subcommand.alias.desc")
    private String help_subcommand_alias_desc = "Alternatively, you can also use:";

    @Path("help.subcommand.alias.value")
    private String help_subcommand_alias_value = " - %alias%";


    /**
     * Commands
     */

    @Path("command.bedrock.help.desc")
    private String help_plugin = "Display a help for this plugin";

    @Path("command.bedrock.cmd.list.desc")
    private String string = "Displays a list of all plugin commands";

    @Path("command.bedrock.permissions.list.desc")
    private String help_permission_list = "Show all plugin roles and permissions";

    @Path("command.bedrock.permissions.desc")
    private String help_permission_other = "Shows a player's plugin permissions";

    @Path("command.bedrock.reload.desc")
    private String help_reload = "Reload the plugin";

    @Path("command.bedrock.version.desc")
    private String help_version = "Show the plugin version";

    @Path("command.bedrock.regeneratelocale.desc")
    private String help_regenerate_locale = "Recreate the file for the current locale";

    @Path("command.bedrock.playerinfo.desc")
    private String help_playerinfo_desc = "Shows information about one or more Bedrock players";


    /**
     * Args
     */


    @Path("command.bedrock.username_uuid.desc")
    private String help_args_username_uuid_description = "Username/UUID of a Player";

    @Path("command.bedrock.username_uuid.ph")
    private String help_args_username_uuid_placeholder = "Username/UUID";

    @Path("command.bedrock.page")
    private HashMap<String, String> command_bedrock_page = new HashMap<String, String>() {{
        put("ph", "Page");
        put("desc", "Page");
    }};

    /*
     * PageDesignHelper Messages
    */
    @Path("page.design.header")
    private String page_design_header               = "&FLAG&======= &PRIMARY&%from%/%to% &FLAG&=======";

    @Path("json.page.design.header")
    private String json_page_design_header = "{\"color\":\"&FLAG&\",\"underlined\":false,\"extra\":[{\"color\":\"white\",\"underlined\":false,\"bold\":false,\"strikethrough\":false,\"text\":\" \",\"italic\":false,\"obfuscated\":false},{\"color\":\"&SECONDARY&\",\"underlined\":false,\"bold\":false,\"strikethrough\":false,\"text\":\"%pageheader%\",\"italic\":false,\"obfuscated\":false},{\"color\":\"white\",\"underlined\":false,\"bold\":false,\"strikethrough\":false,\"text\":\" \",\"italic\":false,\"obfuscated\":false},{\"color\":\"&SECONDARY&\",\"underlined\":false,\"bold\":false,\"strikethrough\":false,\"text\":\"(%currentpagecount%\\/%totalpagecount%)\",\"italic\":false,\"obfuscated\":false},{\"color\":\"white\",\"underlined\":false,\"bold\":false,\"strikethrough\":false,\"text\":\" \",\"italic\":false,\"obfuscated\":false},{\"color\":\"&FLAG&\",\"underlined\":false,\"bold\":false,\"strikethrough\":true,\"text\":\"--------\",\"italic\":false,\"obfuscated\":false}],\"bold\":false,\"strikethrough\":true,\"text\":\"--------\",\"italic\":false,\"obfuscated\":false}";

    /*
     * Facing Directions
     */

    @Path("direction.south")
    private String south = "south";

    @Path("direction.southwest")
    private String southwest = "southwest";

    @Path("direction.west")
    private String west = "west";

    @Path("direction.nothwest")
    private String northwest = "nothwest";

    @Path("direction.north")
    private String north = "north";

    @Path("direction.northeast")
    private String northeast = "northeast";

    @Path("direction.east")
    private String east = "east";

    @Path("direction.southeast")
    private String southeast = "southeast";

    // Misc

    @Path("no_description")
    private String no_description = "No description";

}
