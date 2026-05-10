package com.los.loanoriginatingsystem.audit.util;

public class MaskingUtil {

    public static String mask(String input) {

        if (input == null || input.length() < 4) return input;

        int visible = 2;
        String prefix = input.substring(0, visible);
        String suffix = input.substring(input.length() - visible);

        return prefix + "****" + suffix;
    }
}