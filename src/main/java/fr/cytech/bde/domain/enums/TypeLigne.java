package fr.cytech.bde.domain.enums;

public enum TypeLigne {
    RECETTE("Recette"),
    DEPENSE("Dépense");

    private final String libelle;

    TypeLigne(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
