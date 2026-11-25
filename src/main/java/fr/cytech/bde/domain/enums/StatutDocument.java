package fr.cytech.bde.domain.enums;

public enum StatutDocument {
    BROUILLON("Brouillon"),
    EN_VALIDATION("En validation"),
    VALIDE("Validé"),
    ARCHIVE("Archivé");

    private final String libelle;

    StatutDocument(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }

    public StatutDocument suivant() {
        return switch (this) {
            case BROUILLON -> EN_VALIDATION;
            case EN_VALIDATION -> VALIDE;
            case VALIDE -> ARCHIVE;
            case ARCHIVE -> null;
        };
    }
}
