package fr.electronvert.facturation.dao.impl;

import fr.electronvert.facturation.dao.ConnectionManager;
import fr.electronvert.facturation.dao.FactureDAO;
import fr.electronvert.facturation.model.facture.Facture;
import fr.electronvert.facturation.model.facture.StatutFacture;
import fr.electronvert.facturation.model.facture.TypeFacture;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FactureDAOJdbc implements FactureDAO {
    @Override
    public int save(Facture facture, int contratId) throws SQLException {
        String query = "INSERT INTO facture (contrat_id, reference, type_facture, date_emission, date_echeance, " +
                "montant_ht, montant_tva, montant_ttc, statut, contient_frais_changement_offre) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection co = ConnectionManager.getConnection();
             PreparedStatement ps = co.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, contratId);
            ps.setString(2, facture.getReference());
            ps.setString(3, facture.getType().name());
            ps.setDate(4, Date.valueOf(facture.getDateEmission()));
            ps.setDate(5, Date.valueOf(facture.getDateEcheance()));
            ps.setDouble(6, facture.getMontantHT());
            ps.setDouble(7, facture.getMontantTVA());
            ps.setDouble(8, facture.getMontantTTC());
            ps.setString(9, facture.getStatut().name());
            ps.setBoolean(10, facture.isContientFraisChangementOffre());
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
    public Facture findById(int id) throws SQLException {
        String query = "SELECT * FROM facture WHERE id = ?";
        try (Connection co = ConnectionManager.getConnection();
             PreparedStatement ps = co.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return factureFromResultSet(rs);
                }
            }
        }
        return null;
    }



    @Override
    public List<Facture> findByContratId(int contratId) throws SQLException {
        List<Facture> factures = new ArrayList<>();
        String query = "SELECT * FROM facture WHERE contrat_id = ?";
        try (Connection co = ConnectionManager.getConnection();
        PreparedStatement ps = co.prepareStatement(query)) {
            ps.setInt(1, contratId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    factures.add(factureFromResultSet(rs));
                }
            }
        }
        return factures;
    }

    @Override
    public List<Facture> findImpayees() throws SQLException {
        List<Facture> factures = new ArrayList<>();
        String query = "SELECT * FROM facture WHERE statut = 'IMPAYEE'";
        try (Connection co = ConnectionManager.getConnection();
        PreparedStatement ps = co.prepareStatement(query)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    factures.add(factureFromResultSet(rs));
                }
            }
        }
        return factures;
    }

    @Override
    public List<Facture> findEmisesEchues(LocalDate date) throws SQLException {
        List<Facture> factures = new ArrayList<>();
        String query = "SELECT * FROM facture WHERE statut = 'EMISE' AND date_echeance < ?";
        try (Connection co = ConnectionManager.getConnection();
        PreparedStatement ps = co.prepareStatement(query)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    factures.add(factureFromResultSet(rs));
                }
            }
        }
        return factures;
    }

    @Override
    public List<Facture> findARelancer(LocalDate date) throws SQLException {
        List<Facture> factures = new ArrayList<>();
        String query = "SELECT * FROM facture WHERE statut = 'IMPAYEE' AND date_prochaine_relance <= ?";
        try (Connection co = ConnectionManager.getConnection();
        PreparedStatement ps = co.prepareStatement(query)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    factures.add(factureFromResultSet(rs));
                }
            }
        }
        return factures;
    }

    @Override
    public void updateStatut(int id, StatutFacture statut) throws SQLException {
        String query = "UPDATE facture SET statut = ? WHERE id = ?";
        try (Connection co = ConnectionManager.getConnection();
        PreparedStatement ps = co.prepareStatement(query)) {
            ps.setString(1, statut.name());
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    @Override
    public void updateDateProchaineRelance(int id, LocalDate date) throws SQLException {
        String query = "UPDATE facture set date_prochaine_relance = ? WHERE id = ?";
        try (Connection co = ConnectionManager.getConnection();
        PreparedStatement ps = co.prepareStatement(query)) {
            ps.setDate(1, Date.valueOf(date));
            ps.setInt(2, id);
            ps.executeUpdate();
        }

    }

    @Override
    public List<Facture> findImpayeesByClientId(int clientId) throws SQLException {
        List<Facture> factures = new ArrayList<>();
        String query = "SELECT f.* FROM facture f " +
                "JOIN contrat c ON f.contrat_id = c.id " +
                "WHERE c.client_id = ? AND f.statut IN ('EMISE', 'IMPAYEE') " +
                "ORDER BY f.date_echeance DESC";
        try (Connection co = ConnectionManager.getConnection();
             PreparedStatement ps = co.prepareStatement(query)) {
            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    factures.add(factureFromResultSet(rs));
                }
            }
        }
        return factures;
    }

    private Facture factureFromResultSet(ResultSet rs) throws SQLException {
        Date dateProchaineRelance = rs.getDate("date_prochaine_relance");
        return new Facture(
                rs.getInt("id"),
                rs.getString("reference"),
                TypeFacture.valueOf(rs.getString("type_facture")),
                rs.getDate("date_emission").toLocalDate(),
                rs.getDate("date_echeance").toLocalDate(),
                dateProchaineRelance == null ? null : dateProchaineRelance.toLocalDate(),
                rs.getDouble("montant_ht"),
                rs.getDouble("montant_tva"),
                rs.getDouble("montant_ttc"),
                StatutFacture.valueOf(rs.getString("statut")),
                rs.getBoolean("contient_frais_changement_offre")
        );
    }
}
