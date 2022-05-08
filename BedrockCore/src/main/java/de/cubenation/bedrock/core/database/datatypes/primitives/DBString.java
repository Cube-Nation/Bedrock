package de.cubenation.bedrock.core.database.datatypes.primitives;

import de.cubenation.bedrock.core.database.datatypes.PrimitiveDataType;

public class DBString extends PrimitiveDataType<String> {
    public DBString(String value) {
        super(value);
    }

    @Override
    public String getName() {
        return "String";
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }
}
