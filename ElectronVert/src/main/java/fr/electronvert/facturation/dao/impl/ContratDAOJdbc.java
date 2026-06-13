package fr.electronvert.facturation.dao.impl;

import fr.electronvert.facturation.dao.ConnectionManager;
import fr.electronvert.facturation.dao.ContratDAO;
import fr.electronvert.facturation.model.contrat.*;
import fr.electronvert.facturation.model.utilisateur.RoleUtilisateur;
import fr.electronvert.facturation.model.utilisateur.Utilisateur;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ContratDAOJdbc implements ContratDAO {

    @Override
    public int save(Contrat contrat) throws SQLException {
        String query = "INSERT INTO contrat (client_id, adresse_postale, offre_tarifaire, mode_facturation, date_souscription) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection co = ConnectionManager.getConnection();
             PreparedStatement ps = co.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, contrat.getClient().getId());
            ps.setString(2, contrat.getAdressePostale());
            ps.setString(3, contrat.getOffreTarifaire().getClass().getSimpleName());
            ps.setString(4, contrat.getModeFacturation().name());
            ps.setDate(5, Date.valueOf(contrat.getDateSouscription()));
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
    public void update(Contrat contrat) throws SQLException {
        String query = "UPDATE contrat SET adresse_postale = ?, offre_tarifaire = ?, offre_tarifaire_future = ?, " +
                "mode_facturation = ?, mode_facturation_future = ?, statut = ?, date_fin = ?, " +
                "frais_changement_offre_attente = ?, solde_crediteur = ?, facturation_terminee = ? " +
                "WHERE id = ?";
        try (Connection co = ConnectionManager.getConnection();
             PreparedStatement ps = co.prepareStatement(query)) {
            ps.setString(1, contrat.getAdressePostale());
            ps.setString(2, contrat.getOffreTarifaire().getClass().getSimpleName());
            OffreTarifaire offreFuture = contrat.getOffreTarifaireFuture();
            ps.setString(3, offreFuture == null ? null : offreFuture.getClass().getSimpleName());
            ps.setString(4, contrat.getModeFacturation().name());
            ModeFacturation modeFutur = contrat.getModeFacturationFutur();
            ps.setString(5, modeFutur == null ? null : modeFutur.name());
            ps.setString(6, contrat.getStatut().name());
            LocalDate dateFin = contrat.getDateFin();
            ps.setDate(7, dateFin == null ? null : Date.valueOf(dateFin));
            ps.setDouble(8, contrat.getFraisChangementOffreEnAttente());
            ps.setDouble(9, contrat.getSoldeCrediteur());
            ps.setBoolean(10, contrat.estFacturationTerminee());
            ps.setInt(11, contrat.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public Contrat findById(int id) throws SQLException {
        String query = "SELECT c.*, u.id as u_id, u.nom, u.prenom, u.email, u.role " +
                "FROM contrat c JOIN utilisateur u ON c.client_id = u.id " +
                "WHERE c.id = ?";
        try (Connection co = ConnectionManager.getConnection();
             PreparedStatement ps = co.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return contratFromResultSet(rs);
                }
            }
        }
        return null;
    }


    @Override
    public List<Contrat> findAll() throws SQLException {
        List<Contrat> contrats = new ArrayList<>();
        String query = "SELECT c.*, u.id as u_id, u.nom, u.prenom, u.email, u.role " +
                "FROM contrat c JOIN utilisateur u ON c.client_id = u.id " +
                "ORDER BY c.date_souscription DESC";
        try (Connection co = ConnectionManager.getConnection();
             PreparedStatement ps = co.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                contrats.add(contratFromResultSet(rs));
            }
        }
        return contrats;
    }

    @Override
    public List<Contrat> findByClientId(int clientId) throws SQLException {
       List<Contrat> contrats = new ArrayList<>();
       String query = "SELECT c.*, u.id as u_id, u.nom, u.prenom, u.email, u.role " +
               "FROM contrat c JOIN utilisateur u ON c.client_id = u.id " +
               "WHERE c.client_id = ? ORDER BY c.date_souscription ASC";
        try (Connection co = ConnectionManager.getConnection();
             PreparedStatement ps = co.prepareStatement(query)) {
            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    contrats.add(contratFromResultSet(rs));
                }
            }
        }
        return contrats;
    }

    @Override
    public void cloturer(int id, LocalDate dateFin) throws SQLException {
        String query = "UPDATE contrat SET date_fin = ?, statut = ? WHERE id = ?";
        try (Connection co = ConnectionManager.getConnection();
             PreparedStatement ps = co.prepareStatement(query)) {
            ps.setDate(1, Date.valueOf(dateFin));
            ps.setString(2, "CLOTURE");
            ps.setInt(3, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Contrat> findAFacturer() throws SQLException {
        List<Contrat> contrats = new ArrayList<>();
        String query = " SELECT c.*, u.id as u_id, u.nom, u.prenom, u.email, u.role  FROM contrat c JOIN utilisateur u ON c.client_id = u.id " +
                " WHERE c.facturation_terminee = false";
        try (Connection co = ConnectionManager.getConnection();
             PreparedStatement ps = co.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                contrats.add(contratFromResultSet(rs));
            }
        }
        return contrats;
    }

    @Override
    public List<Contrat> findActifs() throws SQLException {
        List<Contrat> contrats = new ArrayList<>();
        String query = " SELECT c.*, u.id as u_id, u.nom, u.prenom, u.email, u.role  FROM contrat c JOIN utilisateur u ON c.client_id = u.id " +
                " WHERE c.statut = 'ACTIF'";
        try (Connection co = ConnectionManager.getConnection();
             PreparedStatement ps = co.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                contrats.add(contratFromResultSet(rs));
            }
        }
        return contrats;
    }

    @Override
    public List<Contrat> findActifsByClientId(int clientId) throws SQLException {
        List<Contrat> contrats = new ArrayList<>();
        String query = "SELECT c.*, u.id as u_id, u.nom, u.prenom, u.email, u.role " +
                "FROM contrat c JOIN utilisateur u ON c.client_id = u.id " +
                "WHERE c.client_id = ? AND c.statut = 'ACTIF'";
        try (Connection co = ConnectionManager.getConnection();
             PreparedStatement ps = co.prepareStatement(query)) {
            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    contrats.add(contratFromResultSet(rs));
                }
            }
        }
        return contrats;
    }

    private static OffreTarifaire offreFromString(String nom) {
        return switch (nom) {
            case "OffreClassique" -> new OffreClassique();
            case "OffreHPHC" -> new OffreHPHC();
            default -> throw new IllegalArgumentException("Offre inconnue : " + nom);
        };
    }



    private Contrat contratFromResultSet(ResultSet rs) throws SQLException {
        Utilisateur client = new Utilisateur(
                rs.getInt("u_id"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("email"),
                RoleUtilisateur.valueOf(rs.getString("role"))
        );
        String offreFutureStr = rs.getString("offre_tarifaire_future");
        String modeFuturStr = rs.getString("mode_facturation_future");
        Date dateFin = rs.getDate("date_fin");
        return new Contrat(
                rs.getInt("id"),
                client,
                rs.getString("adresse_postale"),
                offreFromString(rs.getString("offre_tarifaire")),
                ModeFacturation.valueOf(rs.getString("mode_facturation")),
                rs.getDate("date_souscription").toLocalDate(),
                StatutContrat.valueOf(rs.getString("statut")),
                offreFutureStr == null ? null : offreFromString(offreFutureStr),
                modeFuturStr == null ? null : ModeFacturation.valueOf(modeFuturStr),
                dateFin == null ? null : dateFin.toLocalDate(),
                rs.getDouble("frais_changement_offre_attente"),
                rs.getDouble("solde_crediteur"),
                rs.getBoolean("facturation_terminee")
        );
    }

}
