package com.los.administration.profile.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "profiles",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "profile_id"),
                @UniqueConstraint(columnNames = "profile_name")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_id", nullable = false, updatable = false, length = 20)
    private String profileId;   // esqxxxxxxx

    @Column(name = "profile_name", nullable = false, length = 100)
    private String profileName;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "active", nullable = false)
    private Boolean active;
}
