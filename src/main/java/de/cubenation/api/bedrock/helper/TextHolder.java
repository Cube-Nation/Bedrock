package de.cubenation.api.bedrock.helper;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Created by BenediktHr on 30.07.15.
 * Project: Bedrock
 */
public class TextHolder {

    private TextComponent textComponent;

    private HoverEvent hoverEvent;

    private ClickEvent clickEvent;

    public TextHolder(TextComponent textComponent) {
        this.textComponent = textComponent;
    }

    public TextHolder(TextComponent textComponent, HoverEvent hoverEvent) {
        this.textComponent = textComponent;
        this.hoverEvent = hoverEvent;
    }

    public TextHolder(TextComponent textComponent, ClickEvent clickEvent) {
        this.textComponent = textComponent;
        this.clickEvent = clickEvent;
    }

    public TextHolder(TextComponent textComponent, HoverEvent hoverEvent, ClickEvent clickEvent) {
        this.textComponent = textComponent;
        this.hoverEvent = hoverEvent;
        this.clickEvent = clickEvent;
    }

    public TextComponent getTextComponent() {
        return textComponent;
    }

    public HoverEvent getHoverEvent() {
        return hoverEvent;
    }

    public ClickEvent getClickEvent() {
        return clickEvent;
    }
}
