package com.los.loanoriginatingsystem.event;

import com.los.loanoriginatingsystem.event.core.BaseEvent;

public class LoanUpdatedEvent extends BaseEvent {

    private final String applicationId;

    public LoanUpdatedEvent(String applicationId) {
        super(EventTypes.LOAN_UPDATED);
        this.applicationId = applicationId;
    }

    public String getApplicationId() {
        return applicationId;
    }
}