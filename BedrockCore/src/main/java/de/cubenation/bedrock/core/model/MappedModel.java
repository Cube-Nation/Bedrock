package de.cubenation.bedrock.core.model;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public abstract class MappedModel {

    public Map<String, Object> toMap() {
        return new HashMap<>();
    }

    public Map<String, String> toPrintableMap() {
        return new HashMap<>();
    }
}
