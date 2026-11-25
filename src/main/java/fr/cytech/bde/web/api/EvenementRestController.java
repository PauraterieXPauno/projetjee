package fr.cytech.bde.web.api;

import fr.cytech.bde.domain.Evenement;
import fr.cytech.bde.domain.enums.StatutEvenement;
import fr.cytech.bde.service.EvenementService;
import fr.cytech.bde.service.TresorerieService;
import fr.cytech.bde.web.api.dto.EvenementRequest;
import fr.cytech.bde.web.api.dto.EvenementResponse;
import fr.cytech.bde.web.api.dto.LigneTresorerieResponse;
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
@RequestMapping("/api/v1/evenements")
public class EvenementRestController {

    private final EvenementService evenementService;
    private final TresorerieService tresorerieService;
    private final ApiMapper mapper;

    public EvenementRestController(EvenementService evenementService,
                                   TresorerieService tresorerieService,
                                   ApiMapper mapper) {
        this.evenementService = evenementService;
        this.tresorerieService = tresorerieService;
        this.mapper = mapper;
    }

    @GetMapping
    public List<EvenementResponse> lister(@RequestParam(required = false) String q,
                                          @RequestParam(required = false) StatutEvenement statut) {
        return evenementService.rechercher(q, statut).stream()
                .map(e -> mapper.evenement(e, evenementService.bilan(e.getId())))
                .toList();
    }

    @GetMapping("/{id}")
    public EvenementResponse detail(@PathVariable Long id) {
        Evenement evenement = evenementService.trouver(id);
        return mapper.evenement(evenement, evenementService.bilan(id));
    }

    @PostMapping
    public ResponseEntity<EvenementResponse> creer(@Valid @RequestBody EvenementRequest request) {
        Evenement evenement = evenementService.creer(request.titre(), request.date(), request.lieu(),
                request.budgetPrevisionnel(), request.statut());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(evenement.getId()).toUri();
        return ResponseEntity.created(location)
                .body(mapper.evenement(evenement, evenementService.bilan(evenement.getId())));
    }

    @PutMapping("/{id}")
    public EvenementResponse modifier(@PathVariable Long id, @Valid @RequestBody EvenementRequest request) {
        Evenement evenement = evenementService.modifier(id, request.titre(), request.date(), request.lieu(),
                request.budgetPrevisionnel(), request.statut());
        return mapper.evenement(evenement, evenementService.bilan(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        evenementService.supprimer(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/organisateurs/{membreId}")
    public ResponseEntity<Void> ajouterOrganisateur(@PathVariable Long id, @PathVariable Long membreId) {
        evenementService.ajouterOrganisateur(id, membreId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/organisateurs/{membreId}")
    public ResponseEntity<Void> retirerOrganisateur(@PathVariable Long id, @PathVariable Long membreId) {
        evenementService.retirerOrganisateur(id, membreId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/lignes")
    public List<LigneTresorerieResponse> lignes(@PathVariable Long id) {
        evenementService.trouver(id);
        return tresorerieService.lignesDEvenement(id).stream().map(mapper::ligne).toList();
    }
}
