package ort.easyit.netty.agent;

import java.lang.instrument.Instrumentation;

public class Premain {
    // premain 方法，在 Java 代理的预初始化阶段被调用
    public static void premain(String args, Instrumentation inst) {
        // 调用 recoverMain 方法进行相关操作
        recoverMain(inst);
    }

    // agentmain 方法，在 Java 代理的启动阶段被调用
    public static void agentmain(String args, Instrumentation inst) {
        // 调用 recoverMain 方法进行相关操作
        recoverMain(inst);
    }

    // recoverMain 方法，用于执行恢复主流程的操作
    public static void recoverMain(Instrumentation inst) {
        // 输出 inst 是否支持本地方法前缀
        System.out.println(inst.isNativeMethodPrefixSupported());
        // 创建 NativeWrappingClassFileTransformer 类的对象
        NativeWrappingClassFileTransformer transformer = new NativeWrappingClassFileTransformer();
        // 向 inst 添加转换器，并设置为可以重转换
        inst.addTransformer(transformer,true);
        // 为 inst 设置本地方法前缀
        inst.setNativeMethodPrefix(transformer, NativeWrappingClassFileTransformer.PREFIX);
    }
}
