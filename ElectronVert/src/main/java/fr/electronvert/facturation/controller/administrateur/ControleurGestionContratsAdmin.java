package fr.electronvert.facturation.controller.administrateur;

import fr.electronvert.facturation.exception.ContratAvecFacturesImpayeesException;
import fr.electronvert.facturation.model.contrat.*;
import fr.electronvert.facturation.model.facture.Facture;
import fr.electronvert.facturation.model.releve.Releve;
import fr.electronvert.facturation.model.releve.TypeConso;
import fr.electronvert.facturation.model.releve.TypeReleve;
import fr.electronvert.facturation.model.utilisateur.Client;
import fr.electronvert.facturation.service.*;
import fr.electronvert.facturation.view.administrateur.VueGestionContrats;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Contrôleur de gestion des contrats pour l'administrateur.
 * <p>
 * Ce contrôleur permet à l'administrateur de :
 * <ul>
 *   <li>Rechercher un contrat par sa référence</li>
 *   <li>Lister tous les contrats d'un client</li>
 *   <li>Créer un nouveau contrat pour un client</li>
 *   <li>Consulter les détails d'un contrat</li>
 *   <li>Consulter l'historique des relevés d'un contrat</li>
 *   <li>Consulter les factures d'un contrat</li>
 *   <li>Clôturer un contrat (avec vérification des impayés)</li>
 * </ul>
 * La création d'un contrat génère automatiquement un relevé d'ouverture
 * avec des index initiaux aléatoires.
 *
 *
 * @see VueGestionContrats
 * @see GestionnaireContrats
 * @see GestionnaireClients
 * @see fr.electronvert.facturation.service.GestionnaireFactures
 * @see SimulateurDate
 * @see SimulateurIndex
 */
public class ControleurGestionContratsAdmin {

    // =====================
    // ATTRIBUTS
    // =====================

    /**
     * Vue de gestion des contrats.
     */
    private final VueGestionContrats vue;

    /**
     * Gestionnaire des factures pour les factures de régularisation de cloture de contrat.
     */
    private final GestionnaireFactures gestionnaireFactures;

    /**
     * Gestionnaire des clients pour la recherche lors de la création.
     */
    private final GestionnaireClients gestionnaireClients;

    /**
     * Gestionnaire des contrats pour les opérations métier.
     */
    private final GestionnaireContrats gestionnaireContrats;

    /**
     * Simulateur de date pour obtenir la date courante lors des opérations.
     */
    private final SimulateurDate simulateurDate;

    /**
     * Simulateur d'index pour générer les index initiaux lors de la création.
     */
    private final SimulateurIndex simulateurIndex;

    // =====================
    // CONSTRUCTEUR
    // =====================

    /**
     * Construit le contrôleur de gestion des contrats.
     *
     * @param scanner scanner pour les entrées utilisateur
     * @param gestionnaireClients gestionnaire des clients
     * @param gestionnaireContrats gestionnaire des contrats
     * @param gestionnaireFactures gestionnaire de factures
     * @param simulateurDate simulateur de date
     * @param simulateurIndex simulateur d'index
     *
     * @throws IllegalArgumentException si l'un des paramètres est null
     */
    public ControleurGestionContratsAdmin(
            Scanner scanner,
            GestionnaireFactures gestionnaireFactures,
            GestionnaireClients gestionnaireClients,
            GestionnaireContrats gestionnaireContrats,
            SimulateurDate simulateurDate,
            SimulateurIndex simulateurIndex
    ) {
        if (scanner == null || gestionnaireClients == null
                || gestionnaireContrats == null || simulateurDate == null
                || simulateurIndex == null) {
            throw new IllegalArgumentException("Tous les paramètres sont requis");
        }

        this.vue = new VueGestionContrats(scanner);
        this.gestionnaireFactures = gestionnaireFactures;
        this.gestionnaireClients = gestionnaireClients;
        this.gestionnaireContrats = gestionnaireContrats;
        this.simulateurDate = simulateurDate;
        this.simulateurIndex = simulateurIndex;
    }

    // =====================
    // MENU PRINCIPAL
    // =====================

