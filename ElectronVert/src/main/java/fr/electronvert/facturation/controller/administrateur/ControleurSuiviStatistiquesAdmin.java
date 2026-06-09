package fr.electronvert.facturation.controller.administrateur;

import fr.electronvert.facturation.service.GestionnaireClients;
import fr.electronvert.facturation.service.GestionnaireContrats;
import fr.electronvert.facturation.service.GestionnaireFactures;
import fr.electronvert.facturation.service.GestionnairePaiements;
import fr.electronvert.facturation.view.administrateur.VueSuiviStatistiques;

import java.util.Scanner;

/**
 * Contrôleur de suivi et statistiques pour l'administrateur.
 * <p>
 * Ce contrôleur permet à l'administrateur de consulter des indicateurs
 * globaux sur l'activité du système :
 * <ul>
 *   <li>Nombre de clients actifs (ayant au moins un contrat actif)</li>
 *   <li>Répartition des contrats (par offre tarifaire et mode de facturation)</li>
 *   <li>Chiffre d'affaires global (montant total encaissé)</li>
 *   <li>Statistiques des impayés (nombre, montant, clients concernés, taux)</li>
 *   <li>Nombre de résiliations (contrats clôturés et taux de résiliation)</li>
 * </ul>
 * Ces statistiques permettent un pilotage et un suivi de l'activité
 * de l'entreprise ElectronVert en temps réel.
 *
 *
 * @see VueSuiviStatistiques
 * @see GestionnaireClients
 * @see GestionnaireContrats
 * @see GestionnaireFactures
 * @see GestionnairePaiements
 */
public class ControleurSuiviStatistiquesAdmin {

    // =====================
    // ATTRIBUTS
    // =====================

    /**
     * Vue de suivi et statistiques.
     */
    private final VueSuiviStatistiques vue;

    /**
     * Gestionnaire des clients pour les statistiques clients.
     */
    private final GestionnaireClients gestionnaireClients;

    /**
     * Gestionnaire des contrats pour les statistiques contrats.
     */
    private final GestionnaireContrats gestionnaireContrats;

    /**
     * Gestionnaire des factures pour les statistiques de facturation et impayés.
     */
    private final GestionnaireFactures gestionnaireFactures;

    /**
     * Gestionnaire des paiements pour le calcul du chiffre d'affaires.
     */
    private final GestionnairePaiements gestionnairePaiements;

    // =====================
    // CONSTRUCTEUR
    // =====================

    /**
     * Construit le contrôleur de suivi et statistiques.
     *
     * @param scanner scanner pour les entrées utilisateur
     * @param gestionnaireClients gestionnaire des clients
     * @param gestionnaireContrats gestionnaire des contrats
     * @param gestionnaireFactures gestionnaire des factures
     * @param gestionnairePaiements gestionnaire des paiements
     *
     * @throws IllegalArgumentException si l'un des paramètres est null
     */
    public ControleurSuiviStatistiquesAdmin(
            Scanner scanner,
            GestionnaireClients gestionnaireClients,
            GestionnaireContrats gestionnaireContrats,
            GestionnaireFactures gestionnaireFactures,
            GestionnairePaiements gestionnairePaiements
    ) {
        if (scanner == null || gestionnaireClients == null
                || gestionnaireContrats == null || gestionnaireFactures == null
                || gestionnairePaiements == null) {
            throw new IllegalArgumentException("Tous les paramètres sont requis");
        }

        this.vue = new VueSuiviStatistiques(scanner);
        this.gestionnaireClients = gestionnaireClients;
        this.gestionnaireContrats = gestionnaireContrats;
        this.gestionnaireFactures = gestionnaireFactures;
        this.gestionnairePaiements = gestionnairePaiements;
    }

    // =====================
    // GESTION DU MENU
    // =====================

