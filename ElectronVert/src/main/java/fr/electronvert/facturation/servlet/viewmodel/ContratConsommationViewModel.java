package fr.electronvert.facturation.servlet.viewmodel;

import java.util.List;

public class ContratConsommationViewModel {

    private final int id;
    private final String adressePostale;
    private final String libelleOffre;
    private final String libelleModeFacturation;
    private final String dateOuverture;
    private final String dernierReleve;
    private final boolean hphc;
    private final List<ReleveViewModel> releves;

    public ContratConsommationViewModel(int id, String adressePostale, String libelleOffre,
                                        String libelleModeFacturation, String dateOuverture,
                                        String dernierReleve, boolean hphc,
                                        List<ReleveViewModel> releves) {
        this.id = id;
        this.adressePostale = adressePostale;
        this.libelleOffre = libelleOffre;
        this.libelleModeFacturation = libelleModeFacturation;
        this.dateOuverture = dateOuverture;
        this.dernierReleve = dernierReleve;
        this.hphc = hphc;
        this.releves = releves;
    }

    public int getId() { return id; }
    public String getAdressePostale() { return adressePostale; }
    public String getLibelleOffre() { return libelleOffre; }
    public String getLibelleModeFacturation() { return libelleModeFacturation; }
    public String getDateOuverture() { return dateOuverture; }
    public String getDernierReleve() { return dernierReleve; }
    public boolean isHphc() { return hphc; }
    public List<ReleveViewModel> getReleves() { return releves; }
}
