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

package de.cubenation.bedrock.core.configuration;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public interface BedrockYaml {

    Set<String> getKeys(boolean var1);

    boolean contains(String var1);

    boolean contains(String var1, boolean var2);

    Object get(String var1);

    Object get(String var1, Object var2);

    String getString(String var1);

    String getString(String var1, String var2);

    int getInt(String var1);

    int getInt(String var1, int var2);

    boolean getBoolean(String var1);

    boolean getBoolean(String var1, boolean var2);

    double getDouble(String var1);

    double getDouble(String var1, double var2);

    long getLong(String var1);

    long getLong(String var1, long var2);

    List<?> getList(String var1);

    List<?> getList(String var1, List<?> var2);

    List<String> getStringList(String var1);

    List<Integer> getIntegerList(String var1);

    List<Boolean> getBooleanList(String var1);

    List<Double> getDoubleList(String var1);

    List<Float> getFloatList(String var1);

    List<Long> getLongList(String var1);

    List<Byte> getByteList(String var1);

    List<Character> getCharacterList(String var1);

    List<Short> getShortList(String var1);

}
