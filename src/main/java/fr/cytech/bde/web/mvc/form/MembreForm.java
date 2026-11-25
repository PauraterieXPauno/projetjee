package fr.cytech.bde.web.mvc.form;

import fr.cytech.bde.domain.MembreBureau;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MembreForm {

    @NotBlank(message = "Le nom est obligatoire.")
    @Size(max = 80, message = "Le nom ne peut pas dépasser 80 caractères.")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire.")
    @Size(max = 80, message = "Le prénom ne peut pas dépasser 80 caractères.")
    private String prenom;

    @NotBlank(message = "L'adresse e-mail est obligatoire.")
    @Email(message = "L'adresse e-mail est invalide.")
    @Size(max = 160, message = "L'adresse e-mail est trop longue.")
    private String email;

    @NotBlank(message = "Le rôle est obligatoire.")
    @Size(max = 80, message = "Le rôle ne peut pas dépasser 80 caractères.")
    private String role;

    @NotBlank(message = "La promotion est obligatoire.")
    @Size(max = 40, message = "La promotion ne peut pas dépasser 40 caractères.")
    private String promotion;

    public static MembreForm depuis(MembreBureau membre) {
        MembreForm form = new MembreForm();
        form.setNom(membre.getNom());
        form.setPrenom(membre.getPrenom());
        form.setEmail(membre.getEmail());
        form.setRole(membre.getRole());
        form.setPromotion(membre.getPromotion());
        return form;
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
}
