package fr.paurateriexpauno.projetjee.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EvenementController {

    @GetMapping("/evenements")
    public String afficher(Model model) {
        model.addAttribute("page", "evenements");
        model.addAttribute("titre", "Événements");
        return "evenements";
    }
}
