package fr.cytech.bde.web.api;

import fr.cytech.bde.domain.DocumentAdmin;
import fr.cytech.bde.domain.DossierAdmin;
import fr.cytech.bde.domain.Evenement;
import fr.cytech.bde.domain.Habilitation;
import fr.cytech.bde.domain.LigneTresorerie;
import fr.cytech.bde.domain.MembreBureau;
import fr.cytech.bde.service.dto.BilanEvenement;
import fr.cytech.bde.service.dto.TresorerieSynthese;
import fr.cytech.bde.web.api.dto.BilanEvenementResponse;
import fr.cytech.bde.web.api.dto.DocumentResponse;
import fr.cytech.bde.web.api.dto.DocumentSummary;
import fr.cytech.bde.web.api.dto.DossierResponse;
import fr.cytech.bde.web.api.dto.DossierSummary;
import fr.cytech.bde.web.api.dto.EvenementResponse;
import fr.cytech.bde.web.api.dto.EvenementSummary;
import fr.cytech.bde.web.api.dto.HabilitationResponse;
import fr.cytech.bde.web.api.dto.LigneTresorerieResponse;
import fr.cytech.bde.web.api.dto.MembreResponse;
import fr.cytech.bde.web.api.dto.MembreSummary;
import fr.cytech.bde.web.api.dto.TresorerieSyntheseResponse;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class ApiMapper {

    public MembreResponse membre(MembreBureau membre) {
        return new MembreResponse(
                membre.getId(),
                membre.getNom(),
                membre.getPrenom(),
                membre.getEmail(),
                membre.getRole(),
                membre.getPromotion(),
                habilitation(membre.getHabilitation())
        );
    }

    public HabilitationResponse habilitation(Habilitation habilitation) {
        if (habilitation == null) {
            return null;
        }
        return new HabilitationResponse(
                habilitation.getId(),
                habilitation.getPlafondDepense(),
                habilitation.isActif(),
                habilitation.getDateAttribution()
        );
    }

    public MembreSummary membreSummary(MembreBureau membre) {
        return new MembreSummary(membre.getId(), membre.getNom(), membre.getPrenom(), membre.getRole());
    }

    public EvenementSummary evenementSummary(Evenement evenement) {
        if (evenement == null) {
            return null;
        }
        return new EvenementSummary(
                evenement.getId(),
                evenement.getTitre(),
                evenement.getDate(),
                evenement.getLieu(),
                evenement.getStatut()
        );
    }

    public EvenementResponse evenement(Evenement evenement, BilanEvenement bilan) {
        List<MembreSummary> organisateurs = evenement.getEquipeOrganisatrice().stream()
                .sorted(Comparator.comparing(MembreBureau::getNom).thenComparing(MembreBureau::getPrenom))
                .map(this::membreSummary)
                .toList();
        return new EvenementResponse(
                evenement.getId(),
                evenement.getTitre(),
                evenement.getDate(),
                evenement.getLieu(),
                evenement.getBudgetPrevisionnel(),
                evenement.getStatut(),
                organisateurs,
                bilan(bilan)
        );
    }

    public BilanEvenementResponse bilan(BilanEvenement bilan) {
        return new BilanEvenementResponse(
                bilan.getBudgetPrevisionnel(),
                bilan.getRecettesValidees(),
                bilan.getDepensesValidees(),
                bilan.getMontantEnAttente(),
                bilan.getBudgetReel(),
                bilan.getSoldeNet(),
                bilan.getEcartBudget(),
                bilan.getTauxConsommation(),
                bilan.getNombreLignes(),
                bilan.getNombreEnAttente()
        );
    }

    public DocumentSummary documentSummary(DocumentAdmin document) {
        if (document == null) {
            return null;
        }
        return new DocumentSummary(
                document.getId(),
                document.getTitre(),
                document.getType(),
                document.getStatut(),
                document.getDateCreation()
        );
    }

    public DossierSummary dossierSummary(DossierAdmin dossier) {
        if (dossier == null) {
            return null;
        }
        return new DossierSummary(dossier.getId(), dossier.getNom(), dossier.getCategorie());
    }

    public DocumentResponse document(DocumentAdmin document) {
        List<MembreSummary> relecteurs = document.getRelecteurs().stream()
                .sorted(Comparator.comparing(MembreBureau::getNom).thenComparing(MembreBureau::getPrenom))
                .map(this::membreSummary)
                .toList();
        return new DocumentResponse(
                document.getId(),
                document.getTitre(),
                document.getType(),
                document.getStatut(),
                document.getReference(),
                document.getDateCreation(),
                dossierSummary(document.getDossier()),
                relecteurs
        );
    }

    public DossierResponse dossier(DossierAdmin dossier) {
        List<DocumentSummary> documents = dossier.getDocuments().stream()
                .map(this::documentSummary)
                .toList();
        return new DossierResponse(
                dossier.getId(),
                dossier.getNom(),
                dossier.getCategorie(),
                dossier.getDescription(),
                documents
        );
    }

    public LigneTresorerieResponse ligne(LigneTresorerie ligne) {
        return new LigneTresorerieResponse(
                ligne.getId(),
                ligne.getLibelle(),
                ligne.getType(),
                ligne.getMontant(),
                ligne.getCategorie(),
                ligne.getStatut(),
                ligne.getDateOperation(),
                evenementSummary(ligne.getEvenement()),
                membreSummary(ligne.getCreateur()),
                documentSummary(ligne.getDocumentJustificatif())
        );
    }

    public TresorerieSyntheseResponse synthese(TresorerieSynthese synthese) {
        return new TresorerieSyntheseResponse(
                synthese.getSoldeGlobal(),
                synthese.getTotalRecettes(),
                synthese.getTotalDepenses(),
                synthese.getMontantEnAttente(),
                synthese.getNombreLignes(),
                synthese.getNombreEnAttente()
        );
    }
}
