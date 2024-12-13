package org.easyit.learn.asm.example3;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ClassInfoParser {

    public static final Map<String, Object> FLAG = new ConcurrentHashMap<>();

    static {
        FLAG.put("public", 0x0001);
        FLAG.put("private", 0x0002);
        FLAG.put("protected", 0x0004);
        FLAG.put("static", 0x0008);
        FLAG.put("final", 0x0010);
        FLAG.put("super", 0x0020);
        FLAG.put("volatile", 0x0040);
        FLAG.put("transient", 0x0080);
        FLAG.put("interface", 0x0200);
        FLAG.put("abstract", 0x0400);
        FLAG.put("synthetic", 0x1000);
        FLAG.put("annotation", 0x2000);
        FLAG.put("enum", 0x4000);
        FLAG.put("module", 0x8000);
    }

    public static String accessFlag(final int accessFlag) {
        Set<String> keys = FLAG.keySet();
        Set<String> resultSet = new HashSet<>();
        for (String key : keys) {
            int value = (int) FLAG.get(key);
            if ((accessFlag & value) != 0) {
                resultSet.add(key);
            }
        }
        return String.join(",", resultSet);
    }

}