    /**
     * Lance le menu de gestion des contrats.
     * <p>
     * Options disponibles :
     * <ul>
     *   <li>1 : Rechercher un contrat par référence</li>
     *   <li>2 : Lister les contrats d'un client</li>
     *   <li>3 : Créer un nouveau contrat</li>
     *   <li>0 : Retour au menu administrateur</li>
     * </ul>
     * Les erreurs métier sont capturées et affichées à l'utilisateur.
     *
     */
    public void demarrer() {
        boolean retour = false;

        while (!retour) {
            int choix = vue.afficherMenuGestionContrats();

            try {
                switch (choix) {
                    case 1 -> rechercherContratParReference();
                    case 2 -> listerContratsClient();
                    case 3 -> creerContrat();
                    case 0 -> retour = true;
                }

            } catch (Exception e) {
                vue.afficherMessage("Erreur : " + e.getMessage());
                vue.attendreEntree();
            }
        }
    }

    // =====================
    // RECHERCHE DE CONTRAT
    // =====================

    /**
     * Recherche un contrat par sa référence et affiche le menu contextuel.
     * <p>
     * Demande la référence du contrat, effectue la recherche et affiche
     * le menu d'actions si le contrat est trouvé. Sinon, affiche un message
     * d'erreur.
     * </p>
     */
    private void rechercherContratParReference() {
        String reference = vue.demanderReferenceContrat();

        Contrat contrat = gestionnaireContrats.rechercherParReference(reference);

        if (contrat == null) {
            vue.afficherMessage("Aucun contrat trouvé avec la référence : " + reference);
            vue.attendreEntree();
            return;
        }

        afficherMenuContextuelContrat(contrat);
    }

    // =====================
    // LISTE DES CONTRATS D'UN CLIENT
    // =====================

    /**
     * Liste tous les contrats d'un client recherché par email.
     * <p>
     * Workflow :
     * <ul>
     *   <li>Recherche du client par email</li>
     *   <li>Récupération de tous ses contrats</li>
     *   <li>Affichage de la liste avec résumés</li>
     * </ul>
     * Si le client n'existe pas ou n'a aucun contrat, affiche un message
     * approprié.
     * </p>
     */
    private void listerContratsClient() {
        vue.afficherSousTitre("Recherche des contrats d'un client");
        String email = vue.demanderEmail("Email du client : ");

        Client client = gestionnaireClients.rechercherParEmail(email);

        if (client == null) {
            vue.afficherMessage("Aucun client trouvé avec l'email : " + email);
            vue.attendreEntree();
            return;
        }

        afficherContratsClient(client);
    }

    /**
     * Affiche la liste des contrats d'un client donné.
     * <p>
     * Méthode utilisée depuis le menu contextuel client pour consulter
     * les contrats sans re-saisir l'email.
     * </p>
     *
     * @param client client dont afficher les contrats
     *
     * @throws IllegalArgumentException si le client est null
     */
    public void demarrerPourClient(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Le client ne peut pas être null");
        }

        vue.afficherSousTitre(
                "Contrats du client : " + client.getPrenom() + " " + client.getNom()
        );

