package com.los.loanoriginatingsystem.banking.accountaggregator.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AACallbackEvent {

    private final String documentId;

}