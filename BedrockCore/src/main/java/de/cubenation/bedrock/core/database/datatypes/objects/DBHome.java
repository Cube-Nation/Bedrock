package de.cubenation.bedrock.core.database.datatypes.objects;

import de.cubenation.bedrock.core.database.datatypes.ObjectDataType;
import de.cubenation.bedrock.core.database.datatypes.primitives.DBDouble;
import de.cubenation.bedrock.core.database.datatypes.primitives.DBString;

public class DBHome extends ObjectDataType {
    private DBLocation location;
    private DBString string;

    public DBHome(String name, String world, double x, double y, double z) {
        this(name, world, x, y, z, 0.0, 0.0);
    }

    public DBHome(String name,
                  String worldName,
                  double x,
                  double y,
                  double z,
                  double yaw,
                  double pitch) {
        this.string = new DBString(name);
        this.location = new DBLocation(worldName, x, y, z, yaw, pitch);
    }

    @Override
    public String getName() {
        return "Location";
    }


}
