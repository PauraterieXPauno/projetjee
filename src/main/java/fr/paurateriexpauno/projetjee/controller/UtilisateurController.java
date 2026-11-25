package fr.paurateriexpauno.projetjee.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UtilisateurController {

    @GetMapping("/utilisateurs")
    public String afficher(Model model) {
        model.addAttribute("page", "utilisateurs");
        model.addAttribute("titre", "Gestion des utilisateurs");
        return "utilisateurs";
    }
}
