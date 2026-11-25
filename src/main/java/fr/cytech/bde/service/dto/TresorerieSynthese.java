package fr.cytech.bde.service.dto;

import java.math.BigDecimal;

public record TresorerieSynthese(
        BigDecimal soldeGlobal,
        BigDecimal totalRecettes,
        BigDecimal totalDepenses,
        BigDecimal montantEnAttente,
        long nombreLignes,
        long nombreEnAttente
) {
    public BigDecimal getSoldeGlobal() {
        return soldeGlobal;
    }

    public BigDecimal getTotalRecettes() {
        return totalRecettes;
    }

    public BigDecimal getTotalDepenses() {
        return totalDepenses;
    }

    public BigDecimal getMontantEnAttente() {
        return montantEnAttente;
    }

    public long getNombreLignes() {
        return nombreLignes;
    }

    public long getNombreEnAttente() {
        return nombreEnAttente;
    }
}
