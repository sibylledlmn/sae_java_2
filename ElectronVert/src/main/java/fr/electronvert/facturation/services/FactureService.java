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
        List<FraisRelance> listeFraisFacture = fraisRelanceDAO.findByFactureId(facture.getId());
        double totalTTCMontantFrais = 0;
        for (FraisRelance fraisRelance : listeFraisFacture) {
            totalTTCMontantFrais += fraisRelance.getMontantTTC();
        }
        return totalTTCMontantFrais + facture.getMontantTTC();

    }

}
