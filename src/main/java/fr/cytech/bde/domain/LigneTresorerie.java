package fr.cytech.bde.domain;

import fr.cytech.bde.domain.enums.StatutLigne;
import fr.cytech.bde.domain.enums.TypeLigne;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "lignes_tresorerie")
public class LigneTresorerie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 180)
    private String libelle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TypeLigne type;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    @Column(nullable = false, length = 80)
    private String categorie;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutLigne statut = StatutLigne.EN_ATTENTE;

    @Column(name = "date_operation", nullable = false)
    private LocalDate dateOperation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evenement_id")
    private Evenement evenement;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "createur_id", nullable = false)
    private MembreBureau createur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_justificatif_id")
    private DocumentAdmin documentJustificatif;

    public LigneTresorerie() {
    }

    public LigneTresorerie(String libelle, TypeLigne type, BigDecimal montant, String categorie,
                           LocalDate dateOperation) {
        this.libelle = libelle;
        this.type = type;
        this.montant = montant;
        this.categorie = categorie;
        this.dateOperation = dateOperation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public TypeLigne getType() {
        return type;
    }

    public void setType(TypeLigne type) {
        this.type = type;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public StatutLigne getStatut() {
        return statut;
    }

    public void setStatut(StatutLigne statut) {
        this.statut = statut;
    }

    public LocalDate getDateOperation() {
        return dateOperation;
    }

    public void setDateOperation(LocalDate dateOperation) {
        this.dateOperation = dateOperation;
    }

    public Evenement getEvenement() {
        return evenement;
    }

    public void setEvenement(Evenement evenement) {
        this.evenement = evenement;
    }

    public MembreBureau getCreateur() {
        return createur;
    }

    public void setCreateur(MembreBureau createur) {
        this.createur = createur;
    }

    public DocumentAdmin getDocumentJustificatif() {
        return documentJustificatif;
    }

    public void setDocumentJustificatif(DocumentAdmin documentJustificatif) {
        this.documentJustificatif = documentJustificatif;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LigneTresorerie autre) || id == null) {
            return false;
        }
        return Objects.equals(id, autre.id);
    }

    @Override
    public int hashCode() {
        return LigneTresorerie.class.hashCode();
    }
}
