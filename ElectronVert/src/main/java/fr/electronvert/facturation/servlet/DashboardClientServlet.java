package fr.electronvert.facturation.servlet;

import fr.electronvert.facturation.dao.ContratDAO;
import fr.electronvert.facturation.dao.FactureDAO;
import fr.electronvert.facturation.dao.FraisRelanceDAO;
import fr.electronvert.facturation.dao.impl.ContratDAOJdbc;
import fr.electronvert.facturation.dao.impl.FactureDAOJdbc;
import fr.electronvert.facturation.dao.impl.FraisRelanceDAOJdbc;
import fr.electronvert.facturation.services.FactureService;
import fr.electronvert.facturation.model.contrat.Contrat;
import fr.electronvert.facturation.model.contrat.StatutContrat;
import fr.electronvert.facturation.model.facture.Facture;
import fr.electronvert.facturation.model.utilisateur.Utilisateur;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@WebServlet("/client/dashboard")
public class DashboardClientServlet extends HttpServlet {

    private final ContratDAO contratDAO = new ContratDAOJdbc();
    private final FactureDAO factureDAO = new FactureDAOJdbc();
    private final FraisRelanceDAO fraisRelanceDAO = new FraisRelanceDAOJdbc();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utilisateur utilisateur = (Utilisateur) req.getSession().getAttribute("utilisateur");
        if(utilisateur == null){
            resp.sendRedirect(req.getContextPath() + "/connexion.html");
            return;
        }
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            List<Contrat> contratsActifs = contratDAO.findActifsByClientId(utilisateur.getId());
            List<Contrat> tousLesContrats = contratDAO.findByClientId(utilisateur.getId());
            List<Facture> facturesImpayees = factureDAO.findNonPayeesByClientId(utilisateur.getId());
            List<Facture> factures = factureDAO.findRecentesByClientId(utilisateur.getId(), 5);
            Map<Integer, Contrat> contratsParId = new HashMap<>();
            for (Contrat c : tousLesContrats) {
                contratsParId.put(c.getId(), c);
            }
            List<Contrat> contratsClotures = tousLesContrats.stream()
                    .filter(c -> c.getStatut() == StatutContrat.CLOTURE)
                    .toList();
            FactureService factureService = new FactureService(factureDAO, fraisRelanceDAO);
            double totalDu = 0.0;
            for (Facture facture : facturesImpayees) {
                totalDu += factureService.getTotalMontantAPayerTTCAvecFrais(facture);
            }
            FactureViewModel derniereFacture = null;
            if(!factures.isEmpty()){
                Facture f = factures.get(0);
                derniereFacture = new FactureViewModel(
                        f.getId(),
                        f.getReference(),
                        f.getDateEmission().format(fmt),
                        f.getDateEcheance().format(fmt),
                        f.getMontantTTC(), 0.0,
                        null,
                        f.getStatut(),
                        f.getContratId(),
                        contratsParId.get(f.getContratId()).getAdressePostale()
                );
            }
            LocalDate prochaineEcheance = facturesImpayees.stream()
                    .map(Facture::getDateEcheance)
                    .min(LocalDate::compareTo)
                    .orElse(null);
            List<FactureViewModel> factureViewModels = new ArrayList<>();

            for (Facture facture : factures) {
                factureViewModels.add(new FactureViewModel(facture.getId(), facture.getReference(), facture.getDateEmission().format(fmt), facture.getDateEcheance().format(fmt),
                        facture.getMontantTTC(), 0.0,
                        null,
                        facture.getStatut(), facture.getContratId(), contratsParId.get(facture.getContratId()).getAdressePostale()));
            }

            String mois = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH));
            mois = mois.substring(0, 1).toUpperCase() + mois.substring(1);

            Map<String, Object> model = new HashMap<>();
            model.put("utilisateur", utilisateur);
            model.put("contratsActifs", contratsActifs);
            model.put("facturesImpayees", facturesImpayees);
            model.put("facturesRecentes", factureViewModels);
            model.put("totalDu", totalDu);
            model.put("derniereFacture", derniereFacture);
            model.put("prochaineEcheance", prochaineEcheance);
            model.put("mois", mois);
            model.put("contratsClotures", contratsClotures);
            model.put("pageActive", "dashboard");

            FreeMarkerUtil.render("dashboard-client.ftl", model, resp);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }

    }
}
