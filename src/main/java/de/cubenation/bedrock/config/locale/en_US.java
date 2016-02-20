package de.cubenation.bedrock.config.locale;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.service.config.CustomConfigurationFile;
import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.Path;

import java.io.File;

@SuppressWarnings("unused")
public class en_US extends CustomConfigurationFile {

    public static String getFilename() {
        return "locale" + File.separator + "en_US.yml";
    }

    public en_US(BasePlugin plugin) {
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

    @Path("json.no_such_player")
    private String json_no_such_player = "{text:\"%plugin_prefix% \",color:\"white\",extra:[{text:\"Player \",color:\"red\"},{text:\"%player% \",color:\"white\"},{text:\"not found\",color:\"red\"}]}";


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

    @Path("help.command.args.username_uuid.description")
    private String help_args_username_uuid_description  = "Username/UUID eines Spielers";

    @Path("help.command.args.username_uuid.placeholder")
    private String help_args_username_uuid_placeholder  = "Username/UUID";

    @Path("help.plugin")
    private String help_plugin                      = "&TEXT&Display a help for this plugin";

    @Path("help.version")
    private String help_version                     = "&TEXT&Show the plugin version";

    @Path("help.reload")
    private String help_reload                      = "&TEXT&Reload the plugin";

    @Path("help.permissions.list")
    private String help_permission_list             = "&TEXT&Show all plugin roles and permissions";

    @Path("help.permissions.other")
    private String help_permission_other            = "&TEXT&Shows a player's plugin permissions";


    /*
     * PageDesignHelper Messages
    */
    @Path("page.design.header")
    private String page_design_header               = "&FLAG&======= &PRIMARY&%from%/%to% &FLAG&=======";

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

}
