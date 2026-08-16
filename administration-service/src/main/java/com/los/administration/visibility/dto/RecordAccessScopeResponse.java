package com.los.administration.visibility.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * The visibility "scope" for a user against a record type - used by the
 * calling service to filter LIST queries (e.g.
 * "WHERE created_by IN (:visibleOwnerUserIds) OR branch_id IN (:visibleBranchIds)")
 * rather than checking access one row at a time.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordAccessScopeResponse {
    private boolean seesAll;
    private List<String> visibleOwnerUserIds;
    private List<String> visibleBranchIds;
    /** Individual records explicitly shared in (via RecordShare) that fall outside the owner/branch scope above. */
    private List<String> visibleRecordIds;
}
