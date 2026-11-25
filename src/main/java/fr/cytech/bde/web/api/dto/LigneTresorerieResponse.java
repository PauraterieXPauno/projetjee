package fr.cytech.bde.web.api.dto;

import fr.cytech.bde.domain.enums.StatutLigne;
import fr.cytech.bde.domain.enums.TypeLigne;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LigneTresorerieResponse(
        Long id,
        String libelle,
        TypeLigne type,
        BigDecimal montant,
        String categorie,
        StatutLigne statut,
        LocalDate dateOperation,
        EvenementSummary evenement,
        MembreSummary createur,
        DocumentSummary documentJustificatif
) {
}
