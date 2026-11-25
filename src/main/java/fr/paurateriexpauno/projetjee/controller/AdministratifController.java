package fr.paurateriexpauno.projetjee.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdministratifController {

    @GetMapping("/administratif")
    public String afficher(Model model) {
        model.addAttribute("page", "administratif");
        model.addAttribute("titre", "Administratif");
        return "administratif";
    }
}
