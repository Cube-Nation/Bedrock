package de.cubenation.bedrock.core.database.datatypes;

import java.util.HashMap;

public abstract class DataType {
    private HashMap<String, PrimitiveDataType<?>> valueMap = new HashMap<>();

    protected abstract HashMap<String, PrimitiveDataType<?>> getValueMap(String name) throws IllegalAccessException;
}
