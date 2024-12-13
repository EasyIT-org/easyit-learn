package org.easyit.learn.asm.example3.easy;

import java.util.List;
import javassist.bytecode.FieldInfo;
import org.easyit.learn.asm.example3.ClassInfoParser;

public class EasyFieldInfo {
    // u2             access_flags;
    private short access_flags;
    // u2             name_index;
    private short name_index;
    private String name;
    // u2             descriptor_index;
    private short descriptor_index;
    private String descriptor;
    // u2             attributes_count;
    private short attributes_count;
    // attribute_info attributes[attributes_count];
    private List<EasyAttributeInfo> attributes;

    public static EasyFieldInfo fromJavassist(FieldInfo fieldInfo) {
        EasyFieldInfo target = new EasyFieldInfo();
        target.access_flags = (short) fieldInfo.getAccessFlags();
        target.name = fieldInfo.getName();
        target.descriptor = fieldInfo.getDescriptor();
        target.attributes_count = (short) fieldInfo.getAttributes().size();
        target.attributes = fieldInfo.getAttributes()
                                     .stream()
                                     .map(EasyAttributeInfo::fromJavassist)
                                     .collect(java.util.stream.Collectors.toList());
        return target;
    }

    @Override
    public String toString() {
        return ClassInfoParser.accessFlag(access_flags) + " " + descriptor + " " + name;

    }
}
