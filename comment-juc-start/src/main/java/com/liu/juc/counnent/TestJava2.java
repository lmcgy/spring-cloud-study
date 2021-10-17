package com.liu.juc.counnent;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TestJava2 {


    public static void main(String[] args) {

        Tick2 tick = new Tick2();

        new Thread(() -> {
            for (int i = 0; i < 2000; i++) {
                tick.sale();
            }
        }, "A").start();


        new Thread(() -> {
            for (int i = 0; i < 2000; i++) {
                tick.sale();
            }
        }, "B").start();

        new Thread(() -> {
            for (int i = 0; i < 2000; i++) {
                tick.sale();
            }
        }, "C").start();

    }
}


class Tick2 {
    private int number = 6000;

    private Lock reentrantLock = new ReentrantLock();

    public void sale() {

        reentrantLock.lock();

        try {
            number--;
            System.out.println(Thread.currentThread().getName() + "剩余" + number + "张票");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            reentrantLock.unlock();
        }
    }
}