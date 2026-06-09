package fr.electronvert.facturation.view.administrateur;

import fr.electronvert.facturation.view.VueBase;

import java.util.List;
import java.util.Scanner;

/**
 * Vue de gestion des clients pour l'administrateur.
 * <p>
 * Cette vue permet à l'administrateur de :
 * <ul>
 *   <li>Créer un nouveau client</li>
 *   <li>Rechercher un client par son email</li>
 *   <li>Lister tous les clients enregistrés</li>
 *   <li>Consulter les informations d'un client</li>
 *   <li>Accéder aux contrats d'un client</li>
 *   <li>Créer un nouveau contrat pour un client</li>
 *   <li>Consulter les factures d'un client</li>
 * </ul>
 * La création d'un client nécessite la saisie du nom, prénom et email.
 * L'email sert d'identifiant unique pour la recherche.
 *
 *
 * @see VueBase
 * @see fr.electronvert.facturation.controller.administrateur.ControleurGestionClientsAdmin
 */
public class VueGestionClients extends VueBase {

    // =====================
    // CONSTRUCTEUR
    // =====================

    /**
     * Construit une vue de gestion des clients.
     *
     * @param scanner scanner pour lire les entrées utilisateur
     *
     * @throws IllegalArgumentException si le scanner est null
     */
    public VueGestionClients(Scanner scanner) {
        super(scanner);
    }

    // =====================
    // MENU PRINCIPAL
    // =====================

    /**
     * Affiche le menu principal de gestion des clients.
     * <p>
     * Options disponibles :
     * <ul>
     *   <li>1 : Créer un nouveau client</li>
     *   <li>2 : Rechercher un client par email</li>
     *   <li>3 : Lister tous les clients</li>
     *   <li>0 : Retour au menu principal</li>
     * </ul>
     *
     *
     * @return choix de l'utilisateur (0-3)
     */
    public int afficherMenuGestionClients() {
        afficherTitre("Gestion des clients");

        afficherMessage("1. Créer un nouveau client");
        afficherMessage("2. Rechercher un client par email");
        afficherMessage("3. Lister tous les clients");
        afficherMessage("0. Retour au menu principal");
        afficherMessage("");

        return demanderEntier("Votre choix : ", 0, 3);
    }

    // =====================
    // CRÉATION D'UN CLIENT
    // =====================

    /**
     * Demande le nom du nouveau client.
     *
     * @return nom du client saisi par l'utilisateur (non vide)
     */
    public String demanderNomClient() {
        return demanderTexteNonVide("Nom du client : ");
    }

    /**
     * Demande le prénom du nouveau client.
     *
     * @return prénom du client saisi par l'utilisateur (non vide)
     */
    public String demanderPrenomClient() {
        return demanderTexteNonVide("Prénom du client : ");
    }

    /**
     * Demande l'email du nouveau client.
     * <p>
     * L'email doit être valide (format xxx@xxx.xxx) et sert d'identifiant
     * unique pour le client.
     * </p>
     *
     * @return email du client saisi par l'utilisateur (format valide)
     */
    public String demanderEmailClient() {
        return demanderEmail("Email du client : ");
    }

    /**
     * Affiche un message de confirmation après la création réussie du client.
     * <p>
     * Attend une confirmation de l'utilisateur avant de retourner au menu.
     * </p>
     */
    public void afficherClientCree() {
        afficherMessage("Le client a été créé avec succès.");
        attendreEntree();
    }

    // =====================
    // RECHERCHE DE CLIENT
    // =====================

    /**
     * Demande l'email d'un client pour la recherche.
     * <p>
     * L'email doit être valide (format xxx@xxx.xxx).
     * </p>
     *
     * @return email du client recherché (format valide)
     */
    public String demanderEmailRecherche() {
        afficherSousTitre("Recherche d'un client");
        return demanderEmail("Email du client : ");
    }

    // =====================
    // AFFICHAGE DES CLIENTS
    // =====================

    /**
     * Affiche les informations détaillées d'un client.
     * <p>
     * Les informations incluent : nom, prénom, email, nombre de contrats,
     * et éventuellement d'autres détails selon le formatage du contrôleur.
     * </p>
     *
     * @param clientInfos informations formatées du client
     */
    public void afficherClient(String clientInfos) {
        afficherTitre("Informations du client");
        afficherMessage(clientInfos);
    }

    /**
     * Affiche la liste de tous les clients enregistrés.
     * <p>
     * Présente une liste numérotée de tous les clients avec leurs
     * informations principales (nom, prénom, email).
     * Si aucun client n'est enregistré, affiche un message approprié.
     * </p>
     *
     * @param clients liste des représentations textuelles des clients
     */
    public void afficherListeClients(List<String> clients) {
        afficherTitre("Liste des clients");

        if (clients.isEmpty()) {
            afficherMessage("Aucun client enregistré.");
            attendreEntree();
            return;
        }

        for (int i = 0; i < clients.size(); i++) {
            afficherMessage((i + 1) + ". " + clients.get(i));
        }

        afficherMessage("");
        attendreEntree();
    }

    // =====================
    // MENU CONTEXTUEL CLIENT
    // =====================

    /**
     * Affiche le menu contextuel d'actions sur un client sélectionné.
     * <p>
     * Options disponibles :
     * <ul>
     *   <li>1 : Consulter les contrats du client (liste de tous ses contrats)</li>
     *   <li>2 : Créer un nouveau contrat pour ce client</li>
     *   <li>3 : Consulter les factures du client (toutes factures de tous contrats)</li>
     *   <li>0 : Retour au menu principal</li>
     * </ul>
     *
     *
     * @return choix de l'utilisateur (0-3)
     */
    public int afficherMenuContextuelClient() {
        afficherSousTitre("Actions sur le client");

        afficherMessage("1. Consulter les contrats du client");
        afficherMessage("2. Créer un nouveau contrat pour ce client");
        afficherMessage("3. Consulter les factures du client");
        afficherMessage("0. Retour au menu principal");
        afficherMessage("");

        return demanderEntier("Votre choix : ", 0, 3);
    }
}