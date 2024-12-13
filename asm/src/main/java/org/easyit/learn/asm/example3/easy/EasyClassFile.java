package org.easyit.learn.asm.example3.easy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javassist.bytecode.ClassFile;
import org.easyit.learn.asm.example3.ClassInfoParser;

import static org.easyit.learn.asm.example3.ToStringHelper.toKeyAndValue;
import static org.easyit.learn.asm.example3.ToStringHelper.toKeyAndValueArray;

public class EasyClassFile {
    //    u4             magic;
    private int magic = 0xCAFEBABE;
    //    u2             minor_version;
    private short minor_version;
    //    u2             major_version;
    private short major_version;
    //    u2             constant_pool_count;
    private short constant_pool_count;
    //    cp_info        constant_pool[constant_pool_count-1];
    private List<EasyConstInfo> constant_pool;
    //    u2             access_flags;
    private short access_flags;
    //    u2             this_class;
    private short this_class_index;
    private String this_class;

    //    u2             super_class;
    private short super_class_index;
    private String super_class;
    //    u2             interfaces_count;
    private short interfaces_count;
    //    u2             interfaces[interfaces_count];
    private List<String> interfaces = new ArrayList<>();
    //    u2             fields_count;
    private short fields_count;
    //    field_info     fields[fields_count];
    private List<EasyFieldInfo> fields;
    //    u2             methods_count;
    private short methods_count;
    //    method_info    methods[methods_count];
    private List<EasyMethodInfo> methods;
    //    u2             attributes_count;
    private short attributes_count;
    //    attribute_info attributes[attributes_count];
    private List<EasyAttributeInfo> attributes;

    public static EasyClassFile fromJavassist(final ClassFile cf) {
        EasyClassFile target = new EasyClassFile();
        target.minor_version = (short) cf.getMinorVersion();
        target.major_version = (short) cf.getMajorVersion();
        target.constant_pool_count = (short) (cf.getConstPool().getSize() + 1);
        target.access_flags = (short) cf.getAccessFlags();
        target.this_class_index = (short) cf.getConstPool().getThisClassInfo();
        target.this_class = cf.getConstPool().getClassInfoByDescriptor(target.this_class_index);
        target.super_class_index = (short) cf.getSuperclassId();
        target.super_class = cf.getConstPool().getClassInfoByDescriptor(target.super_class_index);
        target.interfaces_count = (short) cf.getInterfaces().length;
        target.interfaces.addAll(Arrays.asList(cf.getInterfaces()));
        target.fields_count = (short) cf.getFields().size();
        target.fields = cf.getFields().stream().map(EasyFieldInfo::fromJavassist).collect(Collectors.toList());
        target.methods_count = (short) cf.getMethods().size();
        target.methods = cf.getMethods().stream().map(EasyMethodInfo::fromJavassist).collect(Collectors.toList());
        target.attributes_count = (short) cf.getAttributes().size();
        target.attributes = cf.getAttributes()
                              .stream()
                              .map(EasyAttributeInfo::fromJavassist)
                              .collect(Collectors.toList());
        return target;
    }

    @Override
    public String toString() {
        String result = toKeyAndValue("Major Version", this.major_version)
            + toKeyAndValue("Minor Version", this.minor_version)
            + toKeyAndValue("Constant Pool Count", this.constant_pool_count)
            + toKeyAndValue("Access Flag", ClassInfoParser.accessFlag(this.access_flags))
            + toKeyAndValue("This Class", this.this_class)
            + toKeyAndValue("Super Class", this.super_class)
            + toKeyAndValue("Interfaces Count", this.interfaces_count)
            + toKeyAndValue("Interfaces", this.interfaces)
            + toKeyAndValue("Fields Count", this.fields_count)
            + toKeyAndValueArray("Fields", this.fields)
            + toKeyAndValue("Methods Count", this.methods_count)
            + toKeyAndValueArray("Methods", this.methods)
            + toKeyAndValue("Attributes Count", this.attributes_count)
            + toKeyAndValue("Attributes", this.attributes);
        return result;

    }
}
