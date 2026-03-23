package com.los.loanoriginatingsystem.banking.camanalysis.service.impl;

import com.los.loanoriginatingsystem.banking.accountaggregator.dto.BankStatementResponseDTO;
import com.los.loanoriginatingsystem.banking.accountaggregator.dto.BankDataDTO;
import com.los.loanoriginatingsystem.banking.accountaggregator.dto.CamAnalysisMonthlyDTO;
import com.los.loanoriginatingsystem.banking.camanalysis.dto.BankAccountCAMData;
import com.los.loanoriginatingsystem.banking.camanalysis.dto.MonthDataDTO;
import com.los.loanoriginatingsystem.banking.camanalysis.dto.MonthlyAnalysisDTO;
import com.los.loanoriginatingsystem.banking.camanalysis.service.CamAnalysisService;
import com.los.loanoriginatingsystem.banking.ifsc.dto.IfscDetailsDTO;
import com.los.loanoriginatingsystem.banking.ifsc.service.IfscLookupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CamAnalysisServiceImpl implements CamAnalysisService {

    private final IfscLookupService ifscLookupService;

    @Override
    public List<BankAccountCAMData> processBankStatement(BankStatementResponseDTO response) {

        List<BankAccountCAMData> result = new ArrayList<>();

        if (response.getData() == null) {
            return result;
        }

        for (BankDataDTO bankData : response.getData()) {

            BankAccountCAMData dto = new BankAccountCAMData();

            dto.setIfscCode(bankData.getIfscCode());
            dto.setBankName(bankData.getBankName());
            dto.setAccountNumber(bankData.getAccountNumber());

            IfscDetailsDTO ifsc = ifscLookupService.getIFSCDetails(bankData.getIfscCode());

            dto.setBranchAddress(ifsc.getBranchNameAndAddress());

            if (bankData.getCamAnalysisData() != null &&
                    bankData.getCamAnalysisData().getCustomAverageBalance() != null) {

                dto.setAverage(BigDecimal.valueOf(
                        bankData.getCamAnalysisData().getCustomAverageBalance()
                ));
            }

            dto.setRowData(buildMonthlyAnalysis(bankData));

            result.add(dto);
        }

        return result;
    }

    /**
     * Core CAM analysis logic
     */
    private List<MonthDataDTO> buildMonthlyAnalysis(BankDataDTO bankData) {

        List<MonthDataDTO> monthDataList = new ArrayList<>();

        int rowNumber = 1;

        Set<String> lastSixMonths = new LinkedHashSet<>(generateLastSixMonths());

        if (bankData.getCamAnalysisData() != null &&
                bankData.getCamAnalysisData().getCamAnalysisMonthly() != null) {

            for (CamAnalysisMonthlyDTO monthObj :
                    bankData.getCamAnalysisData().getCamAnalysisMonthly()) {

                if ("Grand Total".equals(monthObj.getMonth())) {
                    continue;
                }

                if (!lastSixMonths.contains(monthObj.getMonth())) {
                    continue;
                }

                MonthDataDTO monthData = new MonthDataDTO();

                lastSixMonths.remove(monthObj.getMonth());

                monthData.setMonth(monthObj.getMonth());
                monthData.setAverage(
                        BigDecimal.valueOf(
                                monthObj.getAverageUtilisedCustomDayBalances()
                        )
                );
                monthData.setRow(rowNumber++);

                List<MonthlyAnalysisDTO> columns = new ArrayList<>();

                int col = 1;

                if (monthObj.getCustomDayBalances() != null) {

                    for (Map.Entry<String, String> entry :
                            monthObj.getCustomDayBalances().entrySet()) {

                        MonthlyAnalysisDTO analysis = new MonthlyAnalysisDTO();

                        analysis.setMonthDate(entry.getKey());
                        analysis.setValue(entry.getValue());
                        analysis.setColumn(col++);

                        columns.add(analysis);
                    }
                }

                monthData.setColumns(columns);

                monthDataList.add(monthData);
            }
        }

        // Handle missing months (padding logic)
        for (String missingMonth : lastSixMonths) {

            MonthDataDTO monthData = new MonthDataDTO();

            monthData.setMonth(missingMonth);
            monthData.setAverage(BigDecimal.ZERO);
            monthData.setRow(rowNumber++);

            List<MonthlyAnalysisDTO> columns = new ArrayList<>();

            List<String> days = List.of("2", "9", "16", "23", "30");

            int col = 1;

            for (String day : days) {

                MonthlyAnalysisDTO analysis = new MonthlyAnalysisDTO();

                analysis.setMonthDate(day);
                analysis.setValue("0.00");
                analysis.setColumn(col++);

                columns.add(analysis);
            }

            monthData.setColumns(columns);

            monthDataList.add(monthData);
        }

        return monthDataList;
    }

    /**
     * Generate last 6 months in MMM-yyyy format
     */
    private List<String> generateLastSixMonths() {

        List<String> months = new ArrayList<>();

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("MMM-yyyy", Locale.ENGLISH);

        LocalDate now = LocalDate.now();

        for (int i = 6; i >= 1; i--) {

            months.add(now.minusMonths(i).format(formatter));
        }

        return months;
    }
}