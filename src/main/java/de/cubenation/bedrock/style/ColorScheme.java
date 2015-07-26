package de.cubenation.bedrock.style;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.style.scheme.DefaultColorScheme;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorScheme {

    private ColorSchemeName name;

    private ChatColor primary;

    private ChatColor secondary;

    private ChatColor flag;

    private ChatColor text;

    public enum ColorSchemeName {
        DEFAULT, RED, GREEN, BLUE, YELLOW, CUSTOM
    }


    public ColorScheme(ColorSchemeName name, ChatColor primary, ChatColor secondary, ChatColor flag, ChatColor text) {
        this
                .setName(name)
                .setPrimary(primary)
                .setSecondary(secondary)
                .setFlag(flag)
                .setText(text);
    }

    public static ColorScheme getColorScheme(BasePlugin plugin, String name) {
        if (name.isEmpty() || ColorSchemeName.valueOf(name.toUpperCase()) == null)
            return new DefaultColorScheme(plugin);

        return getColorScheme(plugin, ColorSchemeName.valueOf(name.toUpperCase()));
    }

    @SuppressWarnings("unchecked")
    public static ColorScheme getColorScheme(BasePlugin plugin, ColorSchemeName name) {
        if (ColorSchemeName.valueOf(name.toString()) == null)
            return new DefaultColorScheme(plugin);

        try {
            Class cls = Class.forName(
                    "de.cubenation.bedrock.style.scheme." +
                    name.toString().substring(0, 1).toUpperCase() + name.toString().substring(1).toLowerCase() +
                    "ColorScheme"
            );

            Constructor<?> constructor = cls.getConstructor(BasePlugin.class);
            return (ColorScheme) constructor.newInstance(plugin);

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            return new DefaultColorScheme(plugin);
        }
    }

    public ColorSchemeName getName() {
        return name;
    }

    public ColorScheme setName(ColorSchemeName name) {
        this.name = name;
        return this;
    }

    public ChatColor getPrimary() {
        return primary;
    }

    public ColorScheme setPrimary(ChatColor primary) {
        this.primary = primary;
        return this;
    }

    public ChatColor getSecondary() {
        return secondary;
    }

    public ColorScheme setSecondary(ChatColor secondary) {
        this.secondary = secondary;
        return this;
    }

    public ChatColor getFlag() {
        return flag;
    }

    public ColorScheme setFlag(ChatColor flag) {
        this.flag = flag;
        return this;
    }

    public ChatColor getText() {
        return text;
    }

    public ColorScheme setText(ChatColor text) {
        this.text = text;
        return this;
    }

    public HoverEvent applyColorScheme(HoverEvent event) {
        String text_components = "";
        BaseComponent[] value = event.getValue();

        for (BaseComponent v : value) {
            text_components += this.applyColorScheme(v.toLegacyText());
        }

        return new HoverEvent(event.getAction(), TextComponent.fromLegacyText(text_components));
    }

    public ClickEvent applyColorScheme(ClickEvent event) {
        return new ClickEvent(event.getAction(), ChatColor.stripColor(event.getValue()));
    }

    public TextComponent applyColorScheme(TextComponent component) {
        return new TextComponent(applyColorScheme(component.toLegacyText()));
    }

    public String applyColorScheme(String string) {
        String regex =
                "(&" +
                        "(" +
                        "BLACK|DARK_BLUE|DARK_GREEN|DARK_AQUA|DARK_RED|DARK_PURPLE|GOLD|GRAY|DARK_GRAY|BLUE|GREEN|AQUA|RED|LIGHT_PURPLE|YELLOW|WHITE" +
                        "|" +
                        "STRIKETHROUGH|UNDERLINE|BOLD|MAGIC|ITALIC|RESET" +
                        "|" +
                        "PRIMARY|SECONDARY|FLAG|TEXT" +
                        ")" +
                        "&)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(string);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            if (matcher.group(2).equals("PRIMARY")) {
                matcher.appendReplacement(sb, "" + this.getPrimary());
            } else if (matcher.group(2).equals("SECONDARY")) {
                matcher.appendReplacement(sb, "" + this.getSecondary());
            } else if (matcher.group(2).equals("FLAG")) {
                matcher.appendReplacement(sb, "" + this.getFlag());
            } else if (matcher.group(2).equals("TEXT")) {
                matcher.appendReplacement(sb, "" + this.getText());
            } else {
                matcher.appendReplacement(sb, ChatColor.valueOf(matcher.group(2)).toString());
            }
        }

        matcher.appendTail(sb);

        // in case there are some old-school color codes left in the string, transform them and return
        return ChatColor.translateAlternateColorCodes('&', sb.toString());
    }

}
