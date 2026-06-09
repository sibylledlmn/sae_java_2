package fr.electronvert.facturation.controller.administrateur;

import fr.electronvert.facturation.exception.ClientDejaExistantException;
import fr.electronvert.facturation.model.facture.Facture;
import fr.electronvert.facturation.model.utilisateur.Client;
import fr.electronvert.facturation.service.*;
import fr.electronvert.facturation.view.administrateur.VueGestionClients;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Contrôleur de gestion des clients pour l'administrateur.
 * <p>
 * Ce contrôleur permet à l'administrateur de :
 * <ul>
 *   <li>Créer un nouveau client</li>
 *   <li>Rechercher un client par son email</li>
 *   <li>Lister tous les clients enregistrés</li>
 *   <li>Consulter les contrats d'un client</li>
 *   <li>Créer un nouveau contrat pour un client</li>
 *   <li>Consulter toutes les factures d'un client (tous contrats confondus)</li>
 * </ul>
 * Le contrôleur propose un menu contextuel après chaque création ou
 * recherche de client, permettant d'effectuer des actions sur ce client.
 * Il délègue la gestion des contrats au {@link ControleurGestionContratsAdmin}.
 *
 *
 * @see VueGestionClients
 * @see GestionnaireClients
 * @see ControleurGestionContratsAdmin
 */
public class ControleurGestionClientsAdmin {

    // =====================
    // ATTRIBUTS
    // =====================

    /**
     * Vue de gestion des clients.
     */
    private final VueGestionClients vue;
    /**
     * Gestionnaire des clients.
     */
    private final GestionnaireClients gestionnaireClients;
    /**
     * Gestionnaire des factures pour accéder aux factures des clients.
     */
    private final GestionnaireFactures gestionnaireFactures;
    /**
     * Gestionnaire des contrats pour accéder aux contrats des clients.
     */
    private final GestionnaireContrats gestionnaireContrats;
    /**
     * Simulateur de date pour les opérations temporelles.
     */
    private final SimulateurDate simulateurDate;
    /**
     * Simulateur d'index pour la génération de relevés.
     */
    private final SimulateurIndex simulateurIndex;
    /**
     * Scanner pour les entrées utilisateur, partagé entre les sous-contrôleurs.
     */
    private final Scanner scanner;

    // =====================
    // CONSTRUCTEUR
    // =====================

    /**
     * Construit le contrôleur de gestion des clients.
     *
     * @param scanner scanner pour les entrées utilisateur
     * @param gestionnaireClients gestionnaire des clients
     * @param gestionnaireContrats gestionnaire des contrats
     * @param simulateurDate simulateur de date
     * @param simulateurIndex simulateur d'index
     * @param gestionnaireFacture gestionnaire des factures
     *
     * @throws IllegalArgumentException si l'un des paramètres est null
     */
    public ControleurGestionClientsAdmin(
            Scanner scanner,
            GestionnaireClients gestionnaireClients,
            GestionnaireContrats gestionnaireContrats,
            SimulateurDate simulateurDate,
            SimulateurIndex simulateurIndex,
            GestionnaireFactures gestionnaireFacture
    ) {
        if (scanner == null || gestionnaireClients == null
                || gestionnaireContrats == null || simulateurDate == null
                || simulateurIndex == null) {
            throw new IllegalArgumentException("Tous les paramètres sont requis");
        }

        this.vue = new VueGestionClients(scanner);
        this.gestionnaireClients = gestionnaireClients;
        this.gestionnaireContrats = gestionnaireContrats;
        this.simulateurDate = simulateurDate;
        this.simulateurIndex = simulateurIndex;
        this.gestionnaireFactures = gestionnaireFacture;
        this.scanner = scanner;
    }

    // =====================
    // MENU PRINCIPAL
    // =====================

    /**
     * Lance le menu de gestion des clients.
     * <p>
     * Options disponibles :
     * <ul>
     *   <li>1 : Créer un nouveau client</li>
     *   <li>2 : Rechercher un client par email</li>
     *   <li>3 : Lister tous les clients</li>
     *   <li>0 : Retour au menu administrateur</li>
     * </ul>
     * Les erreurs métier (client déjà existant) sont capturées et
     * affichées à l'utilisateur.
     *
     */
    public void demarrer() {
        boolean retour = false;

        while (!retour) {
            int choix = vue.afficherMenuGestionClients();

            try {
                switch (choix) {
                    case 1 -> creerClient();
                    case 2 -> rechercherClient();
                    case 3 -> listerClients();
                    case 0 -> retour = true;
                }
            } catch (Exception e) {
                vue.afficherMessage("Erreur : " + e.getMessage());
                vue.attendreEntree();
            }
        }
    }

    // =====================
    // ACTIONS CLIENT
    // =====================

