package fr.electronvert.facturation.servlet;

import fr.electronvert.facturation.dao.ContratDAO;
import fr.electronvert.facturation.dao.FactureDAO;
import fr.electronvert.facturation.dao.TarifDAO;
import fr.electronvert.facturation.dao.impl.ContratDAOJdbc;
import fr.electronvert.facturation.dao.impl.FactureDAOJdbc;
import fr.electronvert.facturation.dao.impl.TarifDAOJdbc;
import fr.electronvert.facturation.exception.ChangementModeFacturationImpossibleException;
import fr.electronvert.facturation.model.contrat.*;
import fr.electronvert.facturation.model.tarif.Tarif;
import fr.electronvert.facturation.model.utilisateur.Utilisateur;
import fr.electronvert.facturation.services.ContratService;
import fr.electronvert.facturation.servlet.util.FreeMarkerUtil;
import freemarker.template.TemplateException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.List;
import java.util.Map;

@WebServlet("/client/contrats")
public class ContratsClientServlet extends HttpServlet {

    private final ContratDAO contratDAO = new ContratDAOJdbc();
    private final FactureDAO factureDAO = new FactureDAOJdbc();
    private final TarifDAO tarifDAO = new TarifDAOJdbc();
    private final ContratService contratService = new ContratService(contratDAO, factureDAO);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utilisateur utilisateur = (Utilisateur) req.getSession().getAttribute("utilisateur");
        if(utilisateur == null){
            resp.sendRedirect(req.getContextPath() + "/connexion.html");
            return;
        }
        try {
            List<Contrat> contratsActifs = contratDAO.findActifsByClientId(utilisateur.getId());
            List<Contrat> tousLesContrats = contratDAO.findByClientId(utilisateur.getId());
            List<Contrat> contratsClotures = tousLesContrats.stream()
                    .filter(c -> c.getStatut() == StatutContrat.CLOTURE)
                    .toList();
            Tarif tarifactif = tarifDAO.findActiveAtDate(LocalDate.now());
            Map<String, Object> model = new HashMap<>();
            model.put("utilisateur", utilisateur);
            model.put("contratsActifs", contratsActifs);
            model.put("contratsClotures", contratsClotures);
            model.put("tarif", tarifactif);
            model.put("pageActive", "contrats");

            FreeMarkerUtil.render("mes-contrats.ftl", model, resp);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        int contratId = Integer.parseInt(req.getParameter("contratId"));
        Utilisateur utilisateur = (Utilisateur) req.getSession().getAttribute("utilisateur");
        try {
            if (action.equals("changerOffre")) {
                OffreTarifaire offreCible;
                String offreCibleString = req.getParameter("offreCible");
                if ("CLASSIQUE".equals(offreCibleString)) {
                    offreCible = new OffreClassique();
                } else {
                    offreCible = new OffreHPHC();
                }
                boolean fraisApplicables = contratService.changerOffre(contratId, offreCible);
            }
            else if (action.equals("changerMode")) {
                ModeFacturation modeCible;
                String modeCibleString = req.getParameter("modeCible");
                if ("REEL".equals(modeCibleString)) {
                    modeCible = ModeFacturation.REEL;
                }
                else {
                    modeCible = ModeFacturation.ECHEANCIER;
                }
                contratService.changerModeFacturation(contratId, modeCible);
            }
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"success\": true}");
        } catch (ChangementModeFacturationImpossibleException e) {
            resp.setContentType("application/json;charset=UTF-8");
            String dateFormatee = e.getProchaineeDateAutorisee().format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH));
            resp.getWriter().write("{\"success\": false, \"message\": \"Changement impossible. Prochaine date autorisée : " + dateFormatee + "\"}");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
