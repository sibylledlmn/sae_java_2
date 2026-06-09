package fr.electronvert.facturation.view.client;

import fr.electronvert.facturation.view.VueBase;

import java.util.List;
import java.util.Scanner;

/**
 * Vue de gestion des factures et des paiements pour un client.
 * <p>
 * Cette vue permet au client :
 * <ul>
 *     <li>de sélectionner l'un de ses contrats</li>
 *     <li>de consulter les factures associées à ce contrat</li>
 *     <li>de filtrer les factures (toutes, payées, impayées)</li>
 *     <li>d'effectuer un paiement</li>
 *     <li>de consulter l'historique de ses paiements</li>
 * </ul>
 *
 *
 */
public class VueGestionFacturesEtPaiements extends VueBase {

    /**
     * Construit la vue de gestion des factures et paiements.
     *
     * @param scanner scanner utilisé pour la saisie utilisateur
     */
    public VueGestionFacturesEtPaiements(Scanner scanner) {
        super(scanner);
    }

    // =====================
    // SÉLECTION DU CONTRAT
    // =====================

    /**
     * Affiche la liste des contrats du client pour sélection.
     *
     * @param contrats liste des résumés de contrats
     */
    public void afficherListeContrats(List<String> contrats) {
        afficherTitre("Sélection du contrat");

        if (contrats.isEmpty()) {
            afficherMessage("Vous n'avez aucun contrat.");
            attendreEntree();
            return;
        }

        afficherListeNumerotee(contrats);
        afficherMessage("");
    }

    /**
     * Demande au client de sélectionner un contrat.
     *
     * @param nombreContrats nombre total de contrats disponibles
     * @return indice du contrat sélectionné (0 pour retour)
     */
    public int demanderSelectionContrat(int nombreContrats) {
        return demanderEntier(
                "Sélectionner un contrat (0 pour retour) : ",
                0,
                nombreContrats
        );
    }

    // =====================
    // MENU FACTURES & PAIEMENTS
    // =====================

    /**
     * Affiche le menu principal de gestion des factures et paiements.
     *
     * @return choix de l'utilisateur
     */
    public int afficherMenuFacturesEtPaiements() {
        afficherTitre("Factures et paiements");

        afficherMessage("1. Consulter toutes les factures du contrat");
        afficherMessage("2. Consulter les factures impayées");
        afficherMessage("3. Consulter les factures payées");
        afficherMessage("4. Payer une facture");
        afficherMessage("5. Consulter l'historique des paiements");
        afficherMessage("0. Retour à la sélection du contrat");
        afficherMessage("");

        return demanderEntier("Votre choix : ", 0, 5);
    }

    // =====================
    // FACTURES
    // =====================

    /**
     * Affiche une liste de factures avec un titre personnalisé.
     *
     * @param titre titre de l'affichage
     * @param resumesFactures résumés des factures
     */
    public void afficherListeFactures(String titre, List<String> resumesFactures) {
        afficherTitre(titre);

        if (resumesFactures.isEmpty()) {
            afficherMessage("Aucune facture à afficher.");
            attendreEntree();
            return;
        }

        afficherListeNumerotee(resumesFactures);

        afficherLigneSeparation();
        afficherMessage("0. Retour");
        afficherMessage("Sélectionnez une facture pour voir le détail");
        afficherMessage("");
    }

    /**
     * Demande la sélection d'une facture dans une liste.
     *
     * @param nbFactures nombre de factures affichées
     * @return indice de la facture choisie (0 pour retour)
     */
    public int demanderChoixFacture(int nbFactures) {
        return demanderEntier("Votre choix : ", 0, nbFactures);
    }

    /**
     * Affiche les détails complets d'une facture.
     *
     * @param details détails de la facture
     */
    public void afficherDetailsFacture(String details) {
        afficherTitre("Détails de la facture");
        afficherMessage(details);
        attendreEntree();
    }

    // =====================
    // PAIEMENT
    // =====================

    /**
     * Affiche la liste des factures pouvant être payées.
     *
     * @param resumesFactures résumés des factures à payer
     */
    public void afficherFacturesAPayer(List<String> resumesFactures) {
        afficherTitre("Factures à payer");

        if (resumesFactures.isEmpty()) {
            afficherMessage("Vous n'avez aucune facture à payer.");
            attendreEntree();
            return;
        }

        afficherListeNumerotee(resumesFactures);

        afficherLigneSeparation();
        afficherMessage("0. Annuler");
        afficherMessage("");
    }

    /**
     * Demande la sélection d'une facture à payer.
     *
     * @param nbFactures nombre de factures disponibles
     * @return indice de la facture à payer (0 pour annuler)
     */
    public int demanderChoixFactureAPayer(int nbFactures) {
        return demanderEntier("Numéro de la facture à payer : ", 0, nbFactures);
    }



    /**
     * Affiche un message de confirmation après un paiement réussi.
     *
     * @param detailsPaiement détails du paiement effectué
     */
    public void afficherPaiementReussi(String detailsPaiement) {
        afficherMessage("Paiement effectué avec succès.");
        afficherMessage(detailsPaiement);
        attendreEntree();
    }

    // =====================
    // HISTORIQUE DES PAIEMENTS
    // =====================

    /**
     * Affiche l'historique des paiements du client.
     *
     * @param resumesPaiements résumés des paiements
     */
    public void afficherListePaiements(List<String> resumesPaiements) {
        afficherTitre("Historique des paiements");

        if (resumesPaiements.isEmpty()) {
            afficherMessage("Vous n'avez effectué aucun paiement.");
            attendreEntree();
            return;
        }

        afficherListeNumerotee(resumesPaiements);

        afficherLigneSeparation();
        afficherMessage("0. Retour");
        afficherMessage("Sélectionnez un paiement pour voir le détail");
        afficherMessage("");
    }

    /**
     * Demande la sélection d'un paiement.
     *
     * @param nbPaiements nombre de paiements affichés
     * @return indice du paiement choisi (0 pour retour)
     */
    public int demanderChoixPaiement(int nbPaiements) {
        return demanderEntier("Votre choix : ", 0, nbPaiements);
    }

    /**
     * Affiche les détails d'un paiement.
     *
     * @param details détails du paiement
     */
    public void afficherDetailsPaiement(String details) {
        afficherTitre("Détails du paiement");
        afficherMessage(details);
        attendreEntree();
    }
}
