package fr.electronvert.facturation.dao.impl;

import fr.electronvert.facturation.dao.ConnectionManager;
import fr.electronvert.facturation.dao.EcheancierDAO;
import fr.electronvert.facturation.model.contrat.Echeancier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EcheancierDAOJdbc implements EcheancierDAO {
    @Override
    public int save(Echeancier echeancier, int contratId) throws SQLException {
        String query = "INSERT INTO echeancier (contrat_id, date_debut, montant_mensualite) " +
                "VALUES (?, ?, ?)";
        try(Connection co = ConnectionManager.getConnection();
            PreparedStatement ps = co.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
            ps.setInt(1, contratId);
            ps.setDate(2, Date.valueOf(echeancier.getDateDebut()));
            ps.setDouble(3, echeancier.getMontantMensualite());
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
    public Echeancier findActifByContratId(int contratId) throws SQLException {
        String query = "SELECT * FROM echeancier WHERE contrat_id = ? AND termine = false";
        try(Connection co = ConnectionManager.getConnection();
        PreparedStatement ps = co.prepareStatement(query)){
            ps.setInt(1, contratId);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()) {
                    int nbEmises = rs.getInt("nb_mensualites_emises");
                    boolean termine = rs.getBoolean("termine");
                    return new Echeancier(rs.getInt("id"), rs.getDate("date_debut").toLocalDate(),
                            rs.getDouble("montant_mensualite"), nbEmises,
                            nbEmises < Echeancier.NOMBRE_MENSUALITES && !termine, termine);
                }
            }
        }
        return null;
    }

    @Override
    public List<Echeancier> findByContratId(int contratId) throws SQLException {
        List<Echeancier> echeanciers = new ArrayList<>();
        String query = "SELECT * FROM echeancier WHERE contrat_id = ?";
        try(Connection co = ConnectionManager.getConnection();
        PreparedStatement ps = co.prepareStatement(query)){
            ps.setInt(1, contratId);
            try(ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    int nbEmises = rs.getInt("nb_mensualites_emises");
                    boolean termine = rs.getBoolean("termine");
                    Echeancier echeancier = new Echeancier(rs.getInt("id"), rs.getDate("date_debut").toLocalDate(),
                            rs.getDouble("montant_mensualite"), nbEmises,
                            nbEmises < Echeancier.NOMBRE_MENSUALITES && !termine, termine);
                    echeanciers.add(echeancier);
                }
            }
        }
        return  echeanciers;
    }

    @Override
    public void incrementerNbMensualitesEmises(int id) throws SQLException {
        String query = "UPDATE echeancier SET nb_mensualites_emises = nb_mensualites_emises+1 WHERE id = ?";
        try(Connection co = ConnectionManager.getConnection();
        PreparedStatement ps = co.prepareStatement(query)){
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public void terminer(int id) throws SQLException {
        String query = "UPDATE echeancier SET termine = true WHERE id = ?";
        try(Connection co = ConnectionManager.getConnection();
        PreparedStatement ps = co.prepareStatement(query)){
            ps.setInt(1, id);
            ps.executeUpdate();
        }

    }
}
