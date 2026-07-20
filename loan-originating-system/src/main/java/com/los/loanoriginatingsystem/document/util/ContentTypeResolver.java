package com.los.loanoriginatingsystem.document.util;

public class ContentTypeResolver {

    private ContentTypeResolver() {
    }

    public static String resolve(String fileName) {

        if (fileName == null) {
            return "application/octet-stream";
        }

        String lower =
                fileName.toLowerCase();

        if (lower.endsWith(".pdf")) {
            return "application/pdf";
        }

        if (lower.endsWith(".png")) {
            return "image/png";
        }

        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return "image/jpeg";
        }

        if (lower.endsWith(".gif")) {
            return "image/gif";
        }

        if (lower.endsWith(".webp")) {
            return "image/webp";
        }

        return "application/octet-stream";
    }
}
