package de.cubenation.bedrock.core.model;

import de.cubenation.bedrock.core.annotation.PrintableMapping;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Cube-Nation
 * @version 2.0
 */
@SuppressWarnings("unused")
public abstract class MappedModel {

    /**
     * Converts a MappedModel to a map with all it's fields as entries.
     * @return HashMap with all mapped values
     */
    public Map<String, Object> toMap() {
        Map<String, Object> fieldMap = new HashMap<>();
        for (Field field : getClass().getFields()) {
            try {
                fieldMap.put(field.getName(), field.get(this));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return fieldMap;
    }

    /**
     * Converts a MappedModel to a map with all it's fields as entries and a corresponding local string as key.
     * @return HashMap with all mapped values and localisation keys
     */
    public Map<String, String> toPrintableMap() {
        Map<String, String> fieldMap = new HashMap<>();
        for (Field field : getClass().getFields()) {
            PrintableMapping mapping = field.getAnnotation(PrintableMapping.class);
            if (mapping == null) {
                continue;
            }

            String key = mapping.value();
            Object valueObject = null;
            try {
                valueObject = field.get(this);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            String value = "N/A";
            if (valueObject instanceof Printable pValueObject) {
                value = pValueObject.toPrintableString();
            } else if(valueObject != null) {
                value = valueObject.toString();
            }

            fieldMap.put(key, value);
        }
        return fieldMap;
    }
}
