package org.easyit.learn.asm.example1;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class AsmHelloWorld {

    public static final String ORIGIN_CLASS_PATH = "./asm/target/classes/org/easyit/learn/asm/example1/PrintNothing.class";
    public static final String TARGET_CLASS_PATH = "./asm/target/classes/org/easyit/learn/asm/example1/PrintHelloWorld.class";
    public static final String TARGET_CLASS = "org.easyit.learn.asm.example1.PrintHelloWorld";

    public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        // 读取原始字节码
        byte[] classBytes = Files.readAllBytes(Paths.get(ORIGIN_CLASS_PATH));

        // 修改字节码
        byte[] modifiedBytes = modifyClass(classBytes);

        // 保存修改后的字节码
        Files.write(Paths.get(TARGET_CLASS_PATH), modifiedBytes);

        // 调用新生成的类
        Class<?> printHelloWorldClass = PrintNothing.class.getClassLoader().loadClass(TARGET_CLASS);
        Method print = printHelloWorldClass.getDeclaredMethod("print");
        print.invoke(printHelloWorldClass.getDeclaredConstructor().newInstance());

    }

    public static byte[] modifyClass(byte[] classBytes) {
        ClassReader classReader = new ClassReader(classBytes);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        ClassVisitor classVisitor = new MyClassVisitor(classWriter);

        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);

        return classWriter.toByteArray();
    }

    static class MyClassVisitor extends ClassVisitor {

        public MyClassVisitor(ClassVisitor cv) {
            super(Opcodes.ASM9, cv);
        }

        @Override
        public void visit(int version,
                          int access,
                          String name,
                          String signature,
                          String superName,
                          String[] interfaces) {
            // 将类名从 PrintNothing 更改为 PrintHelloWorld
            cv.visit(
                version, access, "org/easyit/learn/asm/example1/PrintHelloWorld", signature, superName, interfaces);
        }

        @Override
        public MethodVisitor visitMethod(int access,
                                         String name,
                                         String descriptor,
                                         String signature,
                                         String[] exceptions) {
            MethodVisitor methodVisitor = cv.visitMethod(access, name, descriptor, signature, exceptions);

            if (name.equals("print") && descriptor.equals("()V")) {
                return new MyMethodVisitor(methodVisitor);
            }

            return methodVisitor;
        }
    }

    static class MyMethodVisitor extends MethodVisitor {

        public MyMethodVisitor(MethodVisitor mv) {
            super(Opcodes.ASM9, mv);
        }

        @Override
        public void visitCode() {
            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn("Hello World");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        }
    }

}
