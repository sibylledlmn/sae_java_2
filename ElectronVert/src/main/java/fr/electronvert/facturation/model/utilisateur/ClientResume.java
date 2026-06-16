package fr.electronvert.facturation.model.utilisateur;

import java.time.LocalDate;

public class ClientResume {

    private final int id;
    private final String prenom;
    private final String nom;
    private final LocalDate dateInscription;

    public ClientResume(int id, String prenom, String nom, LocalDate dateInscription) {
        this.id = id;
        this.prenom = prenom;
        this.nom = nom;
        this.dateInscription = dateInscription;
    }

    public int getId() { return id; }
    public String getPrenom() { return prenom; }
    public String getNom() { return nom; }
    public LocalDate getDateInscription() { return dateInscription; }
}