    /**
     * Crée un nouveau client après saisie des informations.
     * <p>
     * Workflow de création :
     * <ol>
     *   <li>Demande du nom, prénom et email</li>
     *   <li>Création du client via le gestionnaire</li>
     *   <li>Affichage de la confirmation</li>
     *   <li>Affichage du menu contextuel pour effectuer des actions</li>
     * </ol>
     * L'email doit être unique dans le système.
     * </p>
     *
     * @throws ClientDejaExistantException si l'email est déjà utilisé
     */
    private void creerClient() {
        try {
            String nom = vue.demanderNomClient();
            String prenom = vue.demanderPrenomClient();
            String email = vue.demanderEmailClient();

            Client client = gestionnaireClients.creerClient(nom, prenom, email);

            vue.afficherClientCree();
            afficherMenuContextuelClient(client);

        } catch (ClientDejaExistantException e) {
            vue.afficherMessage("Erreur : " + e.getMessage());
            vue.attendreEntree();
        }
    }

    /**
     * Recherche un client par son email et affiche le menu contextuel.
     * <p>
     * Si le client est trouvé, affiche ses informations et le menu
     * d'actions. Sinon, affiche un message d'erreur.
     * </p>
     */
    private void rechercherClient() {
        String email = vue.demanderEmailRecherche();

        Client client = gestionnaireClients.rechercherParEmail(email);

        if (client == null) {
            vue.afficherMessage("Aucun client trouvé avec cet email.");
            vue.attendreEntree();
            return;
        }

        afficherMenuContextuelClient(client);
    }

    /**
     * Affiche la liste de tous les clients enregistrés.
     * <p>
     * Présente une liste numérotée de tous les clients avec leurs
     * informations principales (nom, prénom, email).
     * Si aucun client n'est enregistré, affiche un message approprié.
     * </p>
     */
    private void listerClients() {
        List<Client> clients = gestionnaireClients.getTousLesClients();

        if (clients.isEmpty()) {
            vue.afficherMessage("Aucun client enregistré.");
            vue.attendreEntree();
            return;
        }

        List<String> resumes = new ArrayList<>();
        for (Client client : clients) {
            resumes.add(client.getInformationsPersonnelles());
        }

        vue.afficherListeClients(resumes);
    }

    // =====================
    // MENU CONTEXTUEL CLIENT
    // =====================

    /**
     * Affiche le menu contextuel d'actions sur un client sélectionné.
     * <p>
     * Affiche d'abord les informations du client, puis propose les
     * options suivantes :
     * <ul>
     *   <li>1 : Consulter les contrats du client</li>
     *   <li>2 : Créer un nouveau contrat pour ce client</li>
     *   <li>3 : Consulter les factures du client</li>
     *   <li>0 : Retour au menu principal</li>
     * </ul>
     * La boucle continue jusqu'à ce que l'administrateur choisisse
     * de revenir au menu principal.
     * </p>
     *
     * @param client client sur lequel effectuer des actions
     */
    private void afficherMenuContextuelClient(Client client) {
        vue.afficherClient(client.getInformationsPersonnelles());

        boolean retour = false;

        while (!retour) {
            int choix = vue.afficherMenuContextuelClient();

            switch (choix) {
                case 1 -> consulterContratsClient(client);
                case 2 -> creerContratPourClient(client);
                case 3 -> consulterFacturesClient(client);
                case 0 -> retour = true;
            }
        }
    }

    // =====================
    // DÉLÉGATIONS
    // =====================

    /**
     * Délègue la consultation des contrats au contrôleur de gestion des contrats.
     * <p>
     * Crée une instance du {@link ControleurGestionContratsAdmin} et appelle
     * sa méthode {@code demarrerPourClient()} pour afficher la liste des
     * contrats du client.
     * </p>
     *
     * @param client client dont consulter les contrats
     */
    private void consulterContratsClient(Client client) {
        ControleurGestionContratsAdmin controleur =
                new ControleurGestionContratsAdmin(
                        scanner,
                        gestionnaireFactures,
                        gestionnaireClients,
                        gestionnaireContrats,
                        simulateurDate,
                        simulateurIndex
                );

        controleur.demarrerPourClient(client);
    }

    /**
     * Délègue la création d'un contrat au contrôleur de gestion des contrats.
     * <p>
     * Crée une instance du {@link ControleurGestionContratsAdmin} et appelle
     * sa méthode {@code creerContrat(Client)} pour créer un nouveau contrat
     * pour le client.
     * </p>
     *
     * @param client client pour lequel créer un contrat
     */
    private void creerContratPourClient(Client client) {
        ControleurGestionContratsAdmin controleur =
                new ControleurGestionContratsAdmin(
                        scanner,
                        gestionnaireFactures,
                        gestionnaireClients,
                        gestionnaireContrats,
                        simulateurDate,
                        simulateurIndex
                );

        controleur.creerContrat(client);
    }

    /**
     * Affiche toutes les factures du client (tous contrats confondus).
     * <p>
     * Récupère les factures de tous les contrats du client et les affiche
     * dans une liste. Si le client n'a aucune facture, affiche un message
     * approprié.
     * </p>
     *
     * @param client client dont consulter les factures
     */
    private void consulterFacturesClient(Client client) {
        List<Facture> factures = gestionnaireFactures.getFacturesParClient(client);

        if (factures.isEmpty()) {
            vue.afficherMessage("Ce client n'a aucune facture.");
            vue.attendreEntree();
            return;
        }

        List<String> resumes = new ArrayList<>();
        for (Facture facture : factures) {
            resumes.add(facture.toResume());
        }

        vue.afficherListeClients(resumes);
    }
}