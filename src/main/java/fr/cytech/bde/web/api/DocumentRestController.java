package fr.cytech.bde.web.api;

import fr.cytech.bde.domain.DocumentAdmin;
import fr.cytech.bde.domain.enums.StatutDocument;
import fr.cytech.bde.domain.enums.TypeDocument;
import fr.cytech.bde.service.DocumentService;
import fr.cytech.bde.web.api.dto.ChangementStatutRequest;
import fr.cytech.bde.web.api.dto.DocumentRequest;
import fr.cytech.bde.web.api.dto.DocumentResponse;
import fr.cytech.bde.web.api.dto.DocumentUpdateRequest;
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
@RequestMapping("/api/v1/documents")
public class DocumentRestController {

    private final DocumentService documentService;
    private final ApiMapper mapper;

    public DocumentRestController(DocumentService documentService, ApiMapper mapper) {
        this.documentService = documentService;
        this.mapper = mapper;
    }

    @GetMapping
    public List<DocumentResponse> lister(@RequestParam(required = false) String q,
                                         @RequestParam(required = false) TypeDocument type,
                                         @RequestParam(required = false) StatutDocument statut,
                                         @RequestParam(required = false) Long dossierId) {
        return documentService.rechercher(q, type, statut, dossierId).stream()
                .map(mapper::document)
                .toList();
    }

    @GetMapping("/{id}")
    public DocumentResponse detail(@PathVariable Long id) {
        return mapper.document(documentService.trouver(id));
    }

    @PostMapping
    public ResponseEntity<DocumentResponse> creer(@Valid @RequestBody DocumentRequest request) {
        DocumentAdmin document = documentService.creerComplet(
                request.titre(),
                request.type(),
                request.reference(),
                request.dateCreation(),
                request.dossierId(),
                request.relecteurIds()
        );
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(document.getId()).toUri();
        return ResponseEntity.created(location).body(mapper.document(document));
    }

    @PutMapping("/{id}")
    public DocumentResponse modifier(@PathVariable Long id,
                                     @Valid @RequestBody DocumentUpdateRequest request) {
        DocumentAdmin document = documentService.modifierComplet(
                id,
                request.titre(),
                request.type(),
                request.reference(),
                request.dateCreation(),
                request.dossierId(),
                request.statut(),
                request.relecteurIds()
        );
        return mapper.document(document);
    }

    @PutMapping("/{id}/statut")
    public DocumentResponse changerStatut(@PathVariable Long id,
                                          @Valid @RequestBody ChangementStatutRequest request) {
        return mapper.document(documentService.changerStatut(id, request.statut()));
    }

    @PostMapping("/{id}/relecteurs/{membreId}")
    public ResponseEntity<Void> ajouterRelecteur(@PathVariable Long id, @PathVariable Long membreId) {
        documentService.ajouterRelecteur(id, membreId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/relecteurs/{membreId}")
    public ResponseEntity<Void> retirerRelecteur(@PathVariable Long id, @PathVariable Long membreId) {
        documentService.retirerRelecteur(id, membreId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        documentService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
