package fr.cytech.bde.web.mvc;

import fr.cytech.bde.domain.MembreBureau;
import fr.cytech.bde.service.HabilitationService;
import fr.cytech.bde.service.MembreService;
import fr.cytech.bde.service.TresorerieService;
import fr.cytech.bde.service.exception.BusinessException;
import fr.cytech.bde.web.mvc.form.HabilitationForm;
import fr.cytech.bde.web.mvc.form.MembreForm;
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
@RequestMapping("/membres")
public class MembreController {

    private final MembreService membreService;
    private final HabilitationService habilitationService;
    private final TresorerieService tresorerieService;

    public MembreController(MembreService membreService,
                            HabilitationService habilitationService,
                            TresorerieService tresorerieService) {
        this.membreService = membreService;
        this.habilitationService = habilitationService;
        this.tresorerieService = tresorerieService;
    }

    @GetMapping
    public String liste(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("membres", membreService.rechercher(q));
        model.addAttribute("recherche", q);
        return "membres/liste";
    }

    @GetMapping("/nouveau")
    public String formulaireCreation(Model model) {
        preparerFormulaire(new MembreForm(), null, model);
        return "membres/formulaire";
    }

    @PostMapping
    public String creer(@Valid @ModelAttribute("membreForm") MembreForm form,
                        BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            preparerFormulaire(form, null, model);
            return "membres/formulaire";
        }
        try {
            MembreBureau membre = membreService.creer(
                    form.getNom(), form.getPrenom(), form.getEmail(), form.getRole(), form.getPromotion());
            redirectAttributes.addFlashAttribute("succes",
                    "Le membre " + membre.getNomComplet() + " a été ajouté au bureau.");
            return "redirect:/membres/" + membre.getId();
        } catch (BusinessException exception) {
            result.rejectValue("email", "email.existe", exception.getMessage());
            preparerFormulaire(form, null, model);
            return "membres/formulaire";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        chargerDetail(id, model);
        return "membres/detail";
    }

    @GetMapping("/{id}/modifier")
    public String formulaireModification(@PathVariable Long id, Model model) {
        MembreBureau membre = membreService.trouver(id);
        preparerFormulaire(MembreForm.depuis(membre), id, model);
        return "membres/formulaire";
    }

    @PostMapping("/{id}")
    public String modifier(@PathVariable Long id,
                           @Valid @ModelAttribute("membreForm") MembreForm form,
                           BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            preparerFormulaire(form, id, model);
            return "membres/formulaire";
        }
        try {
            MembreBureau membre = membreService.modifier(id, form.getNom(), form.getPrenom(),
                    form.getEmail(), form.getRole(), form.getPromotion());
            redirectAttributes.addFlashAttribute("succes", "La fiche de " + membre.getNomComplet() + " a été mise à jour.");
            return "redirect:/membres/" + id;
        } catch (BusinessException exception) {
            result.rejectValue("email", "email.existe", exception.getMessage());
            preparerFormulaire(form, id, model);
            return "membres/formulaire";
        }
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            MembreBureau membre = membreService.trouver(id);
            String nom = membre.getNomComplet();
            membreService.supprimer(id);
            redirectAttributes.addFlashAttribute("succes", "Le membre " + nom + " a été supprimé.");
        } catch (BusinessException exception) {
            redirectAttributes.addFlashAttribute("erreur", exception.getMessage());
        }
        return "redirect:/membres";
    }

    @PostMapping("/{id}/habilitation")
    public String enregistrerHabilitation(@PathVariable Long id,
                                          @Valid @ModelAttribute("habilitationForm") HabilitationForm form,
                                          BindingResult result, Model model,
                                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            chargerDetail(id, model);
            return "membres/detail";
        }
        try {
            habilitationService.enregistrer(id, form.getPlafondDepense(), form.isActif(), form.getDateAttribution());
            redirectAttributes.addFlashAttribute("succes", "L'habilitation a été enregistrée.");
            return "redirect:/membres/" + id;
        } catch (BusinessException exception) {
            result.reject("business", exception.getMessage());
            chargerDetail(id, model);
            return "membres/detail";
        }
    }

    @PostMapping("/{id}/habilitation/supprimer")
    public String dissocierHabilitation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        habilitationService.dissocier(id);
        redirectAttributes.addFlashAttribute("succes",
                "L'habilitation a été dissociée. Les prochaines dépenses du membre passeront en attente.");
        return "redirect:/membres/" + id;
    }

    private void preparerFormulaire(MembreForm form, Long id, Model model) {
        model.addAttribute("membreForm", form);
        model.addAttribute("membreId", id);
        model.addAttribute("action", id == null ? "/membres" : "/membres/" + id);
        model.addAttribute("titrePage", id == null ? "Nouveau membre" : "Modifier le membre");
    }

    private void chargerDetail(Long id, Model model) {
        MembreBureau membre = membreService.trouver(id);
        model.addAttribute("membre", membre);
        if (!model.containsAttribute("habilitationForm")) {
            model.addAttribute("habilitationForm", HabilitationForm.depuis(membre.getHabilitation()));
        }
        model.addAttribute("evenementsOrganises", membre.getEvenementsOrganises());
        model.addAttribute("documentsPartages", membre.getDocumentsPartages());
        model.addAttribute("lignesTresorerie", tresorerieService.lignesDuCreateur(id));
    }
}
