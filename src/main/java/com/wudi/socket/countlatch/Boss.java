package com.wudi.socket.countlatch;

import java.util.concurrent.CountDownLatch;

/**
 * @author Dillon Wu
 * @Description:
 * @date 2020/10/27 16:45
 */
public class Boss implements Runnable {
    private CountDownLatch downLatch;
    public Boss(CountDownLatch downLatch){
        this.downLatch = downLatch;
    }
    public void run() {
        System.out.println("老板正在等所有的工人赶完活。。。。。");
        try{
            //CountDownLatch对象设置一个初始化的数字为计数值，任何调用这个对象上的await()方法都会阻塞
            //直到这个计数器的计数值被其他的线程减为0为止
            this.downLatch.await();
        }catch (Exception e){
        }
        System.out.println("工人活都干完了,老板开始检查啦");
    }
}
