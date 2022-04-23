package de.cubenation.bedrock.core.model;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public abstract class MappedModel {

    public Map<String, Object> toMap() {
        Map<String, Object> fieldMap = new HashMap<>();
        for (Field field : this.getClass().getFields()) {
            try {
                fieldMap.put(field.getName(), field.get(this));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return fieldMap;
    }

    public Map<String, String> toPrintableMap() {
        // ToDo: printable map values with localisation
        return toMap().entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().toString()
        ));
    }
}
