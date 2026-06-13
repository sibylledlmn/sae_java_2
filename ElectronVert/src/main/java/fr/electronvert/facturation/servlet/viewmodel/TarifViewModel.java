package fr.electronvert.facturation.servlet.viewmodel;

import fr.electronvert.facturation.model.tarif.Tarif;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TarifViewModel {

    private final String dateDebut;
    private final String prixKwhClassique;
    private final String prixKwhHP;
    private final String prixKwhHC;
    private final String prixAbonnementClassique;
    private final String prixAbonnementHPHC;

    public TarifViewModel(Tarif tarif) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.dateDebut = tarif.getDateDebut().format(fmt);
        this.prixKwhClassique = String.format(Locale.FRENCH, "%.4f", tarif.getPrixKwhClassique());
        this.prixKwhHP = String.format(Locale.FRENCH, "%.4f", tarif.getPrixKwhHP());
        this.prixKwhHC = String.format(Locale.FRENCH, "%.4f", tarif.getPrixKwhHC());
        this.prixAbonnementClassique = String.format(Locale.FRENCH, "%.2f", tarif.getPrixAbonnementClassique());
        this.prixAbonnementHPHC = String.format(Locale.FRENCH, "%.2f", tarif.getPrixAbonnementHPHC());
    }

    public String getDateDebut() { return dateDebut; }
    public String getPrixKwhClassique() { return prixKwhClassique; }
    public String getPrixKwhHP() { return prixKwhHP; }
    public String getPrixKwhHC() { return prixKwhHC; }
    public String getPrixAbonnementClassique() { return prixAbonnementClassique; }
    public String getPrixAbonnementHPHC() { return prixAbonnementHPHC; }
}
