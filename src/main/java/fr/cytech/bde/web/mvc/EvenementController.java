package fr.cytech.bde.web.mvc;

import fr.cytech.bde.domain.Evenement;
import fr.cytech.bde.domain.MembreBureau;
import fr.cytech.bde.domain.enums.StatutEvenement;
import fr.cytech.bde.service.DocumentService;
import fr.cytech.bde.service.EvenementService;
import fr.cytech.bde.service.MembreService;
import fr.cytech.bde.service.TresorerieService;
import fr.cytech.bde.service.dto.BilanEvenement;
import fr.cytech.bde.service.exception.BusinessException;
import fr.cytech.bde.service.exception.ResourceNotFoundException;
import fr.cytech.bde.web.mvc.form.EvenementForm;
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

import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/evenements")
public class EvenementController {

    private final EvenementService evenementService;
    private final MembreService membreService;
    private final TresorerieService tresorerieService;
    private final DocumentService documentService;

    public EvenementController(EvenementService evenementService,
                               MembreService membreService,
                               TresorerieService tresorerieService,
                               DocumentService documentService) {
        this.evenementService = evenementService;
        this.membreService = membreService;
        this.tresorerieService = tresorerieService;
        this.documentService = documentService;
    }

    @GetMapping
    public String liste(@RequestParam(required = false) String q,
                        @RequestParam(required = false) StatutEvenement statut,
                        Model model) {
        List<Evenement> evenements = evenementService.rechercher(q, statut);
        Map<Long, BilanEvenement> bilans = new LinkedHashMap<>();
        evenements.forEach(e -> bilans.put(e.getId(), evenementService.bilan(e.getId())));
        model.addAttribute("evenements", evenements);
        model.addAttribute("bilans", bilans);
        model.addAttribute("recherche", q);
        model.addAttribute("statutFiltre", statut);
        return "evenements/liste";
    }

    @GetMapping("/nouveau")
    public String formulaireCreation(Model model) {
        preparerFormulaire(new EvenementForm(), null, model);
        return "evenements/formulaire";
    }

