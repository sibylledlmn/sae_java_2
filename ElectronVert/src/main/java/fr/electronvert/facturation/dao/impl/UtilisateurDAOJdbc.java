package fr.electronvert.facturation.dao.impl;

import fr.electronvert.facturation.dao.ConnectionManager;
import fr.electronvert.facturation.dao.UtilisateurDAO;
import fr.electronvert.facturation.model.utilisateur.RoleUtilisateur;
import fr.electronvert.facturation.model.utilisateur.Utilisateur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAOJdbc implements UtilisateurDAO {

    // TODO : lors de la création par l'admin, générer un mot de passe temporaire aléatoire
    //        avec SecureRandom, le hasher avec BCrypt (jbcrypt) et l'envoyer au client.
    //        Le client pourra le changer dans son espace perso.

    @Override
    public int save(Utilisateur utilisateur) throws SQLException {
        String query = "INSERT INTO utilisateur (nom, prenom, email, role, mot_de_passe) VALUES (?, ?, ?, ?, ?)";
        try (Connection co = ConnectionManager.getConnection();
             PreparedStatement ps= co.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
            ps.setString(1, utilisateur.getNom());
            ps.setString(2, utilisateur.getPrenom());
            ps.setString(3, utilisateur.getEmail());
            ps.setString(4, utilisateur.getRole().toString());
            ps.setString(5, utilisateur.getMotDePasse());
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
    public Utilisateur findById(int id) throws SQLException {
        String query = "SELECT * FROM utilisateur WHERE id = ?";
        try (Connection co = ConnectionManager.getConnection();
             PreparedStatement ps = co.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Utilisateur(rs.getInt("id"), rs.getString("nom"), rs.getString("prenom"), rs.getString("email"), RoleUtilisateur.valueOf(rs.getString("role")));
                }
            }
        }
        return null;
    }

    @Override
    public Utilisateur findByEmail(String email) throws SQLException {
        String query = "SELECT * FROM utilisateur WHERE email = ?";
        try (Connection co = ConnectionManager.getConnection();
             PreparedStatement ps = co.prepareStatement(query)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Utilisateur u = new Utilisateur(rs.getInt("id"), rs.getString("nom"), rs.getString("prenom"), rs.getString("email"), RoleUtilisateur.valueOf(rs.getString("role")));
                    u.setMotDePasse(rs.getString("mot_de_passe"));
                    return u;
                }
            }
        }
        return null;
    }

    @Override
    public List<Utilisateur> findAll() throws SQLException {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String query = "SELECT * FROM utilisateur";
        try (Connection co = ConnectionManager.getConnection();
             PreparedStatement ps = co.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                utilisateurs.add(new Utilisateur(rs.getInt("id"), rs.getString("nom"), rs.getString("prenom"), rs.getString("email"), RoleUtilisateur.valueOf(rs.getString("role"))));
            }
        }
        return utilisateurs;
    }

    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM utilisateur WHERE id = ?";
        try (Connection co = ConnectionManager.getConnection();
        PreparedStatement ps = co.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public void update(Utilisateur utilisateur) throws SQLException {
        String query = "UPDATE utilisateur SET nom = ?, prenom = ?, email = ? WHERE id = ?";
        try (Connection co = ConnectionManager.getConnection();
        PreparedStatement ps = co.prepareStatement(query)) {
            ps.setString(1, utilisateur.getNom());
            ps.setString(2, utilisateur.getPrenom());
            ps.setString(3, utilisateur.getEmail());
            ps.setInt(4, utilisateur.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void updateMotDePasse(int id, String nouveauMotDePasseHash) throws SQLException {
        String query = "UPDATE utilisateur SET mot_de_passe = ? WHERE id = ?";
        try (Connection co = ConnectionManager.getConnection();
             PreparedStatement ps = co.prepareStatement(query)) {
            ps.setString(1, nouveauMotDePasseHash);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }
}
