package de.cubenation.bedrock.core.database.datatypes;

import java.lang.reflect.Field;
import java.util.HashMap;

public abstract class ObjectDataType extends DataType{
    public abstract String getName();

    @Override
    public HashMap<String, PrimitiveDataType<?>> getValueMap(String name) throws IllegalAccessException {
        String prefix = "";
        if(name != null)
            prefix = name + "_";
        HashMap<String, PrimitiveDataType<?>> rtn = new HashMap<>();
        for(Field field : this.getClass().getDeclaredFields()){
            field.setAccessible(true);
            if(PrimitiveDataType.class.isAssignableFrom(field.getType())) {
                rtn.put(prefix + field.getName(), ((PrimitiveDataType<?>) field.get(this)));
            }else if(ObjectDataType.class.isAssignableFrom(field.getType())){
                rtn.putAll(((ObjectDataType) field.get(this)).getValueMap(prefix + field.getName()));
            }
        }
        return rtn;
    }
}
