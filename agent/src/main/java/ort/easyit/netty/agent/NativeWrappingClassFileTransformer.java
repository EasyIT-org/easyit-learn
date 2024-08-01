package ort.easyit.netty.agent;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import net.bytebuddy.jar.asm.ClassReader;
import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.jar.asm.ClassWriter;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Type;

import static net.bytebuddy.jar.asm.Opcodes.ACC_FINAL;
import static net.bytebuddy.jar.asm.Opcodes.ACC_NATIVE;
import static net.bytebuddy.jar.asm.Opcodes.ACC_PRIVATE;
import static net.bytebuddy.jar.asm.Opcodes.ACC_STATIC;
import static net.bytebuddy.jar.asm.Opcodes.ALOAD;
import static net.bytebuddy.jar.asm.Opcodes.ASM7;
import static net.bytebuddy.jar.asm.Opcodes.GETSTATIC;
import static net.bytebuddy.jar.asm.Opcodes.ILOAD;
import static net.bytebuddy.jar.asm.Opcodes.INVOKESPECIAL;
import static net.bytebuddy.jar.asm.Opcodes.INVOKESTATIC;
import static net.bytebuddy.jar.asm.Opcodes.INVOKEVIRTUAL;
import static net.bytebuddy.jar.asm.Opcodes.IRETURN;

/**
 * Copy from
 * https://github.com/reactor/BlockHound/blob/312fc5f30370640f69b3c1d6659f1e9eabf32738/agent/src/main/java/reactor/blockhound/NativeWrappingClassFileTransformer.java
 * Under https://www.apache.org/licenses/LICENSE-2.0.
 */

/**
 * This ASM-based transformer finds all methods and creates a delegating method by prefixing the original native
 * method.
 */
class NativeWrappingClassFileTransformer implements ClassFileTransformer {

    // 定义一个表示 BlockHound 运行时类型的 Type 对象
    static final Type BLOCK_HOUND_RUNTIME_TYPE = Type.getType("Lreactor/blockhound/BlockHoundRuntime;");
    // 定义本地方法的前缀字符串
    static final String PREFIX = "$$BlockHound$$_";

    // 构造函数
    NativeWrappingClassFileTransformer() {
    }

    /**
     * 实现 ClassFileTransformer 接口的 transform 方法
     *
     * @param loader          类加载器
     * @param className       类名
     * @param classBeingRedefined 正在被重定义的类
     * @param protectionDomain 保护域
     * @param classfileBuffer 类文件缓冲区
     * @return 转换后的字节数组，如果不进行转换则返回 null
     */
    @Override
    public byte[] transform(
        ClassLoader loader,
        String className,
        Class<?> classBeingRedefined,
        ProtectionDomain protectionDomain,
        byte[] classfileBuffer
    ) {
        //        if (!className.startsWith("java/net/")) {
        //            return null;
        //        }

        // 创建 ClassReader 对象来读取类文件缓冲区
        ClassReader cr = new ClassReader(classfileBuffer);
        // 创建 ClassWriter 对象用于写入转换后的类
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);

        try {
            // 接受 NativeWrappingClassVisitor 进行类的访问和转换
            cr.accept(new NativeWrappingClassVisitor(cw, className), 0);

            // 获取转换后的字节数组
            classfileBuffer = cw.toByteArray();
        } catch (Throwable e) {
            // 打印异常堆栈跟踪
            e.printStackTrace();
            // 抛出异常
            throw e;
        }

        // 返回转换后的类文件字节数组
        return classfileBuffer;
    }

    // 内部静态类 NativeWrappingClassVisitor，继承自 ClassVisitor
    static class NativeWrappingClassVisitor extends ClassVisitor {

        // 存储类名
        private final String className;

        // 构造函数
        NativeWrappingClassVisitor(ClassVisitor cw, String internalClassName) {
            // 调用父类构造函数
            super(ASM7, cw);
            // 初始化类名
            this.className = internalClassName;
        }

        /**
         * 重写 visitMethod 方法，处理方法访问
         *
         * @param access          方法的访问标志
         * @param name            方法名
         * @param descriptor      方法描述符
         * @param signature       方法签名
         * @param exceptions      方法抛出的异常
         * @return 方法访问器
         */
        @Override
        public MethodVisitor visitMethod(int access,
                                         String name,
                                         String descriptor,
                                         String signature,
                                         String[] exceptions) {
            // 如果方法不是本地方法
            if ((access & ACC_NATIVE) == 0) {
                // 调用父类的 visitMethod 方法
                return super.visitMethod(access, name, descriptor, signature, exceptions);
            }

            // 访问修改后的本地方法
            super.visitMethod(
                ACC_NATIVE | ACC_PRIVATE | ACC_FINAL | (access & ACC_STATIC),
                PREFIX + name,
                descriptor,
                signature,
                exceptions
            );

            // 访问原始的非本地方法
            MethodVisitor delegatingMethodVisitor = super.visitMethod(
                access & ~ACC_NATIVE, name, descriptor, signature, exceptions);
            delegatingMethodVisitor.visitCode();

            // 返回自定义的方法访问器
            return new MethodVisitor(ASM7, delegatingMethodVisitor) {

                @Override
                public void visitEnd() {

                    // 添加打印语句打印方法名
                    visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                    visitLdcInsn("Method called: " + className + "." + name);
                    visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);


                    Type returnType = Type.getReturnType(descriptor);
                    Type[] argumentTypes = Type.getArgumentTypes(descriptor);
                    boolean isStatic = (access & ACC_STATIC)!= 0;
                    if (!isStatic) {
                        visitVarInsn(ALOAD, 0);
                    }
                    int index = isStatic? 0 : 1;
                    for (Type argumentType : argumentTypes) {
                        visitVarInsn(argumentType.getOpcode(ILOAD), index);
                        index += argumentType.getSize();
                    }

                    visitMethodInsn(
                        isStatic? INVOKESTATIC : INVOKESPECIAL,
                        className,
                        PREFIX + name,
                        descriptor,
                        false
                    );

                    visitInsn(returnType.getOpcode(IRETURN));
                    visitMaxs(0, 0);
                    super.visitEnd();
                }
            };
        }

    }
}
