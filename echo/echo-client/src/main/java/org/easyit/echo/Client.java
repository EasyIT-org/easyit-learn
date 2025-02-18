package org.easyit.echo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8007"));
    static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));

//    public static void main(String[] args) throws Exception {
//
//        // Configure the client.
//        EventLoopGroup group = new NioEventLoopGroup();
//        try {
//            Bootstrap b = new Bootstrap();
//            b.group(group)
//             .channel(NioSocketChannel.class)
//             .option(ChannelOption.TCP_NODELAY, true)
//             .handler(new ChannelInitializer<SocketChannel>() {
//                 @Override
//                 public void initChannel(SocketChannel ch) throws Exception {
//                     ChannelPipeline p = ch.pipeline();
//                     //                     p.addLast(new LoggingHandler(LogLevel.INFO));
//                     p.addLast(new EchoClientHandler());
//                 }
//             });
//
//            // Start the client.
//            ChannelFuture f = b.connect(HOST, PORT).sync();
//
//            // Wait until the connection is closed.
//            f.channel().closeFuture().sync();
//        } finally {
//            // Shut down the event loop to terminate all threads.
//            group.shutdownGracefully();
//        }
//    }

    public static void main(String[] args) {
        int port = 8080; // 与服务器的端口一致
        String host = "127.0.0.1"; // 服务器的地址

        try (Socket socket = new Socket(host, port)) {
            System.out.println("已连接到服务器");

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            writer.println("Hello, Server!");

            String response = reader.readLine();
            System.out.println("服务器回应: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
