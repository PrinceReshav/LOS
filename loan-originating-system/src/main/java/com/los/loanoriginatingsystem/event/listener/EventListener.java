package com.los.loanoriginatingsystem.event.listener;

import com.los.loanoriginatingsystem.event.core.Event;
import com.los.loanoriginatingsystem.event.context.EventContext;

public interface EventListener<E extends Event> {

    void handle(E event, EventContext context);
}