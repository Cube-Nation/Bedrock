package de.cubenation.bedrock.core.database.datatypes.primitives;

import de.cubenation.bedrock.core.database.datatypes.PrimitiveDataType;

public class DBDouble extends PrimitiveDataType<Double> {


    public DBDouble(Double value) {
        super(value);
    }

    @Override
    public String getName() {
        return "Double";
    }

    @Override
    public Class<? extends Double> getType() {
        return Double.class;
    }
}
