package com.los.loanoriginatingsystem.event.registry;

import com.los.loanoriginatingsystem.event.dispatcher.EventDispatcher;
import com.los.loanoriginatingsystem.event.listener.TypedEventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventRegistry {

    public EventRegistry(EventDispatcher dispatcher,
                         List<TypedEventListener<?>> listeners) {

        for (TypedEventListener<?> listener : listeners) {
            dispatcher.register(listener.getEventType(), listener);
        }
    }
}