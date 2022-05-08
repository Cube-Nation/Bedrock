package de.cubenation.bedrock.core.database;

import de.cubenation.bedrock.core.database.datatypes.DataType;
import de.cubenation.bedrock.core.database.datatypes.ObjectDataType;
import de.cubenation.bedrock.core.database.datatypes.PrimitiveDataType;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public abstract class DatabaseEntity {

    public final HashMap<String, PrimitiveDataType<?>> getValueMap() throws IllegalAccessException {
        HashMap<String, PrimitiveDataType<?>> rtn = new HashMap<>();
        for(Field field : this.getClass().getDeclaredFields()){
            field.setAccessible(true);
            if(PrimitiveDataType.class.isAssignableFrom(field.getType())) {
                //rtn.put(field.getName(), ((PrimitiveDataType<?>) field.get(this)));
                rtn.put(field.getName(), ((PrimitiveDataType<?>) field.get(this)));
            }else if(ObjectDataType.class.isAssignableFrom(field.getType())){
                rtn.putAll(((ObjectDataType) field.get(this)).getValueMap(field.getName()));
            }
        }
        return rtn;
    }

    @Override
    public String toString() {
        Class<?> clazz = this.getClass();
        String rtn = clazz.getSimpleName() + "{";

        try {
            for(Map.Entry<String, PrimitiveDataType<?>> entry : getValueMap().entrySet())
                rtn += "\"" + entry.getKey() + "\"=" + entry.getValue().getValue() + ",";
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            rtn += " ";
        }

        return rtn.substring(0, rtn.length()-1) + "}";
    }
}
