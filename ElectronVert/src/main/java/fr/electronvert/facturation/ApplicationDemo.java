package fr.electronvert.facturation;

import fr.electronvert.facturation.controller.ControleurApplication;
import fr.electronvert.facturation.controller.administrateur.ControleurGestionClientsAdmin;
import fr.electronvert.facturation.controller.administrateur.ControleurGestionContratsAdmin;
import fr.electronvert.facturation.controller.administrateur.ControleurGestionTarifsAdmin;
import fr.electronvert.facturation.controller.administrateur.ControleurSuiviStatistiquesAdmin;
import fr.electronvert.facturation.controller.administrateur.ControleurMenuAdministrateur;
import fr.electronvert.facturation.controller.client.*;
import fr.electronvert.facturation.model.contrat.ModeFacturation;
import fr.electronvert.facturation.model.contrat.OffreClassique;
import fr.electronvert.facturation.model.contrat.OffreHPHC;
import fr.electronvert.facturation.model.contrat.Contrat;
import fr.electronvert.facturation.model.releve.Releve;
import fr.electronvert.facturation.model.releve.TypeReleve;
import fr.electronvert.facturation.model.tarif.Tarif;
import fr.electronvert.facturation.model.utilisateur.Administrateur;
import fr.electronvert.facturation.model.utilisateur.Client;
import fr.electronvert.facturation.service.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Scanner;

/**
 * Classe de démonstration de l'application ElectronVert.
 * Contient des clients avec des contrats datant de la date du système : 15 janvier 2025.
 *
 * Les relevés mensuels et factures seront générés automatiquement
 * par le simulateur de date au fur et à mesure que le temps avance.
 */
public class ApplicationDemo {

