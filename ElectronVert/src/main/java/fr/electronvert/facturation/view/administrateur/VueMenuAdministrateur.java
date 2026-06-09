package fr.electronvert.facturation.view.administrateur;

import fr.electronvert.facturation.model.utilisateur.Administrateur;
import fr.electronvert.facturation.view.VueBase;

import java.util.Scanner;

/**
 * Vue du menu principal de l'espace administrateur.
 * <p>
 * Cette vue permet à l'administrateur d'accéder aux différentes
 * fonctionnalités de gestion du système :
 * <ul>
 *   <li>Gestion des clients</li>
 *   <li>Gestion des contrats</li>
 *   <li>Gestion des tarifs</li>
 *   <li>Suivi et statistiques</li>
 *   <li>Déconnexion</li>
 * </ul>
 * Le menu affiche également les informations de l'administrateur
 * actuellement connecté (prénom et nom).
 *

 *
 * @see VueBase
 * @see Administrateur
 * @see fr.electronvert.facturation.controller.administrateur.ControleurMenuAdministrateur
 */
public class VueMenuAdministrateur extends VueBase {

    // =====================
    // ATTRIBUTS
    // =====================

    /**
     * Administrateur actuellement connecté.
     * Utilisé pour afficher son nom dans l'en-tête du menu.
     */
    private final Administrateur administrateur;

    // =====================
    // CONSTRUCTEUR
    // =====================

    /**
     * Construit une vue du menu administrateur.
     *
     * @param scanner scanner pour lire les entrées utilisateur
     * @param administrateur administrateur actuellement connecté
     *
     * @throws IllegalArgumentException si l'un des paramètres est null
     */
    public VueMenuAdministrateur(Scanner scanner, Administrateur administrateur) {
        super(scanner);

        if (administrateur == null) {
            throw new IllegalArgumentException("L'administrateur ne peut pas être null");
        }

        this.administrateur = administrateur;
    }

    // =====================
    // MENU PRINCIPAL
    // =====================

    /**
     * Affiche le menu principal de l'espace administrateur.
     * <p>
     * Le menu comprend :
     * <ul>
     *   <li>Un en-tête avec le titre et les informations de l'administrateur connecté</li>
     *   <li>La liste des fonctionnalités disponibles</li>
     *   <li>Une option de déconnexion</li>
     * </ul>
     *
     * <p>
     * Options disponibles :
     * <ul>
     *   <li>1 : Gestion des clients (création, consultation, modification)</li>
     *   <li>2 : Gestion des contrats (création, consultation, clôture)</li>
     *   <li>3 : Gestion des tarifs (création, consultation de l'historique)</li>
     *   <li>4 : Suivi et statistiques (chiffre d'affaires, impayés, etc.)</li>
     *   <li>5 : Se déconnecter</li>
     * </ul>
     *
     *
     * @return choix de l'utilisateur (1-5)
     */
    public int afficherMenuPrincipal() {
        afficherLigneSeparation();
        afficherTitre("ESPACE ADMINISTRATEUR");
        afficherMessage("Connecté : " + administrateur.getPrenom() + " " + administrateur.getNom());
        afficherLigneSeparation();

        afficherMessage("1. Gestion des clients");
        afficherMessage("2. Gestion des contrats");
        afficherMessage("3. Gestion des tarifs");
        afficherMessage("4. Suivi et statistiques");
        afficherMessage("5. Se déconnecter");
        afficherMessage("");

        return demanderEntier("Votre choix : ", 1, 5);
    }
}
