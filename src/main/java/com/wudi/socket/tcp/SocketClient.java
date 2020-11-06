package com.wudi.socket.tcp;

import java.io.*;
import java.net.*;

/**
 * @author Dillon Wu
 * @Description: socket-tcp 客户端
 * @date 2020/10/19 19:42
 */
public class SocketClient {
    //远程端口
    private static final int PORT = 20000;
    //本地端口
    private static final int LOCAL_PORT = 20001;

    public static void main(String[] args) throws IOException {
        Socket socket = createSocket();
        //超时时间
        socket.setSoTimeout(3000);
        socket.connect(new InetSocketAddress(Inet4Address.getLocalHost(), LOCAL_PORT), 3000);
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
            System.out.println("获取键盘数据:" + str);
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
    private static void initSocket(Socket socket) throws SocketException{
       //设置读取超时时间为2s
        socket.setSoTimeout(2000);
        //是否复用未完全关闭的Socket的地址，对于指定bind操作后的套接字有效
        socket.setReuseAddress(true);
        //是否开启Nagle算法
        //socket.setTcpNoDelay(false);
        //是否需要在长时无数据响应时发送确认数据(类似心跳包)，时间大约为2小时
        socket.setKeepAlive(true);
        //对于close关闭操作行为进行怎样的处理;默认为false,0
        //false,0;默认情况，关闭时立即返回，底层系统接管输出流，将缓冲去内的数据发送完成
        //true,0:关闭时立即返回，缓冲区数据抛弃，直接发送RST结束命令到对方，并无需经过2MSL等待
        //true,200:关闭时最长阻塞200ms,随后按第二种情况处理
        socket.setSoLinger(true,20);
        //是否让紧急数据内敛，默认false;紧急数据通过，socket.sendUrgentData(1);发送
        socket.setOOBInline(true);
        //socket.sendUrgentData(1);
        //设置接收发送缓冲器大小
        socket.setReceiveBufferSize(64*1024*1024);
        socket.setSendBufferSize(64*1024*1024);
    }

    private static Socket createSocket() throws IOException {
      /*  //无代理模式，等效于空构造函数(new Socket())
        Socket socket = new Socket(Proxy.NO_PROXY);
        //新建一份具有HTTP代代理的套接字，传输数据将通过www.baidu.com:8080端口转发
        Proxy proxy= new Proxy(Proxy.Type.HTTP,new InetSocketAddress(Inet4Address.getByName("www.baidu.com"),8080));
        socket = new Socket(proxy);
        //新建一个套接字，并且直接链到本地20000的服务器上
        socket = new Socket("localhost",PORT);
        //新建一个套接字,并且直接链接到本地20000的服务器上
        socket = new Socket("localhost",PORT,Inet4Address.getLocalHost(),LOCAL_PORT);
        socket = new Socket(Inet4Address.getLocalHost(),PORT,Inet4Address.getLocalHost(),LOCAL_PORT);*/
        //绑定本地20001端口
        Socket socket = new Socket();
        socket.bind(new InetSocketAddress(Inet4Address.getLocalHost(), LOCAL_PORT));
        //socket.connect();
        return socket;
    }
}
