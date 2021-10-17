package com.liu.juc.counnent;


public class TestJava {


    public static void main(String[] args){

        Tick tick = new Tick();

        new Thread(() ->{
            for (int i = 0; i < 2000; i++) {
                tick.sale();
            }
        },"A").start();


        new Thread(() ->{
            for (int i = 0; i < 2000; i++) {
                tick.sale();
            }
        },"B").start();

        new Thread(() ->{
            for (int i = 0; i < 2000; i++) {
                tick.sale();
            }
        },"C").start();

    }
}


class Tick{
    private int number = 6000;

    public synchronized void sale(){
        number--;
        System.out.println(Thread.currentThread().getName()+"剩余"+number+"张票");
    }
}