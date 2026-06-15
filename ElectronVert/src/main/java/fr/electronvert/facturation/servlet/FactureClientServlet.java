package fr.electronvert.facturation.servlet;

import fr.electronvert.facturation.dao.ContratDAO;
import fr.electronvert.facturation.dao.FactureDAO;
import fr.electronvert.facturation.dao.FraisRelanceDAO;
import fr.electronvert.facturation.dao.impl.ContratDAOJdbc;
import fr.electronvert.facturation.dao.impl.FactureDAOJdbc;
import fr.electronvert.facturation.dao.impl.FraisRelanceDAOJdbc;
import fr.electronvert.facturation.model.contrat.Contrat;
import fr.electronvert.facturation.model.facture.Facture;
import fr.electronvert.facturation.model.facture.StatutFacture;
import fr.electronvert.facturation.model.utilisateur.Utilisateur;
import fr.electronvert.facturation.services.FactureService;
import fr.electronvert.facturation.servlet.util.FreeMarkerUtil;
import fr.electronvert.facturation.servlet.viewmodel.FactureViewModel;
import freemarker.template.TemplateException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.*;

@WebServlet("/client/factures")
public class FactureClientServlet extends HttpServlet {

    private final ContratDAO contratDAO = new ContratDAOJdbc();
    private final FactureDAO factureDAO = new FactureDAOJdbc();
    private final FraisRelanceDAO fraisRelanceDAO = new FraisRelanceDAOJdbc();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utilisateur utilisateur = (Utilisateur) req.getSession().getAttribute("utilisateur");
        FactureService factureService = new FactureService(factureDAO, fraisRelanceDAO);
        if(utilisateur == null){
            resp.sendRedirect(req.getContextPath() + "/connexion.html");
            return;
        }
        try {
            List<Facture> allfactures= factureDAO.findAllByClientId(utilisateur.getId());
            List<Facture> allfacturesAPayer = factureDAO.findNonPayeesByClientId(utilisateur.getId());
            List<Contrat> tousContrats = contratDAO.findByClientId(utilisateur.getId());

            Map<Integer, Contrat> contratsParId = new HashMap<>();
            for (Contrat c : tousContrats) {
                contratsParId.put(c.getId(), c);
            }

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH);

            List<FactureViewModel> factures = new ArrayList<>();
            for (Facture f : allfactures) {
                double frais = f.getStatut() != StatutFacture.PAYEE
                        ? factureService.getTotalMontantAPayerTTCAvecFrais(f) - f.getMontantTTC()
                        : 0.0;
                factures.add(new FactureViewModel(
                        f.getId(),
                        f.getReference(),
                        f.getDateEmission().format(fmt),
                        f.getDateEcheance().format(fmt),
                        f.getMontantTTC(), frais,
                        f.getStatut(),
                        f.getContratId(),
                        contratsParId.get(f.getContratId()).getAdressePostale()
                ));
            }

            List<FactureViewModel> facturesAPayer = new ArrayList<>();
            double totalAPayer = 0;
            for (Facture f : allfacturesAPayer) {
                double frais = factureService.getTotalMontantAPayerTTCAvecFrais(f) - f.getMontantTTC();
                FactureViewModel vm = new FactureViewModel(
                        f.getId(),
                        f.getReference(),
                        f.getDateEmission().format(fmt),
                        f.getDateEcheance().format(fmt),
                        f.getMontantTTC(), frais,
                        f.getStatut(),
                        f.getContratId(),
                        contratsParId.get(f.getContratId()).getAdressePostale()
                );
                facturesAPayer.add(vm);
                totalAPayer += vm.getMontantTotalFraisInclusBrut();
            }
            String totalAPayerFormate = String.format("%.2f", totalAPayer).replace(".", ",") + " €";

            Map<String, Object> model = new HashMap<>();
            model.put("utilisateur", utilisateur);
            model.put("factures", factures);
            model.put("facturesAPayer", facturesAPayer);
            model.put("contrats", tousContrats);
            model.put("totalAPayer", totalAPayerFormate);
            model.put("pageActive", "factures");
            FreeMarkerUtil.render("mes-factures.ftl", model, resp);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int factureId = Integer.parseInt(req.getParameter("factureId"));
        try {
            factureDAO.updateStatut(factureId, StatutFacture.PAYEE);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"success\": true}");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
