package com.wudi.socket.chapter5_udp.client;

import com.wudi.socket.chapter5_udp.client.bean.ServerInfo;

public class Client {
    public static void main(String[] args) {
        //搜索超时时间为10s
        ServerInfo info = ClientSearcher.searchServer(10000);
        System.out.println("Server:" + info);
    }
}
