package com.coldwindx.loki.service;

import com.coldwindx.loki.AbstractTest;
import com.coldwindx.loki.entity.TimeBo;
import com.coldwindx.loki.mapper.TimeMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;


public class TimeServiceTest extends AbstractTest {

    @Autowired
    private TimeMapper timeMapper;

    @Test
    public void testInsertAsDate() {

        Date date = new Date();


        System.out.println(date.getTime());
        System.out.println(date);
        TimeBo timeBo = new TimeBo();
        timeBo.setFDate(date);
        timeBo.setFDatetime(date);
//        timeBo.setFTime(date);
        timeBo.setFTimestamp(date);
        timeMapper.insert(timeBo);
    }

    @Test
    public void testUpdateAsDate() {
        List<TimeBo> timeBos = timeMapper.select(19L);
        System.out.println(timeBos);
    }

    @Test
    public void testUpdateAsDate2() {
        List<TimeBo> timeBos = timeMapper.select(18L);
        TimeBo timeBo = timeBos.get(0);
        System.out.println(timeBo);
        timeMapper.insert(timeBo);
    }
}
