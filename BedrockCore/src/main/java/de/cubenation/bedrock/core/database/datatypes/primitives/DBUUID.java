package de.cubenation.bedrock.core.database.datatypes.primitives;

import de.cubenation.bedrock.core.database.datatypes.PrimitiveDataType;

import java.util.UUID;

public class DBUUID extends PrimitiveDataType<UUID> {

    public DBUUID(UUID value) {
        super(value);
    }

    @Override
    public String getName() {
        return "UUID";
    }

    @Override
    public Class<? extends UUID> getType() {
        return UUID.class;
    }
}