    public static void main(String[] args) {
        // Configuration de la locale en français
        Locale.setDefault(Locale.FRANCE);

        System.out.println("╔═════════════════════════════════════════════╗");
        System.out.println("    ELECTRONVERT - Système de facturation");
        System.out.println("╚═════════════════════════════════════════════╝");
        System.out.println();

        // ┌─────────────────────────────────────────┐
        // │  1. INITIALISATION DES GESTIONNAIRES    │
        // └─────────────────────────────────────────┘

        GestionnaireTarifs gestionnaireTarifs = creerGestionnaireTarifs();
        GestionnaireClients gestionnaireClients = new GestionnaireClients();
        GestionnaireContrats gestionnaireContrats = new GestionnaireContrats(gestionnaireTarifs);
        GestionnaireFactures gestionnaireFactures = new GestionnaireFactures(gestionnaireTarifs);
        GestionnairePaiements gestionnairePaiements = new GestionnairePaiements();
        GestionnaireRelances gestionnaireRelances = new GestionnaireRelances();
        GestionnaireReleves gestionnaireReleves = new GestionnaireReleves();
        SimulateurIndex simulateurIndex = new SimulateurIndex();

        // ┌─────────────────────────────────────────┐
        // │  2. SIMULATEUR DE DATE                  │
        // └─────────────────────────────────────────┘

        LocalDate dateInitiale = LocalDate.of(2025, 1, 15);
        SimulateurDate simulateurDate = new SimulateurDate(
                dateInitiale,
                gestionnaireContrats,
                gestionnaireReleves,
                gestionnaireFactures,
                gestionnairePaiements,
                gestionnaireRelances,
                simulateurIndex
        );

        // ┌─────────────────────────────────────────┐
        // │  3. CRÉATION DES UTILISATEURS           │
        // └─────────────────────────────────────────┘

        Administrateur admin = new Administrateur(
                "Admin",
                "Système",
                "admin@electronvert.fr"
        );

        // Clients de test
        Client alice = gestionnaireClients.creerClient(
                "Durand",
                "Alice",
                "alice.durand@mail.fr"
        );

        Client lucas = gestionnaireClients.creerClient(
                "Martin",
                "Lucas",
                "lucas.martin@mail.fr"
        );

        Client sophie = gestionnaireClients.creerClient(
                "Bernard",
                "Sophie",
                "sophie.bernard@mail.fr"
        );

        // ┌─────────────────────────────────────────┐
        // │  4. CRÉATION DES CONTRATS RÉCENTS       │
        // └─────────────────────────────────────────┘
        // Tous les contrats démarrent à la date système (15 janvier 2025)
        // Le simulateur de date gérera automatiquement les relevés et factures

        // Alice : Échéancier avec offre Classique
        Contrat contratAlice = gestionnaireContrats.creerContrat(
                alice,
                "12 rue des Lilas, 69100 Villeurbanne",
                new OffreClassique(),
                ModeFacturation.ECHEANCIER,
                dateInitiale
        );

        // Lucas : Facturation mensuelle au réel avec offre HP/HC
        Contrat contratLucas = gestionnaireContrats.creerContrat(
                lucas,
                "8 avenue du Soleil, 69003 Lyon",
                new OffreHPHC(),
                ModeFacturation.REEL,
                dateInitiale
        );

        // Sophie : Échéancier avec offre HP/HC
        Contrat contratSophie = gestionnaireContrats.creerContrat(
                sophie,
                "45 boulevard des Alpes, 69006 Lyon",
                new OffreHPHC(),
                ModeFacturation.ECHEANCIER,
                dateInitiale
        );

        // ┌─────────────────────────────────────────┐
        // │  5. RELEVÉS D'OUVERTURE                 │
        // └─────────────────────────────────────────┘
        // Créer uniquement les relevés d'ouverture
        // Les relevés mensuels seront créés automatiquement par le simulateur

        Releve releveOuvertureAlice = new Releve(
                contratAlice,
                TypeReleve.OUVERTURE,
                dateInitiale,
                simulateurIndex.genererIndexInitial(contratAlice)
        );
        contratAlice.ajouterReleve(releveOuvertureAlice);

        Releve releveOuvertureLucas = new Releve(
                contratLucas,
                TypeReleve.OUVERTURE,
                dateInitiale,
                simulateurIndex.genererIndexInitial(contratLucas)
        );
        contratLucas.ajouterReleve(releveOuvertureLucas);

        Releve releveOuvertureSophie = new Releve(
                contratSophie,
                TypeReleve.OUVERTURE,
                dateInitiale,
                simulateurIndex.genererIndexInitial(contratSophie)
        );
        contratSophie.ajouterReleve(releveOuvertureSophie);

        // ┌─────────────────────────────────────────┐
        // │  6. INITIALISATION DES CONTRÔLEURS      │
        // └─────────────────────────────────────────┘

        Scanner scanner = new Scanner(System.in);

        // Contrôleurs administrateur
        ControleurGestionClientsAdmin controleurGestionClients =
                new ControleurGestionClientsAdmin(scanner, gestionnaireClients, gestionnaireContrats, simulateurDate, simulateurIndex, gestionnaireFactures);

        ControleurGestionContratsAdmin controleurGestionContrats =
                new ControleurGestionContratsAdmin(scanner, gestionnaireFactures, gestionnaireClients, gestionnaireContrats, simulateurDate, simulateurIndex);

        ControleurGestionTarifsAdmin controleurGestionTarifs =
                new ControleurGestionTarifsAdmin(scanner, gestionnaireTarifs, simulateurDate);

        ControleurSuiviStatistiquesAdmin controleurSuiviStatistiques =
                new ControleurSuiviStatistiquesAdmin(scanner, gestionnaireClients, gestionnaireContrats, gestionnaireFactures, gestionnairePaiements);

        ControleurMenuAdministrateur controleurMenuAdmin =
                new ControleurMenuAdministrateur(
                        scanner,
                        admin,
                        controleurGestionClients,
                        controleurGestionContrats,
                        controleurGestionTarifs,
                        controleurSuiviStatistiques

                );

        // Contrôleurs client
        ControleurInformationsPersonnelles controleurInfosPerso =
                new ControleurInformationsPersonnelles(scanner);

        ControleurGestionContratsClient controleurContratsClient =
                new ControleurGestionContratsClient(scanner, gestionnaireContrats, simulateurDate);

        ControleurGestionFacturesEtPaiements controleurFacturesPaiements =
                new ControleurGestionFacturesEtPaiements(scanner, gestionnaireContrats, gestionnaireFactures, gestionnairePaiements, simulateurDate);

        ControleurConsultationTarifs controleurConsultationTarifs =
                new ControleurConsultationTarifs(scanner, gestionnaireTarifs, simulateurDate);

        ControleurConsultationConsommation controleurConsultationConso =
                new ControleurConsultationConsommation(scanner, gestionnaireContrats);

        ControleurMenuClient controleurMenuClient =
                new ControleurMenuClient(
                        scanner,
                        controleurContratsClient,
                        controleurFacturesPaiements,
                        controleurConsultationConso,
                        controleurInfosPerso,
                        controleurConsultationTarifs
                );

        // ┌─────────────────────────────────────────┐
        // │  7. AFFICHAGE DES INFORMATIONS          │
        // └─────────────────────────────────────────┘

        afficherInformationsDemarrage(admin, gestionnaireClients, dateInitiale);

        // ┌─────────────────────────────────────────┐
        // │  8. LANCEMENT DE L'APPLICATION          │
        // └─────────────────────────────────────────┘

        ControleurApplication controleurApp = new ControleurApplication(
                scanner,
                gestionnaireClients,
                admin,
                simulateurDate,
                controleurMenuAdmin,
                controleurMenuClient
        );

        controleurApp.demarrer();

        scanner.close();
    }

