package fr.cytech.bde.config;

import fr.cytech.bde.domain.DocumentAdmin;
import fr.cytech.bde.domain.DossierAdmin;
import fr.cytech.bde.domain.Evenement;
import fr.cytech.bde.domain.Habilitation;
import fr.cytech.bde.domain.LigneTresorerie;
import fr.cytech.bde.domain.MembreBureau;
import fr.cytech.bde.domain.enums.StatutDocument;
import fr.cytech.bde.domain.enums.StatutEvenement;
import fr.cytech.bde.domain.enums.StatutLigne;
import fr.cytech.bde.domain.enums.TypeDocument;
import fr.cytech.bde.domain.enums.TypeLigne;
import fr.cytech.bde.repository.DocumentAdminRepository;
import fr.cytech.bde.repository.DossierAdminRepository;
import fr.cytech.bde.repository.EvenementRepository;
import fr.cytech.bde.repository.LigneTresorerieRepository;
import fr.cytech.bde.repository.MembreBureauRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataInitializer implements CommandLineRunner {

    private final MembreBureauRepository membreRepository;
    private final DossierAdminRepository dossierRepository;
    private final DocumentAdminRepository documentRepository;
    private final EvenementRepository evenementRepository;
    private final LigneTresorerieRepository ligneRepository;

    public DataInitializer(MembreBureauRepository membreRepository,
                           DossierAdminRepository dossierRepository,
                           DocumentAdminRepository documentRepository,
                           EvenementRepository evenementRepository,
                           LigneTresorerieRepository ligneRepository) {
        this.membreRepository = membreRepository;
        this.dossierRepository = dossierRepository;
        this.documentRepository = documentRepository;
        this.evenementRepository = evenementRepository;
        this.ligneRepository = ligneRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (membreRepository.count() > 0) {
            return;
        }

        LocalDate aujourdHui = LocalDate.now();

        // --- Membres et habilitations (relation 1-1) ---
        MembreBureau lea = membre("Bernard", "Léa", "lea.bernard@bde-cytech.fr", "Présidente", "TC 2026");
        MembreBureau nathan = membre("Petit", "Nathan", "nathan.petit@bde-cytech.fr", "Trésorier", "TC 2026");
        MembreBureau ines = membre("Moreau", "Inès", "ines.moreau@bde-cytech.fr", "Secrétaire générale", "ING 2027");
        MembreBureau hugo = membre("Dubois", "Hugo", "hugo.dubois@bde-cytech.fr", "Vice-président événements", "TC 2026");
        MembreBureau sarah = membre("Leroy", "Sarah", "sarah.leroy@bde-cytech.fr", "Responsable communication", "ING 2027");
        MembreBureau adam = membre("Fontaine", "Adam", "adam.fontaine@bde-cytech.fr", "Responsable partenariats", "TC 2025");

        lea.setHabilitation(new Habilitation(new BigDecimal("2000.00"), true, aujourdHui.minusMonths(4)));
        nathan.setHabilitation(new Habilitation(new BigDecimal("1500.00"), true, aujourdHui.minusMonths(4)));
        ines.setHabilitation(new Habilitation(new BigDecimal("500.00"), true, aujourdHui.minusMonths(3)));
        hugo.setHabilitation(new Habilitation(new BigDecimal("800.00"), true, aujourdHui.minusMonths(3)));
        adam.setHabilitation(new Habilitation(new BigDecimal("1000.00"), false, aujourdHui.minusMonths(8)));
        // Sarah n'a volontairement pas d'habilitation : ses dépenses passent en attente.

        membreRepository.saveAll(java.util.List.of(lea, nathan, ines, hugo, sarah, adam));

        // --- Dossiers et documents (1-N + N-N) ---
        DossierAdmin gouvernance = dossier("Statuts & gouvernance", "Administration",
                "Documents fondateurs et vie associative du BDE.");
        DossierAdmin comptabilite = dossier("Comptabilité 2025-2026", "Trésorerie",
                "Justificatifs, devis et factures de l'exercice en cours.");
        DossierAdmin partenariats = dossier("Partenariats", "Développement",
                "Conventions et dossiers de sponsoring.");
        DossierAdmin evenements = dossier("Événements", "Organisation",
                "Autorisations et documents opérationnels des événements.");
        DossierAdmin reunions = dossier("PV & réunions", "Vie interne",
                "Comptes rendus des réunions du bureau.");
        DossierAdmin archives = dossier("Archives 2024", "Archives",
                "Dossier vide destiné aux documents clos de l'exercice précédent.");
        dossierRepository.saveAll(java.util.List.of(
                gouvernance, comptabilite, partenariats, evenements, reunions, archives));

        DocumentAdmin statuts = document("Statuts du BDE", TypeDocument.STATUTS, gouvernance,
                "https://drive.bde-cytech.fr/statuts-2026.pdf", StatutDocument.VALIDE, aujourdHui.minusDays(320));
        statuts.ajouterRelecteur(lea);
        statuts.ajouterRelecteur(ines);

        DocumentAdmin pvAg = document("PV de l'assemblée générale", TypeDocument.PV, reunions,
                "https://drive.bde-cytech.fr/pv-ag-2025.pdf", StatutDocument.ARCHIVE, aujourdHui.minusDays(210));
        pvAg.ajouterRelecteur(lea);
        pvAg.ajouterRelecteur(ines);

        DocumentAdmin reglement = document("Règlement intérieur du BDE", TypeDocument.STATUTS, gouvernance,
                "https://drive.bde-cytech.fr/reglement-interieur.pdf", StatutDocument.VALIDE, aujourdHui.minusDays(120));
        reglement.ajouterRelecteur(ines);

        DocumentAdmin factureTraiteur = document("Facture traiteur - soirée d'intégration", TypeDocument.FACTURE,
                comptabilite, "https://drive.bde-cytech.fr/factures/traiteur-integration.pdf",
                StatutDocument.VALIDE, aujourdHui.minusDays(12));
        factureTraiteur.ajouterRelecteur(nathan);

        DocumentAdmin devisDj = document("Devis DJ - soirée d'intégration", TypeDocument.DEVIS, comptabilite,
                "https://drive.bde-cytech.fr/devis/dj-integration.pdf", StatutDocument.EN_VALIDATION,
                aujourdHui.minusDays(18));
        devisDj.ajouterRelecteur(nathan);
        devisDj.ajouterRelecteur(hugo);

        DocumentAdmin factureTransportSki = document("Facture transport - week-end ski", TypeDocument.FACTURE,
                comptabilite, "https://drive.bde-cytech.fr/factures/transport-ski.pdf",
                StatutDocument.VALIDE, aujourdHui.minusDays(100));
        factureTransportSki.ajouterRelecteur(nathan);

        DocumentAdmin factureChalet = document("Facture hébergement - week-end ski", TypeDocument.FACTURE,
                comptabilite, "https://drive.bde-cytech.fr/factures/chalet-ski.pdf",
                StatutDocument.VALIDE, aujourdHui.minusDays(102));
        factureChalet.ajouterRelecteur(nathan);
        factureChalet.ajouterRelecteur(hugo);

        DocumentAdmin convention = document("Convention partenaire bancaire", TypeDocument.AUTORISATION,
                partenariats, "https://drive.bde-cytech.fr/partenariats/convention-banque.pdf",
                StatutDocument.VALIDE, aujourdHui.minusDays(65));
        convention.ajouterRelecteur(lea);
        convention.ajouterRelecteur(adam);

        DocumentAdmin autorisationSoiree = document("Autorisation préfecture - soirée d'intégration",
                TypeDocument.AUTORISATION, evenements,
                "https://drive.bde-cytech.fr/evenements/autorisation-integration.pdf",
                StatutDocument.EN_VALIDATION, aujourdHui.minusDays(7));
        autorisationSoiree.ajouterRelecteur(lea);
        autorisationSoiree.ajouterRelecteur(ines);

        DocumentAdmin devisSono = document("Devis sonorisation - gala", TypeDocument.DEVIS, partenariats,
                "https://drive.bde-cytech.fr/devis/sono-gala.pdf", StatutDocument.BROUILLON, aujourdHui.minusDays(2));
        devisSono.ajouterRelecteur(hugo);

        DocumentAdmin pvReunion = document("Compte rendu réunion gala", TypeDocument.PV, reunions,
                "https://drive.bde-cytech.fr/pv/reunion-gala.pdf", StatutDocument.EN_VALIDATION,
                aujourdHui.minusDays(4));
        pvReunion.ajouterRelecteur(lea);
        pvReunion.ajouterRelecteur(hugo);

        // Document volontairement sans dossier, utile pour tester l'association 1-N.
        DocumentAdmin devisPhoto = document("Devis photographe - gala", TypeDocument.DEVIS, null,
                "https://drive.bde-cytech.fr/devis/photographe-gala.pdf", StatutDocument.BROUILLON,
                aujourdHui.minusDays(1));
        devisPhoto.ajouterRelecteur(hugo);

        documentRepository.saveAll(java.util.List.of(
                statuts, pvAg, reglement, factureTraiteur, devisDj, factureTransportSki,
                factureChalet, convention, autorisationSoiree, devisSono, pvReunion, devisPhoto
        ));

        // --- Événements et équipes organisatrices (N-N) ---
        Evenement soiree = new Evenement("Soirée d'intégration 2026", aujourdHui.plusDays(20),
                "Domaine de la Roseraie, Cergy", new BigDecimal("6500.00"), StatutEvenement.PLANIFIE);
        soiree.ajouterOrganisateur(lea);
        soiree.ajouterOrganisateur(nathan);
        soiree.ajouterOrganisateur(hugo);

        Evenement gala = new Evenement("Gala annuel du BDE", aujourdHui.plusDays(115),
                "Pavillon Royal, Paris", new BigDecimal("18000.00"), StatutEvenement.PLANIFIE);
        gala.ajouterOrganisateur(lea);
        gala.ajouterOrganisateur(nathan);
        gala.ajouterOrganisateur(hugo);
        gala.ajouterOrganisateur(sarah);

        Evenement ski = new Evenement("Week-end ski", aujourdHui.minusDays(90),
                "Les Deux Alpes", new BigDecimal("9500.00"), StatutEvenement.TERMINE);
        ski.ajouterOrganisateur(nathan);
        ski.ajouterOrganisateur(hugo);
        ski.ajouterOrganisateur(adam);

        Evenement afterwork = new Evenement("Afterwork partenaires", aujourdHui.minusDays(22),
                "Le Comptoir, Cergy", new BigDecimal("1800.00"), StatutEvenement.TERMINE);
        afterwork.ajouterOrganisateur(lea);
        afterwork.ajouterOrganisateur(adam);
        afterwork.ajouterOrganisateur(ines);

        Evenement tournoi = new Evenement("Tournoi sportif inter-écoles", aujourdHui.plusDays(55),
                "Complexe sportif de Cergy", new BigDecimal("3200.00"), StatutEvenement.PLANIFIE);
        tournoi.ajouterOrganisateur(hugo);
        tournoi.ajouterOrganisateur(sarah);

        evenementRepository.saveAll(java.util.List.of(soiree, gala, ski, afterwork, tournoi));

        // --- Trésorerie : recettes, dépenses, validations et rejets ---
        ligne("Billetterie - soirée d'intégration", TypeLigne.RECETTE, "4200.00", "Billetterie",
                StatutLigne.VALIDEE, aujourdHui.minusDays(6), soiree, lea, null);
        ligne("Traiteur - soirée d'intégration", TypeLigne.DEPENSE, "3100.00", "Restauration",
                StatutLigne.VALIDEE, aujourdHui.minusDays(10), soiree, nathan, factureTraiteur);
        ligne("DJ et sonorisation", TypeLigne.DEPENSE, "800.00", "Animation",
                StatutLigne.VALIDEE, aujourdHui.minusDays(15), soiree, hugo, devisDj);
        ligne("Décoration de la salle", TypeLigne.DEPENSE, "350.00", "Logistique",
                StatutLigne.EN_ATTENTE, aujourdHui.minusDays(3), soiree, sarah, null);

        ligne("Sponsors - gala annuel", TypeLigne.RECETTE, "5000.00", "Sponsoring",
                StatutLigne.VALIDEE, aujourdHui.minusDays(25), gala, lea, convention);
        ligne("Location de la grande salle", TypeLigne.DEPENSE, "6000.00", "Location",
                StatutLigne.EN_ATTENTE, aujourdHui.minusDays(8), gala, nathan, null);
        ligne("Traiteur - gala annuel", TypeLigne.DEPENSE, "9000.00", "Restauration",
                StatutLigne.REJETEE, aujourdHui.minusDays(8), gala, hugo, null);

        ligne("Participations - week-end ski", TypeLigne.RECETTE, "12000.00", "Billetterie",
                StatutLigne.VALIDEE, aujourdHui.minusDays(120), ski, lea, null);
        ligne("Transport en autocar", TypeLigne.DEPENSE, "4800.00", "Transport",
                StatutLigne.VALIDEE, aujourdHui.minusDays(105), ski, hugo, factureTransportSki);
        ligne("Hébergement en chalet", TypeLigne.DEPENSE, "5200.00", "Hébergement",
                StatutLigne.VALIDEE, aujourdHui.minusDays(103), ski, nathan, factureChalet);

        ligne("Recette buvette", TypeLigne.RECETTE, "680.00", "Buvette",
                StatutLigne.VALIDEE, aujourdHui.minusDays(22), afterwork, ines, null);
        ligne("Consommations afterwork", TypeLigne.DEPENSE, "420.00", "Restauration",
                StatutLigne.VALIDEE, aujourdHui.minusDays(22), afterwork, ines, null);

        ligne("Subvention Crous", TypeLigne.RECETTE, "2000.00", "Subvention",
                StatutLigne.VALIDEE, aujourdHui.minusDays(45), null, lea, null);
        ligne("Fournitures de bureau", TypeLigne.DEPENSE, "120.00", "Fournitures",
                StatutLigne.VALIDEE, aujourdHui.minusDays(30), null, ines, null);
        ligne("Impression de flyers", TypeLigne.DEPENSE, "300.00", "Communication",
                StatutLigne.EN_ATTENTE, aujourdHui.minusDays(2), null, sarah, null);
    }

    private MembreBureau membre(String nom, String prenom, String email, String role, String promotion) {
        return new MembreBureau(nom, prenom, email, role, promotion);
    }

    private DossierAdmin dossier(String nom, String categorie, String description) {
        return new DossierAdmin(nom, categorie, description);
    }

    private DocumentAdmin document(String titre, TypeDocument type, DossierAdmin dossier, String reference,
                                   StatutDocument statut, LocalDate dateCreation) {
        DocumentAdmin document = new DocumentAdmin(titre, type, reference, dateCreation);
        document.setStatut(statut);
        document.setDossier(dossier);
        return document;
    }

    private void ligne(String libelle, TypeLigne type, String montant, String categorie,
                       StatutLigne statut, LocalDate date, Evenement evenement,
                       MembreBureau createur, DocumentAdmin justificatif) {
        LigneTresorerie ligne = new LigneTresorerie(libelle, type, new BigDecimal(montant),
                categorie, date);
        ligne.setStatut(statut);
        ligne.setEvenement(evenement);
        ligne.setCreateur(createur);
        ligne.setDocumentJustificatif(justificatif);
        ligneRepository.save(ligne);
    }
}
