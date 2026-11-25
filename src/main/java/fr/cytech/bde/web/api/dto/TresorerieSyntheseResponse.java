package fr.cytech.bde.web.api.dto;

import java.math.BigDecimal;

public record TresorerieSyntheseResponse(
        BigDecimal soldeGlobal,
        BigDecimal totalRecettes,
        BigDecimal totalDepenses,
        BigDecimal montantEnAttente,
        long nombreLignes,
        long nombreEnAttente
) {
}
