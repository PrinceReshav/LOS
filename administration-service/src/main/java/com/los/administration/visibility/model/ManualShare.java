package com.los.administration.visibility.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "manual_share")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManualShare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_user_id", nullable = false)
    private String ownerUserId;

    @Column(name = "shared_with_user_id", nullable = false)
    private String sharedWithUserId;

    @Enumerated(EnumType.STRING)
    private AccessType accessType; // MANUAL
}