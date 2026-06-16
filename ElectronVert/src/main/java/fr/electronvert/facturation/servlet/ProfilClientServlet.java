package fr.electronvert.facturation.servlet;

import fr.electronvert.facturation.dao.ContratDAO;
import fr.electronvert.facturation.dao.UtilisateurDAO;
import fr.electronvert.facturation.dao.impl.ContratDAOJdbc;
import fr.electronvert.facturation.dao.impl.UtilisateurDAOJdbc;
import fr.electronvert.facturation.model.contrat.Contrat;
import fr.electronvert.facturation.model.utilisateur.Utilisateur;
import fr.electronvert.facturation.services.UtilisateurService;
import fr.electronvert.facturation.servlet.util.FreeMarkerUtil;
import freemarker.template.TemplateException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@WebServlet("/client/profil")
public class ProfilClientServlet extends HttpServlet {

    UtilisateurDAO utilisateurDAO = new UtilisateurDAOJdbc();
    ContratDAO contratDAO = new ContratDAOJdbc();
    UtilisateurService utilisateurService = new UtilisateurService(utilisateurDAO, contratDAO);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utilisateur utilisateur = (Utilisateur) req.getSession().getAttribute("utilisateur");
        if (utilisateur == null) {
            resp.sendRedirect(req.getContextPath() + "/connexion.html");
            return;
        }
        try {
            List<Contrat> contrats = contratDAO.findByClientId(utilisateur.getId());
            Contrat contrat = contrats.get(0);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH);
            Map<String, Object> model = new HashMap<>();
            model.put("utilisateur", utilisateur);
            model.put("datePremierContrat", contrat.getDateSouscription().format(fmt));
            model.put("pageActive", "profil");
            FreeMarkerUtil.render("mon-profil.ftl", model, resp);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utilisateur utilisateur = (Utilisateur) req.getSession().getAttribute("utilisateur");
        if (utilisateur == null) {
            resp.sendRedirect(req.getContextPath() + "/connexion.html");
            return;
        }
        String prenom = req.getParameter("prenom");
        String nom = req.getParameter("nom");
        String email = req.getParameter("email");
        String mdpActuel = req.getParameter("mdpActuel");
        String mdpNouveau = req.getParameter("mdpNouveau");
        String mdpConfirm = req.getParameter("mdpConfirm");
        resp.setContentType("application/json;charset=UTF-8");
        if (mdpActuel != null && !mdpActuel.isEmpty()) {
            try {
                utilisateurService.modifierMotDePasse(utilisateur, mdpActuel, mdpNouveau, mdpConfirm);
                resp.getWriter().write("{\"success\": true}");
            } catch (IllegalArgumentException e) {
                resp.getWriter().write("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                utilisateurService.modifierInfos(utilisateur, prenom, nom, email);
                resp.getWriter().write("{\"success\": true}");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
