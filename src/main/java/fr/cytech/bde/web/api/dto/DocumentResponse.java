package fr.cytech.bde.web.api.dto;

import fr.cytech.bde.domain.enums.StatutDocument;
import fr.cytech.bde.domain.enums.TypeDocument;

import java.time.LocalDate;
import java.util.List;

public record DocumentResponse(
        Long id,
        String titre,
        TypeDocument type,
        StatutDocument statut,
        String reference,
        LocalDate dateCreation,
        DossierSummary dossier,
        List<MembreSummary> relecteurs
) {
}
