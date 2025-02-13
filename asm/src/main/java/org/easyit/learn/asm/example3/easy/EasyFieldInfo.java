package org.easyit.learn.asm.example3.easy;

import io.netty.buffer.ByteBuf;
import java.util.List;
import javassist.bytecode.FieldInfo;
import org.easyit.learn.asm.example3.ClassInfoParser;

public class EasyFieldInfo {
    // u2             access_flags;
    private int access_flags;
    // u2             name_index;
    private int name_index;
    private String name;
    // u2             descriptor_index;
    private int descriptor_index;
    private String descriptor;
    // u2             attributes_count;
    private int attributes_count;
    // attribute_info attributes[attributes_count];
    private List<EasyAttributeInfo> attributes;

    public static EasyFieldInfo fromJavassist(FieldInfo fieldInfo) {
        EasyFieldInfo target = new EasyFieldInfo();
        target.access_flags = fieldInfo.getAccessFlags();
        target.name = fieldInfo.getName();
        target.descriptor = fieldInfo.getDescriptor();
        target.attributes_count = fieldInfo.getAttributes().size();
        target.attributes = fieldInfo.getAttributes()
                                     .stream()
                                     .map(EasyAttributeInfo::fromJavassist)
                                     .collect(java.util.stream.Collectors.toList());
        return target;
    }

    public static EasyFieldInfo fromRaw(final ByteBuf buf) {
        EasyFieldInfo target = new EasyFieldInfo();
        target.access_flags = buf.readUnsignedShort();
        target.name_index = buf.readUnsignedShort();
        target.descriptor_index = buf.readUnsignedShort();
        target.attributes_count = buf.readUnsignedShort();
        target.attributes = new java.util.ArrayList<>();
        for (int i = 0; i < target.attributes_count; i++) {
            target.attributes.add(EasyAttributeInfo.fromRaw(buf));
        }
        return target;

    }

    @Override
    public String toString() {
        return ClassInfoParser.accessFlag(access_flags) + " " + descriptor + " " + name;

    }
}
