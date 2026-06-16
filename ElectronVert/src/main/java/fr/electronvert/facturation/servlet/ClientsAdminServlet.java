package fr.electronvert.facturation.servlet;

import fr.electronvert.facturation.dao.ContratDAO;
import fr.electronvert.facturation.dao.UtilisateurDAO;
import fr.electronvert.facturation.dao.impl.ContratDAOJdbc;
import fr.electronvert.facturation.dao.impl.UtilisateurDAOJdbc;
import fr.electronvert.facturation.model.utilisateur.RoleUtilisateur;
import fr.electronvert.facturation.model.utilisateur.Utilisateur;
import fr.electronvert.facturation.services.UtilisateurService;
import fr.electronvert.facturation.servlet.util.FreeMarkerUtil;
import fr.electronvert.facturation.servlet.viewmodel.ClientAdminViewModel;
import freemarker.template.TemplateException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/clients")
public class ClientsAdminServlet extends HttpServlet {

    UtilisateurDAO utilisateurDAO = new UtilisateurDAOJdbc();
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
        try{
            String email = req.getParameter("email");
            UtilisateurService utilisateurService = new UtilisateurService(utilisateurDAO, contratDAO);
            List<ClientAdminViewModel> clients;
            if(email != null && !email.isBlank()){
                clients = utilisateurService.getClientAdminParEmail(email);
            }
            else {
                clients = utilisateurService.getClientsAdmin();
            }
            int nbClients = clients.size();

            Map<String, Object> model = new HashMap<>();
            model.put("clients", clients);
            model.put("nbClients", nbClients);
            model.put("email", email != null ? email : "");
            model.put("administrateur", utilisateur);
            model.put("pageActive", "clients");
            FreeMarkerUtil.render("clients-admin.ftl", model, resp);

        }  catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }
    }
}
