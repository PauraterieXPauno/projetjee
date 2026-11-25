package fr.cytech.bde.web.mvc;

import fr.cytech.bde.domain.LigneTresorerie;
import fr.cytech.bde.domain.enums.StatutLigne;
import fr.cytech.bde.domain.enums.TypeLigne;
import fr.cytech.bde.service.DocumentService;
import fr.cytech.bde.service.EvenementService;
import fr.cytech.bde.service.MembreService;
import fr.cytech.bde.service.TresorerieService;
import fr.cytech.bde.service.exception.BusinessException;
import fr.cytech.bde.service.exception.ResourceNotFoundException;
import fr.cytech.bde.web.mvc.form.TresorerieForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tresorerie")
public class TresorerieController {

    private final TresorerieService tresorerieService;
    private final MembreService membreService;
    private final EvenementService evenementService;
    private final DocumentService documentService;

    public TresorerieController(TresorerieService tresorerieService,
                                MembreService membreService,
                                EvenementService evenementService,
                                DocumentService documentService) {
        this.tresorerieService = tresorerieService;
        this.membreService = membreService;
        this.evenementService = evenementService;
        this.documentService = documentService;
    }

    @GetMapping
    public String liste(@RequestParam(required = false) String q,
                        @RequestParam(required = false) TypeLigne type,
                        @RequestParam(required = false) StatutLigne statut,
                        @RequestParam(required = false) Long evenementId,
                        Model model) {
        model.addAttribute("lignes", tresorerieService.rechercher(q, type, statut, evenementId));
        model.addAttribute("synthese", tresorerieService.synthese());
        model.addAttribute("evenements", evenementService.tous());
        model.addAttribute("recherche", q);
        model.addAttribute("typeFiltre", type);
        model.addAttribute("statutFiltre", statut);
        model.addAttribute("evenementFiltre", evenementId);
        return "tresorerie/liste";
    }

    @GetMapping("/nouveau")
    public String formulaireCreation(@RequestParam(required = false) Long evenementId, Model model) {
        TresorerieForm form = new TresorerieForm();
        form.setEvenementId(evenementId);
        preparerFormulaire(form, null, model);
        return "tresorerie/formulaire";
    }

    @PostMapping
    public String creer(@Valid @ModelAttribute("tresorerieForm") TresorerieForm form,
                        BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            preparerFormulaire(form, null, model);
            return "tresorerie/formulaire";
        }
        try {
            LigneTresorerie ligne = tresorerieService.creer(form.versCommande());
            redirectAttributes.addFlashAttribute("succes",
                    "La ligne a été créée avec le statut « " + ligne.getStatut().getLibelle() + " ».");
            return "redirect:/tresorerie";
        } catch (BusinessException | ResourceNotFoundException exception) {
            result.reject("business", exception.getMessage());
            preparerFormulaire(form, null, model);
            return "tresorerie/formulaire";
        }
    }

    @GetMapping("/{id}/modifier")
    public String formulaireModification(@PathVariable Long id, Model model) {
        LigneTresorerie ligne = tresorerieService.trouver(id);
        preparerFormulaire(TresorerieForm.depuis(ligne), id, model);
        return "tresorerie/formulaire";
    }

    @PostMapping("/{id}")
    public String modifier(@PathVariable Long id,
                           @Valid @ModelAttribute("tresorerieForm") TresorerieForm form,
                           BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            preparerFormulaire(form, id, model);
            return "tresorerie/formulaire";
        }
        try {
            LigneTresorerie ligne = tresorerieService.modifier(id, form.versCommande());
            redirectAttributes.addFlashAttribute("succes",
                    "La ligne a été mise à jour (statut : " + ligne.getStatut().getLibelle() + ").");
            return "redirect:/tresorerie";
        } catch (BusinessException | ResourceNotFoundException exception) {
            result.reject("business", exception.getMessage());
            preparerFormulaire(form, id, model);
            return "tresorerie/formulaire";
        }
    }

    @PostMapping("/{id}/valider")
    public String valider(@PathVariable Long id,
                          @RequestParam(required = false) String retour,
                          RedirectAttributes redirectAttributes) {
        try {
            tresorerieService.valider(id);
            redirectAttributes.addFlashAttribute("succes", "La ligne a été validée explicitement.");
        } catch (ResourceNotFoundException exception) {
            redirectAttributes.addFlashAttribute("erreur", exception.getMessage());
        }
        return Redirections.vers(retour, "/tresorerie");
    }

    @PostMapping("/{id}/rejeter")
    public String rejeter(@PathVariable Long id,
                          @RequestParam(required = false) String retour,
                          RedirectAttributes redirectAttributes) {
        try {
            tresorerieService.rejeter(id);
            redirectAttributes.addFlashAttribute("succes",
                    "La ligne a été rejetée : elle reste visible mais est exclue du solde.");
        } catch (ResourceNotFoundException exception) {
            redirectAttributes.addFlashAttribute("erreur", exception.getMessage());
        }
        return Redirections.vers(retour, "/tresorerie");
    }

    @PostMapping("/{id}/detacher")
    public String detacherEvenement(@PathVariable Long id,
                                    @RequestParam(required = false) String retour,
                                    RedirectAttributes redirectAttributes) {
        try {
            tresorerieService.detacherEvenement(id);
            redirectAttributes.addFlashAttribute("succes", "La ligne a été détachée de l'événement.");
        } catch (ResourceNotFoundException exception) {
            redirectAttributes.addFlashAttribute("erreur", exception.getMessage());
        }
        return Redirections.vers(retour, "/tresorerie");
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id,
                            @RequestParam(required = false) String retour,
                            RedirectAttributes redirectAttributes) {
        try {
            tresorerieService.supprimer(id);
            redirectAttributes.addFlashAttribute("succes", "La ligne de trésorerie a été supprimée.");
        } catch (ResourceNotFoundException exception) {
            redirectAttributes.addFlashAttribute("erreur", exception.getMessage());
        }
        return Redirections.vers(retour, "/tresorerie");
    }

    private void preparerFormulaire(TresorerieForm form, Long id, Model model) {
        model.addAttribute("tresorerieForm", form);
        model.addAttribute("ligneId", id);
        model.addAttribute("membres", membreService.tous());
        model.addAttribute("evenements", evenementService.tous());
        model.addAttribute("documents", documentService.tous());
        model.addAttribute("action", id == null ? "/tresorerie" : "/tresorerie/" + id);
        model.addAttribute("titrePage", id == null ? "Nouvelle ligne de trésorerie" : "Modifier la ligne");
    }
}
