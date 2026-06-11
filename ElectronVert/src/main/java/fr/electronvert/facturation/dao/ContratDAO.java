package fr.electronvert.facturation.dao;

import fr.electronvert.facturation.model.contrat.Contrat;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface ContratDAO {

    int save(Contrat contrat) throws SQLException;

    void update(Contrat contrat) throws SQLException;

    Contrat findById(int id) throws SQLException;

    List<Contrat> findAll() throws SQLException;

    List<Contrat> findByClientId(int clientId) throws SQLException;

    void cloturer(int id, LocalDate dateFin) throws SQLException;

    List<Contrat> findAFacturer() throws SQLException;

    List<Contrat> findActifs() throws SQLException;
    List<Contrat> findActifsByClientId(int clientId) throws SQLException;



}
