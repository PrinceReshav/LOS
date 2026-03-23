package com.los.loanoriginatingsystem.event.core;

public abstract class BaseEvent implements Event {

    private final String type;

    protected BaseEvent(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }
}