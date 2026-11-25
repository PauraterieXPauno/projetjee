package fr.cytech.bde.domain.enums;

public enum StatutLigne {
    EN_ATTENTE("En attente"),
    VALIDEE("Validée"),
    REJETEE("Rejetée");

    private final String libelle;

    StatutLigne(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
