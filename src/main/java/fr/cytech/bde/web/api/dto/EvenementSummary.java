package fr.cytech.bde.web.api.dto;

import fr.cytech.bde.domain.enums.StatutEvenement;

import java.time.LocalDate;

public record EvenementSummary(
        Long id,
        String titre,
        LocalDate date,
        String lieu,
        StatutEvenement statut
) {
}
