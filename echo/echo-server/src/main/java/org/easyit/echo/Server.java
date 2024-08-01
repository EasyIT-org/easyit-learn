package org.easyit.echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    static final int PORT = Integer.parseInt(System.getProperty("port", "8007"));

//    public static void main(String[] args) throws Exception {
//        // Configure the server.
//        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
//        EventLoopGroup workerGroup = new NioEventLoopGroup();
//        final EchoServerHandler serverHandler = new EchoServerHandler();
//        try {
//            ServerBootstrap b = new ServerBootstrap();
//            b.group(bossGroup, workerGroup)
//             .channel(NioServerSocketChannel.class)
//             .option(ChannelOption.SO_BACKLOG, 100)
//             //             .handler(new LoggingHandler(LogLevel.INFO))
//             .childHandler(new ChannelInitializer<SocketChannel>() {
//                 @Override
//                 public void initChannel(SocketChannel ch) throws Exception {
//                     ChannelPipeline p = ch.pipeline();
//                     p.addLast(serverHandler);
//                 }
//             });
//
//            // Start the server.
//            ChannelFuture f = b.bind(PORT).sync();
//
//            // Wait until the server socket is closed.
//            f.channel().closeFuture().sync();
//        } finally {
//            // Shut down all event loops to terminate all threads.
//            bossGroup.shutdownGracefully();
//            workerGroup.shutdownGracefully();
//        }
//    }

    public static void main(String[] args) throws InterruptedException {


        int port = 8080; // 您可以根据需要修改端口

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("服务器正在监听端口: " + port);

            Socket socket = serverSocket.accept();
            System.out.println("客户端已连接");

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            String message;
            while ((message = reader.readLine())!= null) {
                System.out.println("收到客户端消息: " + message);
                writer.println(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
