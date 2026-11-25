package fr.cytech.bde.web.api.dto;

import fr.cytech.bde.domain.enums.StatutEvenement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record EvenementResponse(
        Long id,
        String titre,
        LocalDate date,
        String lieu,
        BigDecimal budgetPrevisionnel,
        StatutEvenement statut,
        List<MembreSummary> equipeOrganisatrice,
        BilanEvenementResponse bilan
) {
}
