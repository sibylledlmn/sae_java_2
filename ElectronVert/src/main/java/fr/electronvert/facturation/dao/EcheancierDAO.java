package fr.electronvert.facturation.dao;

import fr.electronvert.facturation.model.contrat.Echeancier;

import java.sql.SQLException;
import java.util.List;

public interface EcheancierDAO {

    int save(Echeancier echeancier, int contratId) throws SQLException;

    Echeancier findActifByContratId(int contratId) throws SQLException;

    List<Echeancier> findByContratId(int contratId) throws SQLException;

    void incrementerNbMensualitesEmises(int id)  throws SQLException;

    void terminer(int id) throws SQLException;


}
