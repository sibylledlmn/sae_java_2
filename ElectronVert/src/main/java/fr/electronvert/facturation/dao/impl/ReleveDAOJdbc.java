package fr.electronvert.facturation.dao.impl;

import fr.electronvert.facturation.dao.ConnectionManager;
import fr.electronvert.facturation.dao.ReleveDAO;
import fr.electronvert.facturation.model.releve.Releve;
import fr.electronvert.facturation.model.releve.TypeConso;
import fr.electronvert.facturation.model.releve.TypeReleve;

import java.sql.*;
import java.util.EnumMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class ReleveDAOJdbc implements ReleveDAO {
    @Override
    public int save(Releve releve, int contratId) throws SQLException {
        String query = "INSERT INTO releve (contrat_id, type_releve, date_releve, index_total, index_hp, index_hc) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection co = ConnectionManager.getConnection();
             PreparedStatement ps = co.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, contratId);
            ps.setString(2, releve.getTypeReleve().name());
            ps.setDate(3, Date.valueOf(releve.getDateDeReleve()));
            ps.setObject(4, releve.getIndex().get(TypeConso.TOTAL));
            ps.setObject(5, releve.getIndex().get(TypeConso.HP));
            ps.setObject(6, releve.getIndex().get(TypeConso.HC));
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
    public List<Releve> findByContractId(int contractId) throws SQLException {
        List<Releve> releves = new ArrayList<>();
        String query = "SELECT * FROM releve WHERE contrat_id = ? ORDER BY date_releve DESC";
        try (Connection co = ConnectionManager.getConnection();
        PreparedStatement ps = co.prepareStatement(query)){
            ps.setInt(1, contractId);
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    releves.add(releveFromResultSet(rs));
                }
            }
        }
        return releves;
    }

    @Override
    public Releve findDernerByContractId(int contractId) throws SQLException {
        String query = "SELECT * FROM releve WHERE contrat_id = ? ORDER BY date_releve DESC LIMIT 1";
        try (Connection co = ConnectionManager.getConnection();
        PreparedStatement ps = co.prepareStatement(query)){
            ps.setInt(1, contractId);
            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()) {
                    return releveFromResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Releve> findAllByClientId(int clientId) throws SQLException {
        List<Releve> releves = new ArrayList<>();
        String query = " SELECT r.* FROM releve r\n" +
                "  JOIN contrat c ON r.contrat_id = c.id\n" +
                "  WHERE c.client_id = ?\n" +
                "  ORDER BY r.date_releve DESC\n";
        try (Connection co = ConnectionManager.getConnection();
        PreparedStatement ps = co.prepareStatement(query)){
            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    releves.add(releveFromResultSet(rs));
                }
            }
        }
        return releves;
    }

    private Releve releveFromResultSet(ResultSet rs) throws SQLException {
        Map<TypeConso, Double> index = new EnumMap<>(TypeConso.class);
        Double indexTotal = (Double) rs.getObject("index_total");
        Double indexHp = (Double) rs.getObject("index_hp");
        Double indexHc = (Double) rs.getObject("index_hc");
        if (indexTotal != null) index.put(TypeConso.TOTAL, indexTotal);
        if (indexHp != null) index.put(TypeConso.HP, indexHp);
        if (indexHc != null) index.put(TypeConso.HC, indexHc);
        return new Releve(
                rs.getInt("id"),
                rs.getInt("contrat_id"),
                TypeReleve.valueOf(rs.getString("type_releve")),
                rs.getDate("date_releve").toLocalDate(),
                index
        );
    }
}
