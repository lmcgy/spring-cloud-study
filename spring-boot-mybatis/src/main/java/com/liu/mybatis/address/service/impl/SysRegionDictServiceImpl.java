package com.liu.mybatis.address.service.impl;

import cn.hutool.core.util.StrUtil;
import com.liu.mybatis.address.entity.SysRegionDict;
import com.liu.mybatis.address.mapper.SysRegionDictMapper;
import com.liu.mybatis.address.module.AreaDTO;
import com.liu.mybatis.address.service.SysRegionDictService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.mybatis.address.utils.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>
 * 地区字典表 服务实现类
 * </p>
 *
 * @author jobob
 * @since 2020-12-10
 */
@Slf4j
@Service
public class SysRegionDictServiceImpl extends ServiceImpl<SysRegionDictMapper, SysRegionDict> implements SysRegionDictService {

    /**
     * 标识符
     */
    private static final String NONE = "[]";



    private static final String PROVINCE = "province";
    private static final String CITY = "city";
    private static final String DISTRICT = "district";
    private static final String STREET = "street";
    private static final String COUNTRY = "country";

    @Resource
    SysRegionDictMapper sysRegionDictMapper;

    @Override
    @Transactional
    public void saveRegionDict() {

        // 获取国家节点数据
        List<AreaDTO> countryList = HttpUtil.getInfo("", "3");
        saveAddress(countryList.get(0).getDistricts(),0L);

    }



    public void saveAddress(List<AreaDTO> countryList,Long parentCode){
        if (!CollectionUtils.isEmpty(countryList)){
            for (int i = 0; i < countryList.size(); i++) {
                AreaDTO area = countryList.get(i);

                SysRegionDict dict = new SysRegionDict();

                if (StrUtil.equals(NONE, area.getCitycode())) {
                    dict.setCityCode("");
                }else{
                    dict.setCityCode(area.getCitycode());
                }

                if (StrUtil.equals(STREET, area.getLevel())) {
                    Long streetId = Long.valueOf(area.getAdcode().concat(String.format("%02d", i + 1)));
                    dict.setId(streetId);
                }else{
                    dict.setId(Long.valueOf(area.getAdcode()));
                }

                dict.setRegionCode(area.getAdcode());
                dict.setRegionName(area.getName());
                dict.setParentRegionId(parentCode);

                Integer level = getLevel(area.getLevel());
                dict.setRegionLevel(level);
                dict.setRegionType(level);
                dict.setCenterId(1L);

                if (!CollectionUtils.isEmpty(area.getDistricts())){
                    saveAddress(area.getDistricts(),dict.getId());
                }

                sysRegionDictMapper.insert(dict);

            }

        }
    }

    private Integer getLevel(String level){
        if (StrUtil.equals(PROVINCE,level)){
            return 1;
        }
        if (StrUtil.equals(CITY,level)){
            return 2;
        }
        if (StrUtil.equals(DISTRICT,level)){
            return 3;
        }
        if (StrUtil.equals(STREET,level)){
            return 4;
        }
        return null;
    }



    @Override
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
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (Integer dto : finalNewList) {
                        System.out.println("dto:"+dto);
                        countDownLatch.countDown();//发出线程任务完成的信号
                    }
                    lastList.addAll(finalNewList);
                    System.out.println("vehicleExportSize"+lastList.size());
                }
            });
            executor.execute(thread);

            try {
                countDownLatch.await();  //调用await()方法的线程会被挂起，它会等待直到count值为0才继续执行
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        //所有线程完成任务后的一些业务
        System.out.println("All thread task is go,the vehicle total is "+lastList.size());

        //关闭线程池
        executor.shutdown();

    }


}
