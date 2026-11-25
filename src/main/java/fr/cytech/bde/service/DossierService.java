package fr.cytech.bde.service;

import fr.cytech.bde.domain.DocumentAdmin;
import fr.cytech.bde.domain.DossierAdmin;
import fr.cytech.bde.repository.DocumentAdminRepository;
import fr.cytech.bde.repository.DossierAdminRepository;
import fr.cytech.bde.service.exception.BusinessException;
import fr.cytech.bde.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class DossierService {

    private final DossierAdminRepository dossierRepository;
    private final DocumentAdminRepository documentRepository;

    public DossierService(DossierAdminRepository dossierRepository,
                          DocumentAdminRepository documentRepository) {
        this.dossierRepository = dossierRepository;
        this.documentRepository = documentRepository;
    }

    public List<DossierAdmin> rechercher(String recherche) {
        List<DossierAdmin> dossiers = dossierRepository.findAllByOrderByNomAsc();
        if (recherche == null || recherche.isBlank()) {
            return dossiers;
        }
        String terme = recherche.trim().toLowerCase(Locale.ROOT);
        return dossiers.stream()
                .filter(d -> contient(d.getNom(), terme)
                        || contient(d.getCategorie(), terme)
                        || contient(d.getDescription(), terme))
                .toList();
    }

    public List<DossierAdmin> tous() {
        return dossierRepository.findAllByOrderByNomAsc();
    }

    public DossierAdmin trouver(Long id) {
        return dossierRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.pour("Dossier", id));
    }

    @Transactional
    public DossierAdmin creer(String nom, String categorie, String description) {
        return dossierRepository.save(new DossierAdmin(nom.trim(), categorie.trim(), nettoyer(description)));
    }

    @Transactional
    public DossierAdmin modifier(Long id, String nom, String categorie, String description) {
        DossierAdmin dossier = trouver(id);
        dossier.setNom(nom.trim());
        dossier.setCategorie(categorie.trim());
        dossier.setDescription(nettoyer(description));
        return dossierRepository.save(dossier);
    }

    @Transactional
    public void supprimer(Long id) {
        DossierAdmin dossier = trouver(id);
        if (documentRepository.existsByDossierId(id)) {
            throw new BusinessException(
                    "Impossible de supprimer ce dossier : il contient encore des documents. "
                            + "Dissociez-les d'abord.");
        }
        dossierRepository.delete(dossier);
    }

    @Transactional
    public DossierAdmin rattacherDocument(Long dossierId, Long documentId) {
        DossierAdmin dossier = trouver(dossierId);
        DocumentAdmin document = documentRepository.findById(documentId)
                .orElseThrow(() -> ResourceNotFoundException.pour("Document", documentId));
        document.setDossier(dossier);
        documentRepository.save(document);
        return dossier;
    }

    @Transactional
    public DossierAdmin detacherDocument(Long dossierId, Long documentId) {
        DossierAdmin dossier = trouver(dossierId);
        DocumentAdmin document = documentRepository.findById(documentId)
                .orElseThrow(() -> ResourceNotFoundException.pour("Document", documentId));
        if (document.getDossier() == null || !document.getDossier().getId().equals(dossierId)) {
            throw new BusinessException("Ce document n'est pas rattaché à ce dossier.");
        }
        document.setDossier(null);
        documentRepository.save(document);
        return dossier;
    }

    public long compter() {
        return dossierRepository.count();
    }

    private boolean contient(String valeur, String terme) {
        return valeur != null && valeur.toLowerCase(Locale.ROOT).contains(terme);
    }

    private String nettoyer(String valeur) {
        return valeur == null || valeur.isBlank() ? null : valeur.trim();
    }
}
