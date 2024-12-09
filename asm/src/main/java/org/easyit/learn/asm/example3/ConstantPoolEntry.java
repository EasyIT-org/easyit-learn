package org.easyit.learn.asm.example3;

import java.util.HashMap;
import java.util.Map;

public class ConstantPoolEntry {

    public static Map<Integer, ConstantPoolEntry> CONSTANT_POOL;
    private String tag;
    private Map<String, Integer> refs = new HashMap<String, Integer>();

    public void setTag(final String tag) {
        this.tag = tag;
    }

    public void addRef(String name, int index) {
        refs.put(name, index);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        String tagInfo = "[" + tag + "]:";
        sb.append(tagInfo);
        for (final String s : refs.keySet()) {
            sb.append(s + ":" + CONSTANT_POOL.get(refs.get(s)) + ",");
        }
        return sb.toString();

    }
}
