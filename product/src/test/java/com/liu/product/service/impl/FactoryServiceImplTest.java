package com.liu.product.service.impl;

import com.liu.product.service.FactoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;



@SpringBootTest
public class FactoryServiceImplTest {

    @Autowired
    private FactoryService factoryService;

    @Test
    public void payMoney() {
        int i = factoryService.payMoney(1, 10);
        System.out.println(i);
    }
}