    /**
     * Lance le menu de suivi et statistiques.
     * <p>
     * Options disponibles :
     * <ul>
     *   <li>1 : Nombre de clients actifs</li>
     *   <li>2 : Répartition des contrats</li>
     *   <li>3 : Chiffre d'affaires global</li>
     *   <li>4 : Statistiques des impayés</li>
     *   <li>5 : Nombre de résiliations</li>
     *   <li>0 : Retour au menu administrateur</li>
     * </ul>
     * Les erreurs lors de la récupération ou du calcul des statistiques
     * sont capturées et affichées à l'utilisateur.
     *
     */
    public void demarrer() {
        boolean retour = false;

        while (!retour) {
            int choix = vue.afficherMenuSuiviStatistiques();

            try {
                switch (choix) {
                    case 1 -> afficherNombreClientsActifs();
                    case 2 -> afficherRepartitionContrats();
                    case 3 -> afficherChiffreAffairesGlobal();
                    case 4 -> afficherStatistiquesImpayes();
                    case 5 -> afficherNombreResiliations();
                    case 0 -> retour = true;
                }
            } catch (Exception e) {
                vue.afficherStatistique("Erreur", e.getMessage());
            }
        }
    }

    // =====================
    // STATISTIQUES CLIENTS
    // =====================

    /**
     * Affiche le nombre de clients ayant au moins un contrat actif.
     * <p>
     * Un client est considéré comme actif s'il possède au moins un contrat
     * dont le statut est ACTIF.
     * </p>
     */
    private void afficherNombreClientsActifs() {
        long nombreClientsActifs = gestionnaireClients.getNombreClientsActifs();

        String contenu =
                "Clients avec contrats actifs : " + nombreClientsActifs + "\n";

        vue.afficherStatistique("Nombre de clients avec des contrats actifs", contenu);
    }

    // =====================
    // STATISTIQUES CONTRATS
    // =====================

    /**
     * Affiche la répartition des contrats par offre et mode de facturation.
     * <p>
     * Présente :
     * <ul>
     *   <li>Répartition par offre tarifaire (Classique vs HP/HC) avec pourcentages</li>
     *   <li>Répartition par mode de facturation (Réel vs Échéancier) avec pourcentages</li>
     *   <li>Total des contrats actifs</li>
     * </ul>
     * Si aucun contrat actif n'existe, affiche un message approprié.
     * </p>
     */
    private void afficherRepartitionContrats() {
        long nombreActifs = gestionnaireContrats.getNombreContratsActifs();

        if (nombreActifs == 0) {
            vue.afficherMessageAucuneDonnee();
            return;
        }

        long nombreClassique = gestionnaireContrats.getNombreContratsOffreClassique();
        long nombreHPHC = gestionnaireContrats.getNombreContratsOffreHPHC();
        long nombreReel = gestionnaireContrats.getNombreContratsModeReel();
        long nombreEcheancier = gestionnaireContrats.getNombreContratsModeEcheancier();

        String contenu =
                "=== Répartition par offre tarifaire ===\n"
                        + "Offre Classique : " + nombreClassique + " ("
                        + arrondir(nombreClassique * 100.0 / nombreActifs, 1) + " %)\n"
                        + "Offre HP/HC : " + nombreHPHC + " ("
                        + arrondir(nombreHPHC * 100.0 / nombreActifs, 1) + " %)\n\n"
                        + "=== Répartition par mode de facturation ===\n"
                        + "Facturation réelle : " + nombreReel + " ("
                        + arrondir(nombreReel * 100.0 / nombreActifs, 1) + " %)\n"
                        + "Échéancier : " + nombreEcheancier + " ("
                        + arrondir(nombreEcheancier * 100.0 / nombreActifs, 1) + " %)\n\n"
                        + "Total contrats actifs : " + nombreActifs;

        vue.afficherStatistique("Répartition des contrats", contenu);
    }

    /**
     * Affiche le nombre de contrats résiliés et le taux de résiliation.
     * <p>
     * Présente :
     * <ul>
     *   <li>Nombre de contrats clôturés</li>
     *   <li>Nombre de contrats actifs</li>
     *   <li>Total des contrats</li>
     *   <li>Taux de résiliation (% de contrats clôturés sur le total)</li>
     * </ul>
     * </p>
     */
    private void afficherNombreResiliations() {
        long nombreClotures = gestionnaireContrats.getNombreContratsClotures();
        long nombreActifs = gestionnaireContrats.getNombreContratsActifs();
        int totalContrats = gestionnaireContrats.getContrats().size();

        double tauxResiliation = totalContrats > 0
                ? arrondir(nombreClotures * 100.0 / totalContrats, 1)
                : 0.0;

        String contenu =
                "Contrats clôturés : " + nombreClotures + "\n"
                        + "Contrats actifs : " + nombreActifs + "\n"
                        + "Total contrats : " + totalContrats + "\n\n"
                        + "Taux de résiliation : " + tauxResiliation + " %";

        vue.afficherStatistique("Nombre de résiliations", contenu);
    }

