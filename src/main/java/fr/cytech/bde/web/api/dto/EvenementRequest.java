package fr.cytech.bde.web.api.dto;

import fr.cytech.bde.domain.enums.StatutEvenement;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EvenementRequest(
        @NotBlank(message = "Le titre est obligatoire.")
        @Size(max = 180, message = "Le titre ne peut pas dépasser 180 caractères.")
        String titre,

        @NotNull(message = "La date est obligatoire.")
        LocalDate date,

        @NotBlank(message = "Le lieu est obligatoire.")
        @Size(max = 160, message = "Le lieu ne peut pas dépasser 160 caractères.")
        String lieu,

        @NotNull(message = "Le budget prévisionnel est obligatoire.")
        @DecimalMin(value = "0.00", message = "Le budget ne peut pas être négatif.")
        @Digits(integer = 13, fraction = 2, message = "Le budget doit contenir au maximum 2 décimales.")
        BigDecimal budgetPrevisionnel,

        @NotNull(message = "Le statut est obligatoire.")
        StatutEvenement statut
) {
}
