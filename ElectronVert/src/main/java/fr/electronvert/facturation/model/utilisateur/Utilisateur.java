package fr.electronvert.facturation.model.utilisateur;

import fr.electronvert.facturation.model.contrat.Contrat;
import fr.electronvert.facturation.util.ValidationFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utilisateur {

    private static final String DOMAINE_EMAIL_ADMIN = "@electronvert.fr";

    private int id;
    protected String nom;
    protected String prenom;
    protected String email;
    private String motDePasse;
    private final RoleUtilisateur role;
    private final List<Contrat> contrats = new ArrayList<>();

    // Constructeur pour création (pas encore d'id, la BDD l'attribuera)
    public Utilisateur(String nom, String prenom, String email, RoleUtilisateur role) {
        ValidationFormat.verifierNonVide(nom, "Nom");
        ValidationFormat.verifierNonVide(prenom, "Prénom");
        ValidationFormat.verifierEmail(email);

        if (role == null) {
            throw new IllegalArgumentException("Le rôle est obligatoire");
        }
        if (role == RoleUtilisateur.ADMINISTRATEUR
                && !email.toLowerCase().endsWith(DOMAINE_EMAIL_ADMIN)) {
            throw new IllegalArgumentException(
                    "L'adresse email d'un administrateur doit se terminer par " + DOMAINE_EMAIL_ADMIN
            );
        }

        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
    }

    // Constructeur pour reconstruction depuis la BDD (id connu)
    public Utilisateur(int id, String nom, String prenom, String email, RoleUtilisateur role) {
        ValidationFormat.verifierNonVide(nom, "Nom");
        ValidationFormat.verifierNonVide(prenom, "Prénom");
        ValidationFormat.verifierEmail(email);

        if (role == null) {
            throw new IllegalArgumentException("Le rôle est obligatoire");
        }

        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
    }

    // =====================
    // MÉTHODES MÉTIER
    // =====================

    public void ajouterContrat(Contrat contrat) {
        contrats.add(contrat);
    }

    public boolean aUnContratActif() {
        return contrats.stream().anyMatch(Contrat::estActif);
    }

    public String getInformationsPersonnelles() {
        return "ID : " + id + "\n"
                + "Nom : " + prenom + " " + nom + "\n"
                + "Email : " + email;
    }

    // =====================
    // GETTERS
    // =====================

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getEmail() { return email; }
    public String getMotDePasse() { return motDePasse; }
    public RoleUtilisateur getRole() { return role; }

    public List<Contrat> getContrats() {
        return Collections.unmodifiableList(contrats);
    }

    // =====================
    // SETTERS
    // =====================

    public void setId(int id) { this.id = id; }

    public void setNom(String nom) {
        ValidationFormat.verifierNonVide(nom, "Nom");
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        ValidationFormat.verifierNonVide(prenom, "Prénom");
        this.prenom = prenom;
    }

    public void setEmail(String email) {
        ValidationFormat.verifierNonVide(email, "Email");
        this.email = email;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }
}