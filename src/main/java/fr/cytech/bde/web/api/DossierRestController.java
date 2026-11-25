package fr.cytech.bde.web.api;

import fr.cytech.bde.domain.DossierAdmin;
import fr.cytech.bde.service.DossierService;
import fr.cytech.bde.web.api.dto.DossierRequest;
import fr.cytech.bde.web.api.dto.DossierResponse;
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
@RequestMapping("/api/v1/dossiers")
public class DossierRestController {

    private final DossierService dossierService;
    private final ApiMapper mapper;

    public DossierRestController(DossierService dossierService, ApiMapper mapper) {
        this.dossierService = dossierService;
        this.mapper = mapper;
    }

    @GetMapping
    public List<DossierResponse> lister(@RequestParam(required = false) String q) {
        return dossierService.rechercher(q).stream().map(mapper::dossier).toList();
    }

    @GetMapping("/{id}")
    public DossierResponse detail(@PathVariable Long id) {
        return mapper.dossier(dossierService.trouver(id));
    }

    @PostMapping
    public ResponseEntity<DossierResponse> creer(@Valid @RequestBody DossierRequest request) {
        DossierAdmin dossier = dossierService.creer(request.nom(), request.categorie(), request.description());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(dossier.getId()).toUri();
        return ResponseEntity.created(location).body(mapper.dossier(dossier));
    }

    @PutMapping("/{id}")
    public DossierResponse modifier(@PathVariable Long id, @Valid @RequestBody DossierRequest request) {
        DossierAdmin dossier = dossierService.modifier(id, request.nom(), request.categorie(),
                request.description());
        return mapper.dossier(dossier);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        dossierService.supprimer(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/documents/{documentId}")
    public ResponseEntity<Void> rattacherDocument(@PathVariable Long id, @PathVariable Long documentId) {
        dossierService.rattacherDocument(id, documentId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/documents/{documentId}")
    public ResponseEntity<Void> detacherDocument(@PathVariable Long id, @PathVariable Long documentId) {
        dossierService.detacherDocument(id, documentId);
        return ResponseEntity.noContent().build();
    }
}
