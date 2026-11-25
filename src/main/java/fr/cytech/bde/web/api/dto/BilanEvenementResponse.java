package fr.cytech.bde.web.api.dto;

import java.math.BigDecimal;

public record BilanEvenementResponse(
        BigDecimal budgetPrevisionnel,
        BigDecimal recettesValidees,
        BigDecimal depensesValidees,
        BigDecimal montantEnAttente,
        BigDecimal budgetReel,
        BigDecimal soldeNet,
        BigDecimal ecartBudget,
        int tauxConsommation,
        long nombreLignes,
        long nombreEnAttente
) {
}
