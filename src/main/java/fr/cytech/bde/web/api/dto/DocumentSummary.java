package fr.cytech.bde.web.api.dto;

import fr.cytech.bde.domain.enums.StatutDocument;
import fr.cytech.bde.domain.enums.TypeDocument;

import java.time.LocalDate;

public record DocumentSummary(
        Long id,
        String titre,
        TypeDocument type,
        StatutDocument statut,
        LocalDate dateCreation
) {
}
