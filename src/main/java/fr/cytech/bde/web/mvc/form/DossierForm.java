package fr.cytech.bde.web.mvc.form;

import fr.cytech.bde.domain.DossierAdmin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DossierForm {

    @NotBlank(message = "Le nom du dossier est obligatoire.")
    @Size(max = 120, message = "Le nom ne peut pas dépasser 120 caractères.")
    private String nom;

    @NotBlank(message = "La catégorie est obligatoire.")
    @Size(max = 80, message = "La catégorie ne peut pas dépasser 80 caractères.")
    private String categorie;

    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères.")
    private String description;

    public static DossierForm depuis(DossierAdmin dossier) {
        DossierForm form = new DossierForm();
        form.setNom(dossier.getNom());
        form.setCategorie(dossier.getCategorie());
        form.setDescription(dossier.getDescription());
        return form;
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
}
