package com.los.losadminservice.employee.validator;

import java.util.List;
import java.util.Map;

public final class HierarchyRoleRules {

    private HierarchyRoleRules(){}

    public static final Map<String, List<String>> ALLOWED_MANAGERS =

            // Map.of ( supports only up to 10 key-value pairs. Use Map.ofEntries() instead. )

            Map.ofEntries(

                    Map.entry(
                            "RELATIONSHIP_OFFICER",
                            List.of("RELATIONSHIP_MANAGER")
                    ),

                    Map.entry(
                            "RELATIONSHIP_MANAGER",
                            List.of(
                                    "TERRITORY_MANAGER",
                                    "CLUSTER_BUSINESS_MANAGER",
                                    "DIVISIONAL_BRANCH_MANAGER",
                                    "ZONAL_BRANCH_MANAGER",
                                    "BUSINESS_HEAD"
                            )
                    ),

                    Map.entry(
                            "TERRITORY_MANAGER",
                            List.of(
                                    "CLUSTER_BUSINESS_MANAGER",
                                    "DIVISIONAL_BRANCH_MANAGER",
                                    "ZONAL_BRANCH_MANAGER",
                                    "BUSINESS_HEAD"
                            )
                    ),

                    Map.entry(
                            "CLUSTER_BUSINESS_MANAGER",
                            List.of(
                                    "DIVISIONAL_BRANCH_MANAGER",
                                    "ZONAL_BRANCH_MANAGER",
                                    "BUSINESS_HEAD"
                            )
                    ),

                    Map.entry(
                            "DIVISIONAL_BRANCH_MANAGER",
                            List.of(
                                    "ZONAL_BRANCH_MANAGER",
                                    "BUSINESS_HEAD"
                            )
                    ),

                    Map.entry(
                            "ZONAL_BRANCH_MANAGER",
                            List.of("BUSINESS_HEAD")
                    ),

                    Map.entry(
                            "BRANCH_CREDIT_MANAGER",
                            List.of(
                                    "CLUSTER_CREDIT_MANAGER",
                                    "DIVISIONAL_CREDIT_MANAGER",
                                    "ZONAL_CREDIT_MANAGER",
                                    "BUSINESS_HEAD"
                            )
                    ),

                    Map.entry(
                            "CLUSTER_CREDIT_MANAGER",
                            List.of(
                                    "DIVISIONAL_CREDIT_MANAGER",
                                    "ZONAL_CREDIT_MANAGER",
                                    "BUSINESS_HEAD"
                            )
                    ),

                    Map.entry(
                            "DIVISIONAL_CREDIT_MANAGER",
                            List.of(
                                    "ZONAL_CREDIT_MANAGER",
                                    "BUSINESS_HEAD"
                            )
                    ),

                    Map.entry(
                            "ZONAL_CREDIT_MANAGER",
                            List.of("BUSINESS_HEAD")
                    ),

                    Map.entry(
                            "BUSINESS_HEAD",
                            List.of(
                                    "CEO",
                                    "MD",
                                    "DY_CEO"
                            )
                    ),

                    Map.entry(
                            "CEO",
                            List.of("MD")
                    ),

                    Map.entry(
                            "DY_CEO",
                            List.of("MD")
                    )
            );
}