package fr.cytech.bde.web.api;

import fr.cytech.bde.domain.Habilitation;
import fr.cytech.bde.domain.MembreBureau;
import fr.cytech.bde.service.HabilitationService;
import fr.cytech.bde.service.MembreService;
import fr.cytech.bde.service.exception.ResourceNotFoundException;
import fr.cytech.bde.web.api.dto.HabilitationRequest;
import fr.cytech.bde.web.api.dto.HabilitationResponse;
import fr.cytech.bde.web.api.dto.MembreRequest;
import fr.cytech.bde.web.api.dto.MembreResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/v1/membres")
public class MembreRestController {

    private final MembreService membreService;
    private final HabilitationService habilitationService;
    private final ApiMapper mapper;

    public MembreRestController(MembreService membreService,
                                HabilitationService habilitationService,
                                ApiMapper mapper) {
        this.membreService = membreService;
        this.habilitationService = habilitationService;
        this.mapper = mapper;
    }

    @GetMapping
    public List<MembreResponse> lister(@RequestParam(required = false) String q) {
        return membreService.rechercher(q).stream().map(mapper::membre).toList();
    }

    @GetMapping("/{id}")
    public MembreResponse detail(@PathVariable Long id) {
        return mapper.membre(membreService.trouver(id));
    }

    @PostMapping
    public ResponseEntity<MembreResponse> creer(@Valid @RequestBody MembreRequest request) {
        MembreBureau membre = membreService.creer(request.nom(), request.prenom(), request.email(),
                request.role(), request.promotion());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(membre.getId()).toUri();
        return ResponseEntity.created(location).body(mapper.membre(membre));
    }

    @PutMapping("/{id}")
    public MembreResponse modifier(@PathVariable Long id, @Valid @RequestBody MembreRequest request) {
        MembreBureau membre = membreService.modifier(id, request.nom(), request.prenom(),
                request.email(), request.role(), request.promotion());
        return mapper.membre(membre);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        membreService.supprimer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/habilitation")
    public HabilitationResponse habilitation(@PathVariable Long id) {
        Habilitation habilitation = habilitationService.trouverParMembre(id);
        if (habilitation == null) {
            throw new ResourceNotFoundException("Ce membre n'a pas d'habilitation.");
        }
        return mapper.habilitation(habilitation);
    }

    @PutMapping("/{id}/habilitation")
    public ResponseEntity<HabilitationResponse> enregistrerHabilitation(
            @PathVariable Long id, @Valid @RequestBody HabilitationRequest request) {
        boolean existait = habilitationService.trouverParMembre(id) != null;
        Habilitation habilitation = habilitationService.enregistrer(id, request.plafondDepense(),
                request.actif(), request.dateAttribution());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
        return ResponseEntity.status(existait ? HttpStatus.OK : HttpStatus.CREATED)
                .location(location)
                .body(mapper.habilitation(habilitation));
    }

    @DeleteMapping("/{id}/habilitation")
    public ResponseEntity<Void> dissocierHabilitation(@PathVariable Long id) {
        habilitationService.dissocier(id);
        return ResponseEntity.noContent().build();
    }
}
