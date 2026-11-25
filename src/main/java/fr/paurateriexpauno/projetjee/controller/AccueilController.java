package fr.paurateriexpauno.projetjee.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccueilController {

    @GetMapping("/")
    public String afficher(Model model) {
        model.addAttribute("page", "accueil");
        model.addAttribute("nomAssociation", "Nom de l'association");
        model.addAttribute("presentation", "Présentez ici votre association : son but, ses activités et la manière de la rejoindre.");
        return "index";
    }
}
