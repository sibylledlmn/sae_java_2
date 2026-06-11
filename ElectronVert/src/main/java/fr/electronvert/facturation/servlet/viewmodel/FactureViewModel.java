package fr.electronvert.facturation.servlet.viewmodel;

import fr.electronvert.facturation.model.facture.StatutFacture;

public class FactureViewModel {
    private final String reference;
    private final String dateEcheance;
    private final String montantTTC;
    private final StatutFacture statut;
    private final int contratId;
    private final String contratAdresse;

    public FactureViewModel(String reference, String dateEcheance, String montantTTC,
                            StatutFacture statut, int contratId, String contratAdresse) {
        this.reference = reference;
        this.dateEcheance = dateEcheance;
        this.montantTTC = montantTTC;
        this.statut = statut;
        this.contratId = contratId;
        this.contratAdresse = contratAdresse;
    }

    public String getReference() { return reference; }
    public String getDateEcheance() { return dateEcheance; }
    public String getMontantTTC() { return montantTTC; }
    public StatutFacture getStatut() { return statut; }
    public int getContratId() { return contratId; }
    public String getContratAdresse() { return contratAdresse; }
}
