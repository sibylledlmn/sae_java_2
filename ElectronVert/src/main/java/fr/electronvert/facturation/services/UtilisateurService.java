package fr.electronvert.facturation.services;

import fr.electronvert.facturation.dao.UtilisateurDAO;
import fr.electronvert.facturation.model.utilisateur.ClientResume;
import fr.electronvert.facturation.model.utilisateur.Utilisateur;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.time.YearMonth;
import java.util.List;

public class UtilisateurService {

    private final UtilisateurDAO utilisateurDAO;

    public UtilisateurService(UtilisateurDAO utilisateurDAO) {
        this.utilisateurDAO = utilisateurDAO;
    }

    public void modifierInfos(Utilisateur utilisateur, String prenom, String nom, String email) throws SQLException {
        utilisateur.setPrenom(prenom);
        utilisateur.setNom(nom);
        utilisateur.setEmail(email);
        utilisateurDAO.update(utilisateur);
    }

    public void modifierMotDePasse(Utilisateur utilisateur, String mdpActuel, String nouveauMdp, String confirmationMdp) throws SQLException {
        if (!BCrypt.checkpw(mdpActuel, utilisateur.getMotDePasse())) {
            throw new IllegalArgumentException("Mot de passe actuel incorrect");
        }
        if (!nouveauMdp.equals(confirmationMdp)) {
            throw new IllegalArgumentException("Les mots de passe saisis ne correspondent pas.");
        }
        String hash = BCrypt.hashpw(nouveauMdp, BCrypt.gensalt());
        utilisateur.setMotDePasse(hash);
        utilisateurDAO.updateMotDePasse(utilisateur.getId(), hash);
    }

    public int getNbNouveauxClients(YearMonth mois) throws SQLException{
        return utilisateurDAO.getNbNouveauClient(mois);
    }

    public List<ClientResume>  findDerniersClients(int nb) throws SQLException{
        return  utilisateurDAO.findDerniersClients(nb);
    }

}
