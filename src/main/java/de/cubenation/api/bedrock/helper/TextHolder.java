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

package de.cubenation.api.bedrock.helper;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * @author Cube-Nation
 * @version 1.0
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
