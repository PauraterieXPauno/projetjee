package fr.cytech.bde.service;

import fr.cytech.bde.domain.Habilitation;
import fr.cytech.bde.domain.MembreBureau;
import fr.cytech.bde.repository.HabilitationRepository;
import fr.cytech.bde.repository.MembreBureauRepository;
import fr.cytech.bde.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
public class HabilitationService {

    private final MembreBureauRepository membreRepository;
    private final HabilitationRepository habilitationRepository;

    public HabilitationService(MembreBureauRepository membreRepository,
                               HabilitationRepository habilitationRepository) {
        this.membreRepository = membreRepository;
        this.habilitationRepository = habilitationRepository;
    }

    public Habilitation trouverParMembre(Long membreId) {
        MembreBureau membre = membreRepository.findById(membreId)
                .orElseThrow(() -> ResourceNotFoundException.pour("Membre", membreId));
        return membre.getHabilitation();
    }

    /**
     * Crée l'habilitation si elle n'existe pas, sinon la met à jour.
     * La relation 1-1 est portée par MembreBureau (cascade + orphanRemoval).
     */
    @Transactional
    public Habilitation enregistrer(Long membreId, BigDecimal plafondDepense, boolean actif,
                                    LocalDate dateAttribution) {
        MembreBureau membre = membreRepository.findById(membreId)
                .orElseThrow(() -> ResourceNotFoundException.pour("Membre", membreId));

        Habilitation habilitation = membre.getHabilitation();
        if (habilitation == null) {
            habilitation = new Habilitation();
            membre.setHabilitation(habilitation);
        }
        habilitation.setPlafondDepense(Montants.arrondir(plafondDepense));
        habilitation.setActif(actif);
        habilitation.setDateAttribution(dateAttribution == null ? LocalDate.now() : dateAttribution);
        return habilitationRepository.save(habilitation);
    }

    @Transactional
    public void dissocier(Long membreId) {
        MembreBureau membre = membreRepository.findById(membreId)
                .orElseThrow(() -> ResourceNotFoundException.pour("Membre", membreId));
        if (membre.getHabilitation() != null) {
            membre.setHabilitation(null);
            membreRepository.save(membre);
        }
    }

    public boolean estAutorise(MembreBureau membre, BigDecimal montant) {
        Habilitation habilitation = membre.getHabilitation();
        return habilitation != null
                && habilitation.isActif()
                && habilitation.getPlafondDepense() != null
                && montant != null
                && montant.compareTo(habilitation.getPlafondDepense()) <= 0;
    }
}
