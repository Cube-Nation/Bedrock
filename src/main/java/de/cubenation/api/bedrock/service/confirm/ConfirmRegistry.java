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

package de.cubenation.api.bedrock.service.confirm;

import org.bukkit.command.CommandSender;

import java.util.HashMap;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public class ConfirmRegistry {

    /*
     * implicit synchronized singleton
     */
    private static HashMap<CommandSender,ConfirmInterface> confirm
            = new HashMap<>();

    private static final class InstanceHolder {
        static final ConfirmRegistry INSTANCE = new ConfirmRegistry();
    }

    /*
     * private constructor
     */
    private ConfirmRegistry() {}

    /*
     * instance methods
     */
    public static ConfirmRegistry getInstance () {
        return InstanceHolder.INSTANCE;
    }

    public void put(CommandSender sender, ConfirmInterface command) {
        // Check if there is already a confirm action & remove it.
        ConfirmInterface confirmInterface = confirm.get(sender);
        if (confirmInterface != null) {
            confirmInterface.invalidate();
        }
        confirm.put(sender, command);
    }

    public boolean has(CommandSender sender) {
        return confirm.containsKey(sender);
    }

    public ConfirmInterface get(CommandSender sender) {
        return confirm.get(sender);
    }

    public void remove(CommandSender sender) {
        confirm.remove(sender);
    }

}
