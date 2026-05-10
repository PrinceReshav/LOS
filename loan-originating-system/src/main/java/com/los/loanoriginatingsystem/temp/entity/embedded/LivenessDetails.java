package com.los.loanoriginatingsystem.temp.entity.embedded;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.Data;

@Embeddable
@Data
public class LivenessDetails {

    @Lob
    private String photo;

    private Boolean verified;
}