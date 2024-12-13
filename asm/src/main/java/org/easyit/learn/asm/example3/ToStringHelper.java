package org.easyit.learn.asm.example3;

import java.util.List;

public class ToStringHelper {

    public static final String KEY_VALUE_TEMPLATE = "[%20s]:\t%s\n";

    public static String toKeyAndValue(String key, Object value) {
        return KEY_VALUE_TEMPLATE.formatted(key, value.toString());

    }

    public static String toKeyAndValueArray(String key, List values) {

        String result = "";
        for (int i = 0; i < values.size(); i++) {
            result += toKeyAndValue(key + "[" + i + "]", values.get(i).toString());
        }
        return result;
    }
}
