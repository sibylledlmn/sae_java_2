package fr.electronvert.facturation.view.administrateur;

import fr.electronvert.facturation.view.VueBase;

import java.util.List;
import java.util.Scanner;

/**
 * Vue de gestion des contrats pour l'administrateur.
 * <p>
 * Cette vue permet à l'administrateur de :
 * <ul>
 *   <li>Rechercher un contrat par sa référence</li>
 *   <li>Lister tous les contrats d'un client</li>
 *   <li>Créer un nouveau contrat pour un client</li>
 *   <li>Consulter les détails d'un contrat</li>
 *   <li>Consulter l'historique des relevés d'un contrat</li>
 *   <li>Consulter les factures associées à un contrat</li>
 *   <li>Clôturer un contrat</li>
 * </ul>
 * La création d'un contrat nécessite la saisie de l'adresse,
 * le choix de l'offre tarifaire et du mode de facturation.
 *
 *
 * @see VueBase
 * @see fr.electronvert.facturation.controller.administrateur.ControleurGestionContratsAdmin
 */
public class VueGestionContrats extends VueBase {

    // =====================
    // CONSTRUCTEUR
    // =====================

    /**
     * Construit une vue de gestion des contrats.
     *
     * @param scanner scanner pour lire les entrées utilisateur
     *
     * @throws IllegalArgumentException si le scanner est null
     */
    public VueGestionContrats(Scanner scanner) {
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
     *   <li>1 : Rechercher un contrat par référence</li>
     *   <li>2 : Lister les contrats d'un client</li>
     *   <li>3 : Créer un nouveau contrat</li>
     *   <li>0 : Retour au menu administrateur</li>
     * </ul>
     *
     *
     * @return choix de l'utilisateur (0-3)
     */
    public int afficherMenuGestionContrats() {
        afficherTitre("Gestion des contrats");

        afficherMessage("1. Rechercher un contrat par référence");
        afficherMessage("2. Lister les contrats d'un client");
        afficherMessage("3. Créer un nouveau contrat");
        afficherMessage("0. Retour au menu administrateur");
        afficherMessage("");

        return demanderEntier("Votre choix : ", 0, 3);
    }

    // =====================
    // RECHERCHE DE CONTRAT
    // =====================

    /**
     * Demande la référence d'un contrat pour la recherche.
     *
     * @return référence du contrat saisie par l'utilisateur (non vide)
     */
    public String demanderReferenceContrat() {
        afficherSousTitre("Recherche d'un contrat");
        return demanderTexteNonVide("Référence du contrat : ");
    }

    // =====================
    // AFFICHAGE DES LISTES
    // =====================

    /**
     * Affiche la liste des contrats.
     * <p>
     * Présente tous les contrats trouvés avec une ligne de séparation
     * entre chaque contrat. Si aucun contrat n'est trouvé, affiche
     * un message approprié.
     * </p>
     *
     * @param contrats liste des représentations textuelles des contrats
     */
    public void afficherListeContrats(List<String> contrats) {
        afficherTitre("Liste des contrats");

        if (contrats.isEmpty()) {
            afficherMessage("Aucun contrat trouvé.");
            attendreEntree();
            return;
        }

        for (String contrat : contrats) {
            afficherMessage(contrat);
            afficherLigneSeparation();
        }

        attendreEntree();
    }

    /**
     * Affiche la liste des factures d'un contrat.
     * <p>
     * Présente toutes les factures avec une ligne de séparation
     * entre chaque facture. Si aucune facture n'existe, affiche
     * un message approprié.
     * </p>
     *
     * @param factures liste des représentations textuelles des factures
     */
    public void afficherListeFactures(List<String> factures) {
        afficherTitre("Liste des factures");

        if (factures.isEmpty()) {
            afficherMessage("Aucune facture trouvée.");
            attendreEntree();
            return;
        }

        for (String facture : factures) {
            afficherMessage(facture);
            afficherLigneSeparation();
        }

        attendreEntree();
    }

    // =====================
    // CRÉATION D'UN CONTRAT
    // =====================

    /**
     * Demande l'adresse du logement pour le nouveau contrat.
     *
     * @return adresse du logement saisie par l'utilisateur (non vide)
     */
    public String demanderAdresseContrat() {
        afficherSousTitre("Création d'un contrat");
        return demanderTexteNonVide("Adresse du logement : ");
    }

    /**
     * Demande le choix de l'offre tarifaire pour le nouveau contrat.
     * <p>
     * Offres disponibles :
     * <ul>
     *   <li>1 : Classique (tarif unique)</li>
     *   <li>2 : Heures Pleines / Heures Creuses</li>
     * </ul>
     *
     *
     * @return choix de l'offre (1 pour Classique, 2 pour HP/HC)
     */
    public int demanderChoixOffreTarifaire() {
        afficherMessage("Offre tarifaire :");
        afficherMessage("1. Classique");
        afficherMessage("2. Heures Pleines / Heures Creuses");

        return demanderEntier("Votre choix : ", 1, 2);
    }

    /**
     * Demande le mode de facturation pour le nouveau contrat.
     * <p>
     * Modes disponibles :
     * <ul>
     *   <li>1 : Facturation mensuelle au réel (facture mensuelle basée sur la consommation)</li>
     *   <li>2 : Échéancier (mensualités fixes + régularisation annuelle)</li>
     * </ul>
     *
     *
     * @return choix du mode (1 pour REEL, 2 pour ECHEANCIER)
     */
    public int demanderModeFacturation() {
        afficherMessage("Mode de facturation :");
        afficherMessage("1. Facturation mensuelle au réel");
        afficherMessage("2. Échéancier");

        return demanderEntier("Votre choix : ", 1, 2);
    }

    /**
     * Affiche un message de confirmation après la création réussie du contrat.
     * <p>
     * Attend une confirmation de l'utilisateur avant de retourner au menu.
     * </p>
     */
    public void afficherContratCree() {
        afficherMessage("Le contrat a été créé avec succès.");
        attendreEntree();
    }

    // =====================
    // MENU CONTEXTUEL CONTRAT
    // =====================

    /**
     * Affiche le menu contextuel d'actions sur un contrat sélectionné.
     * <p>
     * Options disponibles :
     * <ul>
     *   <li>1 : Voir les détails du contrat (informations complètes)</li>
     *   <li>2 : Voir l'historique des relevés (consommation mois par mois)</li>
     *   <li>3 : Voir les factures du contrat (toutes les factures émises)</li>
     *   <li>4 : Clôturer le contrat (fin de fourniture)</li>
     *   <li>0 : Retour</li>
     * </ul>
     *
     *
     * @return choix de l'utilisateur (0-4)
     */
    public int afficherMenuContextuelContrat() {
        afficherSousTitre("Actions sur le contrat");

        afficherMessage("1. Voir les détails du contrat");
        afficherMessage("2. Voir l'historique des relevés");
        afficherMessage("3. Voir les factures du contrat");
        afficherMessage("4. Clôturer le contrat");
        afficherMessage("0. Retour");
        afficherMessage("");

        return demanderEntier("Votre choix : ", 0, 4);
    }

    // =====================
    // AFFICHAGE DES DÉTAILS
    // =====================

    /**
     * Affiche les détails complets d'un contrat.
     * <p>
     * Les détails incluent toutes les informations du contrat :
     * référence, client, adresse, offre tarifaire, mode de facturation,
     * dates, statut, etc.
     * </p>
     *
     * @param details détails formatés du contrat
     */
    public void afficherDetailsContrat(String details) {
        afficherTitre("Détails du contrat");
        afficherMessage(details);
        attendreEntree();
    }
}