package fr.cytech.bde.web.mvc;

import fr.cytech.bde.domain.DossierAdmin;
import fr.cytech.bde.service.DocumentService;
import fr.cytech.bde.service.DossierService;
import fr.cytech.bde.service.exception.BusinessException;
import fr.cytech.bde.service.exception.ResourceNotFoundException;
import fr.cytech.bde.web.mvc.form.DossierForm;
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
@RequestMapping("/dossiers")
public class DossierController {

    private final DossierService dossierService;
    private final DocumentService documentService;

    public DossierController(DossierService dossierService, DocumentService documentService) {
        this.dossierService = dossierService;
        this.documentService = documentService;
    }

    @GetMapping
    public String liste(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("dossiers", dossierService.rechercher(q));
        model.addAttribute("recherche", q);
        return "dossiers/liste";
    }

    @GetMapping("/nouveau")
    public String formulaireCreation(Model model) {
        preparerFormulaire(new DossierForm(), null, model);
        return "dossiers/formulaire";
    }

    @PostMapping
    public String creer(@Valid @ModelAttribute("dossierForm") DossierForm form,
                        BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            preparerFormulaire(form, null, model);
            return "dossiers/formulaire";
        }
        DossierAdmin dossier = dossierService.creer(form.getNom(), form.getCategorie(), form.getDescription());
        redirectAttributes.addFlashAttribute("succes", "Le dossier a été créé.");
        return "redirect:/dossiers/" + dossier.getId();
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        DossierAdmin dossier = dossierService.trouver(id);
        model.addAttribute("dossier", dossier);
        model.addAttribute("documents", documentService.documentsDuDossier(id));
        model.addAttribute("documentsDisponibles", documentService.documentsSansDossier());
        return "dossiers/detail";
    }

    @GetMapping("/{id}/modifier")
    public String formulaireModification(@PathVariable Long id, Model model) {
        DossierAdmin dossier = dossierService.trouver(id);
        preparerFormulaire(DossierForm.depuis(dossier), id, model);
        return "dossiers/formulaire";
    }

    @PostMapping("/{id}")
    public String modifier(@PathVariable Long id,
                           @Valid @ModelAttribute("dossierForm") DossierForm form,
                           BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            preparerFormulaire(form, id, model);
            return "dossiers/formulaire";
        }
        dossierService.modifier(id, form.getNom(), form.getCategorie(), form.getDescription());
        redirectAttributes.addFlashAttribute("succes", "Le dossier a été mis à jour.");
        return "redirect:/dossiers/" + id;
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            dossierService.supprimer(id);
            redirectAttributes.addFlashAttribute("succes", "Le dossier a été supprimé.");
            return "redirect:/dossiers";
        } catch (BusinessException exception) {
            redirectAttributes.addFlashAttribute("erreur", exception.getMessage());
            return "redirect:/dossiers/" + id;
        }
    }

    @PostMapping("/{id}/documents")
    public String rattacherDocument(@PathVariable Long id,
                                    @RequestParam(required = false) Long documentId,
                                    RedirectAttributes redirectAttributes) {
        if (documentId == null) {
            redirectAttributes.addFlashAttribute("erreur", "Sélectionnez un document à rattacher.");
            return "redirect:/dossiers/" + id;
        }
        try {
            dossierService.rattacherDocument(id, documentId);
            redirectAttributes.addFlashAttribute("succes", "Le document a été rattaché au dossier.");
        } catch (ResourceNotFoundException | BusinessException exception) {
            redirectAttributes.addFlashAttribute("erreur", exception.getMessage());
        }
        return "redirect:/dossiers/" + id;
    }

    @PostMapping("/{id}/documents/{documentId}/detacher")
    public String detacherDocument(@PathVariable Long id, @PathVariable Long documentId,
                                   RedirectAttributes redirectAttributes) {
        try {
            dossierService.detacherDocument(id, documentId);
            redirectAttributes.addFlashAttribute("succes", "Le document a été dissocié du dossier.");
        } catch (ResourceNotFoundException | BusinessException exception) {
            redirectAttributes.addFlashAttribute("erreur", exception.getMessage());
        }
        return "redirect:/dossiers/" + id;
    }

    private void preparerFormulaire(DossierForm form, Long id, Model model) {
        model.addAttribute("dossierForm", form);
        model.addAttribute("dossierId", id);
        model.addAttribute("action", id == null ? "/dossiers" : "/dossiers/" + id);
        model.addAttribute("titrePage", id == null ? "Nouveau dossier" : "Modifier le dossier");
    }
}
