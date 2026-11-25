package fr.cytech.bde.service;

import fr.cytech.bde.domain.Evenement;
import fr.cytech.bde.domain.LigneTresorerie;
import fr.cytech.bde.domain.MembreBureau;
import fr.cytech.bde.domain.enums.StatutEvenement;
import fr.cytech.bde.domain.enums.StatutLigne;
import fr.cytech.bde.domain.enums.TypeLigne;
import fr.cytech.bde.repository.EvenementRepository;
import fr.cytech.bde.repository.LigneTresorerieRepository;
import fr.cytech.bde.repository.MembreBureauRepository;
import fr.cytech.bde.service.dto.BilanEvenement;
import fr.cytech.bde.service.exception.BusinessException;
import fr.cytech.bde.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class EvenementService {

    private final EvenementRepository evenementRepository;
    private final LigneTresorerieRepository ligneRepository;
    private final MembreBureauRepository membreRepository;

    public EvenementService(EvenementRepository evenementRepository,
                            LigneTresorerieRepository ligneRepository,
                            MembreBureauRepository membreRepository) {
        this.evenementRepository = evenementRepository;
        this.ligneRepository = ligneRepository;
        this.membreRepository = membreRepository;
    }

    public List<Evenement> rechercher(String recherche, StatutEvenement statut) {
        String terme = recherche == null ? "" : recherche.trim().toLowerCase(Locale.ROOT);
        return evenementRepository.findAllByOrderByDateDescIdDesc().stream()
                .filter(e -> statut == null || e.getStatut() == statut)
                .filter(e -> terme.isBlank()
                        || contient(e.getTitre(), terme)
                        || contient(e.getLieu(), terme)
                        || contient(e.getStatut().getLibelle(), terme))
                .toList();
    }

    public List<Evenement> tous() {
        return evenementRepository.findAllByOrderByDateDescIdDesc();
    }

    public List<Evenement> prochains(int limite) {
        LocalDate aujourdHui = LocalDate.now();
        return evenementRepository.findAllByOrderByDateDescIdDesc().stream()
                .filter(e -> !e.getDate().isBefore(aujourdHui) && e.getStatut() != StatutEvenement.ANNULE)
                .sorted(Comparator.comparing(Evenement::getDate))
                .limit(limite)
                .toList();
    }

    public Evenement trouver(Long id) {
        return evenementRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.pour("Événement", id));
    }

    @Transactional
    public Evenement creer(String titre, LocalDate date, String lieu, BigDecimal budgetPrevisionnel,
                           StatutEvenement statut) {
        Evenement evenement = new Evenement(
                titre.trim(),
                date,
                lieu.trim(),
                Montants.arrondir(budgetPrevisionnel),
                statut == null ? StatutEvenement.PLANIFIE : statut
        );
        return evenementRepository.save(evenement);
    }

    @Transactional
    public Evenement modifier(Long id, String titre, LocalDate date, String lieu,
                              BigDecimal budgetPrevisionnel, StatutEvenement statut) {
        Evenement evenement = trouver(id);
        evenement.setTitre(titre.trim());
        evenement.setDate(date);
        evenement.setLieu(lieu.trim());
        evenement.setBudgetPrevisionnel(Montants.arrondir(budgetPrevisionnel));
        evenement.setStatut(statut == null ? StatutEvenement.PLANIFIE : statut);
        return evenementRepository.save(evenement);
    }

    /**
     * La suppression est refusée dès qu'une ligne validée existe.
     * Les lignes non validées sont détachées pour préserver leur historique comptable.
     */
    @Transactional
    public void supprimer(Long id) {
        Evenement evenement = trouver(id);
        if (ligneRepository.existsByEvenementIdAndStatut(id, StatutLigne.VALIDEE)) {
            throw new BusinessException(
                    "Impossible de supprimer cet événement : il contient des lignes de trésorerie validées. "
                            + "Rejetez ces lignes ou conservez l'événement.");
        }
        List<LigneTresorerie> lignes = ligneRepository.findByEvenementIdOrderByDateOperationDescIdDesc(id);
        lignes.forEach(ligne -> ligne.setEvenement(null));
        evenementRepository.delete(evenement);
    }

    @Transactional
    public Evenement ajouterOrganisateur(Long evenementId, Long membreId) {
        Evenement evenement = trouver(evenementId);
        MembreBureau membre = membreRepository.findById(membreId)
                .orElseThrow(() -> ResourceNotFoundException.pour("Membre", membreId));
        evenement.ajouterOrganisateur(membre);
        return evenementRepository.save(evenement);
    }

    @Transactional
    public Evenement retirerOrganisateur(Long evenementId, Long membreId) {
        Evenement evenement = trouver(evenementId);
        MembreBureau membre = membreRepository.findById(membreId)
                .orElseThrow(() -> ResourceNotFoundException.pour("Membre", membreId));
        evenement.retirerOrganisateur(membre);
        return evenementRepository.save(evenement);
    }

    public BilanEvenement bilan(Long evenementId) {
        Evenement evenement = trouver(evenementId);
        BigDecimal recettes = Montants.nonNull(
                ligneRepository.sumMontantForEvenement(evenementId, TypeLigne.RECETTE, StatutLigne.VALIDEE));
        BigDecimal depenses = Montants.nonNull(
                ligneRepository.sumMontantForEvenement(evenementId, TypeLigne.DEPENSE, StatutLigne.VALIDEE));
        BigDecimal enAttente = Montants.nonNull(
                ligneRepository.sumMontantForEvenementByStatut(evenementId, StatutLigne.EN_ATTENTE));
        BigDecimal budget = Montants.nonNull(evenement.getBudgetPrevisionnel());
        BigDecimal soldeNet = recettes.subtract(depenses);
        BigDecimal ecart = budget.subtract(depenses);

        int taux = 0;
        if (budget.signum() > 0) {
            taux = depenses.multiply(BigDecimal.valueOf(100))
                    .divide(budget, 0, RoundingMode.HALF_UP)
                    .min(BigDecimal.valueOf(100))
                    .intValue();
        } else if (depenses.signum() > 0) {
            taux = 100;
        }

        return new BilanEvenement(
                budget,
                recettes,
                depenses,
                enAttente,
                depenses,
                soldeNet,
                ecart,
                taux,
                ligneRepository.countByEvenementId(evenementId),
                ligneRepository.countByEvenementIdAndStatut(evenementId, StatutLigne.EN_ATTENTE)
        );
    }

    public long compter() {
        return evenementRepository.count();
    }

    private boolean contient(String valeur, String terme) {
        return valeur != null && valeur.toLowerCase(Locale.ROOT).contains(terme);
    }
}
