package com.los.loanoriginatingsystem.event.dispatcher;

import com.los.loanoriginatingsystem.event.core.Event;
import com.los.loanoriginatingsystem.event.context.EventContext;
import com.los.loanoriginatingsystem.event.listener.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class EventDispatcher {

    private final Map<String, List<EventListener<?>>> listeners = new HashMap<>();

    public void register(String eventType, EventListener<?> listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }


    public void dispatch(Event event, EventContext context) {

        List<EventListener<?>> eventListeners = listeners.get(event.getType());

        if (eventListeners == null) return;

        for (EventListener listener : eventListeners) {
            listener.handle(event, context);
        }
    }

    @Async
    public void dispatchAsync(Event event, EventContext context) {
        dispatch(event, context);
    }
}