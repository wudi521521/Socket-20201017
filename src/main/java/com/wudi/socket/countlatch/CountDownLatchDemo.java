package com.wudi.socket.countlatch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Dillon Wu
 * @Description:
 * @date 2020/10/27 16:52
 */
public class CountDownLatchDemo {
    public static void main(String[] args) {
        //这个线程池可以设置多个线程
        ExecutorService executorService = Executors.newCachedThreadPool();
        CountDownLatch latch = new CountDownLatch(3);
        Worker w1 = new Worker(latch,"张三");
        Worker w2 = new Worker(latch,"李四");
        Worker w3 = new Worker(latch,"王二");
        Boss boss = new Boss(latch);
        executorService.execute(w3);
        executorService.execute(w1);
        executorService.execute(w2);
        executorService.execute(boss);
        executorService.shutdown();
    }
}
