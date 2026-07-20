package com.los.loanoriginatingsystem.report.service;

import com.los.loanoriginatingsystem.report.entity.*;
import com.los.loanoriginatingsystem.report.enums.*;
import com.los.loanoriginatingsystem.report.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

// Seeds the "Standard Reports" / "Standard Dashboards" folders with
// a set of pre-built reports every user sees out of the box —
// equivalent to Salesforce's standard report types. Runs on every
// startup (the schema is recreated each time per
// spring.jpa.hibernate.ddl-auto=create), guarded by an emptiness
// check so it's still safe if that ever changes.
@Component
@RequiredArgsConstructor
@Order(100)
public class StandardReportSeeder implements CommandLineRunner {

    private final ReportFolderRepository folderRepository;
    private final ReportDefinitionRepository reportRepository;
    private final ReportFilterCriteriaRepository filterRepository;
    private final DashboardRepository dashboardRepository;
    private final DashboardComponentRepository componentRepository;

    @Override
    public void run(String... args) {

        if (reportRepository.count() > 0) {
            return;
        }

        String reportFolderId = seedFolder(
                "Standard Reports",
                "Pre-built reports available to everyone",
                true
        );

        String dashboardFolderId = seedFolder(
                "Standard Dashboards",
                "Pre-built dashboards available to everyone",
                true
        );

        String leadsByStatus = seedSummaryReport(
                reportFolderId,
                "Leads by Status",
                "Count of leads grouped by their current status",
                ReportSourceObject.LEAD,
                "status",
                null,
                AggregateFunction.COUNT,
                null,
                ChartType.PIE
        );

        String leadsBySource = seedSummaryReport(
                reportFolderId,
                "Leads by Source",
                "Count of leads grouped by lead source",
                ReportSourceObject.LEAD,
                "leadSource",
                null,
                AggregateFunction.COUNT,
                null,
                ChartType.BAR
        );

        String applicationsByStage = seedSummaryReport(
                reportFolderId,
                "Loan Applications by Stage",
                "Count of loan applications at each pipeline stage",
                ReportSourceObject.LOAN_APPLICATION,
                "stage",
                null,
                AggregateFunction.COUNT,
                null,
                ChartType.FUNNEL
        );

        String applicationsByProduct = seedSummaryReport(
                reportFolderId,
                "Loan Applications by Product",
                "Count of loan applications grouped by loan product",
                ReportSourceObject.LOAN_APPLICATION,
                "loanProductCode",
                null,
                AggregateFunction.COUNT,
                null,
                ChartType.BAR
        );

        String requestedAmountByStage = seedSummaryReport(
                reportFolderId,
                "Requested Amount by Stage",
                "Total requested loan amount grouped by stage",
                ReportSourceObject.LOAN_APPLICATION,
                "stage",
                "requestedAmount",
                AggregateFunction.SUM,
                null,
                ChartType.BAR
        );

        String documentsByStatus = seedSummaryReport(
                reportFolderId,
                "Document Verification Status",
                "Count of uploaded documents grouped by verification status",
                ReportSourceObject.DOCUMENT,
                "status",
                null,
                AggregateFunction.COUNT,
                null,
                ChartType.DONUT
        );

        seedSummaryReport(
                reportFolderId,
                "Applicants by Gender",
                "Count of loan applicants grouped by gender",
                ReportSourceObject.LOAN_APPLICANT,
                "gender",
                null,
                AggregateFunction.COUNT,
                null,
                ChartType.PIE
        );

        seedTabularReport(
                reportFolderId,
                "All Loan Applications",
                "Flat list of every loan application",
                ReportSourceObject.LOAN_APPLICATION,
                List.of(
                        "applicationNumber",
                        "applicantName",
                        "stage",
                        "loanProductCode",
                        "requestedAmount",
                        "mobileNumber"
                )
        );

        seedTabularReport(
                reportFolderId,
                "All Leads",
                "Flat list of every lead",
                ReportSourceObject.LEAD,
                List.of(
                        "leadNumber",
                        "firstName",
                        "lastName",
                        "mobileNumber",
                        "status",
                        "leadSource",
                        "interestLevel"
                )
        );

        seedMatrixReport(
                reportFolderId,
                "Applications by Stage and Product",
                "Count of loan applications, broken down by stage and product",
                ReportSourceObject.LOAN_APPLICATION,
                "stage",
                "loanProductCode",
                AggregateFunction.COUNT
        );

        seedDashboard(
                dashboardFolderId,
                "Loan Origination Overview",
                "A snapshot of leads and loan applications across the pipeline",
                List.of("stage"),
                leadsByStatus,
                applicationsByStage,
                applicationsByProduct,
                requestedAmountByStage,
                documentsByStatus,
                leadsBySource
        );
    }

