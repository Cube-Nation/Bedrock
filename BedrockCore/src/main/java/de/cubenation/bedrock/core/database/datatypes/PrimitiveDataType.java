package de.cubenation.bedrock.core.database.datatypes;

import java.util.HashMap;

public abstract class PrimitiveDataType<T> extends DataType{
    private T value;

    public abstract String getName();

    public abstract Class<? extends T> getType();

    public PrimitiveDataType(T value) {
        this.value = value;
    }

    public void setValue(T value){
        this.value = value;
    }

    public T getValue(){
        return this.value;
    }

    @Override
    protected HashMap<String, PrimitiveDataType<?>> getValueMap(String name) throws IllegalAccessException {
        HashMap<String, PrimitiveDataType<?>> rtn = new HashMap<>();
        rtn.put("", this);
        return rtn;
    }
}
