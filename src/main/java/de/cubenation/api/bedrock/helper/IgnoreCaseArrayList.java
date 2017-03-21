package de.cubenation.api.bedrock.helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BenediktHr on 28.07.15.
 * Project: Bedrock
 */
public class IgnoreCaseArrayList extends ArrayList<String> {

    public IgnoreCaseArrayList(List<String> strings) {
        for (String s : strings) {
            this.add(s.toLowerCase());
        }
    }

    @Override
    public boolean remove(Object o) {
        String string = (String) o;
        string = string.toLowerCase();
        return super.remove(string);
    }

    @Override
    public int indexOf(Object o) {
        String string = (String) o;
        for (int i = 0; i < size(); i++ ) {
            if (get(i).toLowerCase().equalsIgnoreCase(string)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean contains(Object o) {
        String string = (String) o;
        for (String s : this) {
            if (string.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }
}
