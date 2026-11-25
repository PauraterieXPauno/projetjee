package fr.cytech.bde.web.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record HabilitationRequest(
        @NotNull(message = "Le plafond de dépense est obligatoire.")
        @DecimalMin(value = "0.00", message = "Le plafond ne peut pas être négatif.")
        @Digits(integer = 13, fraction = 2, message = "Le plafond doit contenir au maximum 2 décimales.")
        BigDecimal plafondDepense,

        boolean actif,

        @NotNull(message = "La date d'attribution est obligatoire.")
        LocalDate dateAttribution
) {
}
