package com.liu.product.beanaware;

import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "strategy")
public class StrategyFactory implements ApplicationContextAware {

    private ApplicationContext   applicationContext;

    private Map<Integer,String> strategyMap;

    public Strategy getStrategy(Integer level){
        //根据等级获取策略ID
        String id = strategyMap.get(level);
        //根据ID获取对应实例
        return applicationContext.getBean(id,Strategy.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