    // =====================
    // STATISTIQUES FINANCIÈRES
    // =====================

    /**
     * Affiche le chiffre d'affaires global et l'état de la facturation.
     * <p>
     * Présente :
     * <ul>
     *   <li>Montant total encaissé (somme de tous les paiements)</li>
     *   <li>Nombre de paiements effectués</li>
     *   <li>Nombre de factures payées</li>
     *   <li>Nombre de factures en attente de paiement</li>
     *   <li>Total des factures émises</li>
     * </ul>
     * Si aucun paiement n'a été effectué, affiche un message approprié.
     * </p>
     */
    private void afficherChiffreAffairesGlobal() {
        double chiffreAffaires = gestionnairePaiements.calculerChiffreAffaires();
        int nombrePaiements = gestionnairePaiements.getPaiements().size();

        if (nombrePaiements == 0) {
            vue.afficherMessageAucuneDonnee();
            return;
        }

        long facturesPayees = gestionnaireFactures.getNombreFacturesPayees();
        long facturesAPayer = gestionnaireFactures.getNombreFacturesImpayees() + gestionnaireFactures.getNombreFacturesEmises();
        int totalFactures = gestionnaireFactures.getFactures().size();

        String contenu =
                "=== Chiffre d'affaires ===\n"
                        + "Montant total encaissé : " + arrondir(chiffreAffaires, 2) + " € TTC\n"
                        + "Nombre de paiements : " + nombrePaiements + "\n\n"
                        + "=== État de la facturation ===\n"
                        + "Factures payées : " + facturesPayees + "\n"
                        + "Factures en attente de paiement : " + facturesAPayer + "\n"
                        + "Total factures : " + totalFactures;

        vue.afficherStatistique("Chiffre d'affaires global", contenu);
    }

    /**
     * Affiche les statistiques détaillées sur les factures impayées.
     * <p>
     * Présente :
     * <ul>
     *   <li>Nombre de factures impayées</li>
     *   <li>Montant total des impayés (TTC)</li>
     *   <li>Nombre de clients concernés</li>
     *   <li>Taux d'impayés (% de factures impayées sur le total)</li>
     * </ul>
     * Si aucune facture impayée n'existe, affiche un message de félicitation
     * pour l'excellent taux de recouvrement.
     * </p>
     */
    private void afficherStatistiquesImpayes() {
        long nombreImpayes = gestionnaireFactures.getNombreFacturesImpayees();

        if (nombreImpayes == 0) {
            String contenu =
                    "Aucune facture impayée.\n\n";
            vue.afficherStatistique("Statistiques des impayés", contenu);
            return;
        }

        double montantTotalImpayes = gestionnaireFactures.getMontantTotalImpayes();
        long nombreClientsImpayes = gestionnaireFactures.getNombreClientsAvecImpayes();
        int totalFactures = gestionnaireFactures.getFactures().size();



        String contenu =
                "=== Factures impayées ===\n"
                        + "Nombre de factures : " + nombreImpayes + "\n"
                        + "Montant total (TTC) : " + arrondir(montantTotalImpayes, 2) + " €\n\n"
                        + "=== Impact ===\n"
                        + "Clients concernés : " + nombreClientsImpayes + "\n";

        vue.afficherStatistique("Statistiques des impayés", contenu);
    }

    // =====================
    // UTILITAIRES
    // =====================

    /**
     * Arrondit une valeur à un nombre de décimales spécifié.
     *
     * @param valeur valeur à arrondir
     * @param decimales nombre de décimales souhaitées
     * @return valeur arrondie
     */
    private double arrondir(double valeur, int decimales) {
        double facteur = Math.pow(10, decimales);
        return Math.round(valeur * facteur) / facteur;
    }
}