    private String seedFolder(
            String name,
            String description,
            boolean isSystem
    ) {

        ReportFolder folder = new ReportFolder();

        folder.setId(UUID.randomUUID().toString());
        folder.setName(name);
        folder.setDescription(description);
        folder.setIsSystemFolder(isSystem);
        folder.setCreatedBy("system");
        folder.setCreatedAt(LocalDateTime.now());

        folderRepository.save(folder);

        return folder.getId();
    }

    private String seedSummaryReport(
            String folderId,
            String name,
            String description,
            ReportSourceObject source,
            String groupByField,
            String aggregateField,
            AggregateFunction aggregateFunction,
            String sortField,
            ChartType chartType
    ) {

        ReportDefinition report = new ReportDefinition();

        report.setId(UUID.randomUUID().toString());
        report.setName(name);
        report.setDescription(description);
        report.setFolderId(folderId);
        report.setSourceObject(source);
        report.setReportType(ReportType.SUMMARY);
        report.setGroupByField1(groupByField);
        report.setAggregateField(aggregateField);
        report.setAggregateFunction(aggregateFunction);
        report.setSortField(sortField);
        report.setChartType(chartType);
        report.setIsStandard(true);
        report.setCreatedBy("system");
        report.setCreatedAt(LocalDateTime.now());

        reportRepository.save(report);

        return report.getId();
    }

    private String seedTabularReport(
            String folderId,
            String name,
            String description,
            ReportSourceObject source,
            List<String> fields
    ) {

        ReportDefinition report = new ReportDefinition();

        report.setId(UUID.randomUUID().toString());
        report.setName(name);
        report.setDescription(description);
        report.setFolderId(folderId);
        report.setSourceObject(source);
        report.setReportType(ReportType.TABULAR);
        report.setSelectedFields(fields);
        report.setChartType(ChartType.NONE);
        report.setIsStandard(true);
        report.setCreatedBy("system");
        report.setCreatedAt(LocalDateTime.now());

        reportRepository.save(report);

        return report.getId();
    }

    private String seedMatrixReport(
            String folderId,
            String name,
            String description,
            ReportSourceObject source,
            String groupByField1,
            String groupByField2,
            AggregateFunction aggregateFunction
    ) {

        ReportDefinition report = new ReportDefinition();

        report.setId(UUID.randomUUID().toString());
        report.setName(name);
        report.setDescription(description);
        report.setFolderId(folderId);
        report.setSourceObject(source);
        report.setReportType(ReportType.MATRIX);
        report.setGroupByField1(groupByField1);
        report.setGroupByField2(groupByField2);
        report.setAggregateFunction(aggregateFunction);
        report.setChartType(ChartType.NONE);
        report.setIsStandard(true);
        report.setCreatedBy("system");
        report.setCreatedAt(LocalDateTime.now());

        reportRepository.save(report);

        return report.getId();
    }

    private void seedDashboard(
            String folderId,
            String name,
            String description,
            List<String> filterFields,
            String... reportIds
    ) {

        Dashboard dashboard = new Dashboard();

        dashboard.setId(UUID.randomUUID().toString());
        dashboard.setName(name);
        dashboard.setDescription(description);
        dashboard.setFolderId(folderId);
        dashboard.setDashboardFilterFields(filterFields);
        dashboard.setIsStandard(true);
        dashboard.setCreatedBy("system");
        dashboard.setCreatedAt(LocalDateTime.now());

        dashboardRepository.save(dashboard);

        int row = 0;
        int col = 0;

        for (String reportId : reportIds) {

            ReportDefinition report =
                    reportRepository.findById(reportId)
                            .orElseThrow();

            DashboardComponent component = new DashboardComponent();

            component.setId(UUID.randomUUID().toString());
            component.setDashboardId(dashboard.getId());
            component.setReportId(reportId);
            component.setTitle(report.getName());
            component.setComponentType(
                    report.getChartType() == ChartType.NONE
                            ? DashboardComponentType.TABLE
                            : DashboardComponentType.CHART
            );
            component.setChartType(report.getChartType());
            component.setPositionRow(row);
            component.setPositionCol(col);
            component.setWidth(2);
            component.setHeight(1);

            componentRepository.save(component);

            col += 2;

            if (col >= 6) {
                col = 0;
                row++;
            }
        }
    }
}
