package org.easyit.learn.asm.example3.easy;

import java.util.List;
import javassist.bytecode.MethodInfo;
import org.easyit.learn.asm.example3.ClassInfoParser;
import org.easyit.learn.asm.example3.easy.attribute.EasyCodeAttribute;

public class EasyMethodInfo {

    //u2             access_flags;
    private short access_flags;
    //u2             name_index;
    private short name_index;
    private String name;
    //u2             descriptor_index;
    private short descriptor_index;
    private String descriptor;
    //u2             attributes_count;
    private short attributes_count;
    //attribute_info attributes[attributes_count];
    private List<EasyAttributeInfo> attributes;

    private EasyCodeAttribute codeAttribute;

    public static EasyMethodInfo fromJavassist(MethodInfo methodInfo) {
        EasyMethodInfo target = new EasyMethodInfo();
        target.access_flags = (short) methodInfo.getAccessFlags();

        target.name = methodInfo.getName();
        target.descriptor = methodInfo.getDescriptor();
        target.attributes_count = (short) methodInfo.getAttributes().size();
        target.attributes = methodInfo.getAttributes()
                                      .stream()
                                      .map(EasyAttributeInfo::fromJavassist)
                                      .collect(java.util.stream.Collectors.toList());

        target.codeAttribute = EasyCodeAttribute.fromJavassist(methodInfo.getCodeAttribute());
        return target;
    }

    @Override
    public String toString() {
        String result = ClassInfoParser.accessFlag(access_flags) + " " + descriptor + " " + name;
        result += codeAttribute.toString();
        return result;

    }
}
