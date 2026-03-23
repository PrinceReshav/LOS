package com.los.loanoriginatingsystem.kyc.service.processor;

public interface KYCProcessor {

    String getType();

    Object process(byte[] file);
}