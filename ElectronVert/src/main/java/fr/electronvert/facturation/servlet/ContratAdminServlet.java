package fr.electronvert.facturation.servlet;

import fr.electronvert.facturation.dao.ContratDAO;
import fr.electronvert.facturation.dao.impl.ContratDAOJdbc;
import fr.electronvert.facturation.model.contrat.Contrat;
import fr.electronvert.facturation.model.utilisateur.RoleUtilisateur;
import fr.electronvert.facturation.model.utilisateur.Utilisateur;
import fr.electronvert.facturation.servlet.util.FreeMarkerUtil;
import fr.electronvert.facturation.servlet.viewmodel.ContratAdminViewModel;
import freemarker.template.TemplateException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/contrats")
public class ContratAdminServlet extends HttpServlet {

    ContratDAO contratDAO = new ContratDAOJdbc();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utilisateur utilisateur = (Utilisateur) req.getSession().getAttribute("utilisateur");
        if (utilisateur == null) {
            resp.sendRedirect(req.getContextPath() + "/connexion.html");
            return;
        }
        if (utilisateur.getRole() != RoleUtilisateur.ADMINISTRATEUR) {
            resp.sendRedirect(req.getContextPath() + "/client/dashboard");
            return;
        }
        String offre = req.getParameter("offre");
        String statut = req.getParameter("statut");
        String mode = req.getParameter("mode");
        String search = req.getParameter("search");
        try {
            List<Contrat> contrats = contratDAO.findByFiltres(search, statut, offre, mode);
            List<ContratAdminViewModel> contratsVm = new ArrayList<>();
            for (Contrat contrat : contrats) {
                ContratAdminViewModel c = new ContratAdminViewModel(contrat.getId(), contrat.getAdressePostale(),
                        contrat.getClient().getPrenom(), contrat.getClient().getNom(), contrat.getLibelleOffre(),
                        contrat.getLibelleMode(), contrat.getDateSouscription().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), contrat.getStatut().name());
                contratsVm.add(c);
            }
            Map<String, Object> model = new HashMap<>();
            model.put("contrats", contratsVm);
            model.put("statut", statut != null ? statut : "");
            model.put("offre", offre != null ? offre : "");
            model.put("mode", mode != null ? mode : "");
            model.put("search", search != null ? search : "");
            model.put("administrateur", utilisateur);
            model.put("pageActive", "contrats");
            FreeMarkerUtil.render("contrats-admin.ftl", model, resp);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }
    }
}
