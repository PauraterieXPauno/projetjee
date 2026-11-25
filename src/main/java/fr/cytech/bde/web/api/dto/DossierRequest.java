package fr.cytech.bde.web.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DossierRequest(
        @NotBlank(message = "Le nom du dossier est obligatoire.")
        @Size(max = 120, message = "Le nom ne peut pas dépasser 120 caractères.")
        String nom,

        @NotBlank(message = "La catégorie est obligatoire.")
        @Size(max = 80, message = "La catégorie ne peut pas dépasser 80 caractères.")
        String categorie,

        @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères.")
        String description
) {
}
