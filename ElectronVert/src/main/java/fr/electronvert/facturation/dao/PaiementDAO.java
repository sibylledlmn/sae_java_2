package fr.electronvert.facturation.dao;

import fr.electronvert.facturation.model.facture.Paiement;

import java.sql.SQLException;
import java.util.List;

public interface PaiementDAO {

    int save (Paiement paiement, int factureId) throws SQLException;
    int saveForEcheancier(Paiement paiement, int echeancierId) throws SQLException;
    Paiement findByFactureId(int factureId) throws SQLException;
    List<Paiement> findByContratId(int contratId) throws SQLException;
    List<Paiement> findByEcheancièrId(int echeancierId)  throws SQLException;

}
