package com.wudi.socket.test.client;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author Dillon Wu
 * @Description: socket-tcp 客户端
 * @date 2020/10/19 19:42
 */
public class SocketClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket();
        //超时时间
        socket.setSoTimeout(3000);
        socket.connect(new InetSocketAddress(Inet4Address.getLocalHost(), 2000), 3000);
        System.out.println("已发起服务器连接，并进入后续流程");
        System.out.println("客户端信息:" + socket.getLocalAddress() + "P:" + socket.getLocalPort());
        System.out.println("服务器信息:");
        try {
            todo(socket);
        } catch (Exception e) {
            System.out.println("异常关闭");
        }
        //资源释放
        socket.close();
        System.out.println("客户端已经退出~");
    }

    private static void todo(Socket client) throws IOException {
        //键盘输入
        InputStream inputStream = System.in;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        //得到Socket输入流,并转换为BufferReader
        InputStream clientInputStream = client.getInputStream();
        BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientInputStream));
        boolean flag = true;
        //得到Socket输出流======》并转换为打印流
        OutputStream outputStream = client.getOutputStream();
        PrintStream socketPrintStream = new PrintStream(outputStream);
        do {
            //键盘读取一行
            String str = bufferedReader.readLine();
            System.out.println("获取键盘数据:"+str);
            //发送出去---》发送给服务器
            socketPrintStream.println(str);
            //客户端接收----》服务器回送信息
            String echo = clientReader.readLine();
            if ("bye".equalsIgnoreCase(echo)) {
                flag = false;
            } else {
                System.out.println("客户端接收服务器信息:" + echo);
            }
        } while (flag);
        //资源释放
        socketPrintStream.close();
        clientReader.close();

    }


}
