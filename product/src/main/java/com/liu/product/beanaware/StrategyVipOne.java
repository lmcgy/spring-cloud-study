package com.liu.product.beanaware;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component(value = "strategyVipOne")
public class StrategyVipOne implements Strategy {


    @Override
    public Integer payMoney(Integer payMoney) {
        return payMoney;
    }
}
