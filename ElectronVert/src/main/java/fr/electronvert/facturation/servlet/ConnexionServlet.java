package fr.electronvert.facturation.servlet;

import fr.electronvert.facturation.dao.UtilisateurDAO;
import fr.electronvert.facturation.dao.impl.UtilisateurDAOJdbc;
import fr.electronvert.facturation.model.utilisateur.RoleUtilisateur;
import fr.electronvert.facturation.model.utilisateur.Utilisateur;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/connexion")
public class ConnexionServlet extends HttpServlet {

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAOJdbc();


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String email = req.getParameter("email");
        String mdp = req.getParameter("mdp");
        try {
            Utilisateur utilisateur = utilisateurDAO.findByEmail(email);
            if (utilisateur == null) {
                resp.sendRedirect(req.getContextPath() + "/connexion.html?erreur=1");
                return;
            }
            if (!BCrypt.checkpw(mdp, utilisateur.getMotDePasse())) {
                resp.sendRedirect(req.getContextPath() + "/connexion.html?erreur=1");
                return;
            }
            req.getSession().setAttribute("utilisateur", utilisateur);
            if (utilisateur.getRole() == RoleUtilisateur.ADMINISTRATEUR) {
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            } else resp.sendRedirect(req.getContextPath() + "/client/dashboard");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }



}
