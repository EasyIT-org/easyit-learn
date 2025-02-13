package org.easyit.learn.asm.example3.easy;

import io.netty.buffer.ByteBuf;
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
    private int minor_version;
    //    u2             major_version;
    private int major_version;
    //    u2             constant_pool_count;
    private int constant_pool_count;
    //    cp_info        constant_pool[constant_pool_count-1];
    private List<EasyConstInfo> constant_pool;
    //    u2             access_flags;
    private int access_flags;
    //    u2             this_class;
    private int this_class_index;
    private String this_class;

    //    u2             super_class;
    private int super_class_index;
    private String super_class;
    //    u2             interfaces_count;
    private int interfaces_count;
    //    u2             interfaces[interfaces_count];
    private List<String> interfaces = new ArrayList<>();
    //    u2             fields_count;
    private int fields_count;
    //    field_info     fields[fields_count];
    private List<EasyFieldInfo> fields;
    //    u2             methods_count;
    private int methods_count;
    //    method_info    methods[methods_count];
    private List<EasyMethodInfo> methods;
    //    u2             attributes_count;
    private int attributes_count;
    //    attribute_info attributes[attributes_count];
    private List<EasyAttributeInfo> attributes;

    public static EasyClassFile fromJavassist(final ClassFile cf) {
        EasyClassFile target = new EasyClassFile();
        target.minor_version = cf.getMinorVersion();
        target.major_version = cf.getMajorVersion();
        target.constant_pool_count = (cf.getConstPool().getSize() + 1);
        target.access_flags = cf.getAccessFlags();
        target.this_class_index = cf.getConstPool().getThisClassInfo();
        target.this_class = cf.getConstPool().getClassInfoByDescriptor(target.this_class_index);
        target.super_class_index = cf.getSuperclassId();
        target.super_class = cf.getConstPool().getClassInfoByDescriptor(target.super_class_index);
        target.interfaces_count = cf.getInterfaces().length;
        target.interfaces.addAll(Arrays.asList(cf.getInterfaces()));
        target.fields_count = cf.getFields().size();
        target.fields = cf.getFields().stream().map(EasyFieldInfo::fromJavassist).collect(Collectors.toList());
        target.methods_count = cf.getMethods().size();
        target.methods = cf.getMethods().stream().map(EasyMethodInfo::fromJavassist).collect(Collectors.toList());
        target.attributes_count = cf.getAttributes().size();
        target.attributes = cf.getAttributes()
                              .stream()
                              .map(EasyAttributeInfo::fromJavassist)
                              .collect(Collectors.toList());
        return target;
    }

    public static EasyClassFile fromRaw(final ByteBuf buf) {
        EasyClassFile target = new EasyClassFile();
        long magic = buf.readUnsignedInt();

        target.minor_version = buf.readUnsignedShort();
        target.major_version = buf.readUnsignedShort();
        target.constant_pool_count = buf.readUnsignedShort();
        // todo read constantPoolInfo
        target.access_flags = buf.readUnsignedShort();
        target.this_class_index = buf.readUnsignedShort();
        //        target.this_class = cf.getConstPool().getClassInfoByDescriptor(target.this_class_index);
        target.super_class_index = buf.readUnsignedShort();
        //        target.super_class = cf.getConstPool().getClassInfoByDescriptor(target.super_class_index);
        target.interfaces_count = buf.readUnsignedShort();

        target.interfaces = new ArrayList<>();
        for (int i = 0; i < target.interfaces_count; i++) {
            target.interfaces.add(String.valueOf(buf.readUnsignedShort()));//todo
        }

        //        target.interfaces.addAll(Arrays.asList(cf.getInterfaces()));
        target.fields_count = buf.readUnsignedShort();
        target.fields = new ArrayList<>();
        for (int i = 0; i < target.fields_count; i++) {
            target.fields.add(EasyFieldInfo.fromRaw(buf));
        }
        target.methods_count = buf.readUnsignedShort();
        target.methods = new ArrayList<>();
        for (int i = 0; i < target.methods_count; i++) {
            target.methods.add(EasyMethodInfo.fromRaw(buf));
        }
        target.attributes_count = buf.readUnsignedShort();
        target.attributes = new ArrayList<>();
        for (int i = 0; i < target.attributes_count; i++) {
            target.attributes.add(EasyAttributeInfo.fromRaw(buf));
        }
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
