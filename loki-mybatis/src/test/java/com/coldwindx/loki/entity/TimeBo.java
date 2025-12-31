package com.coldwindx.loki.entity;

import lombok.Data;

import java.util.Date;

@Data
public class TimeBo {
    private Long id;
    private Date fDate;
    private Date fDatetime;
    private Date fTime;
    private Date fTimestamp;
}
