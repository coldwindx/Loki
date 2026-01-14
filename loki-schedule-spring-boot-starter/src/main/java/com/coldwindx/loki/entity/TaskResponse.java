package com.coldwindx.loki.entity;

import lombok.Data;

@Data
public class TaskResponse<T> {
    private Long id;
    private String topic;
    private String fingerprint;
    private T args;
    private Integer status;
}
