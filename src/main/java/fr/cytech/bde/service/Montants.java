package fr.cytech.bde.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Montants {

    private Montants() {
    }

    public static BigDecimal arrondir(BigDecimal valeur) {
        return valeur == null ? null : valeur.setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal nonNull(BigDecimal valeur) {
        return valeur == null ? BigDecimal.ZERO.setScale(2) : valeur;
    }
}
