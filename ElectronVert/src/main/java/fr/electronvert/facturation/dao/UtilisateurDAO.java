package fr.electronvert.facturation.dao;

import fr.electronvert.facturation.model.utilisateur.Utilisateur;

import java.sql.SQLException;
import java.util.List;

public interface UtilisateurDAO {

    int save(Utilisateur utilisateur) throws SQLException;

    Utilisateur findById(int id) throws SQLException;

    Utilisateur findByEmail(String email) throws SQLException;

    List<Utilisateur> findAll() throws SQLException;

    void delete(int id) throws SQLException;

    void update(Utilisateur utilisateur) throws SQLException;

    void updateMotDePasse(int id, String nouveauMotDePasseHash) throws SQLException;

}
