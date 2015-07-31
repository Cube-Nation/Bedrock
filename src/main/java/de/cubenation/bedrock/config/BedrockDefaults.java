package de.cubenation.bedrock.config;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.service.config.CustomConfigurationFile;
import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.Comments;
import net.cubespace.Yamler.Config.Path;
import net.md_5.bungee.api.ChatColor;

import java.io.File;

@SuppressWarnings("unused")
public class BedrockDefaults extends CustomConfigurationFile {

    public String[] getHeader() {
        return new String[]{
                "",
                "This file has been automatically created by the Bedrock Plugin.",
                "Here you may find all settings from the Bedrock Plugin that are applicable to this certain Plugin,",
                "depending on which Bedrock services were used.",
                "",
                "1. To Server owners/Plugin administrators:",
                "   You may change these settings to your needs, *except settings in the service.config tree*. If you",
                "   do so, the whole file will be regenerated!",
                "",
                "   In case you delete or set an invalid value to an option, the default value from the Bedrock Plugin",
                "   config.yml are taken.",
                "",
                "   Please note: In general it is a bad idea to edit the config.yml if the Bedrock Plugin. Please leave",
                "                this file *as is* if so.",
                "                To change the behaviour of a certain plugin refer to its own config.yml!",
                "",
                "",
                "2. To Plugin developers:",
                "   Tbis file is NOT intended to hold a custom configuration for this plugin. Custom settings have",
                "   to be placed in custom configuration files, which can be registered, read, modified and saved by",
                "   the Bedrock config service.",
                "   For more details on how to manage custom configuration files please see here:",
                "   http://tbd", //TODO: set link to public docs
                "",
                "   In case this plugin overrides the Bedrock config service (and thereby the creation of this file),",
                "   settings in this file are not recognized anymore by the plugin and the defaults will be used.",
                "   You have been warned.",
                "",
                "",
                "3. To whom it may concern:",
                "   If you encounter any errors or bugs feel free to file a ticket:",
                "   http://cube-nation.de:8082/secure/CreateIssueDetails!init.jspa?pid=10202&issuetype=1",
                ""
        };
    }

    public BedrockDefaults() {

    }

    public BedrockDefaults(BasePlugin plugin, String name) {
        CONFIG_FILE = new File(plugin.getDataFolder(), "config.yml");
        CONFIG_HEADER = getHeader();
    }


    /**
     * Scheme Color Service
     */
    @Path("service.colorscheme.name")
    @Comments({
            "The name of the scheme to be used.",
            "Currently there are these schemes available:",
            "DEFAULT, RED, GREEN, BLUE, YELLOW and CUSTOM",
            "",
            "If you want to use a custom color scheme, you can specify colors like so:",
            "",
            "  name: CUSTOM",
            "  primary: GOLD",
            "  secondary: YELLOW",
            "  flag: DARK_GRAY",
            "  text: WHITE"
    })
    private String colorscheme_name         = "DEFAULT";

    @Path("service.colorscheme.primary")
    @Comment("The primary color")
    private String colorscheme_primary      = null;

    @Path("service.colorscheme.secondary")
    @Comment("The secondary color")
    private String colorscheme_secondary    = null;

    @Path("service.colorscheme.flag")
    @Comment("The flag color")
    private String colorscheme_flag         = null;

    @Path("service.colorscheme.text")
    @Comment("The text color")
    private String colorscheme_text         = null;


    public void setColorSchemeName(String name) {
        this.colorscheme_name = name;
    }

    public void setColorSchemePrimary(String name) {
        this.colorscheme_primary = name.toUpperCase();
    }

    public void setColorSchemePrimary(ChatColor color) {
        this.setColorSchemePrimary(color.getName());
    }

    public void setColorSchemeSecondary(String name) {
        this.colorscheme_secondary = name.toUpperCase();
    }

    public void setColorSchemeSecondary(ChatColor color) {
        this.setColorSchemeSecondary(color.getName());
    }

    public void setColorSchemeFlag(String name) {
        this.colorscheme_flag = name.toUpperCase();
    }

    public void setColorSchemeFlag(ChatColor color) {
        this.setColorSchemeFlag(color.getName());
    }

    public void setColorSchemeText(String name) {
        this.colorscheme_text = name.toUpperCase();
    }

    public void setColorSchemeText(ChatColor color) {
        this.setColorSchemeText(color.getName());
    }



    /**
     * Confirm Service
     */

    @Path("service.confirm.timeout")
    @Comment("The timeout value. After this time a confirm command is being invalidated")
    private int confirm_timeout             = 30;

    public void setConfirmTimeout(int timeout) {
        this.confirm_timeout = timeout;
    }



    /**
     * Pageable List Service
     */

    @Path("service.pageablelist.timeout")
    @Comment("After this timeout a pageable list is being invalidated")
    private int pageablelist_timeout        = 180;

    @Path("service.pageablelist.next_amount")
    @Comment("mount of entries per page.")
    private int getPageablelist_next_amount = 10;

    public void setPageablelistTimeout(int timeout) {
        this.pageablelist_timeout = timeout;
    }

    public void setGetPageablelistAextAmount(int next_amount) {
        this.getPageablelist_next_amount = next_amount;
    }



    /**
     * Localization Service
     */

    @Path("service.localization.locale")
    @Comment("the default locale that is being represented by a file, e.g. de_DE represents locale/de_DE.yml.")
    private String localization_locale      = "en_US";

    public void setLocalizationLocale(String locale) {
        this.localization_locale = locale;
    }



    /**
     * Permission Service
     */

    @Path("service.permission.file_name")
    @Comment("File where all permissions are stored and read from.")
    private String permission_file_name     = "permissions.yml";

    @Path("service.permission.grant_all_permssions_to_op")
    @Comment("Does Operators have all permissions? Valid values are true or false.")
    private boolean permission_grant_op     = true;

    @Path("service.permission.prefix")
    @Comment("custom permission prefix. If left empty or undefined the lowercased plugin name will be taken as prefix.")
    private String permission_prefix        = "";

    public void setPermissionFileName(String filename) {
        this.permission_file_name = filename;
    }

    public void setPermission_grant_op(boolean grant_op) {
        this.permission_grant_op = grant_op;
    }

    public void setPermissionPrefix(String prefix) {
        this.permission_prefix = prefix;
    }



    /**
     * Metrics service
     */

    @Path("service.metrics.use")
    @Comment("Submit plugin metrics to mcstats.org. Defaults to true.")
    private boolean metrics_use             = false;
    //TODO: set to true for all plugins once the Bukkit/mcstats.org sites are set up

    public void setMetricsUse(boolean metrics_use) {
        this.metrics_use = metrics_use;
    }



    /**
     * Config Service
     */

    @Path("service.config.do_not_delete_me")
    @Comments({
            "Indicates that this configuration file comes from Bedrock.",
            "If you delete this entry (or file), the whole file will be regenerated.",
            "You have been warned!"
    })
    private String config_dnd         = "Seriously. Do not delete this";

}