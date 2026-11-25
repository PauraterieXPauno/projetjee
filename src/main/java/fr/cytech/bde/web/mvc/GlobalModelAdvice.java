package fr.cytech.bde.web.mvc;

import fr.cytech.bde.domain.enums.StatutDocument;
import fr.cytech.bde.domain.enums.StatutEvenement;
import fr.cytech.bde.domain.enums.StatutLigne;
import fr.cytech.bde.domain.enums.TypeDocument;
import fr.cytech.bde.domain.enums.TypeLigne;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDate;
import java.time.Year;

@ControllerAdvice(basePackages = "fr.cytech.bde.web.mvc")
public class GlobalModelAdvice {

    @ModelAttribute
    public void ajouterAttributsCommuns(Model model, HttpServletRequest request) {
        model.addAttribute("typesDocument", TypeDocument.values());
        model.addAttribute("statutsDocument", StatutDocument.values());
        model.addAttribute("statutsEvenement", StatutEvenement.values());
        model.addAttribute("typesLigne", TypeLigne.values());
        model.addAttribute("statutsLigne", StatutLigne.values());
        model.addAttribute("anneeCourante", Year.now().getValue());
        model.addAttribute("aujourdHui", LocalDate.now());

        String uri = request.getRequestURI();
        String actif = "accueil";
        if (uri.startsWith("/membres")) {
            actif = "membres";
        } else if (uri.startsWith("/evenements")) {
            actif = "evenements";
        } else if (uri.startsWith("/tresorerie")) {
            actif = "tresorerie";
        } else if (uri.startsWith("/documents")) {
            actif = "documents";
        } else if (uri.startsWith("/dossiers")) {
            actif = "dossiers";
        }
        model.addAttribute("navActif", actif);
    }
}
