package org.easyit.learn.asm.example3;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.easyit.learn.asm.utils.ByteBufUtils;

public class ReadSimpleDemo {

    public static final Map<Integer, ConstantPoolEntry> constantPool = new HashMap<>();

    public static final String SIMPLE_DEMO_CLASS_PATH = "./asm/target/classes/org/easyit/learn/asm/example3/SimpleDemo.class";

    public static void main(String[] args) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(SIMPLE_DEMO_CLASS_PATH));
        ByteBuf buf = ByteBufUtils.buf(bytes);
        parse(buf);
    }

    /**
     * https://docs.oracle.com/javase/specs/jvms/se16/html/jvms-4.html
     *
     * @param buf
     */
    private static void parse(final ByteBuf buf) {
        // should be 0xCAFEBABE
        readHex("magic", buf.readUnsignedInt());
        // For a class file whose major_version is 56 or above, the minor_version must be 0 or 65535.
        read("minor_version", buf.readUnsignedShort());
        // depend on your jvm version, look table in url above
        read("major_version", buf.readUnsignedShort());

        int constantPoolCount = buf.readUnsignedShort();
        read("constant_pool_count", constantPoolCount);

        readCpInfo(constantPoolCount, buf);
        readHex("access_flags", (long) buf.readUnsignedShort());
        read("this_class", buf.readUnsignedShort());
        read("super_class", buf.readUnsignedShort());
        int interfaceCount = buf.readUnsignedShort();
        read("interfaces_count", interfaceCount);
        for (int i = 0; i < interfaceCount; i++) {
            read("interfaces[" + i + "]", buf.readUnsignedShort());
        }
        int fieldsCount = buf.readUnsignedShort();
        read("fields_count", fieldsCount);
        for (int i = 0; i < fieldsCount; i++) {
            read("fields[" + i + "].access_flags", buf.readUnsignedShort());
            read("fields[" + i + "].name_index", buf.readUnsignedShort());
            read("fields[" + i + "].descriptor_index", buf.readUnsignedShort());
            int attribute_count = buf.readUnsignedShort();
            read("fields[" + i + "].attributes_count", attribute_count);
            for (int j = 0; j < attribute_count; j++) {
                read("fields[" + i + "].attributes[" + j + "].attribute_name_index", buf.readUnsignedShort());
                int attribute_length = buf.readInt();
                read("fields[" + i + "].attributes[" + j + "].attribute_length", attribute_length);
                byte[] bytes = new byte[attribute_length];
                buf.readBytes(bytes);
                readString("fields[" + i + "].attributes[" + j + "].info", bytes);
            }
        }

        int methodsCount = buf.readUnsignedShort();
        read("methods_count", methodsCount);
        for (int i = 0; i < methodsCount; i++) {
            read("methods[" + i + "].access_flags", buf.readUnsignedShort());
            read("methods[" + i + "].name_index", buf.readUnsignedShort());
            read("methods[" + i + "].descriptor_index", buf.readUnsignedShort());
            int attribute_count = buf.readUnsignedShort();
            read("methods[" + i + "].attributes_count", attribute_count);
            for (int j = 0; j < attribute_count; j++) {
                read("methods[" + i + "].attributes[" + j + "].attribute_name_index", buf.readUnsignedShort());
                int attribute_length = buf.readInt();
                read("methods[" + i + "].attributes[" + j + "].attribute_length", attribute_length);
                byte[] bytes = new byte[attribute_length];
                buf.readBytes(bytes);
                readString("methods[" + i + "].attributes[" + j + "].info", bytes);
            }
        }
        int attributesCount = buf.readUnsignedShort();
        read("attributes_count", attributesCount);
        for (int i = 0; i < attributesCount; i++) {
            read("attributes[" + i + "].attribute_name_index", buf.readUnsignedShort());
            int attribute_length = buf.readInt();
            read("attributes[" + i + "].attribute_length", attribute_length);
            byte[] bytes = new byte[attribute_length];
            buf.readBytes(bytes);
            readString("attributes[" + i + "].info", bytes);
        }

        // check last
        System.out.println("writer index: " + buf.writerIndex());
        System.out.println("read index: " + buf.readerIndex());

        Set<Integer> integers = constantPool.keySet();
        ConstantPoolEntry.CONSTANT_POOL = constantPool;
        for (Integer integer : integers) {
            System.out.println(integer + ": " + constantPool.get(integer));
        }
    }

    /**
     * cp_info { u1 tag; u1 info[]; } info[] length depends on the value of tag
     *
     * @param constantPoolCount
     * @param buf
     */
    private static void readCpInfo(final int constantPoolCount, final ByteBuf buf) {
        for (int i = 1; i < constantPoolCount; i++) {
            int tag = 0;
            switch (tag = buf.readUnsignedByte()) {
                case Symbol.CONSTANT_FIELDREF_TAG:
                    ConstantPoolEntry cp = new ConstantPoolEntry();
                    readAndSaveTag("CONSTANT_FIELDREF", i, cp);
                    readAndPutIndex("class_index", buf.readUnsignedShort(), cp);
                    readAndPutIndex("name_and_type_index", buf.readUnsignedShort(), cp);
                    break;
                case Symbol.CONSTANT_METHODREF_TAG:
                    cp = new ConstantPoolEntry();
                    readAndSaveTag("CONSTANT_METHODREF", i, cp);
                    readAndPutIndex("class_index", buf.readUnsignedShort(), cp);
                    read("name_and_type_index", buf.readUnsignedShort());
                    break;
                case Symbol.CONSTANT_INTERFACE_METHODREF_TAG:
                    cp = new ConstantPoolEntry();
                    readAndSaveTag("CONSTANT_INTERFACE_METHODREF", i, cp);
                    readAndPutIndex("class_index", buf.readUnsignedShort(), cp);
                    readAndPutIndex("name_and_type_index", buf.readUnsignedShort(), cp);
                    break;
                case Symbol.CONSTANT_INTEGER_TAG:
                    cp = new ConstantPoolEntry();
                    readAndSaveTag("CONSTANT_INTEGER", i, cp);
                    readAndPutIndex("bytes", buf.readInt(), cp);
                    break;
                case Symbol.CONSTANT_FLOAT_TAG:
                    cp = new ConstantPoolEntry();
                    readAndSaveTag("CONSTANT_FLOAT", i, cp);
                    readAndPutIndex("bytes", buf.readInt(), cp);
                    break;
                case Symbol.CONSTANT_NAME_AND_TYPE_TAG:
                    cp = new ConstantPoolEntry();
                    readAndSaveTag("CONSTANT_NAME_AND_TYPE", i, cp);
                    readAndPutIndex("name_index", buf.readUnsignedShort(), cp);
                    readAndPutIndex("descriptor_index", buf.readUnsignedShort(), cp);
                    break;
                case Symbol.CONSTANT_DYNAMIC_TAG:
                    cp = new ConstantPoolEntry();
                    readAndSaveTag("CONSTANT_DYNAMIC", i, cp);
                    readAndPutIndex("bootstrap_method_attr_index", buf.readUnsignedShort(), cp);
                    readAndPutIndex("name_and_type_index", buf.readUnsignedShort(), cp);
                    break;
                case Symbol.CONSTANT_INVOKE_DYNAMIC_TAG:
                    cp = new ConstantPoolEntry();
                    readAndSaveTag("CONSTANT_INVOKE_DYNAMIC", i, cp);
                    readAndPutIndex("bootstrap_method_attr_index", buf.readUnsignedShort(), cp);
                    read("name_and_type_index", buf.readUnsignedShort());
                    break;
                case Symbol.CONSTANT_LONG_TAG:
                    cp = new ConstantPoolEntry();
                    readAndSaveTag("CONSTANT_LONG", i, cp);
                    read("high_bytes", buf.readLong());
                    break;
                case Symbol.CONSTANT_DOUBLE_TAG:
                    cp = new ConstantPoolEntry();
                    readAndSaveTag("CONSTANT_DOUBLE", i, cp);
                    read("high_bytes", buf.readLong());
                    break;
                case Symbol.CONSTANT_UTF8_TAG:
                    ValueConstantEntry vcp = new ValueConstantEntry();
                    cp = vcp;
                    readAndSaveTag("CONSTANT_UTF8", i, cp);
                    int length = buf.readUnsignedShort();
                    byte[] bytes = new byte[length];
                    buf.readBytes(bytes);
                    vcp.setValue(readString("bytes", bytes));
                    break;
                case Symbol.CONSTANT_METHOD_HANDLE_TAG:
                    cp = new ConstantPoolEntry();
                    readAndSaveTag("CONSTANT_METHOD_HANDLE", i, cp);
                    read("reference_kind", buf.readUnsignedByte());
                    readAndPutIndex("reference_index", buf.readUnsignedShort(), cp);
                    break;
                case Symbol.CONSTANT_CLASS_TAG:
                    cp = new ConstantPoolEntry();
                    readAndSaveTag("CONSTANT_CLASS", i, cp);
                    readAndPutIndex("name_index", buf.readUnsignedShort(), cp);
                    break;
                case Symbol.CONSTANT_STRING_TAG:
                    cp = new ConstantPoolEntry();
                    readAndSaveTag("CONSTANT_STRING", i, cp);
                    readAndPutIndex("string_index", buf.readUnsignedShort(), cp);
                    break;
                case Symbol.CONSTANT_METHOD_TYPE_TAG:
                    cp = new ConstantPoolEntry();
                    readAndSaveTag("CONSTANT_METHOD_TYPE", i, cp);
                    readAndPutIndex("descriptor_index", buf.readUnsignedShort(), cp);
                    break;
                case Symbol.CONSTANT_PACKAGE_TAG:
                    cp = new ConstantPoolEntry();
                    readAndSaveTag("CONSTANT_PACKAGE", i, cp);
                    readAndPutIndex("name_index", buf.readUnsignedShort(), cp);
                    break;

                case Symbol.CONSTANT_MODULE_TAG:
                    cp = new ConstantPoolEntry();
                    readAndSaveTag("CONSTANT_MODULE", i, cp);
                    readAndPutIndex("name_index", buf.readUnsignedShort(), cp);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    private static void readAndPutIndex(final String string, final int i, ConstantPoolEntry cp) {
        cp.addRef(string, i);
        read(string, i);
    }

    private static void readAndSaveTag(final String string, final int i, final ConstantPoolEntry cp) {
        cp.setTag(string);
        constantPool.put(i, cp);
        read(string, i);
    }

    private static String readString(final String bytes, final byte[] bytes1) {
        System.out.print(bytes + ": ");
        String x = new String(bytes1);
        System.out.println(x);
        return x;
    }

    private static void readHex(final String name, final Long value) {
        System.out.println(name + ": " + Long.toHexString(value).toUpperCase());
    }

    private static void read(String name, long value) {
        System.out.println(name + ": " + value);
    }
}
