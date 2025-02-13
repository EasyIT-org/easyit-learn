package org.easyit.learn.asm.example3.easy;

import io.netty.buffer.ByteBuf;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.CodeAttribute;
import lombok.ToString;

@ToString
public class EasyAttributeInfo {

    //u2 attribute_name_index;
    protected int attribute_name_index;
    protected String attribute_name;
    //u4 attribute_length;
    protected int attribute_length;
    //u1 info[attribute_length];
    protected byte[] info;

    public static EasyAttributeInfo fromJavassist(AttributeInfo attributeInfo) {
        EasyAttributeInfo target = new EasyAttributeInfo();
        target.attribute_name = attributeInfo.getName();
        target.attribute_length = attributeInfo.length();
        if (attributeInfo instanceof CodeAttribute) {
            target.info = ((CodeAttribute) attributeInfo).getCode();
        } else {
            target.info = attributeInfo.get();
        }
        return target;
    }

    public static EasyAttributeInfo fromRaw(final ByteBuf buf) {
        EasyAttributeInfo target = new EasyAttributeInfo();
        target.attribute_name_index = buf.readUnsignedShort();
        target.attribute_length = buf.readInt();
        target.info = new byte[target.attribute_length];
        buf.readBytes(target.info);
        return target;
    }
}
