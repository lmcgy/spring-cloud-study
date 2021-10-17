package com.liu.juc.readwritelock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockTest {

    public static void main(String[] args) {

        MyCache myCache = new MyCache();

        for (int i = 0; i < 20; i++) {
            final int tem = i;
            new Thread(()->{
                myCache.addCache(tem+"",tem+"");
            },String.valueOf(i)).start();
        }

        for (int i = 39; i >= 20; i--) {
            final int tem = i;
            new Thread(()->{
                myCache.findaddCache(String.valueOf(tem-20));
            },String.valueOf(i)).start();
        }


    }
}

class MyCache{
    private Map<String,Object> cache = new HashMap<>();

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public  void addCache(String key,Object value){
        readWriteLock.writeLock().lock();
        try {
            System.out.println(Thread.currentThread().getName()+"开始插入");
            cache.put(key, value);
            System.out.println(Thread.currentThread().getName()+"插入完毕");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            readWriteLock.writeLock().unlock();
        }

    }

    public void findaddCache(String key){
        //readWriteLock.readLock().lock();
        try {
            System.out.println(Thread.currentThread().getName()+"开始获取");
            Object o = cache.get(key);
            System.out.println(Thread.currentThread().getName()+"获取完毕:"+o);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //readWriteLock.readLock().unlock();
        }

    }
}



