package com.los.loanoriginatingsystem.event;



import com.los.loanoriginatingsystem.event.core.BaseEvent;

public class CamCompletedEvent extends BaseEvent {

    private final String applicationId;

    public CamCompletedEvent(String applicationId) {
        super(EventTypes.CAM_COMPLETED);
        this.applicationId = applicationId;
    }

    public String getApplicationId() {
        return applicationId;
    }
}
