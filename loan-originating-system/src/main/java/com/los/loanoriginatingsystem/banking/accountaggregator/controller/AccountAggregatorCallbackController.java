package com.los.loanoriginatingsystem.banking.accountaggregator.controller;

import com.los.loanoriginatingsystem.banking.accountaggregator.dto.AACallbackDTO;
import com.los.loanoriginatingsystem.banking.accountaggregator.service.AccountAggregatorCallbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/cart_callback")
@RequiredArgsConstructor
@Slf4j
public class AccountAggregatorCallbackController {

    private final AccountAggregatorCallbackService callbackService;

    @PostMapping
    public void cartApiCallback(@RequestBody AACallbackDTO wrapper) {

        log.info("AA callback received for fileNo {}", wrapper.getFileNo());

        callbackService.handleCallback(wrapper);
    }
}