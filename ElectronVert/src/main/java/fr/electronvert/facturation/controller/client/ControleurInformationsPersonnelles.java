package fr.electronvert.facturation.controller.client;

import fr.electronvert.facturation.model.utilisateur.Client;
import fr.electronvert.facturation.view.client.VueInformationsPersonnelles;

import java.util.Scanner;

/**
 * Contrôleur de gestion des informations personnelles du client.
 * <p>
 * Ce contrôleur permet au client de :
 * <ul>
 *   <li>Consulter ses informations personnelles</li>
 *   <li>Modifier son nom</li>
 *   <li>Modifier son prénom</li>
 *   <li>Modifier son email</li>
 * </ul>
 * Chaque modification nécessite une confirmation de l'utilisateur
 * avant d'être appliquée.
 *
 *
 * @see VueInformationsPersonnelles
 * @see Client
 */
public class ControleurInformationsPersonnelles {

    // =====================
    // ATTRIBUTS
    // =====================

    /**
     * Vue de gestion des informations personnelles.
     */
    private final VueInformationsPersonnelles vue;

    // =====================
    // CONSTRUCTEUR
    // =====================

    /**
     * Construit le contrôleur de gestion des informations personnelles.
     *
     * @param scanner scanner pour les entrées utilisateur
     *
     * @throws IllegalArgumentException si le scanner est null
     */
    public ControleurInformationsPersonnelles(Scanner scanner) {
        if (scanner == null) {
            throw new IllegalArgumentException("Le scanner ne peut pas être null");
        }

        this.vue = new VueInformationsPersonnelles(scanner);
    }

    // =====================
    // GESTION DU MENU
    // =====================

    /**
     * Lance la gestion des informations personnelles pour un client donné.
     * <p>
     * Affiche le menu et permet de :
     * <ul>
     *   <li>1 : Consulter les informations personnelles</li>
     *   <li>2 : Modifier le nom</li>
     *   <li>3 : Modifier le prénom</li>
     *   <li>4 : Modifier l'email</li>
     *   <li>5 : Retour au menu principal</li>
     * </ul>
     * La boucle continue jusqu'à ce que le client choisisse de revenir
     * au menu principal.
     *
     * @param client client dont gérer les informations
     *
     * @throws IllegalArgumentException si le client est null
     */
    public void demarrer(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Le client ne peut pas être null");
        }

        boolean retour = false;

        while (!retour) {
            vue.afficherMenuInformations();
            int choix = vue.demanderChoixMenuInformations();

            switch (choix) {
                case 1 -> afficherInformations(client);
                case 2 -> modifierNom(client);
                case 3 -> modifierPrenom(client);
                case 4 -> modifierEmail(client);
                case 5 -> retour = true;
            }
        }
    }

    // =====================
    // AFFICHAGE DES INFORMATIONS
    // =====================

    /**
     * Affiche les informations personnelles du client.
     * <p>
     * Les informations affichées comprennent :
     * nom, prénom, email, et tout autre détail personnel.
     * </p>
     *
     * @param client client dont afficher les informations
     */
    private void afficherInformations(Client client) {
        vue.afficherInformations(client.getInformationsPersonnelles());
    }

    // =====================
    // MODIFICATIONS DES INFORMATIONS
    // =====================

    /**
     * Permet la modification du nom du client.
     * <p>
     * Workflow :
     * <ul>
     *   <li>Demande le nouveau nom</li>
     *   <li>Demande confirmation de la modification</li>
     *   <li>Si confirmé : applique le changement et affiche un message de succès</li>
     *   <li>Si refusé : annule la modification</li>
     * </ul>
     * </p>
     *
     * @param client client dont modifier le nom
     */
    private void modifierNom(Client client) {
        String nouveauNom = vue.demanderNouveauNom();

        if (vue.demanderConfirmationModification()) {
            client.setNom(nouveauNom);
            vue.afficherSuccesModification("Nom");
        }
    }

    /**
     * Permet la modification du prénom du client.
     * <p>
     * Workflow :
     * <ul>
     *   <li>Demande le nouveau prénom</li>
     *   <li>Demande confirmation de la modification</li>
     *   <li>Si confirmé : applique le changement et affiche un message de succès</li>
     *   <li>Si refusé : annule la modification</li>
     * </ul>
     * </p>
     *
     * @param client client dont modifier le prénom
     */
    private void modifierPrenom(Client client) {
        String nouveauPrenom = vue.demanderNouveauPrenom();

        if (vue.demanderConfirmationModification()) {
            client.setPrenom(nouveauPrenom);
            vue.afficherSuccesModification("Prénom");
        }
    }

    /**
     * Permet la modification de l'email du client.
     * <p>
     * Workflow :
     * <ul>
     *   <li>Demande le nouvel email (avec validation du format)</li>
     *   <li>Demande confirmation de la modification</li>
     *   <li>Si confirmé : applique le changement et affiche un message de succès</li>
     *   <li>Si refusé : annule la modification</li>
     * </ul>
     * </p>
     *
     * @param client client dont modifier l'email
     */
    private void modifierEmail(Client client) {
        String nouvelEmail = vue.demanderNouvelEmail();

        if (vue.demanderConfirmationModification()) {
            client.setEmail(nouvelEmail);
            vue.afficherSuccesModification("Email");
        }
    }
}