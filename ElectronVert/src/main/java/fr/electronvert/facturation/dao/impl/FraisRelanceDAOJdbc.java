package fr.electronvert.facturation.dao.impl;

import fr.electronvert.facturation.dao.ConnectionManager;
import fr.electronvert.facturation.dao.FraisRelanceDAO;
import fr.electronvert.facturation.model.facture.FraisRelance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FraisRelanceDAOJdbc implements FraisRelanceDAO {
    @Override
    public int save(FraisRelance frais, int factureId) throws SQLException {
        String query = "INSERT INTO frais_relance (facture_id, numero_relance, date_relance, montant_ht, montant_tva, montant_ttc) VALUES (?, ?, ?, ?, ?, ?)";
        try(Connection co = ConnectionManager.getConnection();
            PreparedStatement ps = co.prepareStatement(query,  Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, factureId);
            ps.setInt(2, frais.getNumeroRelance());
            ps.setDate(3, Date.valueOf(frais.getDateRelance()));
            ps.setDouble(4, frais.getMontantHT());
            ps.setDouble(5, frais.getMontantTVA());
            ps.setDouble(6, frais.getMontantTTC());
            ps.executeUpdate();
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        return 0;
    }

    @Override
    public List<FraisRelance> findByFactureId(int factureId) throws SQLException {
        List<FraisRelance> fraisRelances = new ArrayList<>();
        String query = "SELECT * FROM frais_relance WHERE facture_id = ?";
        try(Connection co = ConnectionManager.getConnection();
        PreparedStatement ps = co.prepareStatement(query)){
            ps.setInt(1, factureId);
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    FraisRelance frais = new FraisRelance(rs.getInt("id"), rs.getInt("numero_relance"),
                            rs.getDate("date_relance").toLocalDate(), rs.getDouble("montant_ht"),
                            rs.getDouble("montant_tva"), rs.getDouble("montant_ttc") );
                    fraisRelances.add(frais);
                }
            }
        }
        return fraisRelances;
    }

}
