package fr.paurateriexpauno.projetjee.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TresorerieController {

    @GetMapping("/tresorerie")
    public String afficher(Model model) {
        model.addAttribute("page", "tresorerie");
        model.addAttribute("titre", "Trésorerie");
        return "tresorerie";
    }
}
