package com.wudi.socket.test.server;

import com.sun.security.ntlm.Client;
import com.sun.security.ntlm.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Dillon Wu
 * @Description: socket-tcp 服务端
 * @date 2020/10/19 19:43
 */
public class SocketServer {
    public static void main(String[] args) throws IOException {
        //如果不指定地址，默认为127.0.0.1
        ServerSocket serverSocket = new ServerSocket(2000);
        System.out.println("服务器追本就绪");
        System.out.println("服务器信息:" + serverSocket.getInetAddress() + "P:" + serverSocket.getLocalPort());
        // TODO: 2020/10/19
        for (; ; ) {
            //等待客户端连接
            Socket client = serverSocket.accept();
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
}
