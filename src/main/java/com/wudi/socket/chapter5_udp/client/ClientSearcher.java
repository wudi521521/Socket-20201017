package com.wudi.socket.chapter5_udp.client;

import com.wudi.socket.chapter5_udp.client.bean.ServerInfo;
import com.wudi.socket.chapter5_udp.clink.ByteUtils;
import com.wudi.socket.chapter5_udp.constants.UDPConstants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ClientSearcher {
    private static final int LISTEN_PORT = UDPConstants.PORT_CLIENT_RESPONSE;

    public static ServerInfo searchServer(int timeout) {
        System.out.println("UDPSearcher Started.");

        // 成功收到回送的栅栏
        CountDownLatch receiveLatch = new CountDownLatch(1);
        //监听者
        Listener listener = null;
        try {
            listener = listen(receiveLatch);
            //发送
            sendBroadcast();
            //阻塞等待时长
            receiveLatch.await(timeout, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 完成
        System.out.println("UDPSearcher Finished.");
        if (listener == null) {
            return null;
        }
        List<ServerInfo> devices = listener.getServerAndClose();
        if (devices.size() > 0) {
            return devices.get(0);
        }
        return null;
    }

    /**
     * 监听
     * @param receiveLatch
     * @return
     * @throws InterruptedException
     */
    private static Listener listen(CountDownLatch receiveLatch) throws InterruptedException {
        System.out.println("UDPSearcher start listen.");
        CountDownLatch startDownLatch = new CountDownLatch(1);
        Listener listener = new Listener(LISTEN_PORT, startDownLatch, receiveLatch);
        listener.start();
        startDownLatch.await();
        return listener;
    }

    private static void sendBroadcast() throws IOException {
        System.out.println("UDPSearcher sendBroadcast started.");

        // 作为搜索方，让系统自动分配端口
        DatagramSocket ds = new DatagramSocket();

        // 构建一份请求数据
        ByteBuffer byteBuffer = ByteBuffer.allocate(128);
        // 头部
        byteBuffer.put(UDPConstants.HEADER);
        // CMD命名
        byteBuffer.putShort((short) 1);
        // 回送端口信息
        byteBuffer.putInt(LISTEN_PORT);
        // 直接构建packet
        DatagramPacket requestPacket = new DatagramPacket(byteBuffer.array(),
                byteBuffer.position() + 1);
        // 广播地址
        requestPacket.setAddress(InetAddress.getByName("255.255.255.255"));
        // 设置服务器端口
        requestPacket.setPort(UDPConstants.PORT_SERVER);

        // 发送
        ds.send(requestPacket);
        ds.close();

        // 完成
        System.out.println("UDPSearcher sendBroadcast finished.");
    }

    /**
     * 监听者实现一个线程
     */
    private static class Listener extends Thread {
        //监听端口
        private final int listenPort;
        //开始countDown
        private final CountDownLatch startDownLatch;
        //接收countDown
        private final CountDownLatch receiveDownLatch;
        //所有接收到的serverInfo列表
        private final List<ServerInfo> serverInfoList = new ArrayList<ServerInfo>();
        private final byte[] buffer = new byte[128];
        //最小验证长度
        private final int minLen = UDPConstants.HEADER.length + 2 + 4;
        //是否完成一个状态
        private boolean done = false;
        //发送socket
        private DatagramSocket ds = null;

        private Listener(int listenPort, CountDownLatch startDownLatch, CountDownLatch receiveDownLatch) {
            super();
            this.listenPort = listenPort;
            this.startDownLatch = startDownLatch;
            this.receiveDownLatch = receiveDownLatch;
        }

        @Override
        public void run() {
            super.run();

            // 通知已启动
            startDownLatch.countDown();
            try {
                // 监听回送端口
                ds = new DatagramSocket(listenPort);
                // 构建接收实体
                DatagramPacket receivePack = new DatagramPacket(buffer, buffer.length);

                while (!done) {
                    // 接收
                    ds.receive(receivePack);
                    // 打印接收到的信息与发送者的信息
                    // 发送者的IP地址
                    String ip = receivePack.getAddress().getHostAddress();
                    int port = receivePack.getPort();
                    int dataLen = receivePack.getLength();
                    byte[] data = receivePack.getData();
                    boolean isValid = dataLen >= minLen
                            && ByteUtils.startsWith(data, UDPConstants.HEADER);

                    System.out.println("UDPSearcher receive form ip:" + ip
                            + "\tport:" + port + "\tdataValid:" + isValid);

                    if (!isValid) {
                        // 无效继续
                        continue;
                    }

                    ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, UDPConstants.HEADER.length, dataLen);
                    final short cmd = byteBuffer.getShort();
                    final int serverPort = byteBuffer.getInt();
                    if (cmd != 2 || serverPort <= 0) {
                        System.out.println("UDPSearcher receive cmd:" + cmd + "\tserverPort:" + serverPort);
                        continue;
                    }

                    String sn = new String(buffer, minLen, dataLen - minLen);
                    ServerInfo info = new ServerInfo(serverPort, ip, sn);
                    serverInfoList.add(info);
                    // 成功接收到一份
                    receiveDownLatch.countDown();
                }
            } catch (Exception ignored) {
            } finally {
                close();
            }
            System.out.println("UDPSearcher listener finished.");
        }

        private void close() {
            if (ds != null) {
                ds.close();
                ds = null;
            }
        }

        List<ServerInfo> getServerAndClose() {
            done = true;
            close();
            return serverInfoList;
        }
    }
}
