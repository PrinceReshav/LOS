package com.los.administration.visibility.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "user_visibility",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"viewer_user_id", "target_user_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVisibility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "viewer_user_id", nullable = false)
    private String viewerUserId;

    @Column(name = "target_user_id", nullable = false)
    private String targetUserId;

    @Enumerated(EnumType.STRING)
    private AccessType accessType;
}