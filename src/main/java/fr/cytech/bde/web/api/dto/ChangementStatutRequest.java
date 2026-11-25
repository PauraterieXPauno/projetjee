package fr.cytech.bde.web.api.dto;

import fr.cytech.bde.domain.enums.StatutDocument;
import jakarta.validation.constraints.NotNull;

public record ChangementStatutRequest(
        @NotNull(message = "Le statut cible est obligatoire.")
        StatutDocument statut
) {
}
