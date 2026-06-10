package fr.electronvert.facturation.dao;

import fr.electronvert.facturation.model.facture.FraisRelance;

import java.sql.SQLException;
import java.util.List;

public interface FraisRelanceDAO {

    int save(FraisRelance frais, int factureId) throws SQLException;
    List<FraisRelance> findByFactureId(int factureId) throws SQLException;

}