      afficherContratsClient(client);
    }

    /**
     * Affiche la liste des contrats d'un client donné.
     *
     * @param client client dont afficher les contrats
     *
     */
    public void afficherContratsClient(Client client) {
        List<Contrat> contrats = client.getContrats();

        if (contrats.isEmpty()) {
            vue.afficherMessage("Ce client n'a aucun contrat.");
            vue.attendreEntree();
            return;
        }

        List<String> resumes = new ArrayList<>();
        for (Contrat contrat : contrats) {
            resumes.add(contrat.toResume());
        }

        vue.afficherListeContrats(resumes);
    }

    // =====================
    // CRÉATION DE CONTRAT
    // =====================

    /**
     * Lance la procédure de création d'un contrat.
     * <p>
     * Recherche d'abord le client par email, puis délègue à
     * {@link #creerContrat(Client)} pour la création effective.
     * </p>
     */
    private void creerContrat() {
        vue.afficherSousTitre("Création d'un contrat");
        String email = vue.demanderEmail("Email du client : ");

        Client client = gestionnaireClients.rechercherParEmail(email);

        if (client == null) {
            vue.afficherMessage("Aucun client trouvé avec cet email.");
            vue.attendreEntree();
            return;
        }

        creerContrat(client);
    }

    /**
     * Crée un nouveau contrat pour un client donné.
     * <p>
     * Workflow de création en 6 étapes :
     * <ol>
     *   <li>Demande de l'adresse du logement</li>
     *   <li>Choix de l'offre tarifaire (Classique ou HP/HC)</li>
     *   <li>Choix du mode de facturation (REEL ou ECHEANCIER)</li>
     *   <li>Utilisation de la date courante comme date de souscription</li>
     *   <li>Création du contrat via le gestionnaire</li>
     *   <li>Génération automatique du relevé d'ouverture avec index initiaux</li>
     * </ol>
     * Les index initiaux sont générés aléatoirement par le {@link SimulateurIndex}
     * (valeurs entre 1000 et 10000 pour simuler un compteur existant).
     *
     *
     * @param client client pour lequel créer le contrat
     *
     * @throws IllegalArgumentException si le client est null
     */
    public void creerContrat(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Le client ne peut pas être null");
        }

        try {
            // 1. Adresse du logement
            String adresse = vue.demanderAdresseContrat();

            // 2. Choix de l'offre tarifaire
            int choixOffre = vue.demanderChoixOffreTarifaire();
            OffreTarifaire offre;
            if (choixOffre == 1) {
                offre = new OffreClassique();
            } else {
                offre = new OffreHPHC();
            }

            // 3. Mode de facturation
            int choixMode = vue.demanderModeFacturation();
            ModeFacturation mode =
                    (choixMode == 1)
                            ? ModeFacturation.REEL
                            : ModeFacturation.ECHEANCIER;

            // 4. Date de souscription (date courante)
            LocalDate dateSouscription = simulateurDate.getDateCourante();

            // 5. Création du contrat via le service
            Contrat contrat = gestionnaireContrats.creerContrat(
                    client,
                    adresse,
                    offre,
                    mode,
                    dateSouscription
            );

            // 6. Création automatique du relevé d'ouverture avec index initiaux
            Releve releveOuverture = new Releve(
                    contrat,
                    TypeReleve.OUVERTURE,
                    dateSouscription,
                    simulateurIndex.genererIndexInitial(contrat)
            );
            contrat.ajouterReleve(releveOuverture);

            vue.afficherContratCree();

        } catch (Exception e) {
            vue.afficherMessage("Erreur : " + e.getMessage());
            vue.attendreEntree();
        }
    }

    // =====================
    // MENU CONTEXTUEL CONTRAT
    // =====================

    /**
     * Affiche le menu contextuel d'actions sur un contrat sélectionné.
     * <p>
     * Options disponibles :
     * <ul>
     *   <li>1 : Voir les détails du contrat</li>
     *   <li>2 : Voir l'historique des relevés</li>
     *   <li>3 : Voir les factures du contrat</li>
     *   <li>4 : Clôturer le contrat</li>
     *   <li>0 : Retour</li>
     * </ul>
     * Les erreurs métier sont capturées et affichées à l'utilisateur.
     * </p>
     *
     * @param contrat contrat sur lequel effectuer des actions
     */
    private void afficherMenuContextuelContrat(Contrat contrat) {
        boolean retour = false;

        while (!retour) {
            int choix = vue.afficherMenuContextuelContrat();

            try {
                switch (choix) {
                    case 1 -> vue.afficherDetailsContrat(contrat.getDetails());
                    case 2 -> afficherHistoriqueReleves(contrat);
                    case 3 -> afficherFacturesContrat(contrat);
                    case 4 -> cloturerContrat(contrat);
                    case 0 -> retour = true;
                }
            } catch (Exception e) {
                vue.afficherMessage("Erreur : " + e.getMessage());
                vue.attendreEntree();
            }
        }
    }

    // =====================
    // ACTIONS SUR LE CONTRAT
    // =====================

    /**
     * Affiche l'historique complet des relevés d'un contrat.
     * <p>
     * Présente tous les relevés (ouverture, mensuels, clôture) avec
     * leurs index et dates. Si aucun relevé n'existe, affiche un message
     * approprié.
     * </p>
     *
     * @param contrat contrat dont afficher les relevés
     */
    private void afficherHistoriqueReleves(Contrat contrat) {
        List<Releve> releves = contrat.getReleves();

        if (releves.isEmpty()) {
            vue.afficherMessage("Aucun relevé pour ce contrat.");
            vue.attendreEntree();
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (Releve releve : releves) {
            sb.append(releve.toResume()).append("\n");
            sb.append("--------------------\n");
        }

        vue.afficherDetailsContrat(sb.toString());
    }

    /**
     * Affiche la liste des factures d'un contrat.
     * <p>
     * Présente toutes les factures (mensuelles, échéances, régularisations)
     * avec leurs montants et statuts. Si aucune facture n'existe, affiche
     * un message approprié.
     * </p>
     *
     * @param contrat contrat dont afficher les factures
     */
    private void afficherFacturesContrat(Contrat contrat) {
        List<Facture> factures = contrat.getFactures();

        if (factures.isEmpty()) {
            vue.afficherMessage("Aucune facture pour ce contrat.");
            vue.attendreEntree();
            return;
        }

        List<String> resumes = new ArrayList<>();
        for (Facture facture : factures) {
            resumes.add(facture.toResume());
        }

        vue.afficherListeFactures(resumes);
    }

    // =====================
    // CLÔTURE DU CONTRAT
    // =====================

    /**
     * Clôture un contrat après vérification et création du relevé de clôture.
     * <p>
     * Workflow :
     * <ul>
     *   <li>Vérification que le contrat est actif</li>
     *   <li>Demande de confirmation à l'utilisateur</li>
     *   <li>Création d'un relevé de clôture à la date courante</li>
     *   <li>Si mode ECHEANCIER : génération d'une facture de régularisation</li>
     *   <li>Clôture effective du contrat</li>
     * </ul>
     * </p>
     *
     * @param contrat contrat à clôturer
     *
     * @throws ContratAvecFacturesImpayeesException si le contrat a des factures impayées
     */
    private void cloturerContrat(Contrat contrat) {
        if (!contrat.estActif()) {
            vue.afficherMessage("Ce contrat est déjà clôturé.");
            vue.attendreEntree();
            return;
        }

        boolean confirmation = vue.demanderConfirmation(
                "Êtes-vous sûr de vouloir clôturer ce contrat ?"
        );

        if (!confirmation) {
            vue.afficherMessage("Clôture annulée.");
            vue.attendreEntree();
            return;
        }

        try {
            LocalDate dateCloture = simulateurDate.getDateCourante();

            Map<TypeConso, Double> indexCloture = simulateurIndex.calculerIndex(
                    contrat,
                    dateCloture
            );

            Releve releveCloture = new Releve(
                    contrat,
                    TypeReleve.CLOTURE,
                    dateCloture,
                    indexCloture
            );

            contrat.ajouterReleve(releveCloture);


            if (contrat.getEcheancier() != null) {
                contrat.getEcheancier();
                contrat.getEcheancier().setPeutEmmetreMensualite(false);
                gestionnaireFactures.regulariserClotureEcheancier(contrat, dateCloture);
            }

            gestionnaireContrats.cloturerContrat(contrat, dateCloture);

            if (contrat.getEcheancier() != null) {
                vue.afficherMessage("Contrat clôturé. Facture de régularisation générée.");
            } else {
                vue.afficherMessage("Contrat clôturé avec succès.");
            }

            vue.attendreEntree();

        } catch (ContratAvecFacturesImpayeesException e) {
            vue.afficherMessage("Erreur : " + e.getMessage());
            vue.attendreEntree();
        } catch (Exception e) {
            vue.afficherMessage("Erreur lors de la clôture : " + e.getMessage());
            vue.attendreEntree();
        }
    }
}