package com.coldwindx.loki.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_task_schedule")
public class ScheduleTask {
    private Long id;
    private String topic;
    private String fingerprint;
    private String clazz;
    private String args;
    private String result;
    private Integer status;
    private Integer retryCount;
    private Date awakeTime;
    private Date createTime;
    private Date updateTime;
    private Boolean deleted;
}