    /**
     * Crée et configure le gestionnaire de tarifs avec les tarifs initiaux.
     */
    private static GestionnaireTarifs creerGestionnaireTarifs() {
        GestionnaireTarifs gestionnaireTarifs = new GestionnaireTarifs();

        // Tarif initial valable depuis le 1er janvier 2024
        Tarif tarifInitial = new Tarif(
                LocalDate.of(2024, 1, 1),
                0.2516,  // Prix kWh classique (€)
                0.27,    // Prix kWh HP (€)
                0.20,    // Prix kWh HC (€)
                12.44,   // Abonnement mensuel Classique (€)
                13.20    // Abonnement mensuel HP/HC (€)
        );

        gestionnaireTarifs.ajouterTarifInitial(tarifInitial);


        return gestionnaireTarifs;
    }

    /**
     * Affiche les informations de démarrage de l'application.
     */
    private static void afficherInformationsDemarrage(
            Administrateur admin,
            GestionnaireClients gestionnaireClients,
            LocalDate dateInitiale
    ) {
        System.out.println("\n┌───────────────────────────────────────────────────┐");
        System.out.println("│  DONNÉES DE DÉMONSTRATION CHARGÉES             │");
        System.out.println("└───────────────────────────────────────────────────┘\n");

        DateTimeFormatter formatFrancais = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRANCE);
        System.out.println("📅 Date du système : " + dateInitiale.format(formatFrancais));
        System.out.println("\n👤 COMPTES DISPONIBLES :");
        System.out.println("───────────────────────────────────────────────────");
        System.out.printf("   Admin : %s%n", admin.getEmail());

        for (Client client : gestionnaireClients.getTousLesClients()) {
            System.out.printf("   Client : %s (%s %s)%n",
                    client.getEmail(),
                    client.getPrenom(),
                    client.getNom()
            );
        }

        System.out.println("───────────────────────────────────────────────────");
        System.out.println("\n💡 CONSEILS POUR LA DÉMO :");
        System.out.println("   • Les 3 contrats ont été créés le 15 janvier 2025");
        System.out.println("   • Seuls les relevés d'ouverture existent pour l'instant");
        System.out.println("   • Utilisez le simulateur de date pour avancer dans le temps");
        System.out.println("   • Les relevés mensuels seront créés en fin de mois");
        System.out.println("   • Les factures seront générées le 5 de chaque mois");
        System.out.println("   • Les mensualités d'échéancier seront prélevées le 20");
        System.out.println("\n╚═════════════════════════════════════════════╝\n");
    }
}