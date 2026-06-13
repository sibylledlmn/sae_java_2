package fr.electronvert.facturation.servlet.viewmodel;

public class ReleveViewModel {

    private final String date;
    private final String typeLibelle;

    // Classique
    private final String indexTotal;
    private final String consoTotal;

    // HP/HC
    private final String indexHP;
    private final String indexHC;
    private final String consoHP;
    private final String consoHC;

    // Constructeur Classique
    public ReleveViewModel(String date, String typeLibelle, String indexTotal, String consoTotal) {
        this.date = date;
        this.typeLibelle = typeLibelle;
        this.indexTotal = indexTotal;
        this.consoTotal = consoTotal;
        this.indexHP = null;
        this.indexHC = null;
        this.consoHP = null;
        this.consoHC = null;
    }

    // Constructeur HP/HC
    public ReleveViewModel(String date, String typeLibelle,
                           String indexHP, String indexHC,
                           String consoHP, String consoHC) {
        this.date = date;
        this.typeLibelle = typeLibelle;
        this.indexTotal = null;
        this.consoTotal = null;
        this.indexHP = indexHP;
        this.indexHC = indexHC;
        this.consoHP = consoHP;
        this.consoHC = consoHC;
    }

    public String getDate() { return date; }
    public String getTypeLibelle() { return typeLibelle; }
    public String getIndexTotal() { return indexTotal; }
    public String getConsoTotal() { return consoTotal; }
    public String getIndexHP() { return indexHP; }
    public String getIndexHC() { return indexHC; }
    public String getConsoHP() { return consoHP; }
    public String getConsoHC() { return consoHC; }
}
