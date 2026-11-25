package fr.cytech.bde.web.api.dto;

import fr.cytech.bde.domain.enums.TypeLigne;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LigneTresorerieRequest(
        @NotBlank(message = "Le libellé est obligatoire.")
        @Size(max = 180, message = "Le libellé ne peut pas dépasser 180 caractères.")
        String libelle,

        @NotNull(message = "Le type est obligatoire.")
        TypeLigne type,

        @NotNull(message = "Le montant est obligatoire.")
        @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à zéro.")
        @Digits(integer = 13, fraction = 2, message = "Le montant doit contenir au maximum 2 décimales.")
        BigDecimal montant,

        @NotBlank(message = "La catégorie est obligatoire.")
        @Size(max = 80, message = "La catégorie ne peut pas dépasser 80 caractères.")
        String categorie,

        @NotNull(message = "La date d'opération est obligatoire.")
        LocalDate dateOperation,

        Long evenementId,

        @NotNull(message = "Le créateur est obligatoire.")
        Long createurId,

        Long documentJustificatifId,

        boolean validationExplicite
) {
}
