package fr.electronvert.facturation.view.administrateur;

import fr.electronvert.facturation.view.VueBase;

import java.util.Scanner;

/**
 * Vue de suivi et statistiques pour l'administrateur.
 * <p>
 * Cette vue permet à l'administrateur de consulter des indicateurs
 * globaux du système :
 * <ul>
 *   <li>Nombre de clients actifs</li>
 *   <li>Répartition des contrats (par offre et par mode de facturation)</li>
 *   <li>Chiffre d'affaires global</li>
 *   <li>Statistiques des impayés (nombre, montant total, clients concernés)</li>
 *   <li>Nombre de résiliations</li>
 * </ul>
 *

 *
 * @see VueBase
 * @see fr.electronvert.facturation.controller.administrateur.ControleurSuiviStatistiquesAdmin
 */
public class VueSuiviStatistiques extends VueBase {

    // =====================
    // CONSTRUCTEUR
    // =====================

    /**
     * Construit une vue de suivi et statistiques.
     *
     * @param scanner scanner pour lire les entrées utilisateur
     *
     * @throws IllegalArgumentException si le scanner est null
     */
    public VueSuiviStatistiques(Scanner scanner) {
        super(scanner);
    }

    // =====================
    // MENU PRINCIPAL
    // =====================

    /**
     * Affiche le menu de suivi et statistiques.
     * <p>
     * Options disponibles :
     * <ul>
     *   <li>1 : Nombre de clients actifs (clients ayant au moins un contrat actif)</li>
     *   <li>2 : Répartition des contrats (par offre tarifaire et mode de facturation)</li>
     *   <li>3 : Chiffre d'affaires global (somme de tous les paiements)</li>
     *   <li>4 : Statistiques des impayés (nombre de factures, montant total, clients concernés)</li>
     *   <li>5 : Nombre de résiliations (contrats clôturés)</li>
     *   <li>0 : Retour au menu administrateur</li>
     * </ul>
     *
     *
     * @return choix de l'utilisateur (0-5)
     */
    public int afficherMenuSuiviStatistiques() {
        afficherTitre("Suivi et statistiques");

        afficherMessage("1. Nombre de clients actifs");
        afficherMessage("2. Répartition des contrats");
        afficherMessage("3. Chiffre d'affaires global");
        afficherMessage("4. Statistiques des impayés");
        afficherMessage("5. Nombre de résiliations");
        afficherMessage("0. Retour au menu administrateur");
        afficherMessage("");

        return demanderEntier("Votre choix : ", 0, 5);
    }

    // =====================
    // AFFICHAGE DES STATISTIQUES
    // =====================

    /**
     * Affiche une statistique avec son titre et son contenu.
     * <p>
     * Utilisé pour présenter de manière uniforme toutes les statistiques
     * du système. Le contenu est formaté par le contrôleur et peut inclure
     * plusieurs lignes d'information.
     * </p>
     * <p>
     * Attend une confirmation de l'utilisateur avant de retourner au menu.
     * </p>
     *
     * @param titre titre de la statistique affichée
     * @param contenu contenu formaté de la statistique
     */
    public void afficherStatistique(String titre, String contenu) {
        afficherTitre(titre);
        afficherMessage(contenu);
        attendreEntree();
    }

    /**
     * Affiche un message indiquant qu'aucune donnée n'est disponible.
     * <p>
     * Utilisé lorsqu'une statistique ne peut pas être calculée faute
     * de données (par exemple, aucun client, aucune facture, etc.).
     * </p>
     * <p>
     * Attend une confirmation de l'utilisateur avant de retourner au menu.
     * </p>
     */
    public void afficherMessageAucuneDonnee() {
        afficherMessage("Aucune donnée disponible.");
        attendreEntree();
    }
}