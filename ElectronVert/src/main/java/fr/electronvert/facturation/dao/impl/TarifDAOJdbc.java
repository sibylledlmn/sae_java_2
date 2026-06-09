package fr.electronvert.facturation.dao.impl;

import fr.electronvert.facturation.dao.ConnectionManager;
import fr.electronvert.facturation.dao.TarifDAO;
import fr.electronvert.facturation.model.tarif.Tarif;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TarifDAOJdbc implements TarifDAO {
    @Override
    public void save(Tarif tarif) throws SQLException {
        String query = "INSERT INTO tarif (date_debut, prix_kwh_classique, prix_kwh_hp, prix_kwh_hc, prix_abonnement_classique, prix_abonnement_hphc) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection co = ConnectionManager.getConnection();
             PreparedStatement ps = co.prepareStatement(query)) {
            ps.setDate(1, Date.valueOf(tarif.getDateDebut()));
            ps.setDouble(2, tarif.getPrixKwhClassique());
            ps.setDouble(3, tarif.getPrixKwhHP());
            ps.setDouble(4, tarif.getPrixKwhHC());
            ps.setDouble(5, tarif.getPrixAbonnementClassique());
            ps.setDouble(6, tarif.getPrixAbonnementHPHC());
            ps.executeUpdate();
        }
    }

    @Override
    public List<Tarif> findAll() throws SQLException {
        List<Tarif> tarifs = new ArrayList<>();
        String query = "SELECT * FROM tarif ORDER BY date_debut DESC";
        try (Connection co = ConnectionManager.getConnection();
             PreparedStatement ps = co.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tarifs.add(new Tarif(rs.getDate("date_debut").toLocalDate(), rs.getDouble("prix_kwh_classique"),
                        rs.getDouble("prix_kwh_hp"), rs.getDouble("prix_kwh_hc"),
                        rs.getDouble("prix_abonnement_classique"), rs.getDouble("prix_abonnement_hphc")));
            }
        }
        return tarifs;
    }

    @Override
    public Tarif findActiveAtDate(LocalDate localDate) throws SQLException {
        String query = "SELECT * FROM tarif WHERE date_debut <= ? ORDER BY date_debut DESC LIMIT 1";
        try (Connection co = ConnectionManager.getConnection();
             PreparedStatement ps = co.prepareStatement(query)) {
            ps.setDate(1, Date.valueOf(localDate));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Tarif tarif = new Tarif(rs.getDate("date_debut").toLocalDate(), rs.getDouble("prix_kwh_classique"),
                            rs.getDouble("prix_kwh_hp"), rs.getDouble("prix_kwh_hc"),
                            rs.getDouble("prix_abonnement_classique"), rs.getDouble("prix_abonnement_hphc"));
                    return tarif;
                }
            }
        }
        return null;
    }
}
