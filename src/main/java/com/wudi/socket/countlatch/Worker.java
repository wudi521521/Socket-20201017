package com.wudi.socket.countlatch;

import java.sql.SQLOutput;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Dillon Wu
 * @Description:
 * @date 2020/10/27 16:40
 */
public class Worker implements Runnable {
    private CountDownLatch downLatch;
    private String name;
    public Worker(CountDownLatch downLatch,String name){
        this.downLatch = downLatch;
        this.name = name;
    }
    public void run() {
         this.doWork();
         try {
             TimeUnit.SECONDS.sleep(new Random().nextInt(10));
         }catch (Exception e){

         }
        System.out.println(this.name+"活干完了");
         //减1
         this.downLatch.countDown();
    }
    private void doWork(){
        System.out.println(this.name+"正在干活!");
    }
}
