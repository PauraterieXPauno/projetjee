package fr.cytech.bde.service;

import fr.cytech.bde.domain.DocumentAdmin;
import fr.cytech.bde.domain.Evenement;
import fr.cytech.bde.domain.LigneTresorerie;
import fr.cytech.bde.domain.MembreBureau;
import fr.cytech.bde.domain.enums.StatutLigne;
import fr.cytech.bde.domain.enums.TypeLigne;
import fr.cytech.bde.repository.DocumentAdminRepository;
import fr.cytech.bde.repository.EvenementRepository;
import fr.cytech.bde.repository.LigneTresorerieRepository;
import fr.cytech.bde.repository.MembreBureauRepository;
import fr.cytech.bde.service.dto.TresorerieSynthese;
import fr.cytech.bde.service.exception.BusinessException;
import fr.cytech.bde.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class TresorerieService {

    private final LigneTresorerieRepository ligneRepository;
    private final MembreBureauRepository membreRepository;
    private final EvenementRepository evenementRepository;
    private final DocumentAdminRepository documentRepository;
    private final HabilitationService habilitationService;

    public TresorerieService(LigneTresorerieRepository ligneRepository,
                             MembreBureauRepository membreRepository,
                             EvenementRepository evenementRepository,
                             DocumentAdminRepository documentRepository,
                             HabilitationService habilitationService) {
        this.ligneRepository = ligneRepository;
        this.membreRepository = membreRepository;
        this.evenementRepository = evenementRepository;
        this.documentRepository = documentRepository;
        this.habilitationService = habilitationService;
    }

    public List<LigneTresorerie> rechercher(String recherche, TypeLigne type,
                                            StatutLigne statut, Long evenementId) {
        String terme = recherche == null ? "" : recherche.trim().toLowerCase(Locale.ROOT);
        return ligneRepository.findAllByOrderByDateOperationDescIdDesc().stream()
                .filter(l -> type == null || l.getType() == type)
                .filter(l -> statut == null || l.getStatut() == statut)
                .filter(l -> evenementId == null
                        || (l.getEvenement() != null && evenementId.equals(l.getEvenement().getId())))
                .filter(l -> terme.isBlank()
                        || contient(l.getLibelle(), terme)
                        || contient(l.getCategorie(), terme)
                        || contient(l.getEvenement() == null ? null : l.getEvenement().getTitre(), terme)
                        || contient(l.getCreateur().getNomComplet(), terme))
                .toList();
    }

    public List<LigneTresorerie> toutes() {
        return ligneRepository.findAllByOrderByDateOperationDescIdDesc();
    }

    public List<LigneTresorerie> dernieres(int limite) {
        return ligneRepository.findAllByOrderByDateOperationDescIdDesc().stream().limit(limite).toList();
    }

    public List<LigneTresorerie> lignesDEvenement(Long evenementId) {
        return ligneRepository.findByEvenementIdOrderByDateOperationDescIdDesc(evenementId);
    }

    public List<LigneTresorerie> lignesNonRattachees() {
        return ligneRepository.findByEvenementIsNullOrderByDateOperationDescIdDesc();
    }

    public List<LigneTresorerie> lignesDuJustificatif(Long documentId) {
        return ligneRepository.findByDocumentJustificatifId(documentId);
    }

    public List<LigneTresorerie> lignesDuCreateur(Long membreId) {
        return ligneRepository.findByCreateurIdOrderByDateOperationDescIdDesc(membreId);
    }

    public LigneTresorerie trouver(Long id) {
        return ligneRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.pour("Ligne de trésorerie", id));
    }

    @Transactional
    public LigneTresorerie creer(LigneTresorerieCommand commande) {
        LigneTresorerie ligne = new LigneTresorerie();
        appliquerChamps(ligne, commande);
        ligne.setStatut(statutInitial(commande.type(), commande.montant(), ligne.getCreateur(),
                commande.validationExplicite()));
        return ligneRepository.save(ligne);
    }

    /**
     * Une ligne rejetée le reste tant qu'une validation explicite n'est pas demandée.
     * Les autres lignes sont réévaluées après modification (plafond, habilitation, montant).
     */
    @Transactional
    public LigneTresorerie modifier(Long id, LigneTresorerieCommand commande) {
        LigneTresorerie ligne = trouver(id);
        appliquerChamps(ligne, commande);
        if (ligne.getStatut() == StatutLigne.REJETEE && !commande.validationExplicite()) {
            return ligneRepository.save(ligne);
        }
        ligne.setStatut(statutInitial(commande.type(), commande.montant(), ligne.getCreateur(),
                commande.validationExplicite()));
        return ligneRepository.save(ligne);
    }

    @Transactional
    public LigneTresorerie valider(Long id) {
        LigneTresorerie ligne = trouver(id);
        ligne.setStatut(StatutLigne.VALIDEE);
        return ligneRepository.save(ligne);
    }

    @Transactional
    public LigneTresorerie rejeter(Long id) {
        LigneTresorerie ligne = trouver(id);
        ligne.setStatut(StatutLigne.REJETEE);
        return ligneRepository.save(ligne);
    }

    @Transactional
    public LigneTresorerie rattacherEvenement(Long ligneId, Long evenementId) {
        LigneTresorerie ligne = trouver(ligneId);
        Evenement evenement = evenementRepository.findById(evenementId)
                .orElseThrow(() -> ResourceNotFoundException.pour("Événement", evenementId));
        ligne.setEvenement(evenement);
        return ligneRepository.save(ligne);
    }

    @Transactional
    public LigneTresorerie detacherEvenement(Long ligneId) {
        LigneTresorerie ligne = trouver(ligneId);
        ligne.setEvenement(null);
        return ligneRepository.save(ligne);
    }

    @Transactional
    public void supprimer(Long id) {
        LigneTresorerie ligne = trouver(id);
        ligneRepository.delete(ligne);
    }

    public TresorerieSynthese synthese() {
        BigDecimal recettes = Montants.nonNull(
                ligneRepository.sumMontantByTypeAndStatut(TypeLigne.RECETTE, StatutLigne.VALIDEE));
        BigDecimal depenses = Montants.nonNull(
                ligneRepository.sumMontantByTypeAndStatut(TypeLigne.DEPENSE, StatutLigne.VALIDEE));
        BigDecimal enAttente = Montants.nonNull(
                ligneRepository.sumMontantByStatut(StatutLigne.EN_ATTENTE));
        return new TresorerieSynthese(
                recettes.subtract(depenses),
                recettes,
                depenses,
                enAttente,
                ligneRepository.count(),
                ligneRepository.countByStatut(StatutLigne.EN_ATTENTE)
        );
    }

    private void appliquerChamps(LigneTresorerie ligne, LigneTresorerieCommand commande) {
        if (commande.montant() == null || commande.montant().signum() <= 0) {
            throw new BusinessException("Le montant doit être strictement positif.");
        }
        if (commande.type() == null) {
            throw new BusinessException("Le type de ligne est obligatoire.");
        }
        if (commande.createurId() == null) {
            throw new BusinessException("Le créateur de la ligne est obligatoire.");
        }

        MembreBureau createur = membreRepository.findById(commande.createurId())
                .orElseThrow(() -> ResourceNotFoundException.pour("Membre", commande.createurId()));
        Evenement evenement = null;
        if (commande.evenementId() != null) {
            evenement = evenementRepository.findById(commande.evenementId())
                    .orElseThrow(() -> ResourceNotFoundException.pour("Événement", commande.evenementId()));
        }
        DocumentAdmin justificatif = null;
        if (commande.documentJustificatifId() != null) {
            justificatif = documentRepository.findById(commande.documentJustificatifId())
                    .orElseThrow(() -> ResourceNotFoundException.pour(
                            "Document justificatif", commande.documentJustificatifId()));
        }

        ligne.setLibelle(commande.libelle().trim());
        ligne.setType(commande.type());
        ligne.setMontant(Montants.arrondir(commande.montant()));
        ligne.setCategorie(commande.categorie().trim());
        ligne.setDateOperation(commande.dateOperation() == null ? LocalDate.now() : commande.dateOperation());
        ligne.setCreateur(createur);
        ligne.setEvenement(evenement);
        ligne.setDocumentJustificatif(justificatif);
    }

    private StatutLigne statutInitial(TypeLigne type, BigDecimal montant, MembreBureau createur,
                                      boolean validationExplicite) {
        if (validationExplicite) {
            return StatutLigne.VALIDEE;
        }
        if (type == TypeLigne.RECETTE) {
            return StatutLigne.VALIDEE;
        }
        return habilitationService.estAutorise(createur, montant)
                ? StatutLigne.VALIDEE
                : StatutLigne.EN_ATTENTE;
    }

    private boolean contient(String valeur, String terme) {
        return valeur != null && valeur.toLowerCase(Locale.ROOT).contains(terme);
    }
}
