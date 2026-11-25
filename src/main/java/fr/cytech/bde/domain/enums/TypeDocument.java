package fr.cytech.bde.domain.enums;

public enum TypeDocument {
    STATUTS("Statuts"),
    AUTORISATION("Autorisation"),
    DEVIS("Devis"),
    FACTURE("Facture"),
    PV("Procès-verbal");

    private final String libelle;

    TypeDocument(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
