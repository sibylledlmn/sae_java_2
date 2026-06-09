package fr.electronvert.facturation.view;

import java.util.Scanner;

/**
 * Vue principale de l'application.
 * Affiche le menu de démarrage et récupère le choix utilisateur.
 */
public class VuePrincipale extends VueBase {

    public VuePrincipale(Scanner scanner) {
        super(scanner);
    }

    /**
     * Affiche le menu principal de l'application.
     *
     * @return le choix de l'utilisateur
     */
    public int afficherMenuPrincipal() {
        afficherTitre("ElectronVert");

        afficherMessage("1. Se connecter");
        afficherMessage("2. Avancer la date");
        afficherMessage("0. Quitter");
        afficherMessage("");

        return demanderEntier("Votre choix : ", 0, 2);
    }
}
