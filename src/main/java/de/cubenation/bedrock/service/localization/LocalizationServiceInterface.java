package de.cubenation.bedrock.service.localization;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.exception.*;
import de.cubenation.bedrock.service.ServiceInterface;
import de.cubenation.bedrock.service.customconfigurationfile.CustomConfigurationFile;
import de.cubenation.bedrock.service.customconfigurationfile.CustomConfigurationRegistry;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocalizationServiceInterface implements ServiceInterface {

    private BasePlugin plugin;

    private Locale locale;

    private String locale_file;

    private YamlConfiguration data;

    public LocalizationServiceInterface(BasePlugin plugin, Locale locale) throws ServiceInitException {
        this.setPlugin(plugin);
        this.setLocale(locale);

        this.init();
    }

    @Override
    public void init() throws ServiceInitException {
        // determine locale file and check if it exists
        try {
            this.loadLocaleFile();
        } catch (LocalizationFileNotFoundException e) {
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
    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }


    /*
     * Locale_file Getter/Setter
     */
    public String getLocale_file() {
        return locale_file;
    }

    public void setLocale_file(String locale_file) {
        this.locale_file = locale_file;
    }


    private void loadLocaleFile() throws LocalizationFileNotFoundException {
        YamlConfiguration yc = null;
        String[] language_files = {this.getLocale().getLocale(), this.getLocale().getDefaultLocale()};

        for (String file : language_files) {
            String locale_file = this.getLocale().getLocalePath() +
                    java.lang.System.getProperty("file.separator") +
                    file + ".yml";
            try {
                CustomConfigurationFile ccrfile = CustomConfigurationRegistry.get(this.plugin, locale_file, null);
                yc = ccrfile.load();
            } catch (NoSuchRegisterableException e) {
                continue;
            }

            if (yc != null) {
                this.setLocale_file(file);
                break;
            }
        }

        if (yc == null)
            throw new LocalizationFileNotFoundException("Could not find a suitable locale file");

        this.data = yc;
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
