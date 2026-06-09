package fr.electronvert.facturation.view.client;

import fr.electronvert.facturation.view.VueBase;

import java.util.Scanner;

/**
 * Vue pour le menu principal de l'espace client.
 */
public class VueMenuClient extends VueBase {

    public VueMenuClient(Scanner scanner) {
        super(scanner);
    }

    /**
     * Affiche le menu principal client.
     */
    public void afficherMenuPrincipal() {
        afficherTitre("ESPACE CLIENT");
        System.out.println();
        System.out.println("1. Mon contrat");
        System.out.println("2. Mes factures et paiements");
        System.out.println("3. Ma consommation");
        System.out.println("4. Mes informations personnelles");
        System.out.println("5. Les tarifs Electronvert");
        System.out.println("6. Me déconnecter");
    }

    /**
     * Demande le choix de menu.
     * Retourne toujours un nombre entre 1 et 6.
     */
    public int demanderChoixMenuPrincipal() {
        return demanderEntier("\nVotre choix : ", 1, 6);
    }
}