package com.wudi.socket.chapter5_udp.server;

import com.wudi.socket.chapter5_udp.constants.TCPConstants;

/**
 * @author Dillon Wu
 * @Description: 服务器端
 * @date 2020/10/26 11:28
 */
public class Server {
    public static void main(String[] args) {
        ServerProvider.start(TCPConstants.PORT_SERVER);
        try{
            System.in.read();
        }catch (Exception e){
            e.printStackTrace();
        }
        ServerProvider.stop();
    }
}
