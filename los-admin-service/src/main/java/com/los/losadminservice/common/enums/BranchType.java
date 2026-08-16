package com.los.losadminservice.common.enums;

/**
 * Classifies a Branch for hierarchy purposes.
 *
 * NORMAL       - a regular branch office.
 * HEAD_OFFICE  - the corporate Head Office. Employees mapped here for
 *                certain departments (e.g. Credit's pre-sanctioning team)
 *                follow a different, shorter reporting chain than the
 *                same role would follow at a normal branch.
 */
public enum BranchType {
    NORMAL,
    HEAD_OFFICE
}
