package fr.electronvert.facturation.model.utilisateur;


import fr.electronvert.facturation.util.ValidationFormat;

public abstract class Utilisateur {

    protected final String id;
    protected final String nom;
    protected final String prenom;
    protected final String email;

    protected Utilisateur(String id, String nom, String prenom, String email) {
        ValidationFormat.verifierNonVide(id, "Id");
        ValidationFormat.verifierNonVide(nom, "Nom");
        ValidationFormat.verifierNonVide(prenom, "Prénom");
        ValidationFormat.verifierEmail(email);
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
    }

    public String getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getEmail() { return email; }

    public abstract RoleUtilisateur getRole();
}
