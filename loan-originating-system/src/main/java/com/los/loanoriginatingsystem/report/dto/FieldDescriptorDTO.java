package com.los.loanoriginatingsystem.report.dto;

import lombok.Data;

import java.util.List;

@Data
public class FieldDescriptorDTO {

    private String name;

    // One of: STRING, NUMBER, BOOLEAN, DATE, PICKLIST
    private String type;

    // Populated only when type == "PICKLIST" — the enum's possible
    // values, so the frontend can render a dropdown instead of a
    // free-text box.
    private List<String> enumValues;
}
