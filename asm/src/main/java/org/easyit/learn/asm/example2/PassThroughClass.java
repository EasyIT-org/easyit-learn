package org.easyit.learn.asm.example2;

import java.nio.file.Files;
import java.nio.file.Paths;
import org.easyit.learn.asm.example2.visitor.MyClassVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

public class PassThroughClass {

    public static final String ORIGIN_CLASS_PATH = "./asm/target/classes/org/easyit/learn/asm/example2/target/MyClass.class";
    public static final String ORIGIN_SUB_CLASS_PATH = "./asm/target/classes/org/easyit/learn/asm/example2/target/MyClass$A.class";
    public static final String ORIGIN_LOCAL_CLASS_PATH = "./asm/target/classes/org/easyit/learn/asm/example2/target/MyClass$1C.class";
    public static final String ORIGIN_RECORD_CLASS_PATH = "./asm/target/classes/org/easyit/learn/asm/example2/target/MyRecord.class";
    public static final String ORIGIN_MODULE_CLASS_PATH = "./asm/target/classes/module-info.class";

    public static void main(String[] args) throws Exception {
        MyClassVisitor classVisitor = new MyClassVisitor(Opcodes.ASM9);

        passThroughClass(Files.readAllBytes(Paths.get(ORIGIN_CLASS_PATH)), classVisitor);
        passThroughClass(Files.readAllBytes(Paths.get(ORIGIN_SUB_CLASS_PATH)), classVisitor);
        passThroughClass(Files.readAllBytes(Paths.get(ORIGIN_LOCAL_CLASS_PATH)), classVisitor);
        passThroughClass(Files.readAllBytes(Paths.get(ORIGIN_RECORD_CLASS_PATH)), classVisitor);
        passThroughClass(Files.readAllBytes(Paths.get(ORIGIN_MODULE_CLASS_PATH)), classVisitor);

        System.out.println("Print un-visitMethod:");
        classVisitor.printUnVisitedMethod();
    }

    private static void passThroughClass(final byte[] classBytes, final MyClassVisitor classVisitor) {
        ClassReader classReader = new ClassReader(classBytes);
        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
        System.out.println("\n\n");
    }
}
