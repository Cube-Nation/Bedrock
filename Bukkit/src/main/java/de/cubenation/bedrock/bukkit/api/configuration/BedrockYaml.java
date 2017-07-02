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

package de.cubenation.bedrock.bukkit.api.configuration;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;
import java.util.Set;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public class BedrockYaml implements de.cubenation.bedrock.core.configuration.BedrockYaml {

    private YamlConfiguration configuration;

    public BedrockYaml(YamlConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Set<String> getKeys(boolean var1) {
        return configuration.getKeys(var1);
    }

    @Override
    public boolean contains(String var1) {
        return configuration.contains(var1);
    }

    @Override
    public boolean contains(String var1, boolean var2) {
        return configuration.contains(var1, var2);
    }

    @Override
    public Object get(String var1) {
        return configuration.get(var1);
    }

    @Override
    public Object get(String var1, Object var2) {
        return configuration.get(var1, var2);
    }

    @Override
    public String getString(String var1) {
        return configuration.getString(var1);
    }

    @Override
    public String getString(String var1, String var2) {
        return configuration.getString(var1, var2);
    }

    @Override
    public int getInt(String var1) {
        return configuration.getInt(var1);
    }

    @Override
    public int getInt(String var1, int var2) {
        return configuration.getInt(var1, var2);
    }

    @Override
    public boolean getBoolean(String var1) {
        return configuration.getBoolean(var1);
    }

    @Override
    public boolean getBoolean(String var1, boolean var2) {
        return configuration.getBoolean(var1, var2);
    }

    @Override
    public double getDouble(String var1) {
        return configuration.getDouble(var1);
    }

    @Override
    public double getDouble(String var1, double var2) {
        return configuration.getDouble(var1, var2);
    }

    @Override
    public long getLong(String var1) {
        return configuration.getLong(var1);
    }

    @Override
    public long getLong(String var1, long var2) {
        return configuration.getLong(var1, var2);
    }

    @Override
    public List<?> getList(String var1) {
        return configuration.getList(var1);
    }

    @Override
    public List<?> getList(String var1, List<?> var2) {
        return configuration.getList(var1, var2);
    }

    @Override
    public List<String> getStringList(String var1) {
        return configuration.getStringList(var1);
    }

    @Override
    public List<Integer> getIntegerList(String var1) {
        return configuration.getIntegerList(var1);
    }

    @Override
    public List<Boolean> getBooleanList(String var1) {
        return configuration.getBooleanList(var1);
    }

    @Override
    public List<Double> getDoubleList(String var1) {
        return configuration.getDoubleList(var1);
    }

    @Override
    public List<Float> getFloatList(String var1) {
        return configuration.getFloatList(var1);
    }

    @Override
    public List<Long> getLongList(String var1) {
        return configuration.getLongList(var1);
    }

    @Override
    public List<Byte> getByteList(String var1) {
        return configuration.getByteList(var1);
    }

    @Override
    public List<Character> getCharacterList(String var1) {
        return configuration.getCharacterList(var1);
    }

    @Override
    public List<Short> getShortList(String var1) {
        return configuration.getShortList(var1);
    }
}
