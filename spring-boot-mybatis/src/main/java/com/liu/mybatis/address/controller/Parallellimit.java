package com.liu.mybatis.address.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Parallellimit {

    public static void main(String[] args) {

        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 105; i++) {
            list.add(i);
        }
        CountRunnable runnable = new CountRunnable();
        runnable.task(list);
    }
}



class CountRunnable {

    public void task(List<Integer> exportVehicleRelationDtos) {
        //100分一组
        int count = 20;

        int listSize = exportVehicleRelationDtos.size();
        //线程数
        int RunSize = listSize % count == 0 ? (listSize / count):(listSize / count)+1;
        ThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(RunSize);

        List<Integer>  newList = null ;
        List<Integer>  lastList = new ArrayList<>();
        for (int i = 0; i < RunSize; i++) {
            if((i+1)==RunSize){
                int startIndex = (i*count);
                int endIndex = exportVehicleRelationDtos.size();
                newList =exportVehicleRelationDtos.subList(startIndex,endIndex);
            }else{
                int startIndex = i*count;
                int endIndex = (i+1)*count;
                newList =exportVehicleRelationDtos.subList(startIndex,endIndex);
            }
            List<Integer> finalNewList = newList;

            CountDownLatch countDownLatch = new CountDownLatch(finalNewList.size());

            for (Integer dto : finalNewList) {
                StartAgent startAgent = new StartAgent(countDownLatch,dto);
                executor.submit(startAgent);
            }

            try {
                countDownLatch.await();  //调用await()方法的线程会被挂起，它会等待直到count值为0才继续执行
                System.out.println("vehicleExportSize"+lastList.size());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //关闭线程池
        executor.shutdown();

    }



    class StartAgent implements Runnable {

        private CountDownLatch countDownLatch;
        private Integer dto;


        @Override
        public void run() {
            System.out.println("dto:"+dto);
            //注意一定要在finally调用countDown，否则产生异常导致没调用到countDown造成程序死锁
            countDownLatch.countDown();
            long count = countDownLatch.getCount();
            if (0==count){
                System.out.println("执行完一批");
            }
        }

        public StartAgent(CountDownLatch countDownLatch, Integer dto) {
            this.countDownLatch = countDownLatch;
            this.dto = dto;
        }
    }

    }
