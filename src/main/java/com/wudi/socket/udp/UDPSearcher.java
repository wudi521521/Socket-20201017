package com.wudi.socket.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author Dillon Wu
 * @Description:UDP 搜索者，用户搜索服务支持方
 * @date 2020/10/22 11:16
 */
public class UDPSearcher {
    private static final int LISTEN_PORT = 30000;

    private static void sendBroadcast() throws IOException {
        System.out.println("UDPSearcher sendBroadcast Started");
        //作为搜索方，让系统自动分配端口
        DatagramSocket ds = new DatagramSocket();

        //监听
        String requestData = MessageCreator.buildWithPort(LISTEN_PORT);
        byte[] requestDataBytes = requestData.getBytes();
        DatagramPacket requestPacket = new DatagramPacket(requestDataBytes,
                requestDataBytes.length);
        //广播地址
        requestPacket.setAddress(InetAddress.getByName("255.255.255.255"));
        requestPacket.setPort(2000);
        //发送
        ds.send(requestPacket);
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
        System.out.println("UDPSearcher receive form ip:" + ip + "\tport:" + port + "\tdata:" + data);

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("UDPSearcher Started");
        Listener listen = listen();
        sendBroadcast();
        //读取任意键盘信息后可以退出
        System.in.read();
        List<Device> devices = listen.getDevicesAndClose();
        for (Device device : devices) {
            System.out.println("Device:"+device.toString());
        }
        System.out.println("UDPSearcher Finished!");
    }

    private static Listener listen() throws InterruptedException {
        System.out.println("UDPSearcher start listen");
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Listener listener = new Listener(LISTEN_PORT, countDownLatch);
        listener.start();
        countDownLatch.await();
        return listener;
    }


    private static class Device {
        final int port;
        final String ip;
        final String sn;

        private Device(int port, String ip, String sn) {
            this.port = port;
            this.ip = ip;
            this.sn = sn;
        }
    }

    private static class Listener extends Thread {
        private final int listenPort;
        private final CountDownLatch countDownLatch;
        private final List<Device> devices = new ArrayList();
        private boolean done = false;
        private DatagramSocket ds = null;

        public Listener(int listenPort, CountDownLatch countDownLatch) {
            super();
            this.listenPort = listenPort;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            super.run();
            //通知已启动
            countDownLatch.countDown();
            try {
                ds = new DatagramSocket(listenPort);
                while (!done) {
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
                    String sn = MessageCreator.parseSn(data);
                    if (sn != null) {
                        Device device = new Device(port, ip, sn);
                        devices.add(device);
                    }
                }
            } catch (Exception e) {

            } finally {
                close();
            }
            System.out.println("UDPSearcher listener finish!");
        }

        private void close() {
            if (ds != null) {
                ds.close();
                ds = null;
            }
        }

        List<Device> getDevicesAndClose() {
            done = true;
            close();
            return devices;
        }
    }
}
