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

package de.cubenation.bedrock.core.helper;

import java.util.UUID;

/**
 * @author Cube-Nation
 * @version 2.0
 */

public class UUIDUtil {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean isUUID(String string) {
        try {
            UUID.fromString(string);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isUUIDRegex(String input){
        return input.toLowerCase().matches("[0-9a-f]{8}(-?[0-9a-f]{4}){3}-?[0-9a-f]{12}");
    }

    public static boolean isTrimmed(String uuid){
        return uuid.toLowerCase().matches("[0-9a-f]{8}([0-9a-f]{4}){3}[0-9a-f]{12}");
    }

    public static String trim(UUID uuid){
        return trim(uuid.toString());
    }

    public static String trim(String uuid){
        return uuid.replace("-","");
    }

    public static String untrim(String uuid){
        return uuid.substring(0,8) + "-" + uuid.substring(8,12) + "-" + uuid.substring(12,16) + "-" + uuid.substring(16,20)  + "-" + uuid.substring(20,32);
    }

}
