package com.los.loanoriginatingsystem.documentgeneration.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

/** Thin wrapper around OpenHTMLtoPDF - renders a self-contained HTML string to PDF bytes. */
@Component
public class PdfRenderer {

    public byte[] render(String html) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(outputStream);
            builder.run();

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to render PDF from template output", e);
        }
    }
}
