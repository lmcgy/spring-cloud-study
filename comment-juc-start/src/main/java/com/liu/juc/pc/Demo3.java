package com.liu.juc.pc;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Demo3 {

    public static void main(String[] args) {
        Data3 data3 = new Data3();
        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                data3.printA();
            }
        }).start();

        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                data3.printB();
            }
        }).start();

        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                data3.printC();
            }
        }).start();
    }


}

class Data3{
    private ReentrantLock reentrantLock = new ReentrantLock();
    private Condition conditionA = reentrantLock.newCondition();
    private Condition conditionB = reentrantLock.newCondition();
    private Condition conditionC = reentrantLock.newCondition();

    private int number = 1;

    public void printA(){
        reentrantLock.lock();
        try {
            while (number!=1) {
                conditionA.await();
            }

            System.out.println("当前是A在运行");
            number = 2;
            conditionB.signal();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            reentrantLock.unlock();
        }

    }
    public void printB(){

        reentrantLock.lock();
        try {
            while (number!=2) {
                conditionB.await();
            }

            System.out.println("当前是B在运行");
            number =3;
            conditionC.signal();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            reentrantLock.unlock();
        }

    }
    public void printC(){


        reentrantLock.lock();
        try {
            while (number!=3) {
                conditionC.await();
            }

            System.out.println("当前是C在运行");
            number =1;
            conditionA.signal();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            reentrantLock.unlock();
        }
    }

}
