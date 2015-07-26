package de.cubenation.bedrock.service.localization;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.BedrockPlugin;
import de.cubenation.bedrock.exception.LocalizationNotFoundException;
import de.cubenation.bedrock.exception.NoSuchRegisterableException;
import de.cubenation.bedrock.exception.ServiceInitException;
import de.cubenation.bedrock.exception.ServiceReloadException;
import de.cubenation.bedrock.service.ServiceInterface;
import de.cubenation.bedrock.service.customconfigurationfile.CustomConfigurationRegistry;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocalizationService implements ServiceInterface {

    private BasePlugin plugin;

    private String locale;

    private YamlConfiguration data;

    public LocalizationService(BasePlugin plugin, String locale) {
        this.setPlugin(plugin);
        this.setLocale(locale);
    }

    @Override
    public void init() throws ServiceInitException {
        // determine locale file and check if it exists
        try {
            this.loadLocaleFiles();
        } catch (IOException e) {
            throw new ServiceInitException(e.getMessage());
        }
    }

    @Override
    public void reload() throws ServiceReloadException {
        try {
            this.init();
        } catch (ServiceInitException e) {
            throw new ServiceReloadException(e.getMessage());
        }
    }


    /*
     * Plugin Getter/Setter
     */
    public BasePlugin getPlugin() {
        return plugin;
    }

    public void setPlugin(BasePlugin plugin) {
        this.plugin = plugin;
    }


    /*
     * Locale Getter/Setter
     */
    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }


    private void loadLocaleFiles() throws IOException {
        // check if there are any locale files
        String[] language_files = {
                this.getLocale(),
                BedrockPlugin.getInstance().getConfig().getString("service.localization.locale")
        };

        YamlConfiguration yc = null;
        for (String file : language_files) {
            try {
                yc = this.loadLocaleFile(file);
            } catch (NoSuchRegisterableException e) {
                yc = null;
            }
        }

        if (yc != null) {
            this.data = yc;
            return;
        }

        // create a default locale file
        new DefaultLocale(this.getPlugin());
    }


    private YamlConfiguration loadLocaleFile(String file) throws NoSuchRegisterableException {
        String locale_file =
                BedrockPlugin.getInstance().getConfig().getString("service.localization.locale_dir") +
                        java.lang.System.getProperty("file.separator") +
                        file + ".yml";

        return CustomConfigurationRegistry.get(this.plugin, locale_file, null).load();
    }


    public String getTranslation(String ident, String[] args) throws LocalizationNotFoundException {
        String s;
        try {
            s = this.data.getString(ident);
        } catch (NullPointerException e) {
            throw new LocalizationNotFoundException(ident);
        }
        if (s == null)
            throw new LocalizationNotFoundException(ident);

        // apply args
        if (args.length % 2 == 0)
            s = this.applyArgs(s, args);

        // apply colors
        return this.applyColors(s);
    }

    /*
    public String[] getTranslationStrings(String path, String[] args) throws LocalizationNotFoundException {
        List<String> out = this.getTranslationList(path, args);

        String[] s = new String[args.length];
        s = out.toArray(s);
        return s;
    }

    @SuppressWarnings("unchecked")
    public List<String> getTranslationList(String path, String[] args) throws LocalizationNotFoundException {
        List<String> list = null;
        try {
            list = (List<String>) this.data.getList(path);
        } catch (NullPointerException e) {
            throw new LocalizationNotFoundException(path);
        }

        if (list == null || list.size() == 0) throw new LocalizationNotFoundException(path);

        // create a copy!
        List<String> out = new ArrayList<String>();
        for (int i = 1; i <= list.size(); i++) {
            out.add(this.applyArgs(list.get(i-1), args));
        }

        return out;
    }
    */

    
    private String applyArgs(String s, String[] args) {
        for (int i = 0; i < args.length; i++) {
            s = s.replaceAll("%" + args[i] + "%", args[i + 1]);
            i++;
        }

        return s;
    }

    @Deprecated
    private String applyColors(String s) {
        String regex =
                "(&" +
                    "(" +
                    "BLACK|DARK_BLUE|DARK_GREEN|DARK_AQUA|DARK_RED|DARK_PURPLE|GOLD|GRAY|DARK_GRAY|BLUE|GREEN|AQUA|RED|LIGHT_PURPLE|YELLOW|WHITE" +
                    "|" +
                    "STRIKETHROUGH|UNDERLINE|BOLD|MAGIC|ITALIC|RESET" +
                    "|" +
                    "PRIMARY|SECONDARY|FLAG" +
                    ")" +
                "&)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            if (matcher.group(2).equals("PRIMARY")) {
                matcher.appendReplacement(sb, "" + this.plugin.getPrimaryColor());
            } else if (matcher.group(2).equals("SECONDARY")) {
                matcher.appendReplacement(sb, "" + this.plugin.getSecondaryColor());
            } else if (matcher.group(2).equals("FLAG")) {
                matcher.appendReplacement(sb, "" + this.plugin.getFlagColor());
            } else {
                matcher.appendReplacement(sb, ChatColor.valueOf(matcher.group(2)).toString());
            }
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

}
