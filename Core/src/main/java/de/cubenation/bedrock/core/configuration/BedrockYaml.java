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
