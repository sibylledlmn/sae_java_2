package fr.electronvert.facturation.services;

import fr.electronvert.facturation.dao.FactureDAO;
import fr.electronvert.facturation.dao.FraisRelanceDAO;
import fr.electronvert.facturation.model.facture.Facture;
import fr.electronvert.facturation.model.facture.FraisRelance;

import java.sql.SQLException;
import java.util.List;

public class FactureService {

    FactureDAO factureDAO ;
    FraisRelanceDAO fraisRelanceDAO;

    public FactureService(FactureDAO factureDAO, FraisRelanceDAO fraisRelanceDAO) {
        this.factureDAO = factureDAO;
        this.fraisRelanceDAO = fraisRelanceDAO;
    }

    public double getTotalMontantAPayerTTCAvecFrais(Facture facture) throws SQLException {
        return getTotauxFrais(facture).getTtc() + facture.getMontantTTC();
    }

    public TotauxFraisRelance getTotauxFrais(Facture facture) throws SQLException {
        List<FraisRelance> frais = fraisRelanceDAO.findByFactureId(facture.getId());
        double ht  = frais.stream().mapToDouble(FraisRelance::getMontantHT).sum();
        double tva = frais.stream().mapToDouble(FraisRelance::getMontantTVA).sum();
        double ttc = frais.stream().mapToDouble(FraisRelance::getMontantTTC).sum();
        return new TotauxFraisRelance(ht, tva, ttc);
    }

}
