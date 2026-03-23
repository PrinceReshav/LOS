package com.los.loanoriginatingsystem.event.listener;

import com.los.loanoriginatingsystem.event.core.Event;

public interface TypedEventListener<E extends Event> extends EventListener<E> {
    String getEventType();
}