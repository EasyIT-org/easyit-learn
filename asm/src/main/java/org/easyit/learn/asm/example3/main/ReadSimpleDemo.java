package org.easyit.learn.asm.example3.main;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.easyit.learn.asm.example3.ClassInfoParser;
import org.easyit.learn.asm.example3.ConstantPoolEntry;
import org.easyit.learn.asm.example3.Symbol;
import org.easyit.learn.asm.example3.ValueConstantEntry;
import org.easyit.learn.asm.example3.easy.EasyClassFile;
import org.easyit.learn.asm.utils.ByteBufUtils;

public class ReadSimpleDemo {

    public static final Map<Integer, ConstantPoolEntry> constantPool = new HashMap<>();
    public static final Map<String, Object> info = new ConcurrentHashMap<>();

    public static final String SIMPLE_DEMO_CLASS_PATH = "./asm/target/classes/org/easyit/learn/asm/example3/SimpleDemo.class";

    public static void main(String[] args) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(SIMPLE_DEMO_CLASS_PATH));
        ByteBuf buf = ByteBufUtils.buf(bytes);
        parse(buf);
        print();
    }

    private static void print() {
        print("magic");
        print("minor_version");
        print("major_version");
        print("constant_pool_count");
        //        long cpCount = (Long) info.get("constant_pool_count");
        //        for (int index = 0; index <= cpCount; index++) {
        //            String name = "cp_info[" + index + "]";
        //            print(name);
        //        }
        printAccessFlag("access_flags");
        printConstant("this_class");
        printConstant("super_class");
        print("interfaces_count");
        Number interfacesCount = (Number) info.get("interfaces_count");
        for (int index = 0; index < interfacesCount.intValue(); index++) {
            String name = "interfaces[" + index + "]";
            printConstant(name);
        }

        Number fieldsCount = (Number) info.get("fields_count");
        for (int index = 0; index < fieldsCount.intValue(); index++) {
            printAccessFlag("fields[" + index + "].access_flags");
            printConstant("fields[" + index + "].name_index");
            printConstant("fields[" + index + "].descriptor_index");
        }

    }

    public static void printAccessFlag(final String name) {
        int flag = (int) info.get(name);
        String accessFlag = ClassInfoParser.accessFlag(flag);
        System.out.printf("[%s]:%s\n", name, String.join(",", accessFlag));

    }

    private static void printConstant(final String name) {
        Object o = info.get(name);
        ConstantPoolEntry constantPoolEntry = constantPool.get(o);
        System.out.println(toString(constantPoolEntry));
    }

    private static void print(String name) {
        String value = toString(info.get(name));
        System.out.printf("[%s]:%s\n", name, value);
    }

    private static String toString(Object obj) {
        if (obj == null) {
            return "null";
        }
        return obj.toString();
    }

    /**
     * https://docs.oracle.com/javase/specs/jvms/se16/html/jvms-4.html
     *
     * @param buf
     */
    private static void parse(final ByteBuf buf) {
        EasyClassFile.fromRaw(buf);
        EasyClassFile easyClassFile = new EasyClassFile();

        // should be 0xCAFEBABE
        readHex("magic", buf.readUnsignedInt());
        // For a class file whose major_version is 56 or above, the minor_version must be 0 or 65535.
        read("minor_version", buf.readUnsignedShort());
        // depend on your jvm version, look table in url above
        read("major_version", buf.readUnsignedShort());

        int constantPoolCount = buf.readUnsignedShort();
        read("constant_pool_count", constantPoolCount);

        readConstantPoolInfo(constantPoolCount, buf);
        read("access_flags", buf.readUnsignedShort());
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
    private static void readConstantPoolInfo(final int constantPoolCount, final ByteBuf buf) {
        for (int index = 1; index < constantPoolCount; index++) {
            int tag = 0;
            ConstantPoolEntry cp;
            switch (tag = buf.readUnsignedByte()) {
                case Symbol.CONSTANT_FIELDREF_TAG:
                    cp = new ConstantPoolEntry();
                    readAndSaveTag("CONSTANT_FIELDREF", index, cp);
                    readAndPutIndex("class_index", buf.readUnsignedShort(), cp);
                    readAndPutIndex("name_and_type_index", buf.readUnsignedShort(), cp);
                    break;
                case Symbol.CONSTANT_METHODREF_TAG:
                    cp = new ConstantPoolEntry();
                    readAndSaveTag("CONSTANT_METHODREF", index, cp);
                    readAndPutIndex("class_index", buf.readUnsignedShort(), cp);
                    readAndPutIndex("name_and_type_index", buf.readUnsignedShort(), cp);
                    break;
                case Symbol.CONSTANT_INTERFACE_METHODREF_TAG:
                    cp = new ConstantPoolEntry();
                    readAndSaveTag("CONSTANT_INTERFACE_METHODREF", index, cp);
                    readAndPutIndex("class_index", buf.readUnsignedShort(), cp);
                    readAndPutIndex("name_and_type_index", buf.readUnsignedShort(), cp);
                    break;
                case Symbol.CONSTANT_INTEGER_TAG:
                    cp = new ConstantPoolEntry();
                    readAndSaveTag("CONSTANT_INTEGER", index, cp);
                    readAndPutIndex("bytes", buf.readInt(), cp);
                    break;
                case Symbol.CONSTANT_FLOAT_TAG:
                    cp = new ConstantPoolEntry();
                    readAndSaveTag("CONSTANT_FLOAT", index, cp);
                    readAndPutIndex("bytes", buf.readInt(), cp);
                    break;
                case Symbol.CONSTANT_NAME_AND_TYPE_TAG:
                    cp = new ConstantPoolEntry();
                    readAndSaveTag("CONSTANT_NAME_AND_TYPE", index, cp);
                    readAndPutIndex("name_index", buf.readUnsignedShort(), cp);
                    readAndPutIndex("descriptor_index", buf.readUnsignedShort(), cp);
                    break;
                case Symbol.CONSTANT_DYNAMIC_TAG:
                    cp = new ConstantPoolEntry();
                    readAndSaveTag("CONSTANT_DYNAMIC", index, cp);
                    readAndPutIndex("bootstrap_method_attr_index", buf.readUnsignedShort(), cp);
                    readAndPutIndex("name_and_type_index", buf.readUnsignedShort(), cp);
                    break;
                case Symbol.CONSTANT_INVOKE_DYNAMIC_TAG:
                    cp = new ConstantPoolEntry();
                    readAndSaveTag("CONSTANT_INVOKE_DYNAMIC", index, cp);
                    readAndPutIndex("bootstrap_method_attr_index", buf.readUnsignedShort(), cp);
                    readAndPutIndex("name_and_type_index", buf.readUnsignedShort(), cp);
                    break;
                case Symbol.CONSTANT_LONG_TAG:
                    ValueConstantEntry vcp = new ValueConstantEntry();
                    cp = vcp;
                    readAndSaveTag("CONSTANT_LONG", index, cp);
                    readAndSaveValue(buf.readLong(), vcp);
                    break;
                case Symbol.CONSTANT_DOUBLE_TAG:
                    vcp = new ValueConstantEntry();
                    cp = vcp;
                    readAndSaveTag("CONSTANT_DOUBLE", index, cp);
                    readAndSaveValue(buf.readLong(), vcp);
                    break;
                case Symbol.CONSTANT_UTF8_TAG:
                    vcp = new ValueConstantEntry();
                    cp = vcp;
                    readAndSaveTag("CONSTANT_UTF8", index, cp);
                    int length = buf.readUnsignedShort();
                    byte[] bytes = new byte[length];
                    buf.readBytes(bytes);
                    vcp.setValue(readString("bytes", bytes));
                    break;
                case Symbol.CONSTANT_METHOD_HANDLE_TAG:
                    vcp = new ValueConstantEntry();
                    cp = vcp;
                    readAndSaveTag("CONSTANT_METHOD_HANDLE", index, cp);
                    vcp.setValue(buf.readUnsignedByte());
                    readAndPutIndex("reference_index", buf.readUnsignedShort(), cp);
                    break;
                case Symbol.CONSTANT_CLASS_TAG:
                    cp = new ConstantPoolEntry();
                    readAndSaveTag("CONSTANT_CLASS", index, cp);
                    readAndPutIndex("name_index", buf.readUnsignedShort(), cp);
                    break;
                case Symbol.CONSTANT_STRING_TAG:
                    cp = new ConstantPoolEntry();
                    readAndSaveTag("CONSTANT_STRING", index, cp);
                    readAndPutIndex("string_index", buf.readUnsignedShort(), cp);
                    break;
                case Symbol.CONSTANT_METHOD_TYPE_TAG:
                    cp = new ConstantPoolEntry();
                    readAndSaveTag("CONSTANT_METHOD_TYPE", index, cp);
                    readAndPutIndex("descriptor_index", buf.readUnsignedShort(), cp);
                    break;
                case Symbol.CONSTANT_PACKAGE_TAG:
                    cp = new ConstantPoolEntry();
                    readAndSaveTag("CONSTANT_PACKAGE", index, cp);
                    readAndPutIndex("name_index", buf.readUnsignedShort(), cp);
                    break;

                case Symbol.CONSTANT_MODULE_TAG:
                    cp = new ConstantPoolEntry();
                    readAndSaveTag("CONSTANT_MODULE", index, cp);
                    readAndPutIndex("name_index", buf.readUnsignedShort(), cp);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            info.put("cp_info[" + index + "]", cp);
        }
    }

    private static void readAndSaveValue(final long l, final ValueConstantEntry cp) {
        cp.setValue(l);
    }

    private static void readAndPutIndex(final String string, final int index, ConstantPoolEntry cp) {
        cp.addRef(string, index);
    }

    private static void readAndSaveTag(final String string, final int index, final ConstantPoolEntry cp) {
        cp.setTag(string);
        constantPool.put(index, cp);
    }

    private static String readString(final String name, final byte[] bytes1) {
        String str = new String(bytes1);
        info.put(name, str);
        return str;
    }

    private static void readHex(final String name, final Long value) {
        info.put(name, Long.toHexString(value).toUpperCase());
    }

    private static void read(String name, Object value) {
        info.put(name, value);
    }
}
