package fr.electronvert.facturation.services;

public class TotauxFraisRelance {

    private final double ht;
    private final double tva;
    private final double ttc;

    public TotauxFraisRelance(double ht, double tva, double ttc) {
        this.ht = ht;
        this.tva = tva;
        this.ttc = ttc;
    }

    public double getHt()  { return ht; }
    public double getTva() { return tva; }
    public double getTtc() { return ttc; }
}
