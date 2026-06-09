package fr.electronvert.facturation.view.client;

import fr.electronvert.facturation.view.VueBase;

import java.util.List;
import java.util.Scanner;

/**
 * Vue de gestion des contrats côté client.
 * <p>
 * Cette vue permet au client de :
 * <ul>
 *   <li>Consulter la liste de ses contrats</li>
 *   <li>Sélectionner un contrat pour effectuer des actions</li>
 *   <li>Consulter les informations détaillées d'un contrat</li>
 *   <li>Demander un changement d'offre tarifaire</li>
 *   <li>Demander un changement de mode de facturation</li>
 * </ul>
 * La vue suit le pattern MVC : elle gère uniquement l'affichage et la
 * collecte des entrées utilisateur, sans logique métier.
 *
 *
 * @see VueBase
 * @see fr.electronvert.facturation.controller.client.ControleurGestionContratsClient
 */
public class VueGestionContratsClient extends VueBase {

    // =====================
    // CONSTRUCTEUR
    // =====================

    /**
     * Construit une vue de gestion des contrats client.
     *
     * @param scanner scanner pour lire les entrées utilisateur
     *
     * @throws IllegalArgumentException si le scanner est null
     */
    public VueGestionContratsClient(Scanner scanner) {
        super(scanner);
    }

    // =====================
    // MENU PRINCIPAL
    // =====================

    /**
     * Affiche le menu principal de gestion des contrats.
     * <p>
     * Options disponibles :
     * <ul>
     *   <li>1 : Consulter mes contrats</li>
     *   <li>0 : Retour au menu principal</li>
     * </ul>
     *
     *
     * @return choix de l'utilisateur (0-1)
     */
    public int afficherMenuContrats() {
        afficherTitre("Mes contrats");

        afficherMessage("1. Consulter mes contrats");
        afficherMessage("0. Retour au menu principal");
        afficherMessage("");

        return demanderEntier("Votre choix : ", 0, 1);
    }

    // =====================
    // LISTE DES CONTRATS
    // =====================

    /**
     * Affiche la liste des contrats du client.
     * <p>
     * Si le client n'a aucun contrat, affiche un message approprié
     * et attend une confirmation avant de retourner au menu.
     * Sinon, affiche une liste numérotée des contrats.
     * </p>
     *
     * @param contrats liste des représentations textuelles des contrats
     */
    public void afficherListeContrats(List<String> contrats) {
        afficherTitre("Liste de mes contrats");

        if (contrats.isEmpty()) {
            afficherMessage("Vous n'avez aucun contrat.");
            attendreEntree();
            return;
        }

        afficherListeNumerotee(contrats);

        afficherMessage("");
    }

    /**
     * Demande à l'utilisateur de sélectionner un contrat dans la liste.
     *
     * @param nombreContrats nombre total de contrats disponibles
     * @return numéro du contrat sélectionné (1 à nombreContrats) ou 0 pour retour
     */
    public int demanderSelectionContrat(int nombreContrats) {
        return demanderEntier(
                "Sélectionner un contrat (0 pour retour) : ",
                0,
                nombreContrats
        );
    }

    // =====================
    // MENU CONTEXTUEL CONTRAT
    // =====================

    /**
     * Affiche le menu contextuel d'actions sur un contrat sélectionné.
     * <p>
     * Options disponibles :
     * <ul>
     *   <li>1 : Consulter les informations du contrat</li>
     *   <li>2 : Demander un changement d'offre tarifaire</li>
     *   <li>3 : Demander un changement de mode de facturation</li>
     *   <li>0 : Retour à la liste des contrats</li>
     * </ul>
     *
     *
     * @return choix de l'utilisateur (0-3)
     */
    public int afficherMenuContratSelectionne() {
        afficherSousTitre("Actions sur le contrat");

        afficherMessage("1. Consulter les informations du contrat");
        afficherMessage("2. Demander un changement d'offre tarifaire");
        afficherMessage("3. Demander un changement de mode de facturation");
        afficherMessage("0. Retour à la liste des contrats");
        afficherMessage("");

        return demanderEntier("Votre choix : ", 0, 3);
    }

    // =====================
    // AFFICHAGE DES INFORMATIONS
    // =====================

    /**
     * Affiche les informations détaillées d'un contrat.
     * <p>
     * Présente toutes les informations du contrat formatées par le contrôleur
     * et attend une confirmation de l'utilisateur avant de retourner au menu.
     * </p>
     *
     * @param informations informations formatées du contrat
     */
    public void afficherInformationsContrat(String informations) {
        afficherTitre("Informations du contrat");
        afficherMessage(informations);
        attendreEntree();
    }

    /**
     * Affiche une demande de changement avec son titre et sa description.
     * <p>
     * Utilisé pour présenter les options de changement d'offre ou de mode
     * de facturation avant que l'utilisateur ne fasse son choix.
     * </p>
     *
     * @param titre titre de la demande
     * @param description description détaillée de la demande
     */
    public void afficherDemande(String titre, String description) {
        afficherSousTitre(titre);
        afficherMessage(description);
        afficherMessage("");
    }



}