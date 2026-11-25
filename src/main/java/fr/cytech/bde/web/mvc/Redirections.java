package fr.cytech.bde.web.mvc;

public final class Redirections {

    private Redirections() {
    }

    /**
     * N'autorise que des chemins internes pour éviter les redirections ouvertes.
     */
    public static String vers(String retour, String defaut) {
        if (retour != null
                && retour.startsWith("/")
                && !retour.startsWith("//")
                && !retour.contains("\r")
                && !retour.contains("\n")) {
            return "redirect:" + retour;
        }
        return "redirect:" + defaut;
    }
}
