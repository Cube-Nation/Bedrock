package de.cubenation.bedrock.core.helper;

import java.util.Map;

public class PrimitiveUtil {

    private static final Map<Class<?>, Class<?>> WRAPPER_TYPE_MAP = Map.of(
            Integer.class, int.class,
            Byte.class, byte.class,
            Character.class, char.class,
            Boolean.class, boolean.class,
            Double.class, double.class,
            Float.class, float.class,
            Long.class, long.class,
            Short.class, short.class,
            Void.class, void.class
    );
    private static final Map<Class<?>, Class<?>> PRIMITIVE_TYPE_MAP = Map.of(
            int.class, Integer.class,
            byte.class, Byte.class,
            char.class, Character.class,
            boolean.class, Boolean.class,
            double.class, Double.class,
            float.class, Float.class,
            long.class, Long.class,
            short.class, Short.class,
            void.class, Void.class
    );

    public static boolean isPrimitiveType(Class input) {
        return WRAPPER_TYPE_MAP.containsKey(input);
    }

    public static Class getPrimitiveType(Class input) {
        return WRAPPER_TYPE_MAP.get(input);
    }

    public static Class getWrapperType(Class input) {
        return PRIMITIVE_TYPE_MAP.get(input);
    }
}
