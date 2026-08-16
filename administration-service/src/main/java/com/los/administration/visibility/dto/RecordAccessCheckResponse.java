package com.los.administration.visibility.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordAccessCheckResponse {
    private boolean allowed;
    private String reason;
}