    @PostMapping
    public String creer(@Valid @ModelAttribute("evenementForm") EvenementForm form,
                        BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            preparerFormulaire(form, null, model);
            return "evenements/formulaire";
        }
        Evenement evenement = evenementService.creer(form.getTitre(), form.getDate(), form.getLieu(),
                form.getBudgetPrevisionnel(), form.getStatut());
        redirectAttributes.addFlashAttribute("succes", "L'événement \"" + evenement.getTitre() + "\" a été créé.");
        return "redirect:/evenements/" + evenement.getId();
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        chargerDetail(id, model);
        return "evenements/detail";
    }

    @GetMapping("/{id}/modifier")
    public String formulaireModification(@PathVariable Long id, Model model) {
        Evenement evenement = evenementService.trouver(id);
        preparerFormulaire(EvenementForm.depuis(evenement), id, model);
        return "evenements/formulaire";
    }

    @PostMapping("/{id}")
    public String modifier(@PathVariable Long id,
                           @Valid @ModelAttribute("evenementForm") EvenementForm form,
                           BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            preparerFormulaire(form, id, model);
            return "evenements/formulaire";
        }
        evenementService.modifier(id, form.getTitre(), form.getDate(), form.getLieu(),
                form.getBudgetPrevisionnel(), form.getStatut());
        redirectAttributes.addFlashAttribute("succes", "L'événement a été mis à jour.");
        return "redirect:/evenements/" + id;
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            evenementService.supprimer(id);
            redirectAttributes.addFlashAttribute("succes",
                    "L'événement a été supprimé. Les lignes non validées ont été détachées.");
            return "redirect:/evenements";
        } catch (BusinessException exception) {
            redirectAttributes.addFlashAttribute("erreur", exception.getMessage());
            return "redirect:/evenements/" + id;
        }
    }

    @PostMapping("/{id}/organisateurs")
    public String ajouterOrganisateur(@PathVariable Long id,
                                      @RequestParam(required = false) Long membreId,
                                      RedirectAttributes redirectAttributes) {
        if (membreId == null) {
            redirectAttributes.addFlashAttribute("erreur", "Sélectionnez un membre à associer.");
            return "redirect:/evenements/" + id;
        }
        try {
            evenementService.ajouterOrganisateur(id, membreId);
            redirectAttributes.addFlashAttribute("succes", "Le membre a rejoint l'équipe organisatrice.");
        } catch (ResourceNotFoundException exception) {
            redirectAttributes.addFlashAttribute("erreur", exception.getMessage());
        }
        return "redirect:/evenements/" + id;
    }

    @PostMapping("/{id}/organisateurs/{membreId}/supprimer")
    public String retirerOrganisateur(@PathVariable Long id, @PathVariable Long membreId,
                                      RedirectAttributes redirectAttributes) {
        try {
            evenementService.retirerOrganisateur(id, membreId);
            redirectAttributes.addFlashAttribute("succes", "Le membre a été dissocié de l'événement.");
        } catch (ResourceNotFoundException exception) {
            redirectAttributes.addFlashAttribute("erreur", exception.getMessage());
        }
        return "redirect:/evenements/" + id;
    }

    @PostMapping("/{id}/tresorerie")
    public String ajouterLigneTresorerie(@PathVariable Long id,
                                         @Valid @ModelAttribute("tresorerieForm") TresorerieForm form,
                                         BindingResult result, Model model,
                                         RedirectAttributes redirectAttributes) {
        form.setEvenementId(id);
        if (result.hasErrors()) {
            chargerDetail(id, model);
            return "evenements/detail";
        }
        try {
            tresorerieService.creer(form.versCommande());
            redirectAttributes.addFlashAttribute("succes", "La ligne de trésorerie a été ajoutée à l'événement.");
            return "redirect:/evenements/" + id;
        } catch (BusinessException exception) {
            result.reject("business", exception.getMessage());
            chargerDetail(id, model);
            return "evenements/detail";
        }
    }

    @PostMapping("/{id}/tresorerie/rattacher")
    public String rattacherLigneExistante(@PathVariable Long id,
                                          @RequestParam(required = false) Long ligneId,
                                          RedirectAttributes redirectAttributes) {
        if (ligneId == null) {
            redirectAttributes.addFlashAttribute("erreur", "Sélectionnez une ligne de trésorerie à rattacher.");
            return "redirect:/evenements/" + id;
        }
        try {
            tresorerieService.rattacherEvenement(ligneId, id);
            redirectAttributes.addFlashAttribute("succes", "La ligne a été rattachée à l'événement.");
        } catch (ResourceNotFoundException exception) {
            redirectAttributes.addFlashAttribute("erreur", exception.getMessage());
        }
        return "redirect:/evenements/" + id;
    }

    private void preparerFormulaire(EvenementForm form, Long id, Model model) {
        model.addAttribute("evenementForm", form);
        model.addAttribute("evenementId", id);
        model.addAttribute("action", id == null ? "/evenements" : "/evenements/" + id);
        model.addAttribute("titrePage", id == null ? "Nouvel événement" : "Modifier l'événement");
    }

    private void chargerDetail(Long id, Model model) {
        Evenement evenement = evenementService.trouver(id);
        model.addAttribute("evenement", evenement);
        model.addAttribute("bilan", evenementService.bilan(id));
        model.addAttribute("lignes", tresorerieService.lignesDEvenement(id));
        model.addAttribute("lignesNonRattachees", tresorerieService.lignesNonRattachees());
        model.addAttribute("membres", membreService.tous());
        model.addAttribute("documents", documentService.tous());

        List<MembreBureau> organisateurs = evenement.getEquipeOrganisatrice().stream()
                .sorted(Comparator.comparing(MembreBureau::getNom).thenComparing(MembreBureau::getPrenom))
                .toList();
        model.addAttribute("organisateurs", organisateurs);
        model.addAttribute("organisateursDisponibles", membreService.tous().stream()
                .filter(membre -> !evenement.getEquipeOrganisatrice().contains(membre))
                .toList());

        if (!model.containsAttribute("tresorerieForm")) {
            TresorerieForm form = new TresorerieForm();
            form.setEvenementId(id);
            form.setDateOperation(LocalDate.now());
            model.addAttribute("tresorerieForm", form);
        }
    }
}
