package com.coldwindx.loki.mapper;

import com.coldwindx.loki.entity.TimeBo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TimeMapper {
    int insert(TimeBo timeBo);
    List<TimeBo> select(Long id);
}
