package fr.electronvert.facturation.dao.impl;

import fr.electronvert.facturation.dao.ConnectionManager;
import fr.electronvert.facturation.dao.PaiementDAO;
import fr.electronvert.facturation.model.facture.Paiement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaiementDAOJdbc implements PaiementDAO {
    @Override
    public int save(Paiement paiement, int factureId) throws SQLException {
        String query = "INSERT INTO paiement (facture_id, date_paiement, montant_paye) VALUES (?, ?, ?)";
        try (Connection co = ConnectionManager.getConnection();
             PreparedStatement ps = co.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
            ps.setInt(1, factureId);
            ps.setDate(2, Date.valueOf(paiement.getDatePaiement()));
            ps.setDouble(3, paiement.getMontantPaye());
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
    public int saveForEcheancier(Paiement paiement, int echeancierId) throws SQLException {
        String query = "INSERT INTO paiement (echeancier_id, date_paiement, montant_paye) VALUES (?, ?, ?)";
        try (Connection co = ConnectionManager.getConnection();
             PreparedStatement ps = co.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
            ps.setInt(1, echeancierId);
            ps.setDate(2, Date.valueOf(paiement.getDatePaiement()));
            ps.setDouble(3, paiement.getMontantPaye());
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
    public Paiement findByFactureId(int factureId) throws SQLException {
        String query = "SELECT * FROM paiement WHERE facture_id = ?";
        try (Connection co = ConnectionManager.getConnection();
        PreparedStatement ps = co.prepareStatement(query)){
            ps.setInt(1, factureId);
            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()) {
                    return paiementFromResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Paiement> findByContratId(int contratId) throws SQLException {
        List<Paiement> paiements = new ArrayList<>();
        String query= "SELECT p.* FROM paiement p " +
                "JOIN facture f ON p.facture_id = f.id WHERE f.contrat_id = ? " +
                "UNION " +
                "SELECT p.* FROM paiement p " +
                "JOIN echeancier e ON p.echeancier_id = e.id WHERE e.contrat_id = ?";
        try (Connection co = ConnectionManager.getConnection();
        PreparedStatement ps = co.prepareStatement(query)){
            ps.setInt(1, contratId);
            ps.setInt(2, contratId);
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    paiements.add(paiementFromResultSet(rs));
                }
            }
        }
        return paiements;
    }

    @Override
    public List<Paiement> findByEcheancièrId(int echeancierId) throws SQLException {
        List<Paiement> paiements = new ArrayList<>();
        String query = "SELECT * from paiement where echeancier_id = ?";
        try (Connection co = ConnectionManager.getConnection();
        PreparedStatement ps = co.prepareStatement(query)){
            ps.setInt(1, echeancierId);
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    paiements.add(paiementFromResultSet(rs));
                }
            }
        }
        return paiements;
    }

    private Paiement paiementFromResultSet(ResultSet rs) throws SQLException {
        return new Paiement(
                rs.getInt("id"),
                rs.getDate("date_paiement").toLocalDate(),
                rs.getDouble("montant_paye")
        );
    }
}
