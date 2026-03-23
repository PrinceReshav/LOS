package com.los.loanoriginatingsystem.banking.accountaggregator.dto;

import lombok.Data;

@Data
public class FileDetailsDTO {

    private String accountNumber;

    private String fileName;

    private Integer pageCount;

    private boolean ocr;
}