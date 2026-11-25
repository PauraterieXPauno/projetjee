package fr.cytech.bde.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "dossiers_admin")
public class DossierAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nom;

    @Column(nullable = false, length = 80)
    private String categorie;

    @Column(length = 1000)
    private String description;

    @OneToMany(mappedBy = "dossier", fetch = FetchType.LAZY)
    @OrderBy("dateCreation DESC, id DESC")
    private List<DocumentAdmin> documents = new ArrayList<>();

    public DossierAdmin() {
    }

    public DossierAdmin(String nom, String categorie, String description) {
        this.nom = nom;
        this.categorie = categorie;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<DocumentAdmin> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentAdmin> documents) {
        this.documents = documents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DossierAdmin autre) || id == null) {
            return false;
        }
        return Objects.equals(id, autre.id);
    }

    @Override
    public int hashCode() {
        return DossierAdmin.class.hashCode();
    }
}
