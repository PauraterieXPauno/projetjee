package fr.cytech.bde.service.dto;

import java.math.BigDecimal;

public record BilanEvenement(
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
    public BigDecimal getBudgetPrevisionnel() {
        return budgetPrevisionnel;
    }

    public BigDecimal getRecettesValidees() {
        return recettesValidees;
    }

    public BigDecimal getDepensesValidees() {
        return depensesValidees;
    }

    public BigDecimal getMontantEnAttente() {
        return montantEnAttente;
    }

    public BigDecimal getBudgetReel() {
        return budgetReel;
    }

    public BigDecimal getSoldeNet() {
        return soldeNet;
    }

    public BigDecimal getEcartBudget() {
        return ecartBudget;
    }

    public int getTauxConsommation() {
        return tauxConsommation;
    }

    public long getNombreLignes() {
        return nombreLignes;
    }

    public long getNombreEnAttente() {
        return nombreEnAttente;
    }
}
