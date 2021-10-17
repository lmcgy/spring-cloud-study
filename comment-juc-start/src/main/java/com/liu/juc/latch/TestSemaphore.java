package com.liu.juc.latch;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class TestSemaphore {

    public static void main(String[] args) {


        Semaphore semaphore = new Semaphore(3);

        for (int i = 0; i <6 ; i++) {
            new Thread(()->{
                try {
                    semaphore.acquire();//得到
                    System.out.println(Thread.currentThread().getName()+"抢到车位");
                    TimeUnit.SECONDS.sleep(2);
                    System.out.println(Thread.currentThread().getName()+"离开车位");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    semaphore.release();//释放
                }
            },String.valueOf(i)).start();
        }
    }
}
