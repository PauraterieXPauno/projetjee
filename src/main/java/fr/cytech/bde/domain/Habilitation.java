package fr.cytech.bde.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "habilitations")
public class Habilitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plafond_depense", nullable = false, precision = 15, scale = 2)
    private BigDecimal plafondDepense;

    @Column(nullable = false)
    private boolean actif;

    @Column(name = "date_attribution", nullable = false)
    private LocalDate dateAttribution;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "membre_id", nullable = false, unique = true)
    private MembreBureau membre;

    public Habilitation() {
    }

    public Habilitation(BigDecimal plafondDepense, boolean actif, LocalDate dateAttribution) {
        this.plafondDepense = plafondDepense;
        this.actif = actif;
        this.dateAttribution = dateAttribution;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPlafondDepense() {
        return plafondDepense;
    }

    public void setPlafondDepense(BigDecimal plafondDepense) {
        this.plafondDepense = plafondDepense;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public LocalDate getDateAttribution() {
        return dateAttribution;
    }

    public void setDateAttribution(LocalDate dateAttribution) {
        this.dateAttribution = dateAttribution;
    }

    public MembreBureau getMembre() {
        return membre;
    }

    public void setMembre(MembreBureau membre) {
        this.membre = membre;
    }
}
