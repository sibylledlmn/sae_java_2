package fr.electronvert.facturation.controller;

import fr.electronvert.facturation.controller.administrateur.ControleurMenuAdministrateur;
import fr.electronvert.facturation.controller.client.ControleurMenuClient;
import fr.electronvert.facturation.model.utilisateur.Administrateur;
import fr.electronvert.facturation.model.utilisateur.Client;
import fr.electronvert.facturation.service.GestionnaireClients;
import fr.electronvert.facturation.service.SimulateurDate;
import fr.electronvert.facturation.view.VuePrincipale;

import java.time.LocalDate;
import java.util.Scanner;

/**
 * Contrôleur principal de l'application ElectronVert.
 * <p>
 * Ce contrôleur est le point d'entrée de l'application et gère :
 * <ul>
 *   <li>Le menu principal de démarrage</li>
 *   <li>L'authentification des utilisateurs (administrateur ou clients)</li>
 *   <li>La simulation de date pour tester l'application</li>
 *   <li>La redirection vers les contrôleurs métier appropriés</li>
 * </ul>
 * Il coordonne les interactions entre les différents espaces utilisateur
 * (administrateur et client) et le système de simulation temporelle.
 *
 * @see ControleurMenuAdministrateur
 * @see ControleurMenuClient
 * @see SimulateurDate
 */
public class ControleurApplication {

    // =====================
    // ATTRIBUTS
    // =====================

    /**
     * Vue principale de l'application.
     */
    private final VuePrincipale vuePrincipale;

    /**
     * Gestionnaire des clients pour la recherche lors de la connexion.
     */
    private final GestionnaireClients gestionnaireClients;

    /**
     * Administrateur du système (compte unique).
     */
    private final Administrateur administrateur;

    /**
     * Simulateur de date pour les tests et démonstrations.
     */
    private final SimulateurDate simulateurDate;

    /**
     * Contrôleur de l'espace administrateur.
     */
    private final ControleurMenuAdministrateur controleurAdministrateur;

    /**
     * Contrôleur de l'espace client.
     */
    private final ControleurMenuClient controleurClient;

    // =====================
    // CONSTRUCTEUR
    // =====================

    /**
     * Construit le contrôleur principal de l'application.
     *
     * @param scanner scanner pour les entrées utilisateur
     * @param gestionnaireClients gestionnaire des clients
     * @param administrateur administrateur du système
     * @param simulateurDate simulateur de date
     * @param controleurAdministrateur contrôleur de l'espace administrateur
     * @param controleurClient contrôleur de l'espace client
     *
     * @throws IllegalArgumentException si l'un des paramètres est null
     */
    public ControleurApplication(
            Scanner scanner,
            GestionnaireClients gestionnaireClients,
            Administrateur administrateur,
            SimulateurDate simulateurDate,
            ControleurMenuAdministrateur controleurAdministrateur,
            ControleurMenuClient controleurClient
    ) {
        if (scanner == null || gestionnaireClients == null || administrateur == null
                || simulateurDate == null || controleurAdministrateur == null
                || controleurClient == null) {
            throw new IllegalArgumentException("Tous les paramètres sont requis");
        }

        this.vuePrincipale = new VuePrincipale(scanner);
        this.gestionnaireClients = gestionnaireClients;
        this.administrateur = administrateur;
        this.simulateurDate = simulateurDate;
        this.controleurAdministrateur = controleurAdministrateur;
        this.controleurClient = controleurClient;
    }

    // =====================
    // DÉMARRAGE DE L'APPLICATION
    // =====================

    /**
     * Lance l'application et affiche le menu principal.
     * <p>
     * Boucle principale de l'application qui propose :
     * <ul>
     *   <li>La connexion à un espace utilisateur</li>
     *   <li>L'accès au simulateur de date</li>
     *   <li>La sortie de l'application</li>
     * </ul>
     * L'application continue de tourner jusqu'à ce que l'utilisateur
     * choisisse de quitter et confirme sa décision.
     */
    public void demarrer() {
        boolean quitter = false;

        while (!quitter) {
            int choix = vuePrincipale.afficherMenuPrincipal();

            switch (choix) {
                case 1 -> gererConnexion();
                case 2 -> gererSimulationDate();
                case 0 -> quitter = vuePrincipale.demanderConfirmation(
                        "Êtes-vous sûr de vouloir quitter l'application ?"
                );
            }
        }
    }

