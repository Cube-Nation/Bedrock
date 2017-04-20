package de.cubenation.api.bedrock.helper;

import java.util.Comparator;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public class LengthComparator implements Comparator<String> {

    public int compare(String o1, String o2) {
        if (o1.length() > o2.length()) {
            return 1;
        } else if (o1.length() < o2.length()) {
            return -1;
        } else {
            return 0;
        }
    }

}