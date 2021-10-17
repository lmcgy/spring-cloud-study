package com.liu.mybatis.address.service;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class ISysRegionDictServiceTest {

    @Autowired
    SysRegionDictService sysRegionDictService;

    @Test
    public void saveRegionDict() {
        sysRegionDictService.saveRegionDict();
    }


    @Test
    public void task() {

        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 55; i++) {
            list.add(i);
        }
        sysRegionDictService.task(list);
    }

}