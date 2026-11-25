package fr.cytech.bde.web.mvc;

import fr.cytech.bde.domain.DocumentAdmin;
import fr.cytech.bde.domain.enums.StatutDocument;
import fr.cytech.bde.domain.enums.TypeDocument;
import fr.cytech.bde.service.DocumentService;
import fr.cytech.bde.service.DossierService;
import fr.cytech.bde.service.MembreService;
import fr.cytech.bde.service.TresorerieService;
import fr.cytech.bde.service.exception.BusinessException;
import fr.cytech.bde.service.exception.ResourceNotFoundException;
import fr.cytech.bde.web.mvc.form.DocumentForm;
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
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final DossierService dossierService;
    private final MembreService membreService;
    private final TresorerieService tresorerieService;

    public DocumentController(DocumentService documentService,
                              DossierService dossierService,
                              MembreService membreService,
                              TresorerieService tresorerieService) {
        this.documentService = documentService;
        this.dossierService = dossierService;
        this.membreService = membreService;
        this.tresorerieService = tresorerieService;
    }

    @GetMapping
    public String liste(@RequestParam(required = false) String q,
                        @RequestParam(required = false) TypeDocument type,
                        @RequestParam(required = false) StatutDocument statut,
                        @RequestParam(required = false) Long dossierId,
                        Model model) {
        model.addAttribute("documents", documentService.rechercher(q, type, statut, dossierId));
        model.addAttribute("dossiers", dossierService.tous());
        model.addAttribute("recherche", q);
        model.addAttribute("typeFiltre", type);
        model.addAttribute("statutFiltre", statut);
        model.addAttribute("dossierFiltre", dossierId);
        return "documents/liste";
    }

    @GetMapping("/nouveau")
    public String formulaireCreation(@RequestParam(required = false) Long dossierId, Model model) {
        DocumentForm form = new DocumentForm();
        if (dossierId != null) {
            form.setDossierId(dossierService.trouver(dossierId).getId());
        }
        preparerFormulaire(form, null, model);
        return "documents/formulaire";
    }

    @PostMapping
    public String creer(@Valid @ModelAttribute("documentForm") DocumentForm form,
                        BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            preparerFormulaire(form, null, model);
            return "documents/formulaire";
        }
        try {
            DocumentAdmin document = documentService.creer(form.getTitre(), form.getType(),
                    form.getReference(), form.getDateCreation(), form.getDossierId());
            redirectAttributes.addFlashAttribute("succes",
                    "Le document a été créé au statut « Brouillon ».");
            return "redirect:/documents/" + document.getId();
        } catch (ResourceNotFoundException exception) {
            result.reject("business", exception.getMessage());
            preparerFormulaire(form, null, model);
            return "documents/formulaire";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        chargerDetail(id, model);
        return "documents/detail";
    }

    @GetMapping("/{id}/modifier")
    public String formulaireModification(@PathVariable Long id, Model model) {
        DocumentAdmin document = documentService.trouver(id);
        preparerFormulaire(DocumentForm.depuis(document), id, model);
        return "documents/formulaire";
    }

    @PostMapping("/{id}")
    public String modifier(@PathVariable Long id,
                           @Valid @ModelAttribute("documentForm") DocumentForm form,
                           BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            preparerFormulaire(form, id, model);
            return "documents/formulaire";
        }
        try {
            documentService.modifier(id, form.getTitre(), form.getType(), form.getReference(),
                    form.getDateCreation(), form.getDossierId());
            redirectAttributes.addFlashAttribute("succes", "Le document a été mis à jour.");
            return "redirect:/documents/" + id;
        } catch (ResourceNotFoundException exception) {
            result.reject("business", exception.getMessage());
            preparerFormulaire(form, id, model);
            return "documents/formulaire";
        }
    }

    @PostMapping("/{id}/statut")
    public String faireAvancerStatut(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            DocumentAdmin document = documentService.trouver(id);
            StatutDocument suivant = document.getStatut().suivant();
            if (suivant == null) {
                redirectAttributes.addFlashAttribute("erreur", "Ce document est déjà archivé.");
            } else {
                documentService.changerStatut(id, suivant);
                redirectAttributes.addFlashAttribute("succes",
                        "Le statut est maintenant « " + suivant.getLibelle() + " ».");
            }
        } catch (BusinessException exception) {
            redirectAttributes.addFlashAttribute("erreur", exception.getMessage());
        }
        return "redirect:/documents/" + id;
    }

    @PostMapping("/{id}/relecteurs")
    public String ajouterRelecteur(@PathVariable Long id,
                                   @RequestParam(required = false) Long membreId,
                                   RedirectAttributes redirectAttributes) {
        if (membreId == null) {
            redirectAttributes.addFlashAttribute("erreur", "Sélectionnez un membre à associer.");
            return "redirect:/documents/" + id;
        }
        try {
            documentService.ajouterRelecteur(id, membreId);
            redirectAttributes.addFlashAttribute("succes", "Le membre a été ajouté aux relecteurs.");
        } catch (ResourceNotFoundException exception) {
            redirectAttributes.addFlashAttribute("erreur", exception.getMessage());
        }
        return "redirect:/documents/" + id;
    }

    @PostMapping("/{id}/relecteurs/{membreId}/supprimer")
    public String retirerRelecteur(@PathVariable Long id, @PathVariable Long membreId,
                                   RedirectAttributes redirectAttributes) {
        try {
            documentService.retirerRelecteur(id, membreId);
            redirectAttributes.addFlashAttribute("succes", "Le membre a été dissocié du document.");
        } catch (ResourceNotFoundException exception) {
            redirectAttributes.addFlashAttribute("erreur", exception.getMessage());
        }
        return "redirect:/documents/" + id;
    }

    @PostMapping("/{id}/dossier")
    public String associerDossier(@PathVariable Long id,
                                  @RequestParam(required = false) Long dossierId,
                                  RedirectAttributes redirectAttributes) {
        try {
            if (dossierId == null) {
                documentService.dissocierDossier(id);
                redirectAttributes.addFlashAttribute("succes", "Le document a été dissocié de son dossier.");
            } else {
                documentService.associerDossier(id, dossierId);
                redirectAttributes.addFlashAttribute("succes", "Le document a été rattaché au dossier.");
            }
        } catch (ResourceNotFoundException | BusinessException exception) {
            redirectAttributes.addFlashAttribute("erreur", exception.getMessage());
        }
        return "redirect:/documents/" + id;
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            documentService.supprimer(id);
            redirectAttributes.addFlashAttribute("succes", "Le document a été supprimé.");
            return "redirect:/documents";
        } catch (BusinessException exception) {
            redirectAttributes.addFlashAttribute("erreur", exception.getMessage());
            return "redirect:/documents/" + id;
        }
    }

    private void preparerFormulaire(DocumentForm form, Long id, Model model) {
        model.addAttribute("documentForm", form);
        model.addAttribute("documentId", id);
        model.addAttribute("dossiers", dossierService.tous());
        model.addAttribute("action", id == null ? "/documents" : "/documents/" + id);
        model.addAttribute("titrePage", id == null ? "Nouveau document" : "Modifier le document");
    }

    private void chargerDetail(Long id, Model model) {
        DocumentAdmin document = documentService.trouver(id);
        model.addAttribute("document", document);
        model.addAttribute("prochainStatut", document.getStatut().suivant());
        model.addAttribute("dossiers", dossierService.tous());
        model.addAttribute("membresDisponibles", membreService.tous().stream()
                .filter(membre -> !document.getRelecteurs().contains(membre))
                .toList());
        model.addAttribute("lignesTresorerie", tresorerieService.lignesDuJustificatif(id));
    }
}
