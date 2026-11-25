package fr.cytech.bde.service;

import fr.cytech.bde.domain.DocumentAdmin;
import fr.cytech.bde.domain.Evenement;
import fr.cytech.bde.domain.MembreBureau;
import fr.cytech.bde.repository.DocumentAdminRepository;
import fr.cytech.bde.repository.EvenementRepository;
import fr.cytech.bde.repository.LigneTresorerieRepository;
import fr.cytech.bde.repository.MembreBureauRepository;
import fr.cytech.bde.service.exception.BusinessException;
import fr.cytech.bde.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class MembreService {

    private final MembreBureauRepository membreRepository;
    private final EvenementRepository evenementRepository;
    private final DocumentAdminRepository documentRepository;
    private final LigneTresorerieRepository ligneRepository;

    public MembreService(MembreBureauRepository membreRepository,
                         EvenementRepository evenementRepository,
                         DocumentAdminRepository documentRepository,
                         LigneTresorerieRepository ligneRepository) {
        this.membreRepository = membreRepository;
        this.evenementRepository = evenementRepository;
        this.documentRepository = documentRepository;
        this.ligneRepository = ligneRepository;
    }

    public List<MembreBureau> rechercher(String recherche) {
        List<MembreBureau> membres = membreRepository.findAllByOrderByNomAscPrenomAsc();
        if (recherche == null || recherche.isBlank()) {
            return membres;
        }
        String terme = recherche.trim().toLowerCase(Locale.ROOT);
        return membres.stream()
                .filter(m -> contient(m.getNom(), terme)
                        || contient(m.getPrenom(), terme)
                        || contient(m.getEmail(), terme)
                        || contient(m.getRole(), terme)
                        || contient(m.getPromotion(), terme))
                .toList();
    }

    public List<MembreBureau> tous() {
        return membreRepository.findAllByOrderByNomAscPrenomAsc();
    }

    public MembreBureau trouver(Long id) {
        return membreRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.pour("Membre", id));
    }

    @Transactional
    public MembreBureau creer(String nom, String prenom, String email, String role, String promotion) {
        verifierEmailDisponible(email, null);
        MembreBureau membre = new MembreBureau(
                nettoyer(nom),
                nettoyer(prenom),
                normaliserEmail(email),
                nettoyer(role),
                nettoyer(promotion)
        );
        return membreRepository.save(membre);
    }

    @Transactional
    public MembreBureau modifier(Long id, String nom, String prenom, String email, String role, String promotion) {
        MembreBureau membre = trouver(id);
        verifierEmailDisponible(email, id);
        membre.setNom(nettoyer(nom));
        membre.setPrenom(nettoyer(prenom));
        membre.setEmail(normaliserEmail(email));
        membre.setRole(nettoyer(role));
        membre.setPromotion(nettoyer(promotion));
        return membreRepository.save(membre);
    }

    @Transactional
    public void supprimer(Long id) {
        MembreBureau membre = trouver(id);
        if (ligneRepository.existsByCreateurId(id)) {
            throw new BusinessException(
                    "Impossible de supprimer ce membre : il est créateur de lignes de trésorerie. "
                            + "Réaffectez ou supprimez d'abord ces lignes.");
        }

        // Les collections inverses ne pilotent pas les tables de jointure :
        // on retire donc le membre du côté propriétaire de chaque relation.
        for (Evenement evenement : new ArrayList<>(membre.getEvenementsOrganises())) {
            evenement.retirerOrganisateur(membre);
            evenementRepository.save(evenement);
        }
        for (DocumentAdmin document : new ArrayList<>(membre.getDocumentsPartages())) {
            document.retirerRelecteur(membre);
            documentRepository.save(document);
        }
        membreRepository.delete(membre);
    }

    public long compter() {
        return membreRepository.count();
    }

    private void verifierEmailDisponible(String email, Long idActuel) {
        if (email == null || email.isBlank()) {
            throw new BusinessException("L'adresse e-mail est obligatoire.");
        }
        membreRepository.findByEmailIgnoreCase(email.trim()).ifPresent(existant -> {
            if (idActuel == null || !existant.getId().equals(idActuel)) {
                throw new BusinessException("L'adresse e-mail " + email.trim() + " est déjà utilisée.");
            }
        });
    }

    private boolean contient(String valeur, String terme) {
        return valeur != null && valeur.toLowerCase(Locale.ROOT).contains(terme);
    }

    private String normaliserEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    private String nettoyer(String valeur) {
        return valeur == null ? null : valeur.trim();
    }
}
