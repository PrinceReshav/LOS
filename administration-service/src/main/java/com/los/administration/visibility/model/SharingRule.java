package com.los.administration.visibility.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sharing_rule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_role_id", nullable = false)
    private String fromRoleId;

    @Column(name = "to_role_id", nullable = false)
    private String toRoleId;

    @Enumerated(EnumType.STRING)
    private AccessType accessType; // RULE

    private Boolean active;
}