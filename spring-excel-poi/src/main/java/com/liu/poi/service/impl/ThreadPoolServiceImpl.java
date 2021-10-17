package com.liu.poi.service.impl;


import com.liu.poi.service.ThreadPoolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ThreadPoolServiceImpl implements ThreadPoolService {


    @Async("taskExecutor") //指定使用那个线程池配置，不然会使用spring默认的线程池
    @Override
    public void executeAsync() {

        log.info("start executeAsync");
        try {
            System.out.println("当前运行的线程名称：" + Thread.currentThread().getName());
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("end executeAsync");
    }
}
