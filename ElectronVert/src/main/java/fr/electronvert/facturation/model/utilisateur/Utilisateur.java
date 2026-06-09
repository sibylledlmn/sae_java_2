package fr.electronvert.facturation.model.utilisateur;

import fr.electronvert.facturation.util.ValidationFormat;

/**
 * Classe abstraite représentant un utilisateur du système ElectronVert.
 * <p>
 * Un utilisateur possède un identifiant (id), un nom, un prénom et une adresse email.
 * Cette classe est destinée à être héritée par des utilisateurs concrets
 * comme {@code Client} ou {@code Administrateur}.
 * </p>
 */
public abstract class Utilisateur {

    /**
     * Identifiant unique de l'utilisateur.
     */
    protected final String id;

    /**
     * Nom de l'utilisateur.
     */
    protected String nom;

    /**
     * Prénom de l'utilisateur.
     */
    protected String prenom;

    /**
     * Adresse email de l'utilisateur.
     */
    protected String email;

    /**
     * Construit un utilisateur avec les informations fournies.
     * <p>
     * Les paramètres sont validés avant l'initialisation :
     * <ul>
     *     <li>Le nom et le prénom ne doivent pas être vides</li>
     *     <li>L'email doit respecter un format valide</li>
     * </ul>
     *
     *
     * @param id identifiant unique de l'utilisateur
     * @param nom nom de l'utilisateur
     * @param prenom prénom de l'utilisateur
     * @param email adresse email de l'utilisateur
     *
     * @throws IllegalArgumentException si les données fournies sont invalides
     */
    protected Utilisateur(String id, String nom, String prenom, String email) {
        ValidationFormat.verifierNonVide(nom, "Nom");
        ValidationFormat.verifierNonVide(prenom, "Prénom");
        ValidationFormat.verifierEmail(email);

        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
    }

    /**
     * Retourne l'identifiant de l'utilisateur.
     *
     * @return identifiant unique
     */
    public String getId() {
        return id;
    }

    /**
     * Retourne le nom de l'utilisateur.
     *
     * @return nom de l'utilisateur
     */
    public String getNom() {
        return nom;
    }

    /**
     * Retourne le prénom de l'utilisateur.
     *
     * @return prénom de l'utilisateur
     */
    public String getPrenom() {
        return prenom;
    }

    /**
     * Retourne l'adresse email de l'utilisateur.
     *
     * @return email de l'utilisateur
     */
    public String getEmail() {
        return email;
    }

    /**
     * Retourne le rôle de l'utilisateur dans le système.
     *
     * @return rôle de l'utilisateur
     */
    public abstract RoleUtilisateur getRole();
}
