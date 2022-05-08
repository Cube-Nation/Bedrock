package de.cubenation.bedrock.core.database.datatypes.primitives;

import de.cubenation.bedrock.core.database.datatypes.PrimitiveDataType;

import java.util.HashMap;
import java.util.UUID;

public class PrimitiveLookup {
    private static final HashMap<String, Class<? extends PrimitiveDataType<?>>> registeredPrimitives = new HashMap<>();

    static{
        registerPrimitive(String.class, DBString.class);
        registerPrimitive(Double.class, DBDouble.class);
        registerPrimitive(UUID.class, DBUUID.class);
    }

    public static void registerPrimitive(Class<?> primitive, Class<? extends PrimitiveDataType<?>> primitiveDataType){
        registeredPrimitives.put(primitive.getName(), primitiveDataType);
    }

    public static Class<? extends PrimitiveDataType<?>> getPrimitive(Class<?> clazz){
        if(PrimitiveDataType.class.isInstance(clazz))
            return (Class<? extends PrimitiveDataType<?>>) clazz;
        return registeredPrimitives.get(clazz.getName());
    }
}
