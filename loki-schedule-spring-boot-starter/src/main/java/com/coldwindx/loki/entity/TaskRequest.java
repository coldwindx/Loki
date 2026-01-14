package com.coldwindx.loki.entity;

import lombok.Data;

@Data
public class TaskRequest<T> {
    private String topic;
    private String fingerprint;
    private String args;
}
