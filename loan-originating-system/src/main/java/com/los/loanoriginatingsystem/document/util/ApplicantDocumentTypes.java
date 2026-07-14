package com.los.loanoriginatingsystem.document.util;

import java.util.Set;

public class ApplicantDocumentTypes {

    public static final Set<String> TYPES = Set.of(
            "Aadhaar Front",
            "PAN Card",
            "Driving License",
            "Voter Id",
            "Customer Photo"
    );

    private ApplicantDocumentTypes() {
    }
}