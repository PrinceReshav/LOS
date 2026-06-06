package com.los.administration.profile.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @Column(name = "profile_id", nullable = false, unique = true, length = 20)
    private String profileId;

    @Column(name = "profile_name", nullable = false, unique = true, length = 100)
    private String profileName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "system_defined", nullable = false)
    private Boolean systemDefined;

    @Column(name = "active", nullable = false)
    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {

        createdAt = LocalDateTime.now();
        updatedAt = createdAt;

        if(active == null){
            active = true;
        }

        if(systemDefined == null){
            systemDefined = false;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}