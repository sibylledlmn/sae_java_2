package fr.electronvert.facturation.servlet.viewmodel;

import fr.electronvert.facturation.model.facture.StatutFacture;

public class FactureViewModel {
    private final int id;
    private final String reference;
    private final String dateEmission;
    private final String dateEcheance;
    private final String montantTTC;
    private final String montantFraisRelance;
    private final double montantFraisRelanceBrut;
    private final String montantTotalFraisInclus;
    private final double montantTotalFraisInclusBrut;
    private final StatutFacture statut;
    private final int contratId;
    private final String contratAdresse;

    public FactureViewModel(int id, String reference, String dateEmission, String dateEcheance,
                            double montantTTC, double montantFraisRelance,
                            StatutFacture statut, int contratId, String contratAdresse) {
        this.id = id;
        this.reference = reference;
        this.dateEmission = dateEmission;
        this.dateEcheance = dateEcheance;
        this.montantTTC = formater(montantTTC);
        this.montantFraisRelance = formater(montantFraisRelance);
        this.montantFraisRelanceBrut = montantFraisRelance;
        this.montantTotalFraisInclusBrut = montantTTC + montantFraisRelance;
        this.montantTotalFraisInclus = formater(this.montantTotalFraisInclusBrut);
        this.statut = statut;
        this.contratId = contratId;
        this.contratAdresse = contratAdresse;
    }

    private static String formater(double montant) {
        return String.format("%.2f", montant).replace(".", ",") + " €";
    }

    public int getId() { return id; }
    public String getReference() { return reference; }
    public String getDateEmission() { return dateEmission; }
    public String getDateEcheance() { return dateEcheance; }
    public String getMontantTTC() { return montantTTC; }
    public String getMontantFraisRelance() { return montantFraisRelance; }
    public double getMontantFraisRelanceBrut() { return montantFraisRelanceBrut; }
    public String getMontantTotalFraisInclus() { return montantTotalFraisInclus; }
    public double getMontantTotalFraisInclusBrut() { return montantTotalFraisInclusBrut; }
    public StatutFacture getStatut() { return statut; }
    public int getContratId() { return contratId; }
    public String getContratAdresse() { return contratAdresse; }
}
