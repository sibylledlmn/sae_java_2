package fr.electronvert.facturation.model.utilisateur;

/**
 * Représente un administrateur du système ElectronVert.
 * <p>
 * Un administrateur est un utilisateur employé par ElectronVert
 * pouvant gérer les clients, les contrats, les factures et les tarifs.
 * Son identifiant est généré automatiquement et son adresse email
 * doit obligatoirement appartenir au domaine ElectronVert.
 * </p>
 */
public class Administrateur extends Utilisateur {

    /**
     * Compteur utilisé pour générer des identifiants uniques
     * pour les administrateurs.
     */
    private static int compteur = 1;

    /**
     * Domaine autorisé pour les adresses email des administrateurs.
     */
    private static final String DOMAINE_EMAIL = "@electronvert.fr";

    /**
     * Construit un administrateur avec les informations fournies.
     * <p>
     * L'identifiant est généré automatiquement.
     * L'adresse email doit obligatoirement se terminer par {@code @electronvert.fr}.
     * </p>
     *
     * @param nom nom de l'administrateur
     * @param prenom prénom de l'administrateur
     * @param email adresse email de l'administrateur
     *
     * @throws IllegalArgumentException si l'adresse email n'est pas valide
     *                                  pour un administrateur
     */
    public Administrateur(String nom, String prenom, String email) {
        super(genererId(), nom, prenom, verifierEmailAdmin(email));
    }

    /**
     * Génère un identifiant unique pour un administrateur.
     *
     * @return identifiant sous la forme {@code ADM-x}
     */
    private static String genererId() {
        return "ADM-" + compteur++;
    }

    /**
     * Vérifie que l'adresse email correspond au domaine autorisé
     * pour les administrateurs.
     *
     * @param email adresse email à vérifier
     * @return l'adresse email si elle est valide
     *
     * @throws IllegalArgumentException si l'email ne se termine pas
     *                                  par le domaine autorisé
     */
    private static String verifierEmailAdmin(String email) {
        if (!email.toLowerCase().endsWith(DOMAINE_EMAIL)) {
            throw new IllegalArgumentException(
                    "L'adresse email d'un administrateur doit se terminer par "
                            + DOMAINE_EMAIL
            );
        }

        return email;
    }

    /**
     * Retourne le rôle de l'utilisateur.
     *
     * @return {@link RoleUtilisateur#ADMINISTRATEUR}
     */
    @Override
    public RoleUtilisateur getRole() {
        return RoleUtilisateur.ADMINISTRATEUR;
    }
}


