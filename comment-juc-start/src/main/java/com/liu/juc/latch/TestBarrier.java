package com.liu.juc.latch;

import java.util.concurrent.CyclicBarrier;

public class TestBarrier {

    public static void main(String[] args) {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(7, new Runnable() {
            @Override
            public void run() {
                System.out.println("开始召唤神龙");
            }
        });

        for (int i = 0; i < 7; i++) {
            new Thread(()->{
                System.out.println(Thread.currentThread().getName());
                try {
                    cyclicBarrier.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            },String.valueOf(i)).start();
        }
    }
}
