package com.wudi.socket.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.UUID;

/**
 * @author Dillon Wu
 * @Description: UDP提供者，用于提供服务
 * @date 2020/10/22 11:15
 */
public class UDPProvider {
    public static void main(String[] args) throws IOException {
        //生成一份唯一标识
        String sn = UUID.randomUUID().toString();
        Provider provider = new Provider(sn);
        provider.start();
        //读取键盘信息退出
        System.in.read();
        provider.exit();


    }
    private static class Provider extends Thread {
        private final String sn;
        private boolean done = false;
        private DatagramSocket ds = null;
        public Provider(String sn) {
            super();
            this.sn = sn;
        }

        @Override
        public void run() {
            super.run();
            System.out.println("UDPProvider Started");

            try {
                //监听端口
                ds = new DatagramSocket(2000);
                while (!done) {
                    //System.out.println("===while====");
                    //构建接收实体
                    final byte[] buf = new byte[512];
                    DatagramPacket receivePack = new DatagramPacket(buf, buf.length);
                    //接收
                    ds.receive(receivePack);
                    //打印接收到的信息与发送者的信息
                    //发送者的IP地址
                    int port = receivePack.getPort();
                    String ip = receivePack.getAddress().getHostAddress();
                    int dataLen = receivePack.getLength();
                    String data = new String(receivePack.getData(), 0, dataLen);
                    System.out.println("UDPProvider receive form ip:" + ip + "\tport:" + port + "\tdata:" + data);
                    //解析端口
                    int responsePort = MessageCreator.parsePort(data);
                    if (responsePort != -1) {
                        //构建一份回送数据
                        String responseData = MessageCreator.buildWithSn(sn);
                        byte[] responseDataBytes = responseData.getBytes();
                        //直接根据发送者构建一份会送信息
                        DatagramPacket responsePacket = new DatagramPacket(responseDataBytes,
                                responseDataBytes.length, receivePack.getAddress(), receivePack.getPort());
                        ds.send(responsePacket);
                    }

                }
            } catch (Exception ignored) {

            } finally {
                close();
            }
            // 完成
            System.out.println("UDPProvider Finished.");


        }

        void  exit() {
            System.out.println("调用exit:");
            done = true;
            close();
        }

        /**
         * 提供结束
         */
        private void close() {
            if (ds != null) {
                ds.close();
                ds = null;
            }
        }
    }
}
