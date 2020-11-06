package com.wudi.socket.chapter5_tcp.clink.net.qiujuer.clink.utils;

import java.io.Closeable;

/**
 * @author Dillon Wu
 * @Description:
 * @date 2020/10/27 18:06
 */
public class CloseUtils {

    public static void close(Closeable ...closeables){
        if (closeables == null){
            return;
        }
        for (Closeable closeable:closeables){
            try{
                closeable.close();
            }catch (Exception e){}

        }
    }
}
