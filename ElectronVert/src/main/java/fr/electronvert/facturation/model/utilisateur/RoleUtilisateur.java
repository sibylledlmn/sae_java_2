package fr.electronvert.facturation.model.utilisateur;

/**
 * Énumération représentant les différents rôles possibles
 * des utilisateurs du système ElectronVert.
 * <p>
 * Le rôle d'un utilisateur détermine ses droits et les fonctionnalités
 * auxquelles il a accès dans l'application.
 * </p>
 */
public enum RoleUtilisateur {

    /**
     * Rôle attribué à un client du système.
     */
    CLIENT,

    /**
     * Rôle attribué à un administrateur du système.
     */
    ADMINISTRATEUR
}
