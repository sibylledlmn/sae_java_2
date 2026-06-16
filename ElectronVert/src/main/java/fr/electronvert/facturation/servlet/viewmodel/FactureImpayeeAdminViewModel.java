package fr.electronvert.facturation.servlet.viewmodel;

public class FactureImpayeeAdminViewModel {

    private final String nomClient;
    private final String reference;
    private final String dateEcheance;
    private final String montantTTC;
    private final int nbRelances;
    private final String badgeClass;

    public FactureImpayeeAdminViewModel(String nomClient, String reference, String dateEcheance, String montantTTC, int nbRelances) {
        this.nomClient = nomClient;
        this.reference = reference;
        this.dateEcheance = dateEcheance;
        this.montantTTC = montantTTC;
        this.nbRelances = nbRelances;
        this.badgeClass = nbRelances >= 3 ? "red2" : nbRelances == 2 ? "red" : "amber";
    }

    public String getNomClient()   { return nomClient; }
    public String getReference()   { return reference; }
    public String getDateEcheance(){ return dateEcheance; }
    public String getMontantTTC()  { return montantTTC; }
    public int getNbRelances()     { return nbRelances; }
    public String getBadgeClass()  { return badgeClass; }
}
