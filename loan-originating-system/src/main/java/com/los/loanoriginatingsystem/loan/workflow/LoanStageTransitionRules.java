package com.los.loanoriginatingsystem.loan.workflow;

import com.los.loanoriginatingsystem.loan.enums.LoanStage;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import static com.los.loanoriginatingsystem.loan.enums.LoanStage.*;

/**
 * The explicit stage-transition graph: from each stage, which stages can be
 * moved to directly. REJECTED is reachable from every non-terminal stage
 * (added programmatically below) rather than listed on every line, so
 * adding a new stage in the middle of the pipeline can't accidentally
 * forget to wire up its reject path.
 *
 * Kept as a static graph (not baked into the enum itself) so the pipeline
 * shape can be reasoned about/tested independently of guard conditions -
 * see LoanStageGuardService for "is this application actually allowed to
 * make this jump right now".
 */
public final class LoanStageTransitionRules {

    private static final Map<LoanStage, Set<LoanStage>> GRAPH = new EnumMap<>(LoanStage.class);

    static {
        GRAPH.put(DATA_ENTRY, EnumSet.of(UNDERWRITING));
        GRAPH.put(UNDERWRITING, EnumSet.of(PRE_SANCTION));
        GRAPH.put(PRE_SANCTION, EnumSet.of(SANCTION));
        GRAPH.put(SANCTION, EnumSet.of(PRE_DISBURSAL_REVIEW));
        GRAPH.put(PRE_DISBURSAL_REVIEW, EnumSet.of(INITIATE_DISBURSEMENT));
        GRAPH.put(INITIATE_DISBURSEMENT, EnumSet.of(DISBURSED));
        GRAPH.put(DISBURSED, EnumSet.noneOf(LoanStage.class));   // terminal
        GRAPH.put(REJECTED, EnumSet.noneOf(LoanStage.class));    // terminal

        // REJECTED is reachable from every non-terminal stage.
        for (LoanStage stage : LoanStage.values()) {
            if (!stage.isTerminal()) {
                GRAPH.get(stage).add(REJECTED);
            }
        }
    }

    private LoanStageTransitionRules() {}

    public static Set<LoanStage> allowedNextStages(LoanStage current) {
        return GRAPH.getOrDefault(current, EnumSet.noneOf(LoanStage.class));
    }

    public static boolean isAllowed(LoanStage from, LoanStage to) {
        return allowedNextStages(from).contains(to);
    }
}
