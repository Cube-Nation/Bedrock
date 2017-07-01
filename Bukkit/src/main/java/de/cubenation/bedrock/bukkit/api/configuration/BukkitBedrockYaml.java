package de.cubenation.bedrock.bukkit.api.configuration;

import de.cubenation.bedrock.core.configuration.BedrockYaml;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;
import java.util.Set;

public class BukkitBedrockYaml implements BedrockYaml {

    private YamlConfiguration configuration;

    public BukkitBedrockYaml(YamlConfiguration configuration) {
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
