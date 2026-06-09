package fr.electronvert.facturation.view.client;

import fr.electronvert.facturation.view.VueBase;

import java.util.Scanner;

/**
 * Vue pour la gestion des informations personnelles du client.
 */
public class VueInformationsPersonnelles extends VueBase {

    public VueInformationsPersonnelles(Scanner scanner) {
        super(scanner);
    }

    /**
     * Affiche le menu des informations personnelles.
     */
    public void afficherMenuInformations() {
        afficherSousTitre("Mes Informations Personnelles");
        System.out.println();
        System.out.println("1. Voir mes informations");
        System.out.println("2. Modifier mon nom");
        System.out.println("3. Modifier mon prénom");
        System.out.println("4. Modifier mon email");
        System.out.println("5. Retour au menu principal");
    }

    /**
     * Demande le choix dans le menu des informations personnelles.
     * Retourne toujours un nombre entre 1 et 5.
     */
    public int demanderChoixMenuInformations() {
        return demanderEntier("\nVotre choix : ", 1, 5);
    }

    /**
     * Affiche les informations personnelles du client.
     */
    public void afficherInformations(String informations) {
        afficherSousTitre("Mes informations");
        afficherMessage(informations);
        attendreEntree();
    }


    /**
     * Demande le nouveau nom.
     * Retourne toujours un nom non vide.
     */
    public String demanderNouveauNom() {
        afficherSousTitre("Modification du Nom");
        return demanderTexteNonVide("Nouveau nom : ");
    }

    /**
     * Demande le nouveau prénom.
     * Retourne toujours un prénom non vide.
     */
    public String demanderNouveauPrenom() {
        afficherSousTitre("Modification du Prénom");
        return demanderTexteNonVide("Nouveau prénom : ");
    }

    /**
     * Demande le nouvel email.
     * Retourne toujours un email valide.
     */
    public String demanderNouvelEmail() {
        afficherSousTitre("Modification de l'Email");
        return demanderEmail("Nouvel email : ");
    }

    /**
     * Demande confirmation pour la modification.
     * Retourne toujours true ou false.
     */
    public boolean demanderConfirmationModification() {
        return demanderConfirmation("Confirmer la modification ?");
    }

    /**
     * Affiche le succès de la modification.
     */
    public void afficherSuccesModification(String champ) {
        afficherMessage(champ + " modifié avec succès");
        attendreEntree();
    }
}