    // =====================
    // AUTHENTIFICATION
    // =====================

    /**
     * Gère le processus de connexion des utilisateurs.
     * <p>
     * Recherche l'utilisateur par son email et redirige vers l'espace approprié :
     * <ul>
     *   <li>Si l'email correspond à l'administrateur : espace administrateur</li>
     *   <li>Si l'email correspond à un client : espace client</li>
     *   <li>Sinon : message d'erreur</li>
     * </ul>
     * La recherche est insensible à la casse.
     * </p>
     */
    private void gererConnexion() {
        String email = vuePrincipale.demanderEmail("Adresse email : ");

        // Vérification si l'email correspond à l'administrateur
        if (administrateur.getEmail().equalsIgnoreCase(email)) {
            controleurAdministrateur.demarrer(administrateur);
            return;
        }

        // Recherche dans les clients
        Client client = gestionnaireClients.rechercherParEmail(email);
        if (client != null) {
            controleurClient.demarrer(client);
            return;
        }

        // Aucun utilisateur trouvé
        vuePrincipale.afficherMessage("Aucun utilisateur trouvé avec cet email.");
        vuePrincipale.attendreEntree();
    }

    // =====================
    // SIMULATION DE DATE
    // =====================

    /**
     * Gère le menu de simulation de date.
     * <p>
     * Permet de manipuler la date du système pour tester l'application :
     * <ul>
     *   <li>Avancer d'un jour (déclenche les opérations quotidiennes)</li>
     *   <li>Avancer de plusieurs jours (1 à 365 jours)</li>
     *   <li>Avancer de plusieurs mois (1 à 120 mois = 10 ans)</li>
     *   <li>Aller à une date précise (date future uniquement)</li>
     * </ul>
     * Chaque avancement de date déclenche automatiquement les opérations
     * planifiées (factures, relevés, prélèvements, etc.) via le {@link SimulateurDate}.
     * </p>
     */
    private void gererSimulationDate() {
        boolean retour = false;

        while (!retour) {
            // Affichage de la date courante
            vuePrincipale.afficherTitre("Simulation de la date");
            vuePrincipale.afficherMessage("Date actuelle : " + simulateurDate.getDateCourante());
            vuePrincipale.afficherMessage("");

            // Menu des options
            vuePrincipale.afficherMessage("1. Avancer d'un jour");
            vuePrincipale.afficherMessage("2. Avancer de plusieurs jours");
            vuePrincipale.afficherMessage("3. Avancer de plusieurs mois");
            vuePrincipale.afficherMessage("4. Aller à une date précise");
            vuePrincipale.afficherMessage("0. Retour au menu principal");
            vuePrincipale.afficherMessage("");

            int choix = vuePrincipale.demanderEntier("Votre choix : ", 0, 4);

            try {
                switch (choix) {
                    case 1 -> simulateurDate.avancerDate();

                    case 2 -> {
                        int jours = vuePrincipale.demanderEntier(
                                "Nombre de jours à avancer : ",
                                1,
                                365
                        );
                        simulateurDate.avancerDeJours(jours);
                    }

                    case 3 -> {
                        int mois = vuePrincipale.demanderEntier(
                                "Nombre de mois à avancer : ",
                                1,
                                120
                        );
                        simulateurDate.avancerDeMois(mois);
                    }

                    case 4 -> {
                        LocalDate dateCible =
                                vuePrincipale.demanderDate("Nouvelle date");
                        simulateurDate.avancerDatePrecise(dateCible);
                    }

                    case 0 -> retour = true;
                }
            } catch (IllegalArgumentException e) {
                vuePrincipale.afficherMessage("Erreur : " + e.getMessage());
                vuePrincipale.attendreEntree();
            }
        }
    }
}