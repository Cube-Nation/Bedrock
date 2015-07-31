package de.cubenation.bedrock.config.locale;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.service.config.CustomConfigurationFile;
import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.Path;

import java.io.File;

@SuppressWarnings("unused")
public class en_US extends CustomConfigurationFile {

    public en_US(BasePlugin plugin) {
        this.setFilename(plugin);
        CONFIG_FILE = new File(plugin.getDataFolder(), this.getFilename());
    }

    @Override
    public void setFilename(BasePlugin plugin) {
        this.filename = "locale" + System.getProperty("file.separator") + "en_US.yml";
    }


    @Path("version")
    private String version                          = "%plugin_prefix%&RESET& &TEXT&Version &PRIMARY&%version%";


    /**
     * Plugin reload
     */

    @Path("reload.complete")
    @Comment("Plugin reload operations")
    private String reload_complete                  = "%plugin_prefix%&RESET& Plugin reloaded successfully";

    @Path("reload.failed")
    private String reload_failed                    = "%plugin_prefix%&RESET& Could not reload plugin";


    /**
     * Permission stuff
     */
    @Path("permission.list.header")
    private String permission_list_header           = "%plugin_prefix%&RESET& &SECONDARY&All permissions:";

    @Path("permission.list.role")
    private String permission_list_role             = "&PRIMARY&%role%&WHITE&:";

    @Path("permission.list.permission")
    private String permission_list_permission       = " - &FLAG&%permission%";

    @Path("permission.no_permissions")
    private String permission_no_permissions        = "%plugin_prefix%&RESET& &RED&Note: &SECONDARY&This plugin has no permissions";

    @Path("permission.insufficient")
    private String permission_insufficient          = "%plugin_prefix%&RESET& &RED&You do not have enough permission for this command";



    @Path("command.invalid")
    private String command_invalid                  = "%plugin_prefix%&RESET& &RED&Unknown command";


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

    @Path("help.plugin")
    private String help_plugin                      = "&TEXT&Display a help for this plugin";

    @Path("help.version")
    private String help_version                     = "&TEXT&Show the plugin version";

    @Path("help.reload")
    private String help_reload                      = "&TEXT&Reload the plugin";

    @Path("help.permission.list")
    private String help_permission_list             = "&TEXT&Show all plugin roles and permissions";


    /*
     * PageDesignHelper Messages
    */
    @Path("page.design.header")
    private String page_design_header               = "&FLAG&======= &PRIMARY&%from%/%to% &FLAG&=======";

}
