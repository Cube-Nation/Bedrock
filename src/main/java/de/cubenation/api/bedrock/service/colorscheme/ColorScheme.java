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

package de.cubenation.api.bedrock.service.colorscheme;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.service.colorscheme.scheme.DefaultColorScheme;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public class ColorScheme {

    private ColorSchemeName name;

    private ChatColor primary;

    private ChatColor secondary;

    private ChatColor flag;

    private ChatColor text;

    private final Pattern pattern = Pattern.compile(
            "(&" +
                    "(" +
                    "BLACK|DARK_BLUE|DARK_GREEN|DARK_AQUA|DARK_RED|DARK_PURPLE|GOLD|GRAY|DARK_GRAY|BLUE|GREEN|AQUA|RED|LIGHT_PURPLE|YELLOW|WHITE" +
                    "|" +
                    "STRIKETHROUGH|UNDERLINE|BOLD|MAGIC|ITALIC|RESET" +
                    "|" +
                    "PRIMARY|SECONDARY|FLAG|TEXT" +
                    ")" +
            "&)"
    );

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
        if (name == null || name.isEmpty() || ColorSchemeName.valueOf(name.toUpperCase()) == null)
            return new DefaultColorScheme(plugin);

        return getColorScheme(plugin, ColorSchemeName.valueOf(name.toUpperCase()));
    }

    @SuppressWarnings("unchecked")
    public static ColorScheme getColorScheme(BasePlugin plugin, ColorSchemeName name) {
        if (ColorSchemeName.valueOf(name.toString()) == null)
            return new DefaultColorScheme(plugin);

        try {
            Class cls = Class.forName(
                    "de.cubenation.api.bedrock.service.colorscheme.scheme." +
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
        StringBuilder text_components = new StringBuilder();
        BaseComponent[] value = event.getValue();

        for (BaseComponent v : value) {
            text_components.append(this.applyColorScheme(v.toLegacyText()));
        }

        return new HoverEvent(event.getAction(), TextComponent.fromLegacyText(text_components.toString()));
    }

    public ClickEvent applyColorScheme(ClickEvent event) {
        return new ClickEvent(event.getAction(), ChatColor.stripColor(applyColorScheme(event.getValue())));
    }

    public TextComponent applyColorScheme(TextComponent component) {
        return new TextComponent(applyColorScheme(component.toLegacyText()));
    }

    public String applyColorScheme(String string) {
        Matcher matcher = this.pattern.matcher(string);
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

    public String applyColorSchemeForJson(String jsonMessage) {
        Matcher matcher = this.pattern.matcher(jsonMessage);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            if (matcher.group(2).equals("PRIMARY")) {
                matcher.appendReplacement(sb, "" + this.getPrimary().getName());
            } else if (matcher.group(2).equals("SECONDARY")) {
                matcher.appendReplacement(sb, "" + this.getSecondary().getName());
            } else if (matcher.group(2).equals("FLAG")) {
                matcher.appendReplacement(sb, "" + this.getFlag().getName());
            } else if (matcher.group(2).equals("TEXT")) {
                matcher.appendReplacement(sb, "" + this.getText().getName());
            } else {
                matcher.appendReplacement(sb, ChatColor.valueOf(matcher.group(2)).getName());
            }
        }

        matcher.appendTail(sb);

        // in case there are some old-school color codes left in the string, transform them and return
        return ChatColor.translateAlternateColorCodes('&', sb.toString());
    }

}
