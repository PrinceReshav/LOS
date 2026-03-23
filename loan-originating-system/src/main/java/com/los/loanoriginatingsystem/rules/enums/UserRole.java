package com.los.loanoriginatingsystem.rules.enums;

public enum UserRole {

    // FIELD LEVEL (no approval)
    RELATIONSHIP_OFFICER("RO", 0),
    RELATIONSHIP_MANAGER("RM", 0),
    TERRITORY_MANAGER("TM", 0),

    // L1
    CLUSTER_BUSINESS_MANAGER("CBM", 1),
    CLUSTER_CREDIT_MANAGER("CCM", 1),

    // L2
    DIVISIONAL_BRANCH_MANAGER("DBM", 2),
    DIVISIONAL_CREDIT_MANAGER("DCM", 2),

    // L3
    ZONAL_BUSINESS_MANAGER("ZBM", 3),
    ZONAL_CREDIT_MANAGER("ZCM", 3),

    // L4
    BUSINESS_HEAD("BH", 4),

    // L5
    DEPUTY_CEO("DY_CEO", 5),
    CEO("CEO", 5),
    MANAGING_DIRECTOR("MD", 5);

    private final String code;
    private final int level;

    UserRole(String code, int level) {
        this.code = code;
        this.level = level;
    }

    public String getCode() {
        return code;
    }

    public int getLevel() {
        return level;
    }

    public static UserRole fromCode(String code) {
        for (UserRole role : values()) {
            if (role.code.equalsIgnoreCase(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid role: " + code);
    }
}