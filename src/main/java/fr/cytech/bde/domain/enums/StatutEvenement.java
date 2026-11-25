package fr.cytech.bde.domain.enums;

public enum StatutEvenement {
    PLANIFIE("Planifié"),
    EN_COURS("En cours"),
    TERMINE("Terminé"),
    ANNULE("Annulé");

    private final String libelle;

    StatutEvenement(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
