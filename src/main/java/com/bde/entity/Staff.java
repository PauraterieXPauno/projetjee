package com.bde.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "staff",
       uniqueConstraints = @UniqueConstraint(
               name = "uq_staff",
               columnNames = {"evenement_id", "compte_id", "staff_type_id"}))
@Getter @Setter @NoArgsConstructor
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evenement_id", nullable = false)
    private Evenement evenement;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "compte_id", nullable = false)
    private Compte compte;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "staff_type_id", nullable = false)
    private StaffType staffType;

    /** Créneau optionnel du poste. */
    private LocalDateTime heureDebut;
    private LocalDateTime heureFin;
}
