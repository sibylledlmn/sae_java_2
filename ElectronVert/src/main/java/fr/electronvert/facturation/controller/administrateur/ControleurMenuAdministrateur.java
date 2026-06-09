package fr.electronvert.facturation.controller.administrateur;

import fr.electronvert.facturation.model.utilisateur.Administrateur;
import fr.electronvert.facturation.view.administrateur.VueMenuAdministrateur;

import java.util.Scanner;

/**
 * Contrôleur principal de l'espace administrateur.
 * <p>
 * Ce contrôleur gère le menu principal de l'espace administrateur et délègue
 * vers les contrôleurs spécialisés pour chaque fonctionnalité :
 * <ul>
 *   <li>Gestion des clients (création, recherche, consultation)</li>
 *   <li>Gestion des contrats (création, recherche, clôture)</li>
 *   <li>Gestion des tarifs (création, consultation)</li>
 *   <li>Suivi et statistiques (indicateurs globaux)</li>
 * </ul>
 * Il maintient la session administrateur active jusqu'à la déconnexion.
 *
 * @see ControleurGestionClientsAdmin
 * @see ControleurGestionContratsAdmin
 * @see ControleurGestionTarifsAdmin
 * @see ControleurSuiviStatistiquesAdmin
 */
public class ControleurMenuAdministrateur {

    // =====================
    // ATTRIBUTS
    // =====================

    /**
     * Vue du menu principal administrateur.
     */
    private final VueMenuAdministrateur vue;

    /**
     * Contrôleur de gestion des clients.
     */
    private final ControleurGestionClientsAdmin controleurGestionClients;

    /**
     * Contrôleur de gestion des contrats.
     */
    private final ControleurGestionContratsAdmin controleurGestionContrats;

    /**
     * Contrôleur de gestion des tarifs.
     */
    private final ControleurGestionTarifsAdmin controleurGestionTarifs;

    /**
     * Contrôleur de suivi et statistiques.
     */
    private final ControleurSuiviStatistiquesAdmin controleurSuiviStatistiques;

    // =====================
    // CONSTRUCTEUR
    // =====================

    /**
     * Construit le contrôleur du menu administrateur.
     *
     * @param scanner scanner pour les entrées utilisateur
     * @param administrateur administrateur actuellement connecté
     * @param controleurGestionClients contrôleur de gestion des clients
     * @param controleurGestionContrats contrôleur de gestion des contrats
     * @param controleurGestionTarifs contrôleur de gestion des tarifs
     * @param controleurSuiviStatistiques contrôleur de suivi et statistiques
     *
     * @throws IllegalArgumentException si l'un des paramètres est null
     */
    public ControleurMenuAdministrateur(
            Scanner scanner,
            Administrateur administrateur,
            ControleurGestionClientsAdmin controleurGestionClients,
            ControleurGestionContratsAdmin controleurGestionContrats,
            ControleurGestionTarifsAdmin controleurGestionTarifs,
            ControleurSuiviStatistiquesAdmin controleurSuiviStatistiques
    ) {
        if (scanner == null || administrateur == null
                || controleurGestionClients == null || controleurGestionContrats == null
                || controleurGestionTarifs == null || controleurSuiviStatistiques == null) {
            throw new IllegalArgumentException("Tous les paramètres sont requis");
        }

        this.vue = new VueMenuAdministrateur(scanner, administrateur);
        this.controleurGestionClients = controleurGestionClients;
        this.controleurGestionContrats = controleurGestionContrats;
        this.controleurGestionTarifs = controleurGestionTarifs;
        this.controleurSuiviStatistiques = controleurSuiviStatistiques;
    }

    // =====================
    // GESTION DU MENU ADMINISTRATEUR
    // =====================

    /**
     * Lance l'espace administrateur pour un administrateur donné.
     * <p>
     * Affiche le menu principal et redirige vers les fonctionnalités
     * selon le choix de l'utilisateur :
     * <ul>
     *   <li>1 : Gestion des clients</li>
     *   <li>2 : Gestion des contrats</li>
     *   <li>3 : Gestion des tarifs</li>
     *   <li>4 : Suivi et statistiques</li>
     *   <li>5 : Se déconnecter</li>
     * </ul>
     * La boucle continue jusqu'à ce que l'administrateur choisisse de
     * se déconnecter (option 5).
     *
     *
     * @param administrateur administrateur actuellement connecté
     *
     * @throws IllegalArgumentException si l'administrateur est null
     */
    public void demarrer(Administrateur administrateur) {
        if (administrateur == null) {
            throw new IllegalArgumentException("L'administrateur ne peut pas être null");
        }

        boolean deconnexion = false;

        while (!deconnexion) {
            int choix = vue.afficherMenuPrincipal();

            switch (choix) {
                case 1 -> controleurGestionClients.demarrer();
                case 2 -> controleurGestionContrats.demarrer();
                case 3 -> controleurGestionTarifs.demarrer();
                case 4 -> controleurSuiviStatistiques.demarrer();
                case 5 -> deconnexion = true;
            }
        }
    }
}