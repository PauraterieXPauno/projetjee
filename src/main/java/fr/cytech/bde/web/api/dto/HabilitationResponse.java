package fr.cytech.bde.web.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record HabilitationResponse(
        Long id,
        BigDecimal plafondDepense,
        boolean actif,
        LocalDate dateAttribution
) {
}
