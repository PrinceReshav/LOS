package com.los.loanoriginatingsystem.insurance.controller;

import com.los.loanoriginatingsystem.insurance.dto.InsuranceCalculationResponse;
import com.los.loanoriginatingsystem.insurance.service.InsuranceMatrixService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/insurance")
@RequiredArgsConstructor
public class InsuranceCalculatorController {

    private final InsuranceMatrixService service;

    @GetMapping("/calculate")
    public InsuranceCalculationResponse calculate(
            @RequestParam int age,
            @RequestParam int tenureMonths,
            @RequestParam BigDecimal sumAssured,
            @RequestParam(required = false) BigDecimal propertyValue,
            @RequestParam(defaultValue = "false") boolean preferentialRate
    ) {
        return service.calculate(age, tenureMonths, sumAssured, propertyValue, preferentialRate);
    }
}
