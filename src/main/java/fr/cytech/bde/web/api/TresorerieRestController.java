package fr.cytech.bde.web.api;

import fr.cytech.bde.domain.LigneTresorerie;
import fr.cytech.bde.domain.enums.StatutLigne;
import fr.cytech.bde.domain.enums.TypeLigne;
import fr.cytech.bde.service.LigneTresorerieCommand;
import fr.cytech.bde.service.TresorerieService;
import fr.cytech.bde.web.api.dto.LigneTresorerieRequest;
import fr.cytech.bde.web.api.dto.LigneTresorerieResponse;
import fr.cytech.bde.web.api.dto.TresorerieSyntheseResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tresorerie")
public class TresorerieRestController {

    private final TresorerieService tresorerieService;
    private final ApiMapper mapper;

    public TresorerieRestController(TresorerieService tresorerieService, ApiMapper mapper) {
        this.tresorerieService = tresorerieService;
        this.mapper = mapper;
    }

    @GetMapping
    public List<LigneTresorerieResponse> lister(@RequestParam(required = false) String q,
                                                @RequestParam(required = false) TypeLigne type,
                                                @RequestParam(required = false) StatutLigne statut,
                                                @RequestParam(required = false) Long evenementId) {
        return tresorerieService.rechercher(q, type, statut, evenementId).stream()
                .map(mapper::ligne)
                .toList();
    }

    @GetMapping("/synthese")
    public TresorerieSyntheseResponse synthese() {
        return mapper.synthese(tresorerieService.synthese());
    }

    @GetMapping("/{id}")
    public LigneTresorerieResponse detail(@PathVariable Long id) {
        return mapper.ligne(tresorerieService.trouver(id));
    }

    @PostMapping
    public ResponseEntity<LigneTresorerieResponse> creer(
            @Valid @RequestBody LigneTresorerieRequest request) {
        LigneTresorerie ligne = tresorerieService.creer(versCommande(request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(ligne.getId()).toUri();
        return ResponseEntity.created(location).body(mapper.ligne(ligne));
    }

    @PutMapping("/{id}")
    public LigneTresorerieResponse modifier(@PathVariable Long id,
                                            @Valid @RequestBody LigneTresorerieRequest request) {
        return mapper.ligne(tresorerieService.modifier(id, versCommande(request)));
    }

    @PostMapping("/{id}/valider")
    public LigneTresorerieResponse valider(@PathVariable Long id) {
        return mapper.ligne(tresorerieService.valider(id));
    }

    @PostMapping("/{id}/rejeter")
    public LigneTresorerieResponse rejeter(@PathVariable Long id) {
        return mapper.ligne(tresorerieService.rejeter(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        tresorerieService.supprimer(id);
        return ResponseEntity.noContent().build();
    }

    private LigneTresorerieCommand versCommande(LigneTresorerieRequest request) {
        return new LigneTresorerieCommand(
                request.libelle(),
                request.type(),
                request.montant(),
                request.categorie(),
                request.dateOperation(),
                request.evenementId(),
                request.createurId(),
                request.documentJustificatifId(),
                request.validationExplicite()
        );
    }
}
