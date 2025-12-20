package com.coldwindx.entity;

import lombok.Builder;
import lombok.Getter;
import org.slf4j.MDC;

@Getter
@Builder
public class Context {
    @Builder.Default
    private String logId = MDC.get("LOG_ID");
}
