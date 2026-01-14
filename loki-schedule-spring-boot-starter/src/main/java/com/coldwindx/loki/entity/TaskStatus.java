package com.coldwindx.loki.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum TaskStatus{
    INIT(10, "初始化"),
    EXECUTING(20, "执行中"),
    SUCCESS(30, "成功"),
    FAIL(40, "失败")
    ;
    final int code;
    final String description;
}