package fr.electronvert.facturation.servlet.viewmodel;

public class ClientViewModel {

    private final int id;
    private final String prenom;
    private final String nom;
    private final String dateInscription;

    public ClientViewModel(int id, String prenom, String nom, String dateInscription) {
        this.id = id;
        this.prenom = prenom;
        this.nom = nom;
        this.dateInscription = dateInscription;
    }

    public int getId() { return id; }
    public String getPrenom() { return prenom; }
    public String getNom() { return nom; }
    public String getDateInscription() { return dateInscription; }
}
