/*
 * Bedrock
 *
 * Copyright (c) 2017 Cube-Nation (Benedikt Hruschka, Tristan Cebulla)
 *
 * Permission is hereby granted, free of charge,
 * to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.cubenation.bedrock.core.helper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Cube-Nation
 * @version 1.0
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
