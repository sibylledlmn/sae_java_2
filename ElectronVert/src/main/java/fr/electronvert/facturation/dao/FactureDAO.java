package fr.electronvert.facturation.dao;

import fr.electronvert.facturation.model.facture.Facture;
import fr.electronvert.facturation.model.facture.StatutFacture;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface FactureDAO {


    int save(Facture facture, int contratId) throws SQLException;
    Facture findById(int id) throws SQLException;
    List<Facture> findByContratId(int contratId) throws SQLException;
    List<Facture> findImpayees() throws SQLException;
    List<Facture> findEmisesEchues(LocalDate date) throws SQLException;
    List<Facture> findARelancer(LocalDate date) throws SQLException;
    void updateStatut(int id, StatutFacture statut) throws SQLException;
    void updateDateProchaineRelance(int id, LocalDate date) throws SQLException;
    List<Facture> findNonPayeesByClientId(int clientId) throws SQLException;
    List<Facture> findRecentesByClientId(int clientId, int limite) throws SQLException;

}
