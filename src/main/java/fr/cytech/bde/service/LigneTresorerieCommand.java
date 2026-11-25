package fr.cytech.bde.service;

import fr.cytech.bde.domain.enums.TypeLigne;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LigneTresorerieCommand(
        String libelle,
        TypeLigne type,
        BigDecimal montant,
        String categorie,
        LocalDate dateOperation,
        Long evenementId,
        Long createurId,
        Long documentJustificatifId,
        boolean validationExplicite
) {
}
