package de.cubenation.bedrock.core.database.datatypes.objects;

import de.cubenation.bedrock.core.database.datatypes.ObjectDataType;
import de.cubenation.bedrock.core.database.datatypes.primitives.DBDouble;
import de.cubenation.bedrock.core.database.datatypes.primitives.DBString;

public class DBLocation extends ObjectDataType {
    private DBString worldName;
    private DBDouble x, y, z, yaw, pitch;

    public DBLocation(String world, double x, double y, double z) {
        this(world, x, y, z, 0.0, 0.0);
    }

    public DBLocation(String worldName,
                      double x,
                      double y,
                      double z,
                      double yaw,
                      double pitch) {
        this.worldName = new DBString(worldName);
        this.x = new DBDouble(x);
        this.y = new DBDouble(y);
        this.z = new DBDouble(z);
        this.yaw = new DBDouble(yaw);
        this.pitch = new DBDouble(pitch);
    }

    @Override
    public String getName() {
        return "Location";
    }

    public DBString getWorldName() {
        return worldName;
    }

    public DBDouble getX() {
        return x;
    }

    public DBDouble getY() {
        return y;
    }

    public DBDouble getZ() {
        return z;
    }

    public DBDouble getYaw() {
        return yaw;
    }

    public DBDouble getPitch() {
        return pitch;
    }
}
