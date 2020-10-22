package com.wudi.socket.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * @author Dillon Wu
 * @Description: UDP提供者，用于提供服务
 * @date 2020/10/22 11:15
 */
public class UDPProvider {
    public static void main(String[] args) throws IOException {
        System.out.println("UDPProvider Started");
        //作为接收者，指定一个端口用于数据接收
        DatagramSocket ds = new DatagramSocket(2000);
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
        //构建一份回送数据
        String responseData = "Receive data with len:" + dataLen;
        byte[] responseDataBytes = responseData.getBytes();
        DatagramPacket responsePacket = new DatagramPacket(responseDataBytes,
                responseDataBytes.length, receivePack.getAddress(), receivePack.getPort());
        ds.send(receivePack);
        // 完成
        System.out.println("UDPProvider Finished.");
        ds.close();


    }
}
