package fr.electronvert.facturation.view.client;

import fr.electronvert.facturation.view.VueBase;

import java.util.List;
import java.util.Scanner;

/**
 * Vue de consultation des tarifs d'électricité ElectronVert.
 * <p>
 * Cette vue permet aux clients de consulter :
 * <ul>
 *   <li>Le tarif actuellement en vigueur</li>
 *   <li>L'historique complet des tarifs (changements passés)</li>
 * </ul>
 *
 *
 * @see VueBase
 * @see fr.electronvert.facturation.controller.client.ControleurConsultationTarifs
 */
public class VueConsultationTarifs extends VueBase {

    // =====================
    // CONSTRUCTEUR
    // =====================

    /**
     * Construit une vue de consultation des tarifs.
     *
     * @param scanner scanner pour lire les entrées utilisateur
     *
     * @throws IllegalArgumentException si le scanner est null
     */
    public VueConsultationTarifs(Scanner scanner) {
        super(scanner);
    }

    // =====================
    // AFFICHAGE DES TARIFS
    // =====================

    /**
     * Affiche l'historique complet des tarifs d'électricité.
     * <p>
     * Présente tous les tarifs qui ont été en vigueur, du plus ancien
     * au plus récent, avec une ligne de séparation entre chaque période.
     * Si aucun tarif n'existe dans l'historique, affiche un message approprié.
     * </p>
     * <p>
     * Chaque tarif affiché comprend :
     * <ul>
     *   <li>La date de début d'application</li>
     *   <li>Les prix du kWh pour les offres Classique et HP/HC</li>
     *   <li>Les prix des abonnements annuels</li>
     * </ul>
     *
     *
     * @param tarifsList liste des représentations textuelles des tarifs,
     *                   triée par date croissante (du plus ancien au plus récent)
     */
    public void afficherHistoriqueTarifs(List<String> tarifsList) {
        afficherTitre("HISTORIQUE DES TARIFS ELECTRONVERT");

        if (tarifsList.isEmpty()) {
            afficherMessage("Aucun tarif disponible.");
            attendreEntree();
            return;
        }

        // Affichage de chaque tarif avec séparation visuelle
        for (int i = 0; i < tarifsList.size(); i++) {
            afficherMessage(tarifsList.get(i));

            // Ligne de séparation entre les tarifs (sauf après le dernier)
            if (i < tarifsList.size() - 1) {
                afficherLigneSeparation();
            }
        }

        attendreEntree();
    }

    /**
     * Affiche le tarif actuellement en vigueur.
     * <p>
     * Présente le tarif en cours d'application, c'est-à-dire le plus récent
     * dont la date de début est antérieure ou égale à la date du jour.
     * </p>
     * <p>
     * Le tarif affiché comprend :
     * <ul>
     *   <li>La date de début d'application</li>
     *   <li>Les prix du kWh pour les offres Classique et HP/HC</li>
     *   <li>Les prix des abonnements annuels</li>
     * </ul>
     *
     *
     * @param tarifActuel représentation textuelle formatée du tarif actuel
     */
    public void afficherTarifActuel(String tarifActuel) {
        afficherTitre("TARIF ACTUEL");
        afficherMessage(tarifActuel);
        attendreEntree();
    }
}