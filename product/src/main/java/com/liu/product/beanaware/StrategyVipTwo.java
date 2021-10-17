package com.liu.product.beanaware;

import org.springframework.stereotype.Component;

@Component(value = "strategyVipTwo")
public class StrategyVipTwo implements Strategy {


    @Override
    public Integer payMoney(Integer payMoney) {
        return payMoney-5;
    }
}
