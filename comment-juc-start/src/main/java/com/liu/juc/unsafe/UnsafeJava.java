package com.liu.juc.unsafe;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

public class UnsafeJava {


    public static void main(String[] args) throws InterruptedException {

        List<String> objects = new ArrayList<>();

        /*for (int i = 0; i < 1000; i++) {
            new Thread(()->{
                for (int j = 0; j < 10; j++) {
                    objects.add(UUID.randomUUID().toString().substring(0,5));
                }
            }).start();
        }*/

        CopyOnWriteArrayList<Object> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
        for (int i = 0; i < 10; i++) {
            new Thread(()->{
                copyOnWriteArrayList.add(UUID.randomUUID().toString().substring(0,5));
            }).start();
        }

        CopyOnWriteArraySet<Object> copyOnWriteArraySet = new CopyOnWriteArraySet<>();
        for (int i = 0; i < 10; i++) {
            new Thread(()->{
                copyOnWriteArraySet.add(UUID.randomUUID().toString().substring(0,5));
            }).start();
        }

        TimeUnit.SECONDS.sleep(2);


        copyOnWriteArrayList.forEach(System.out::println);


        System.out.println("--------------------");

        copyOnWriteArraySet.forEach(System.out::println);
    }
}
