package fr.cytech.bde.service;

import fr.cytech.bde.domain.DocumentAdmin;
import fr.cytech.bde.domain.DossierAdmin;
import fr.cytech.bde.domain.MembreBureau;
import fr.cytech.bde.domain.enums.StatutDocument;
import fr.cytech.bde.domain.enums.TypeDocument;
import fr.cytech.bde.repository.DocumentAdminRepository;
import fr.cytech.bde.repository.DossierAdminRepository;
import fr.cytech.bde.repository.LigneTresorerieRepository;
import fr.cytech.bde.repository.MembreBureauRepository;
import fr.cytech.bde.service.exception.BusinessException;
import fr.cytech.bde.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class DocumentService {

    private final DocumentAdminRepository documentRepository;
    private final DossierAdminRepository dossierRepository;
    private final MembreBureauRepository membreRepository;
    private final LigneTresorerieRepository ligneRepository;

    public DocumentService(DocumentAdminRepository documentRepository,
                           DossierAdminRepository dossierRepository,
                           MembreBureauRepository membreRepository,
                           LigneTresorerieRepository ligneRepository) {
        this.documentRepository = documentRepository;
        this.dossierRepository = dossierRepository;
        this.membreRepository = membreRepository;
        this.ligneRepository = ligneRepository;
    }

    public List<DocumentAdmin> rechercher(String recherche, TypeDocument type,
                                          StatutDocument statut, Long dossierId) {
        String terme = recherche == null ? "" : recherche.trim().toLowerCase(Locale.ROOT);
        return documentRepository.findAllByOrderByDateCreationDescIdDesc().stream()
                .filter(d -> type == null || d.getType() == type)
                .filter(d -> statut == null || d.getStatut() == statut)
                .filter(d -> dossierId == null || (d.getDossier() != null && dossierId.equals(d.getDossier().getId())))
                .filter(d -> terme.isBlank()
                        || contient(d.getTitre(), terme)
                        || contient(d.getReference(), terme)
                        || contient(d.getDossier() == null ? null : d.getDossier().getNom(), terme))
                .toList();
    }

    public List<DocumentAdmin> tous() {
        return documentRepository.findAllByOrderByDateCreationDescIdDesc();
    }

    public List<DocumentAdmin> documentsSansDossier() {
        return documentRepository.findByDossierIsNullOrderByDateCreationDescIdDesc();
    }

    public List<DocumentAdmin> documentsDuDossier(Long dossierId) {
        return documentRepository.findByDossierIdOrderByDateCreationDescIdDesc(dossierId);
    }

    public DocumentAdmin trouver(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.pour("Document", id));
    }

    @Transactional
    public DocumentAdmin creer(String titre, TypeDocument type, String reference,
                               LocalDate dateCreation, Long dossierId) {
        DocumentAdmin document = new DocumentAdmin(
                titre.trim(),
                type,
                nettoyer(reference),
                dateCreation == null ? LocalDate.now() : dateCreation
        );
        document.setStatut(StatutDocument.BROUILLON);
        if (dossierId != null) {
            document.setDossier(dossierRepository.findById(dossierId)
                    .orElseThrow(() -> ResourceNotFoundException.pour("Dossier", dossierId)));
        }
        return documentRepository.save(document);
    }

    @Transactional
    public DocumentAdmin creerComplet(String titre, TypeDocument type, String reference,
                                      LocalDate dateCreation, Long dossierId, Set<Long> relecteurIds) {
        DocumentAdmin document = creer(titre, type, reference, dateCreation, dossierId);
        appliquerRelecteurs(document, relecteurIds);
        return documentRepository.save(document);
    }

    @Transactional
    public DocumentAdmin modifier(Long id, String titre, TypeDocument type, String reference,
                                  LocalDate dateCreation, Long dossierId) {
        DocumentAdmin document = trouver(id);
        appliquerChamps(document, titre, type, reference, dateCreation, dossierId);
        return documentRepository.save(document);
    }

    @Transactional
    public DocumentAdmin modifierComplet(Long id, String titre, TypeDocument type, String reference,
                                         LocalDate dateCreation, Long dossierId,
                                         StatutDocument statutCible, Set<Long> relecteurIds) {
        DocumentAdmin document = trouver(id);
        appliquerChamps(document, titre, type, reference, dateCreation, dossierId);
        if (statutCible != null) {
            appliquerTransition(document, statutCible);
        }
        appliquerRelecteurs(document, relecteurIds);
        return documentRepository.save(document);
    }

    /**
     * Le cycle de vie est strictement séquentiel :
     * BROUILLON -> EN_VALIDATION -> VALIDE -> ARCHIVE.
     */
    @Transactional
    public DocumentAdmin changerStatut(Long id, StatutDocument cible) {
        DocumentAdmin document = trouver(id);
        appliquerTransition(document, cible);
        return documentRepository.save(document);
    }

    @Transactional
    public DocumentAdmin associerDossier(Long documentId, Long dossierId) {
        DocumentAdmin document = trouver(documentId);
        DossierAdmin dossier = dossierRepository.findById(dossierId)
                .orElseThrow(() -> ResourceNotFoundException.pour("Dossier", dossierId));
        document.setDossier(dossier);
        return documentRepository.save(document);
    }

    @Transactional
    public DocumentAdmin dissocierDossier(Long documentId) {
        DocumentAdmin document = trouver(documentId);
        document.setDossier(null);
        return documentRepository.save(document);
    }

    @Transactional
    public DocumentAdmin ajouterRelecteur(Long documentId, Long membreId) {
        DocumentAdmin document = trouver(documentId);
        MembreBureau membre = membreRepository.findById(membreId)
                .orElseThrow(() -> ResourceNotFoundException.pour("Membre", membreId));
        document.ajouterRelecteur(membre);
        return documentRepository.save(document);
    }

    @Transactional
    public DocumentAdmin retirerRelecteur(Long documentId, Long membreId) {
        DocumentAdmin document = trouver(documentId);
        MembreBureau membre = membreRepository.findById(membreId)
                .orElseThrow(() -> ResourceNotFoundException.pour("Membre", membreId));
        document.retirerRelecteur(membre);
        return documentRepository.save(document);
    }

    @Transactional
    public DocumentAdmin remplacerRelecteurs(Long documentId, Set<Long> membreIds) {
        DocumentAdmin document = trouver(documentId);
        appliquerRelecteurs(document, membreIds);
        return documentRepository.save(document);
    }

    @Transactional
    public void supprimer(Long id) {
        DocumentAdmin document = trouver(id);
        if (ligneRepository.existsByDocumentJustificatifId(id)) {
            throw new BusinessException(
                    "Impossible de supprimer ce document : il sert de justificatif à des lignes de trésorerie.");
        }
        documentRepository.delete(document);
    }

    public long compter() {
        return documentRepository.count();
    }

    private boolean contient(String valeur, String terme) {
        return valeur != null && valeur.toLowerCase(Locale.ROOT).contains(terme);
    }

    private void appliquerChamps(DocumentAdmin document, String titre, TypeDocument type, String reference,
                                 LocalDate dateCreation, Long dossierId) {
        document.setTitre(titre.trim());
        document.setType(type);
        document.setReference(nettoyer(reference));
        document.setDateCreation(dateCreation == null ? document.getDateCreation() : dateCreation);
        document.setDossier(dossierId == null ? null : dossierRepository.findById(dossierId)
                .orElseThrow(() -> ResourceNotFoundException.pour("Dossier", dossierId)));
    }

    private void appliquerTransition(DocumentAdmin document, StatutDocument cible) {
        StatutDocument actuel = document.getStatut();
        if (cible == null) {
            throw new BusinessException("Le statut cible est obligatoire.");
        }
        if (cible == actuel) {
            return;
        }
        if (actuel.suivant() != cible) {
            throw new BusinessException(
                    "Transition de statut interdite : " + actuel.getLibelle()
                            + " -> " + cible.getLibelle() + ". Seule l'étape suivante est autorisée.");
        }
        document.setStatut(cible);
    }

    private void appliquerRelecteurs(DocumentAdmin document, Set<Long> membreIds) {
        if (membreIds == null) {
            return;
        }
        List<MembreBureau> actuels = List.copyOf(document.getRelecteurs());
        actuels.forEach(document::retirerRelecteur);
        membreIds.forEach(membreId -> {
            MembreBureau membre = membreRepository.findById(membreId)
                    .orElseThrow(() -> ResourceNotFoundException.pour("Membre", membreId));
            document.ajouterRelecteur(membre);
        });
    }

    private String nettoyer(String valeur) {
        return valeur == null || valeur.isBlank() ? null : valeur.trim();
    }
}
