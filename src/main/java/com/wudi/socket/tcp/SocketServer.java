package com.wudi.socket.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Dillon Wu
 * @Description: socket-tcp 服务端
 * @date 2020/10/19 19:43
 */
public class SocketServer {
    private static final int PORT = 20000;
    public static void main(String[] args) throws IOException {
        //如果不指定地址，默认为127.0.0.1
        ServerSocket server = createServerSocket();
        innitServerSocket(server);
        server.bind(new InetSocketAddress(Inet4Address.getLocalHost(),PORT),50);
        System.out.println("服务器准备就绪");
        System.out.println("服务器信息:" + server.getInetAddress() + "P:" + server.getLocalPort());
        // TODO: 2020/10/19
        for (; ; ) {
            //等待客户端连接
            Socket client = server.accept();
            ClientHandler clientHandler = new ClientHandler(client);
            //启动线程
            clientHandler.start();
        }


    }

    /**
     * 客户端消息处理
     */
    public static class ClientHandler extends Thread {
        private Socket socket;
        private boolean flag = true;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            super.run();
            System.out.println("新客户连接:" + socket.getInetAddress() + "P:" + socket.getLocalPort());
            try {
                //得到打印流,用于数据输出;服务器会送数据使用
                PrintStream socketOutPut = new PrintStream(socket.getOutputStream());
                BufferedReader socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                do {
                    //客户端拿到一条数据
                    String str = socketInput.readLine();
                    if ("bye".equalsIgnoreCase(str)) {
                        flag = false;
                        //会送
                        socketOutPut.println("bye");
                    } else {
                        //打印到屏幕，并回送数据长度======>客户端
                        System.out.println("接收客户端信息:"+str);
                        socketOutPut.println("会送:" + str.length());
                    }
                } while (flag);
                socketInput.close();
                socketOutPut.close();
            } catch (Exception e) {
                System.out.println("连接异常断开");
            } finally {
                try {
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            System.out.println("客户端已退出:" + socket.getInetAddress() + "P:" + socket.getPort());
        }
    }

    private static ServerSocket createServerSocket() throws IOException{
        //创建基础的ServerSocket
        ServerSocket serverSocket = new ServerSocket();
        //绑定到本地端口上
        //serverSocket = new ServerSocket(PORT);
        //等效于上面的方案，队列设置为50个
        //serverSocket = new ServerSocket(PORT,50);
        //与上面等同,等同于bind操作
        //serverSocket = new ServerSocket(PORT,50,Inet4Address.getLocalHost());

        return serverSocket;
    }

    private static void innitServerSocket(ServerSocket serverSocket) throws IOException{
        //是否复用未完全关闭的地址端口
        serverSocket.setReuseAddress(true);
        //等效Socket#setReceiveBufferSize
        serverSocket.setReceiveBufferSize(64*1024*1024);
        //设置serverSocket#accept超时时间
        //serverSocket.setSoTimeout(2000);
        //设置性能参数:短链接，延迟，带宽的相对重要性
        serverSocket.setPerformancePreferences(1,1,1);
    }
}
