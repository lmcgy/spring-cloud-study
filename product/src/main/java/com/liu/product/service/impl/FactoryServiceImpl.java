package com.liu.product.service.impl;

import com.liu.product.beanaware.Strategy;
import com.liu.product.beanaware.StrategyFactory;
import com.liu.product.service.FactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FactoryServiceImpl implements FactoryService {

    @Autowired
    private StrategyFactory strategyFactory;

    @Override
    public int payMoney(Integer level,Integer monery) {
        Strategy strategy = strategyFactory.getStrategy(level);
        return strategy.payMoney(monery);

    }
}
