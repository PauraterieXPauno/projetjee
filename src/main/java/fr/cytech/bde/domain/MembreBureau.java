package fr.cytech.bde.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "membres_bureau")
public class MembreBureau {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nom;

    @Column(nullable = false, length = 80)
    private String prenom;

    @Column(nullable = false, unique = true, length = 160)
    private String email;

    @Column(name = "role_membre", nullable = false, length = 80)
    private String role;

    @Column(nullable = false, length = 40)
    private String promotion;

    @OneToOne(mappedBy = "membre", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Habilitation habilitation;

    @ManyToMany(mappedBy = "equipeOrganisatrice", fetch = FetchType.LAZY)
    private Set<Evenement> evenementsOrganises = new HashSet<>();

    @ManyToMany(mappedBy = "relecteurs", fetch = FetchType.LAZY)
    private Set<DocumentAdmin> documentsPartages = new HashSet<>();

    public MembreBureau() {
    }

    public MembreBureau(String nom, String prenom, String email, String role, String promotion) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
        this.promotion = promotion;
    }

    public String getNomComplet() {
        return prenom + " " + nom;
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

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPromotion() {
        return promotion;
    }

    public void setPromotion(String promotion) {
        this.promotion = promotion;
    }

    public Habilitation getHabilitation() {
        return habilitation;
    }

    public void setHabilitation(Habilitation habilitation) {
        if (this.habilitation == habilitation) {
            return;
        }
        if (this.habilitation != null) {
            this.habilitation.setMembre(null);
        }
        this.habilitation = habilitation;
        if (habilitation != null) {
            habilitation.setMembre(this);
        }
    }

    public Set<Evenement> getEvenementsOrganises() {
        return evenementsOrganises;
    }

    public void setEvenementsOrganises(Set<Evenement> evenementsOrganises) {
        this.evenementsOrganises = evenementsOrganises;
    }

    public Set<DocumentAdmin> getDocumentsPartages() {
        return documentsPartages;
    }

    public void setDocumentsPartages(Set<DocumentAdmin> documentsPartages) {
        this.documentsPartages = documentsPartages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MembreBureau autre) || id == null) {
            return false;
        }
        return Objects.equals(id, autre.id);
    }

    @Override
    public int hashCode() {
        return MembreBureau.class.hashCode();
    }
}
