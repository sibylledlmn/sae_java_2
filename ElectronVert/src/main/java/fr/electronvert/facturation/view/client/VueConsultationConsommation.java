package fr.electronvert.facturation.view.client;

import fr.electronvert.facturation.view.VueBase;

import java.util.List;
import java.util.Scanner;

/**
 * Vue de consultation de la consommation électrique du client.
 * <p>
 * Cette vue permet au client de :
 * <ul>
 *   <li>Accéder au menu de consultation de consommation</li>
 *   <li>Sélectionner un contrat parmi ses contrats actifs</li>
 *   <li>Consulter l'historique mensuel de consommation du contrat</li>
 * </ul>
 * L'historique présente la consommation mois par mois, calculée à partir
 * des relevés de compteur successifs.
 *
 *
 * @see VueBase
 * @see fr.electronvert.facturation.controller.client.ControleurConsultationConsommation
 */
public class VueConsultationConsommation extends VueBase {

    // =====================
    // CONSTRUCTEUR
    // =====================

    /**
     * Construit une vue de consultation de la consommation.
     *
     * @param scanner scanner pour lire les entrées utilisateur
     *
     * @throws IllegalArgumentException si le scanner est null
     */
    public VueConsultationConsommation(Scanner scanner) {
        super(scanner);
    }

    // =====================
    // MENU PRINCIPAL
    // =====================

    /**
     * Affiche le menu de consultation de la consommation.
     * <p>
     * Options disponibles :
     * <ul>
     *   <li>1 : Consulter mon historique de consommation</li>
     *   <li>0 : Retour au menu principal</li>
     * </ul>
     *
     *
     * @return choix de l'utilisateur (0-1)
     */
    public int afficherMenuConsommation() {
        afficherTitre("MA CONSOMMATION");

        afficherMessage("1. Consulter mon historique de consommation");
        afficherMessage("0. Retour au menu principal");
        afficherMessage("");

        return demanderEntier("Votre choix : ", 0, 1);
    }

    // =====================
    // SÉLECTION DU CONTRAT
    // =====================

    /**
     * Affiche la liste des contrats du client pour sélection.
     * <p>
     * Si le client n'a aucun contrat, affiche un message approprié
     * et attend une confirmation avant de retourner au menu.
     * Sinon, affiche une liste numérotée des contrats avec l'option
     * de retour (0).
     * </p>
     *
     * @param contrats liste des représentations textuelles des contrats
     */
    public void afficherListeContrats(List<String> contrats) {
        afficherTitre("CHOISIR UN CONTRAT");

        if (contrats.isEmpty()) {
            afficherMessage("Vous n'avez aucun contrat.");
            attendreEntree();
            return;
        }

        afficherListeNumerotee(contrats);
        afficherMessage("");
        afficherMessage("0. Retour");
    }

    /**
     * Demande à l'utilisateur de sélectionner un contrat dans la liste.
     *
     * @param nombreContrats nombre total de contrats disponibles
     * @return numéro du contrat sélectionné (1 à nombreContrats) ou 0 pour retour
     */
    public int demanderSelectionContrat(int nombreContrats) {
        return demanderEntier(
                "Votre choix : ",
                0,
                nombreContrats
        );
    }

    // =====================
    // AFFICHAGE DE L'HISTORIQUE
    // =====================

    /**
     * Affiche l'historique de consommation mois par mois.
     * <p>
     * Présente la consommation calculée entre chaque paire de relevés
     * successifs, du plus ancien au plus récent. Si aucune consommation
     * n'est disponible (pas assez de relevés), affiche un message approprié.
     * </p>
     * <p>
     * Chaque ligne de consommation affichée comprend :
     * <ul>
     *   <li>La période concernée (date du relevé)</li>
     *   <li>La consommation en kWh (TOTAL ou HP/HC selon l'offre)</li>
     *   <li>Éventuellement le coût estimé</li>
     * </ul>
     *
     *
     * @param lignesConsommation liste des lignes de consommation formatées,
     *                           triées chronologiquement
     */
    public void afficherHistoriqueConsommation(List<String> lignesConsommation) {
        afficherTitre("HISTORIQUE DE MA CONSOMMATION");

        if (lignesConsommation.isEmpty()) {
            afficherMessage("Aucune consommation à afficher.");
            attendreEntree();
            return;
        }

        for (String ligne : lignesConsommation) {
            afficherMessage(ligne);
        }

        attendreEntree();
    }
}