package fr.electronvert.facturation.view;

import fr.electronvert.facturation.util.ValidationFormat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Classe abstraite de base pour toutes les vues de l'application.
 * <p>
 * {@code VueBase} centralise l'ensemble des fonctionnalités communes
 * aux interfaces console :
 * </p>
 * <ul>
 *     <li>affichage standardisé (titres, messages, erreurs)</li>
 *     <li>saisie utilisateur sécurisée et validée</li>
 *     <li>gestion des confirmations et pauses utilisateur</li>
 * </ul>
 *
 * <p>
 * Toutes les vues concrètes de l'application doivent hériter de cette classe
 * afin de garantir une expérience utilisateur cohérente et robuste.
 *
 */
public abstract class VueBase {

    // =====================
    // CONSTANTES
    // =====================

    /**
     * Formateur de date utilisé pour la saisie utilisateur.
     * Format : jj/mm/aaaa
     */
    protected static final DateTimeFormatter FORMAT_DATE =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // =====================
    // ATTRIBUTS
    // =====================

    /**
     * Scanner utilisé pour la saisie utilisateur.
     */
    protected final Scanner scanner;

    // =====================
    // CONSTRUCTEUR
    // =====================

    /**
     * Construit une vue avec un scanner fourni.
     *
     * @param scanner scanner utilisé pour lire les entrées utilisateur
     * @throws IllegalArgumentException si le scanner est null
     */
    public VueBase(Scanner scanner) {
        if (scanner == null) {
            throw new IllegalArgumentException("Le Scanner ne peut pas être null");
        }
        this.scanner = scanner;
    }

    // =====================
    // MÉTHODES D'AFFICHAGE
    // =====================

    /**
     * Affiche un titre principal formaté.
     *
     * @param titre titre à afficher
     */
    public void afficherTitre(String titre) {
        System.out.println("\n" + "-".repeat(50));
        System.out.println(titre);
        System.out.println("-".repeat(50));
    }

    /**
     * Affiche un sous-titre formaté.
     *
     * @param sousTitre sous-titre à afficher
     */
    public void afficherSousTitre(String sousTitre) {
        System.out.println("\n--- " + sousTitre + " ---");
    }

    /**
     * Affiche un message simple.
     *
     * @param message message à afficher
     */
    public void afficherMessage(String message) {
        System.out.println(message);
    }

    /**
     * Affiche un message d'erreur standardisé.
     *
     * @param erreur message d'erreur
     */
    public void afficherErreur(String erreur) {
        System.err.println("Erreur : " + erreur);
    }

    /**
     * Affiche une ligne de séparation.
     */
    public void afficherLigneSeparation() {
        System.out.println("-".repeat(50));
    }

    // =====================
    // SAISIE UTILISATEUR
    // =====================

    /**
     * Demande un texte non vide à l'utilisateur.
     * <p>
     * La saisie est répétée tant que le texte est vide ou invalide.
     * </p>
     *
     * @param prompt message affiché à l'utilisateur
     * @return texte saisi valide
     */
    public String demanderTexteNonVide(String prompt) {
        while (true) {
            System.out.print(prompt);
            String texte = scanner.nextLine().trim();

            try {
                ValidationFormat.verifierNonVide(texte, "Ce champ");
                return texte;
            } catch (IllegalArgumentException e) {
                afficherErreur(e.getMessage());
            }
        }
    }

    /**
     * Demande une adresse email valide à l'utilisateur.
     *
     * @param prompt message affiché
     * @return email valide
     */
    public String demanderEmail(String prompt) {
        while (true) {
            System.out.print(prompt);
            String email = scanner.nextLine().trim();

            try {
                ValidationFormat.verifierEmail(email);
                return email;
            } catch (IllegalArgumentException e) {
                afficherErreur(e.getMessage());
            }
        }
    }

    /**
     * Demande un entier compris dans une plage donnée.
     *
     * @param prompt message affiché
     * @param min valeur minimale autorisée
     * @param max valeur maximale autorisée
     * @return entier valide
     */
    public int demanderEntier(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String saisie = scanner.nextLine().trim();

            try {
                int valeur = Integer.parseInt(saisie);

                if (valeur >= min && valeur <= max) {
                    return valeur;
                }

                afficherErreur("Veuillez saisir un nombre entre " + min + " et " + max);
            } catch (NumberFormatException e) {
                afficherErreur("Veuillez saisir un nombre valide");
            }
        }
    }

    /**
     * Demande un nombre décimal strictement positif.
     *
     * @param prompt message affiché
     * @return valeur décimale positive
     */
    public double demanderDecimalPositif(String prompt) {
        while (true) {
            System.out.print(prompt);
            String saisie = scanner.nextLine().trim();

            try {
                double valeur = Double.parseDouble(saisie);

                if (valeur > 0) {
                    return valeur;
                }

                afficherErreur("Le montant doit être positif");
            } catch (NumberFormatException e) {
                afficherErreur("Veuillez saisir un nombre valide (exemple : 12.50)");
            }
        }
    }

    /**
     * Demande une date au format {@code jj/mm/aaaa}.
     *
     * @param prompt message affiché
     * @return date valide
     */
    public LocalDate demanderDate(String prompt) {
        while (true) {
            System.out.print(prompt + " (format : jj/mm/aaaa) : ");
            String saisie = scanner.nextLine().trim();

            try {
                return LocalDate.parse(saisie, FORMAT_DATE);
            } catch (DateTimeParseException e) {
                afficherErreur(
                        "Format de date invalide. Utilisez jj/mm/aaaa (exemple : 15/01/2025)"
                );
            }
        }
    }

    /**
     * Demande une confirmation à l'utilisateur.
     *
     * @param prompt message affiché
     * @return {@code true} si confirmé, {@code false} sinon
     */
    public boolean demanderConfirmation(String prompt) {
        while (true) {
            System.out.print(prompt + " (O/N) : ");
            String reponse = scanner.nextLine().trim().toUpperCase();

            if (reponse.equals("O") || reponse.equals("OUI")) {
                return true;
            }
            if (reponse.equals("N") || reponse.equals("NON")) {
                return false;
            }

            afficherErreur("Veuillez répondre par O (oui) ou N (non)");
        }
    }

    /**
     * Met l'exécution en pause jusqu'à ce que l'utilisateur appuie sur Entrée.
     */
    public void attendreEntree() {
        System.out.print("\nAppuyez sur Entrée pour continuer...");
        System.out.flush();
        scanner.nextLine();
    }

    // =====================
    // UTILITAIRES D'AFFICHAGE
    // =====================

    /**
     * Affiche une liste d'éléments numérotés.
     *
     * @param elements liste des éléments à afficher
     */
    public void afficherListeNumerotee(List<String> elements) {
        for (int i = 0; i < elements.size(); i++) {
            afficherMessage((i + 1) + ". " + elements.get(i));
        }
    }
}
