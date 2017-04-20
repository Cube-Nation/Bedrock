package de.cubenation.api.bedrock.helper;

import java.util.UUID;

/**
 * @author Cube-Nation
 * @version 1.0
 */

public class UUIDUtil {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean isUUID(String string) {
        try {
            UUID.fromString(string);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

}
