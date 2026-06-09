package fr.electronvert.facturation.dao;

import fr.electronvert.facturation.model.tarif.Tarif;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface TarifDAO {

    void save(Tarif tarif) throws SQLException;

    List<Tarif> findAll() throws SQLException;

    Tarif findActiveAtDate(LocalDate localDate) throws SQLException;

}
