package fr.cytech.bde.web.api.dto;

import java.util.List;

public record DossierResponse(
        Long id,
        String nom,
        String categorie,
        String description,
        List<DocumentSummary> documents
) {
}
