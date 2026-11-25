package fr.cytech.bde.web.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MembreRequest(
        @NotBlank(message = "Le nom est obligatoire.")
        @Size(max = 80, message = "Le nom ne peut pas dépasser 80 caractères.")
        String nom,

        @NotBlank(message = "Le prénom est obligatoire.")
        @Size(max = 80, message = "Le prénom ne peut pas dépasser 80 caractères.")
        String prenom,

        @NotBlank(message = "L'adresse e-mail est obligatoire.")
        @Email(message = "L'adresse e-mail est invalide.")
        @Size(max = 160, message = "L'adresse e-mail est trop longue.")
        String email,

        @NotBlank(message = "Le rôle est obligatoire.")
        @Size(max = 80, message = "Le rôle ne peut pas dépasser 80 caractères.")
        String role,

        @NotBlank(message = "La promotion est obligatoire.")
        @Size(max = 40, message = "La promotion ne peut pas dépasser 40 caractères.")
        String promotion
) {
}
