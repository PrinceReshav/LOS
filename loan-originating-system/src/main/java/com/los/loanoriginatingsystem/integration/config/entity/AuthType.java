package com.los.loanoriginatingsystem.integration.config.entity;

/**
 * How the credential for an integration client should be attached to the
 * outbound HTTP request. Mirrors the "auth strategy" the old Salesforce
 * HTTPCalloutConfiguration__mdt implied but never modeled explicitly.
 */
public enum AuthType {
    NONE,
    API_KEY_HEADER,   // apiKey sent as a plain header, header name = authHeaderName
    BEARER_TOKEN,     // apiKey sent as "Authorization: Bearer <apiKey>"
    BASIC_AUTH        // apiKey = username, apiSecret = password -> Basic base64(user:pass)
}
