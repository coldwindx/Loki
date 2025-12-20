package com.coldwindx.entity;

import lombok.Getter;

public class Message<T> {
    @Getter private final T body;
    @Getter String tags;
    private final Context context;

    public Message(T body) {
        this.body = body;
        this.context = Context.builder().build();
    }

    public Message(T body, String tags) {
        this.body = body;
        this.tags = tags;
        this.context = Context.builder().build();
    }
}
