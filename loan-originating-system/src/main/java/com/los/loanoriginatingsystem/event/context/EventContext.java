package com.los.loanoriginatingsystem.event.context;

import java.util.Map;

public class EventContext {

    private final Map<String, Object> data;

    public EventContext(Map<String, Object> data) {
        this.data = data;
    }

    public <T> T get(String key, Class<T> clazz) {
        return clazz.cast(data.get(key));
    }
}