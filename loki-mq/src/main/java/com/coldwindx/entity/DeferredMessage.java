package com.coldwindx.entity;

import java.util.concurrent.TimeUnit;

public class DeferredMessage<T> extends Message<T> {
    private final long time;
    private final TimeUnit unit;

    public DeferredMessage(T body, long time, TimeUnit unit) {
        super(body);
        this.time = time;
        this.unit = unit;
    }

    public DeferredMessage(T body, String tags, long time, TimeUnit unit) {
        super(body, tags);
        this.time = time;
        this.unit = unit;
    }
}
