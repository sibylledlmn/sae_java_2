package fr.electronvert.facturation.dao;

import fr.electronvert.facturation.model.releve.Releve;

import java.sql.SQLException;
import java.util.List;

public interface ReleveDAO {

    int save(Releve releve, int contratId) throws SQLException;
    List<Releve> findByContractId(int contractId) throws SQLException;
    Releve findDernerByContractId(int contractId) throws SQLException;
    List<Releve> findAllByClientId(int clientId) throws SQLException;

}
