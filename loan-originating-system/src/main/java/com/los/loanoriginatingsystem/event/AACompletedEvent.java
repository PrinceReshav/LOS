package com.los.loanoriginatingsystem.event;

import com.los.loanoriginatingsystem.event.core.BaseEvent;

public class AACompletedEvent extends BaseEvent {

    private final String applicationId;

    public AACompletedEvent(String applicationId) {
        super(EventTypes.AA_COMPLETED);
        this.applicationId = applicationId;
    }

    public String getApplicationId() {
        return applicationId;
    }
}