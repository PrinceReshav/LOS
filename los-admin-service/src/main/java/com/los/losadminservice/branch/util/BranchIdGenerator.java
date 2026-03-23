package com.los.losadminservice.branch.util;

import java.util.Random;

public final class BranchIdGenerator {

    private static final String PREFIX = "bran00";
    private static final String LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final Random RANDOM = new Random();

    private BranchIdGenerator(){}

    public static String generate(String companyBranchId){

        StringBuilder random = new StringBuilder();

        for(int i = 0; i < 3; i++){
            random.append(LETTERS.charAt(RANDOM.nextInt(LETTERS.length())));
        }

        return PREFIX + companyBranchId + random;
    }
}