package com.wudi.socket.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @author Dillon Wu
 * @Description:UDP 搜索者，用户搜索服务支持方
 * @date 2020/10/22 11:16
 */
public class UDPSearcher {
    public static void main(String[] args) throws IOException {
        System.out.println("UDPSearcher Started");
        //作为搜索方，让系统自动分配端口
        DatagramSocket ds = new DatagramSocket();

        //构建一份回送数据
        String requestData = "HelloWorld!";
        byte[] requestDataBytes = requestData.getBytes();
        DatagramPacket requestPacket = new DatagramPacket(requestDataBytes,
                requestDataBytes.length);
        requestPacket.setAddress(InetAddress.getLocalHost());
        requestPacket.setPort(2000);
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

        // 完成
        System.out.println("UDPSearcher Finished.");
        ds.close();


    }
}
