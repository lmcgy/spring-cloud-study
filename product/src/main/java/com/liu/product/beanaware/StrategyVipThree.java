package com.liu.product.beanaware;

import org.springframework.stereotype.Component;

@Component(value = "strategyVipThree")
public class StrategyVipThree  implements Strategy {
    @Override
    public Integer payMoney(Integer payMoney) {
        return Integer.parseInt(String.valueOf(payMoney*0.7));
    }
}
