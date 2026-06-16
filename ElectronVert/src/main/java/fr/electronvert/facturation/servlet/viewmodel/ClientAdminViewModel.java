package fr.electronvert.facturation.servlet.viewmodel;

import java.util.List;

public class ClientAdminViewModel {

    private final int id;
    private final String initiales;
    private final String nom;
    private final String prenom;
    private final String email;
    private final int nbContratsActifs;
    private final List<String> offres;
    private final String dateInscription;

    public ClientAdminViewModel(int id, String prenom, String nom, String email,
                                int nbContratsActifs, List<String> offres, String dateInscription) {
        this.id = id;
        this.prenom = prenom;
        this.nom = nom;
        this.email = email;
        this.initiales = prenom.substring(0, 1).toUpperCase() + nom.substring(0, 1).toUpperCase();
        this.nbContratsActifs = nbContratsActifs;
        this.offres = offres;
        this.dateInscription = dateInscription;
    }

    public int getId()                 { return id; }
    public String getInitiales()       { return initiales; }
    public String getNom()             { return nom; }
    public String getPrenom()          { return prenom; }
    public String getEmail()           { return email; }
    public int getNbContratsActifs()   { return nbContratsActifs; }
    public List<String> getOffres()    { return offres; }
    public String getDateInscription() { return dateInscription; }
}
