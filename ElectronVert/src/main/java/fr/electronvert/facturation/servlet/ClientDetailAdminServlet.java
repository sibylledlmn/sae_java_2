package fr.electronvert.facturation.servlet;

import fr.electronvert.facturation.dao.ContratDAO;
import fr.electronvert.facturation.dao.FactureDAO;
import fr.electronvert.facturation.dao.ReleveDAO;
import fr.electronvert.facturation.dao.UtilisateurDAO;
import fr.electronvert.facturation.dao.impl.ContratDAOJdbc;
import fr.electronvert.facturation.dao.impl.FactureDAOJdbc;
import fr.electronvert.facturation.dao.impl.ReleveDAOJdbc;
import fr.electronvert.facturation.dao.impl.UtilisateurDAOJdbc;
import fr.electronvert.facturation.model.contrat.Contrat;
import fr.electronvert.facturation.model.facture.Facture;
import fr.electronvert.facturation.model.utilisateur.RoleUtilisateur;
import fr.electronvert.facturation.model.utilisateur.Utilisateur;
import fr.electronvert.facturation.services.ReleveService;
import fr.electronvert.facturation.services.UtilisateurService;
import fr.electronvert.facturation.servlet.util.FreeMarkerUtil;
import fr.electronvert.facturation.servlet.viewmodel.ClientAdminViewModel;
import fr.electronvert.facturation.servlet.viewmodel.FactureViewModel;
import fr.electronvert.facturation.servlet.viewmodel.ReleveViewModel;
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
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/admin/client")
public class ClientDetailAdminServlet extends HttpServlet {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAOJdbc();
    private final ContratDAO contratDAO = new ContratDAOJdbc();
    private final FactureDAO factureDAO = new FactureDAOJdbc();
    private final ReleveDAO releveDAO = new ReleveDAOJdbc();

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

        String idParam = req.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/admin/clients");
            return;
        }

        int clientId;
        try {
            clientId = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/clients");
            return;
        }

        try {
            // Client
            UtilisateurService utilisateurService = new UtilisateurService(utilisateurDAO, contratDAO);
            ClientAdminViewModel client = utilisateurService.getClientDetailAdmin(clientId);
            if (client == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // Contrats actifs
            List<Contrat> contratsActifs = contratDAO.findActifsByClientId(clientId);

            // Map contratId → libellé pour le service de relevés
            List<Contrat> tousContrats = contratDAO.findByClientId(clientId);
            Map<Integer, String> contratRefs = tousContrats.stream()
                    .collect(Collectors.toMap(Contrat::getId, c -> "Contrat n°" + c.getId()));

            // Factures récentes (5 dernières)
            List<Facture> factures = factureDAO.findRecentesByClientId(clientId, 5);
            List<FactureViewModel> facturesRecentes = factures.stream()
                    .map(f -> new FactureViewModel(
                            f.getId(),
                            f.getReference(),
                            f.getDateEmission().format(FMT),
                            f.getDateEcheance().format(FMT),
                            f.getMontantTTC(),
                            0.0,
                            f.getDateProchaineRelance() != null ? f.getDateProchaineRelance().format(FMT) : null,
                            f.getStatut(),
                            f.getContratId(),
                            ""
                    ))
                    .toList();

            // Relevés avec consommation calculée
            ReleveService releveService = new ReleveService(releveDAO);
            List<ReleveViewModel> relevesRecents = releveService.getRelevesAvecConso(clientId, contratRefs);

            Map<String, Object> model = new HashMap<>();
            model.put("client", client);
            model.put("contratsActifs", contratsActifs);
            model.put("facturesRecentes", facturesRecentes);
            model.put("relevesRecents", relevesRecents);
            model.put("administrateur", utilisateur);
            model.put("pageActive", "clients");

            FreeMarkerUtil.render("client-detail-admin.ftl", model, resp);

        } catch (SQLException | TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        String nom = req.getParameter("nom");
        String prenom = req.getParameter("prenom");
        String email = req.getParameter("email");
        try {
            Utilisateur client = utilisateurDAO.findById(id);
            if (client == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
            else {
                UtilisateurService utilisateurService = new UtilisateurService(utilisateurDAO, contratDAO);
                utilisateurService.modifierInfos(client, prenom, nom, email);
            }
            resp.sendRedirect(req.getContextPath() + "/admin/client?id=" + id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
