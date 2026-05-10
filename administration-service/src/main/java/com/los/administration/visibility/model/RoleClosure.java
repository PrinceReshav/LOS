package com.los.administration.visibility.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "role_closure",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"ancestor_role_id", "descendant_role_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleClosure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ancestor_role_id", nullable = false)
    private String ancestorRoleId;

    @Column(name = "descendant_role_id", nullable = false)
    private String descendantRoleId;

    private Integer depth;
}