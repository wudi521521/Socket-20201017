package com.wudi.socket.chapter5_tcp.server.handle;

import com.wudi.socket.chapter5_tcp.clink.net.qiujuer.clink.utils.CloseUtils;

import java.io.*;
import java.net.Socket;

/**
 * @author Dillon Wu
 * @Description:
 * @date 2020/10/27 17:47
 */
public class ClientHandler {
    private final Socket socket;
    private final ClientReadHandler readHandler;
    private final ClientWriteHandler writeHandler;
    private boolean flag = true;

    ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.readHandler = new ClientReadHandler(socket.getInputStream());
        this.writeHandler = new ClientWriteHandler(socket.getOutputStream());
        System.out.println("新客户端连接：" + socket.getInetAddress() +
                " P:" + socket.getPort());
    }

    //离开方法
    public void exit() {
        readHandler.exit();
        writeHandler.exit();
        CloseUtils.close(socket);
        System.out.println("客户退出：" + socket.getInetAddress() +
                " P:" + socket.getPort());
    }

    //发送方法
    public void send(String str) {
        writeHandler.send(str);
    }

    //读取并发送
    public void readToPrint() {
        readHandler.start();
    }

    private void exitBySelf() {
        exit();
    }

    class ClientReadHandler extends Thread {
        private boolean done = false;
        private final InputStream inputStream;

        ClientReadHandler(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            super.run();
            try {
                // 得到打印流，用于数据输出；服务器回送数据使用
                //PrintStream socketOutput = new PrintStream(socket.getOutputStream());
                // 得到输入流，用于接收数据
                BufferedReader socketInput = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));

                do {
                    // 客户端拿到一条数据
                    String str = socketInput.readLine();
                    if (str == null) {
                        System.out.println("客户端已无法读取数据");
                        //退出当前客户端
                        ClientHandler.this.exitBySelf();
                        break;
                    }
                    if ("bye".equalsIgnoreCase(str)) {
                        flag = false;
                        // 回送
                        // socketOutput.println("bye");
                    } else {
                        // 打印到屏幕。并回送数据长度
                        System.out.println(str);
                        // socketOutput.println("回送：" + str.length());
                    }
                    //打印到屏幕上
                    System.out.println(str);

                } while (flag);
                socketInput.close();
                //socketOutput.close();
            } catch (Exception e) {
                if (!done) {
                    System.out.println("连接异常断开");
                    //关闭
                    ClientHandler.this.exitBySelf();
                }
                System.out.println("连接异常断开");
            } finally {
                // 连接关闭
                CloseUtils.close(inputStream);
            }
            System.out.println("客户端已退出：" + socket.getInetAddress() +
                    " P:" + socket.getPort());
        }
        //离开
        void exit() {
            done = true;
            CloseUtils.close(inputStream);
        }
    }
    class ClientWriteHandler extends  Thread{
        private boolean done =false;
        private final PrintStream printStream;
        ClientWriteHandler(OutputStream outputStream){
            this.printStream = new PrintStream(outputStream );
        }

        @Override
        public void run() {
            super.run();
            printStream.println("sada");
        }
        void exit(){
            done = true;
            CloseUtils.close(printStream);
        }
        void send(String str){

        }
    }

}
