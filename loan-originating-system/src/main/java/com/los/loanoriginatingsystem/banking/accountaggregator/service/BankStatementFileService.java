package com.los.loanoriginatingsystem.banking.accountaggregator.service;

import com.los.loanoriginatingsystem.document.entity.Document;
import com.los.loanoriginatingsystem.document.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class BankStatementFileService {

    private final RestTemplate restTemplate;
    private final DocumentRepository documentRepository;

    private final String storagePath = "bank-statements/";

    public void downloadBankStatementExcel(
            String documentId,
            String bankStatementDocId) throws Exception {

        String url =
                "Download_Bank_Statement_Excel_AA/" + bankStatementDocId;

        byte[] fileBytes =
                restTemplate.getForObject(url, byte[].class);

        if (fileBytes == null) {
            return;
        }

        uploadBankStatementExcel(
                "Account Aggregator Bank Statement Excel.xlsx",
                fileBytes,
                documentId
        );
    }

    public void uploadBankStatementExcel(
            String fileName,
            byte[] fileData,
            String documentId) throws Exception {

        if (!fileName.toLowerCase().endsWith(".xlsx")) {
            fileName += ".xlsx";
        }

        Path directory = Paths.get(storagePath);

        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        Path filePath =
                directory.resolve(documentId + "_" + fileName);

        Files.write(filePath, fileData);

        Document document =
                documentRepository.findById(documentId)
                        .orElseThrow();

        document.setFileName(fileName);

        document.setPreviewUrl(
                filePath.toAbsolutePath().toString()
        );

        documentRepository.save(document);
    }
}