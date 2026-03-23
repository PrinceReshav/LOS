package com.los.loanoriginatingsystem.banking.accountaggregator.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CamAnalysisDTO {

    private BigDecimal odCcLimit;

    private Integer inwardReturnCount;

    private Integer outwardReturnCount;

    private BigDecimal inwardReturnAmount;

    private BigDecimal outwardReturnAmount;

    private BigDecimal totalNetCredits;

    private Double averageBalance;

    private Double customAverageBalance;

    private Double customAverageBalanceLastThreeMonth;

    private Double averageBalanceLastThreeMonth;

    private Double averageBalanceLastSixMonth;

    private Double averageBalanceLastTwelveMonth;

    private Double averageReceiptLastSixMonth;

    private Double averageReceiptLastTwelveMonth;

    private Integer salaryCreditCountLastThreeMonth;

    private Integer salaryCreditCountLastSixMonth;

    private Double minBalanceLastThreeMonth;

    private Double minBalanceLastSixMonth;

    private Integer minBalanceChargeCountLastSixMonth;

    private List<CamAnalysisMonthlyDTO> camAnalysisMonthly;
}