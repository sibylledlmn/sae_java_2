package fr.electronvert.facturation.controller.client;

import fr.electronvert.facturation.model.utilisateur.Client;
import fr.electronvert.facturation.view.client.VueMenuClient;

import java.util.Scanner;

/**
 * Contrôleur principal de l'espace client.
 * <p>
 * Ce contrôleur gère le menu principal de l'espace client et délègue
 * vers les contrôleurs spécialisés pour chaque fonctionnalité :
 * <ul>
 *   <li>Gestion des contrats (consultation, changements d'offre/mode)</li>
 *   <li>Gestion des factures et paiements</li>
 *   <li>Consultation de la consommation</li>
 *   <li>Informations personnelles</li>
 *   <li>Consultation des tarifs</li>
 * </ul>
 * Il maintient la session client active jusqu'à la déconnexion.
 *
 * @see ControleurGestionContratsClient
 * @see ControleurGestionFacturesEtPaiements
 * @see ControleurConsultationConsommation
 * @see ControleurInformationsPersonnelles
 * @see ControleurConsultationTarifs
 */
public class ControleurMenuClient {

    // =====================
    // ATTRIBUTS
    // =====================

    /**
     * Vue du menu principal client.
     */
    private final VueMenuClient vueMenuClient;

    /**
     * Contrôleur de gestion des contrats du client.
     */
    private final ControleurGestionContratsClient controleurGestionContratsClient;

    /**
     * Contrôleur de gestion des factures et paiements.
     */
    private final ControleurGestionFacturesEtPaiements controleurGestionFacturesEtPaiements;

    /**
     * Contrôleur de consultation de la consommation.
     */
    private final ControleurConsultationConsommation controleurConsultationConsommationClient;

    /**
     * Contrôleur de gestion des informations personnelles.
     */
    private final ControleurInformationsPersonnelles controleurInformationsPersonnelles;

    /**
     * Contrôleur de consultation des tarifs ElectronVert.
     */
    private final ControleurConsultationTarifs controleurConsultationTarifs;

    // =====================
    // CONSTRUCTEUR
    // =====================

    /**
     * Construit le contrôleur du menu client.
     *
     * @param scanner scanner pour les entrées utilisateur
     * @param controleurContratsClient contrôleur de gestion des contrats
     * @param controleurFacturesEtPaiements contrôleur de gestion des factures et paiements
     * @param controleurConsommationClient contrôleur de consultation de la consommation
     * @param controleurInformationsPersonnelles contrôleur des informations personnelles
     * @param controleurConsultationTarifs contrôleur de consultation des tarifs
     *
     * @throws IllegalArgumentException si l'un des paramètres est null
     */
    public ControleurMenuClient(
            Scanner scanner,
            ControleurGestionContratsClient controleurContratsClient,
            ControleurGestionFacturesEtPaiements controleurFacturesEtPaiements,
            ControleurConsultationConsommation controleurConsommationClient,
            ControleurInformationsPersonnelles controleurInformationsPersonnelles,
            ControleurConsultationTarifs controleurConsultationTarifs
    ) {
        if (scanner == null || controleurContratsClient == null
                || controleurFacturesEtPaiements == null || controleurConsommationClient == null
                || controleurInformationsPersonnelles == null
                || controleurConsultationTarifs == null) {
            throw new IllegalArgumentException("Tous les paramètres sont requis");
        }

        this.vueMenuClient = new VueMenuClient(scanner);
        this.controleurGestionContratsClient = controleurContratsClient;
        this.controleurGestionFacturesEtPaiements = controleurFacturesEtPaiements;
        this.controleurConsultationConsommationClient = controleurConsommationClient;
        this.controleurInformationsPersonnelles = controleurInformationsPersonnelles;
        this.controleurConsultationTarifs = controleurConsultationTarifs;
    }

    // =====================
    // GESTION DU MENU CLIENT
    // =====================

    /**
     * Lance l'espace client pour un client donné.
     * <p>
     * Affiche le menu principal et redirige vers les fonctionnalités
     * selon le choix de l'utilisateur :
     * <ul>
     *   <li>1 : Mes contrats (consultation, changements)</li>
     *   <li>2 : Mes factures et paiements</li>
     *   <li>3 : Ma consommation</li>
     *   <li>4 : Mes informations personnelles</li>
     *   <li>5 : Consulter les tarifs ElectronVert</li>
     *   <li>6 : Se déconnecter</li>
     * </ul>
     * La boucle continue jusqu'à ce que le client choisisse de se déconnecter.
     *
     * @param client client actuellement connecté
     *
     * @throws IllegalArgumentException si le client est null
     */
    public void demarrer(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Le client ne peut pas être null");
        }

        boolean connecte = true;

        while (connecte) {
            vueMenuClient.afficherMenuPrincipal();
            int choix = vueMenuClient.demanderChoixMenuPrincipal();

            switch (choix) {
                case 1 -> controleurGestionContratsClient.demarrer(client);

                case 2 -> controleurGestionFacturesEtPaiements.demarrer(client);

                case 3 -> controleurConsultationConsommationClient.demarrer(client);

                case 4 -> controleurInformationsPersonnelles.demarrer(client);

                case 5 -> controleurConsultationTarifs.demarrer();

                case 6 -> connecte = false;
            }
        }
    }
}