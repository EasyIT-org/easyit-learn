package org.easyit.learn.asm.example3.easy;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import javassist.bytecode.MethodInfo;
import org.easyit.learn.asm.example3.ClassInfoParser;
import org.easyit.learn.asm.example3.easy.attribute.EasyCodeAttribute;

public class EasyMethodInfo {

    //u2             access_flags;
    private int access_flags;
    //u2             name_index;
    private int name_index;
    private String name;
    //u2             descriptor_index;
    private int descriptor_index;
    private String descriptor;
    //u2             attributes_count;
    private int attributes_count;
    //attribute_info attributes[attributes_count];
    private List<EasyAttributeInfo> attributes;

    private EasyCodeAttribute codeAttribute;

    public static EasyMethodInfo fromJavassist(MethodInfo methodInfo) {
        EasyMethodInfo target = new EasyMethodInfo();
        target.access_flags = methodInfo.getAccessFlags();

        target.name = methodInfo.getName();
        target.descriptor = methodInfo.getDescriptor();
        target.attributes_count = methodInfo.getAttributes().size();
        target.attributes = methodInfo.getAttributes()
                                      .stream()
                                      .map(EasyAttributeInfo::fromJavassist)
                                      .collect(java.util.stream.Collectors.toList());

        target.codeAttribute = EasyCodeAttribute.fromJavassist(methodInfo.getCodeAttribute());
        return target;
    }

    public static EasyMethodInfo fromRaw(final ByteBuf buf) {
        EasyMethodInfo target = new EasyMethodInfo();
        target.access_flags = buf.readUnsignedShort();
        target.name_index = buf.readUnsignedShort();
        //        target.name = methodInfo.getName();
        target.descriptor_index = buf.readUnsignedShort();
        //        target.descriptor = methodInfo.getDescriptor();
        target.attributes_count = buf.readUnsignedShort();
        target.attributes = new ArrayList<>();
        for (int i = 0; i < target.attributes_count; i++) {
            EasyAttributeInfo attr = EasyAttributeInfo.fromRaw(buf);
            target.attributes.add(attr);
            if (attr instanceof EasyCodeAttribute) {
                target.codeAttribute = (EasyCodeAttribute) attr;
            }
        }
        return target;
    }

    @Override
    public String toString() {
        String result = ClassInfoParser.accessFlag(access_flags) + " " + descriptor + " " + name;
        result += codeAttribute.toString();
        return result;

    }
}
