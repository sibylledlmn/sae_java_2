package fr.electronvert.facturation.view.administrateur;

import fr.electronvert.facturation.view.VueBase;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Vue de gestion des tarifs pour l'administrateur.
 * <p>
 * Cette vue permet à l'administrateur de :
 * <ul>
 *   <li>Consulter le tarif actuellement en vigueur</li>
 *   <li>Consulter l'historique complet des tarifs</li>
 *   <li>Créer un nouveau tarif</li>
 * </ul>
 * La création d'un tarif nécessite la saisie de tous les prix
 * (kWh et abonnements) pour les offres Classique et HP/HC.
 *
 *
 * @see VueBase
 * @see fr.electronvert.facturation.controller.administrateur.ControleurGestionTarifsAdmin
 */
public class VueGestionTarifs extends VueBase {

    // =====================
    // CONSTRUCTEUR
    // =====================

    /**
     * Construit une vue de gestion des tarifs.
     *
     * @param scanner scanner pour lire les entrées utilisateur
     *
     * @throws IllegalArgumentException si le scanner est null
     */
    public VueGestionTarifs(Scanner scanner) {
        super(scanner);
    }

    // =====================
    // MENU PRINCIPAL
    // =====================

    /**
     * Affiche le menu de gestion des tarifs.
     * <p>
     * Options disponibles :
     * <ul>
     *   <li>1 : Consulter le tarif actuel</li>
     *   <li>2 : Consulter l'historique des tarifs</li>
     *   <li>3 : Créer un nouveau tarif</li>
     *   <li>0 : Retour au menu administrateur</li>
     * </ul>
     *
     *
     * @return choix de l'utilisateur (0-3)
     */
    public int afficherMenuGestionTarifs() {
        afficherTitre("Gestion des tarifs");

        afficherMessage("1. Consulter le tarif actuel");
        afficherMessage("2. Consulter l'historique des tarifs");
        afficherMessage("3. Créer un nouveau tarif");
        afficherMessage("0. Retour au menu administrateur");
        afficherMessage("");

        return demanderEntier("Votre choix : ", 0, 3);
    }

    // =====================
    // CONSULTATION DES TARIFS
    // =====================

    /**
     * Affiche le tarif actuellement en vigueur.
     * <p>
     * Présente le tarif actuellement en vigueur avec tous ses détails :
     * date de début, prix du kWh, prix des abonnements.
     * </p>
     *
     * @param tarifActuel représentation textuelle formatée du tarif actuel
     */
    public void afficherTarifActuel(String tarifActuel) {
        afficherTitre("Tarif actuel");
        afficherMessage(tarifActuel);
        attendreEntree();
    }

    /**
     * Affiche l'historique complet des tarifs.
     * <p>
     * Présente tous les tarifs qui ont été en vigueur, du plus ancien
     * au plus récent, avec une ligne de séparation entre chaque tarif.
     * Si aucun tarif n'existe, affiche un message approprié.
     * </p>
     *
     * @param tarifs liste des représentations textuelles des tarifs,
     *               triée chronologiquement
     */
    public void afficherHistoriqueTarifs(List<String> tarifs) {
        afficherTitre("Historique des tarifs ElectronVert");

        if (tarifs.isEmpty()) {
            afficherMessage("Aucun tarif disponible.");
            attendreEntree();
            return;
        }

        for (String tarif : tarifs) {
            afficherMessage(tarif);
            afficherLigneSeparation();
        }

        attendreEntree();
    }

    // =====================
    // CRÉATION D'UN NOUVEAU TARIF
    // =====================

    /**
     * Demande la date d'entrée en vigueur du nouveau tarif.
     * <p>
     * Affiche un message informatif rappelant que les tarifs entrent
     * en vigueur uniquement le 1er février ou le 1er août.
     * </p>
     *
     * @return date d'entrée en vigueur saisie par l'utilisateur
     */
    public LocalDate demanderDateEntreeVigueurTarif() {
        afficherSousTitre("Création d'un nouveau tarif");
        afficherMessage("Information : les tarifs entrent en vigueur le 1er février ou le 1er août.");
        afficherMessage("");

        return demanderDate("Date d'entrée en vigueur");
    }

    /**
     * Demande le prix du kWh pour l'offre Classique.
     *
     * @return prix du kWh en euros (doit être positif)
     */
    public double demanderPrixKwhClassique() {
        return demanderDecimalPositif("Prix du kWh Classique (€) : ");
    }

    /**
     * Demande le prix du kWh en heures pleines pour l'offre HP/HC.
     *
     * @return prix du kWh en heures pleines en euros (doit être positif)
     */
    public double demanderPrixKwhHeuresPleines() {
        return demanderDecimalPositif("Prix du kWh Heures Pleines (€) : ");
    }

    /**
     * Demande le prix du kWh en heures creuses pour l'offre HP/HC.
     *
     * @return prix du kWh en heures creuses en euros (doit être positif)
     */
    public double demanderPrixKwhHeuresCreuses() {
        return demanderDecimalPositif("Prix du kWh Heures Creuses (€) : ");
    }

    /**
     * Demande le prix de l'abonnement mensuel pour l'offre Classique.
     *
     * @return prix de l'abonnement mensuel en euros (doit être positif)
     */
    public double demanderPrixAbonnementClassique() {
        return demanderDecimalPositif("Prix abonnement mensuel Classique (€) : ");
    }

    /**
     * Demande le prix de l'abonnement mensuel pour l'offre HP/HC.
     *
     * @return prix de l'abonnement mensuel en euros (doit être positif)
     */
    public double demanderPrixAbonnementHPHC() {
        return demanderDecimalPositif("Prix abonnement mensuel HP/HC (€) : ");
    }

    /**
     * Affiche le récapitulatif du nouveau tarif avant confirmation.
     * <p>
     * Permet à l'administrateur de vérifier toutes les informations saisies
     * avant la création définitive du tarif.
     * </p>
     *
     * @param recapitulatif récapitulatif formaté du tarif à créer
     */
    public void afficherRecapitulatifTarif(String recapitulatif) {
        afficherSousTitre("Récapitulatif du nouveau tarif");
        afficherMessage(recapitulatif);
        afficherMessage("");
    }

    /**
     * Affiche un message de confirmation après la création réussie du tarif.
     * <p>
     * Attend une confirmation de l'utilisateur avant de retourner au menu.
     * </p>
     */
    public void afficherConfirmationCreationTarif() {
        afficherMessage("Le nouveau tarif a été créé avec succès.");
        attendreEntree();
    }
}