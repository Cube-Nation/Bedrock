package de.cubenation.bedrock.helper;

import java.util.UUID;

/**
 * Created by BenediktHr on 26.01.16.
 * Project: Bedrock
 */

public class UUIDUtil {

    public static boolean isUUID(String string) {
        try {
            UUID.fromString(string);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

}
