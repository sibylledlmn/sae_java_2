package fr.electronvert.facturation.servlet.viewmodel;

public class ContratAdminViewModel {

    private final int id;
    private final String adresse;
    private final String identiteClient;
    private final String libelleOffre;
    private final String libelleMode;
    private final String dateSouscription;
    private final String statut;

    public ContratAdminViewModel(int id, String adresse, String prenom, String nom,
                                 String libelleOffre, String libelleMode,
                                 String dateSouscription, String statut) {
        this.id = id;
        this.adresse = adresse;
        this.identiteClient = prenom + " " + nom;
        this.libelleOffre = libelleOffre;
        this.libelleMode = libelleMode;
        this.dateSouscription = dateSouscription;
        this.statut = statut;
    }

    public int getId()                 { return id; }
    public String getAdresse()         { return adresse; }
    public String getIdentiteClient()  { return identiteClient; }
    public String getLibelleOffre()    { return libelleOffre; }
    public String getLibelleMode()     { return libelleMode; }
    public String getDateSouscription(){ return dateSouscription; }
    public String getStatut()          { return statut; }
}
