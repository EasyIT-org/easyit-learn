package org.easyit.echo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketImpl;

public class Analyze {

    public static void main(String[] args) throws Exception {

        Thread.sleep(1000);
        "".intern();

        System.setProperty("jdk.attach.allowAttachSelf", "true");
        // 做一些初始化操作
        bindAndListen(8081);
        System.out.println("Finish prepare");

        bindAndListen(8080);
        while (true) {
            Thread.sleep(5000);
        }
    }

    private static void bindAndListen(int port) throws Exception {
        Object inet4AddressImpl1 = createInet4AddressImpl();
        InetAddress inetAddress1 = anyLocalAddress(inet4AddressImpl1);

        tick("ServerSocket construct");
        ServerSocket serverSocket = new ServerSocket();

        bindAndListener(serverSocket, inetAddress1, port);
    }

    private static void bindAndListener(final ServerSocket serverSocket,
                                        final InetAddress inetAddress1,
                                        int port) throws Exception {
        tick("bind");
        SecurityManager security = System.getSecurityManager();
        if (security != null)
            security.checkListen(port);

        Method getImpl = serverSocket.getClass().getDeclaredMethod("getImpl");
        getImpl.setAccessible(true);
        SocketImpl socket = (SocketImpl) getImpl.invoke(serverSocket);
        bind0(inetAddress1, port, socket);

        listen0(socket);
    }

    private static void listen0(final SocketImpl socket) throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException, InterruptedException {
        tick("listen");
        Method listen = Class.forName("java.net.AbstractPlainSocketImpl").getDeclaredMethod("listen", int.class);
        listen.setAccessible(true);
        listen.invoke(socket, 50);
    }

    private static void bind0(final InetAddress inetAddress1,
                              final int port,
                              final SocketImpl socket) throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException, InterruptedException {
        tick("bind0");
        Method bind = Class.forName("java.net.AbstractPlainSocketImpl")
                           .getDeclaredMethod("bind", InetAddress.class, int.class);
        bind.setAccessible(true);
        bind.invoke(socket, inetAddress1, port);
    }

    private static InetAddress anyLocalAddress(final Object inet4AddressImpl) throws Exception {
        tick("anyLocalAddress");
        Class<?> aClass = Class.forName("java.net.Inet4AddressImpl");
        Method anyLocalAddress = aClass.getDeclaredMethod("anyLocalAddress");
        anyLocalAddress.setAccessible(true);
        Object invoke = anyLocalAddress.invoke(inet4AddressImpl);
        return (InetAddress) invoke;
    }

    public static Object createInet4AddressImpl() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, InterruptedException {
        tick("createInet4AddressImpl");
        Class<?> aClass = Class.forName("java.net.Inet4AddressImpl");
        Constructor<?> declaredConstructor = aClass.getDeclaredConstructor();
        declaredConstructor.setAccessible(true);
        Object o = declaredConstructor.newInstance();
        return o;

    }

    private static void tick(String name) throws InterruptedException {
        Thread.sleep(1000);
        System.out.println(
            "tick " + name + " ----------------------------- ----------------------------- -----------------------------");
        Thread.sleep(1000);

    }

}
