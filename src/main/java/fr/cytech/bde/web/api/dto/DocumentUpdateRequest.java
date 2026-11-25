package fr.cytech.bde.web.api.dto;

import fr.cytech.bde.domain.enums.StatutDocument;
import fr.cytech.bde.domain.enums.TypeDocument;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

public record DocumentUpdateRequest(
        @NotBlank(message = "Le titre est obligatoire.")
        @Size(max = 180, message = "Le titre ne peut pas dépasser 180 caractères.")
        String titre,

        @NotNull(message = "Le type de document est obligatoire.")
        TypeDocument type,

        @Size(max = 500, message = "La référence ne peut pas dépasser 500 caractères.")
        String reference,

        LocalDate dateCreation,

        Long dossierId,

        Set<Long> relecteurIds,

        StatutDocument statut
) {
}
