package com.liu.juc.pc;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Demo2 {


    public static void main(String[] args) {

        Data2 data = new Data2();


        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                data.incrment();
            }, "incr" + i).start();
        }

        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                data.decrement();
            }, "decr" + i).start();
        }
    }
}


class Data2 {
    private int number = 0;
    private ReentrantLock reentrantLock = new ReentrantLock();
    private Condition condition = reentrantLock.newCondition();


    //添加
    void incrment() {

        reentrantLock.lock();
        try {
            while (number != 0) {
                condition.await();
            }
            number++;
            System.out.println(Thread.currentThread().getName() + "线程执行完毕后，number数量:" + number);
            condition.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            reentrantLock.unlock();
        }
    }


    //减少
    void decrement() {
        reentrantLock.lock();

        try {
            while (number == 0) {
                condition.await();
            }
            number--;
            System.out.println(Thread.currentThread().getName() + "线程执行完毕后，number数量:" + number);
            condition.signalAll();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            reentrantLock.unlock();
        }
    }
}

