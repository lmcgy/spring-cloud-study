package com.liu.poi.service;


import com.liu.poi.PoiApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= PoiApplication.class)
public class ThreadPoolServiceTest {

    @Autowired
    ThreadPoolService threadPoolService;

    @Test
    public void executeAsync() {
        for (int i = 0; i < 20; i++) {
            threadPoolService.executeAsync();
        }

    }
}