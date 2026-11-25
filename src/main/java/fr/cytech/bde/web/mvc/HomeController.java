package fr.cytech.bde.web.mvc;

import fr.cytech.bde.service.DocumentService;
import fr.cytech.bde.service.DossierService;
import fr.cytech.bde.service.EvenementService;
import fr.cytech.bde.service.MembreService;
import fr.cytech.bde.service.TresorerieService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final MembreService membreService;
    private final EvenementService evenementService;
    private final TresorerieService tresorerieService;
    private final DocumentService documentService;
    private final DossierService dossierService;

    public HomeController(MembreService membreService,
                          EvenementService evenementService,
                          TresorerieService tresorerieService,
                          DocumentService documentService,
                          DossierService dossierService) {
        this.membreService = membreService;
        this.evenementService = evenementService;
        this.tresorerieService = tresorerieService;
        this.documentService = documentService;
        this.dossierService = dossierService;
    }

    @GetMapping("/")
    public String tableauDeBord(Model model) {
        model.addAttribute("synthese", tresorerieService.synthese());
        model.addAttribute("dernieresLignes", tresorerieService.dernieres(6));
        model.addAttribute("prochainsEvenements", evenementService.prochains(4));
        model.addAttribute("derniersDocuments",
                documentService.rechercher(null, null, null, null).stream().limit(5).toList());
        model.addAttribute("nombreMembres", membreService.compter());
        model.addAttribute("nombreEvenements", evenementService.compter());
        model.addAttribute("nombreDocuments", documentService.compter());
        model.addAttribute("nombreDossiers", dossierService.compter());
        return "home";
    }
}
