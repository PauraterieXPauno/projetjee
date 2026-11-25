package fr.cytech.bde.domain;

import fr.cytech.bde.domain.enums.StatutDocument;
import fr.cytech.bde.domain.enums.TypeDocument;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "documents_admin")
public class DocumentAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 180)
    private String titre;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_document", nullable = false, length = 30)
    private TypeDocument type;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_document", nullable = false, length = 30)
    private StatutDocument statut = StatutDocument.BROUILLON;

    @Column(length = 500)
    private String reference;

    @Column(name = "date_creation", nullable = false)
    private LocalDate dateCreation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dossier_id")
    private DossierAdmin dossier;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "document_relecteurs",
            joinColumns = @JoinColumn(name = "document_id"),
            inverseJoinColumns = @JoinColumn(name = "membre_id")
    )
    private Set<MembreBureau> relecteurs = new HashSet<>();

    public DocumentAdmin() {
    }

    public DocumentAdmin(String titre, TypeDocument type, String reference, LocalDate dateCreation) {
        this.titre = titre;
        this.type = type;
        this.reference = reference;
        this.dateCreation = dateCreation;
    }

    public void ajouterRelecteur(MembreBureau membre) {
        if (relecteurs.add(membre)) {
            membre.getDocumentsPartages().add(this);
        }
    }

    public void retirerRelecteur(MembreBureau membre) {
        if (relecteurs.remove(membre)) {
            membre.getDocumentsPartages().remove(this);
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

    public TypeDocument getType() {
        return type;
    }

    public void setType(TypeDocument type) {
        this.type = type;
    }

    public StatutDocument getStatut() {
        return statut;
    }

    public void setStatut(StatutDocument statut) {
        this.statut = statut;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    public DossierAdmin getDossier() {
        return dossier;
    }

    public void setDossier(DossierAdmin dossier) {
        this.dossier = dossier;
    }

    public Set<MembreBureau> getRelecteurs() {
        return relecteurs;
    }

    public void setRelecteurs(Set<MembreBureau> relecteurs) {
        this.relecteurs = relecteurs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DocumentAdmin autre) || id == null) {
            return false;
        }
        return Objects.equals(id, autre.id);
    }

    @Override
    public int hashCode() {
        return DocumentAdmin.class.hashCode();
    }
}
