package fr.cytech.bde.domain;

import fr.cytech.bde.domain.enums.StatutEvenement;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "evenements")
public class Evenement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 180)
    private String titre;

    @Column(name = "date_evenement", nullable = false)
    private LocalDate date;

    @Column(nullable = false, length = 160)
    private String lieu;

    @Column(name = "budget_previsionnel", nullable = false, precision = 15, scale = 2)
    private BigDecimal budgetPrevisionnel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatutEvenement statut = StatutEvenement.PLANIFIE;

    @OneToMany(mappedBy = "evenement", fetch = FetchType.LAZY)
    @OrderBy("dateOperation DESC, id DESC")
    private List<LigneTresorerie> lignesTresorerie = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "evenement_organisateurs",
            joinColumns = @JoinColumn(name = "evenement_id"),
            inverseJoinColumns = @JoinColumn(name = "membre_id")
    )
    private Set<MembreBureau> equipeOrganisatrice = new HashSet<>();

    public Evenement() {
    }

    public Evenement(String titre, LocalDate date, String lieu, BigDecimal budgetPrevisionnel,
                     StatutEvenement statut) {
        this.titre = titre;
        this.date = date;
        this.lieu = lieu;
        this.budgetPrevisionnel = budgetPrevisionnel;
        this.statut = statut;
    }

    public void ajouterOrganisateur(MembreBureau membre) {
        if (equipeOrganisatrice.add(membre)) {
            membre.getEvenementsOrganises().add(this);
        }
    }

    public void retirerOrganisateur(MembreBureau membre) {
        if (equipeOrganisatrice.remove(membre)) {
            membre.getEvenementsOrganises().remove(this);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public BigDecimal getBudgetPrevisionnel() {
        return budgetPrevisionnel;
    }

    public void setBudgetPrevisionnel(BigDecimal budgetPrevisionnel) {
        this.budgetPrevisionnel = budgetPrevisionnel;
    }

    public StatutEvenement getStatut() {
        return statut;
    }

    public void setStatut(StatutEvenement statut) {
        this.statut = statut;
    }

    public List<LigneTresorerie> getLignesTresorerie() {
        return lignesTresorerie;
    }

    public void setLignesTresorerie(List<LigneTresorerie> lignesTresorerie) {
        this.lignesTresorerie = lignesTresorerie;
    }

    public Set<MembreBureau> getEquipeOrganisatrice() {
        return equipeOrganisatrice;
    }

    public void setEquipeOrganisatrice(Set<MembreBureau> equipeOrganisatrice) {
        this.equipeOrganisatrice = equipeOrganisatrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Evenement autre) || id == null) {
            return false;
        }
        return Objects.equals(id, autre.id);
    }

    @Override
    public int hashCode() {
        return Evenement.class.hashCode();
    }
}
