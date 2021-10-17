package com.liu.juc.pc;

public class Demo1 {


    public static void main(String[] args) {

        Data data = new  Data();





        for (int i = 0; i < 20; i++) {
            new Thread(()->{
                try {
                    data.incrment();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            },"incr"+i).start();
        }

        for (int i = 0; i < 20; i++) {
            new Thread(()->{
                try {
                    data.decrement();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            },"decr"+i).start();
        }
    }
}



class Data{
    private int number = 0;

    //添加
    synchronized void  incrment() throws InterruptedException {

        while (number != 0){
            this.wait();
        }
        number++ ;
        System.out.println(Thread.currentThread().getName()+"线程执行完毕后，number数量:"+number);
        this.notify();

    }

    //减少
    synchronized void decrement() throws InterruptedException {
        while (number == 0){
            this.wait();
        }
        number-- ;
        System.out.println(Thread.currentThread().getName()+"线程执行完毕后，number数量:"+number);
        this.notify();
    }
}