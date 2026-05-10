package com.los.administration.visibility.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "visibility_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisibilityConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_name", nullable = false, unique = true)
    private String entityName; // USER, LOAN, etc

    @Enumerated(EnumType.STRING)
    private VisibilityType visibilityType;
}