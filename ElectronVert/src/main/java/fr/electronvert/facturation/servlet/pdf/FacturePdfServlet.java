package fr.electronvert.facturation.servlet.pdf;

import fr.electronvert.facturation.dao.ContratDAO;
import fr.electronvert.facturation.dao.FactureDAO;
import fr.electronvert.facturation.dao.FraisRelanceDAO;
import fr.electronvert.facturation.dao.impl.ContratDAOJdbc;
import fr.electronvert.facturation.dao.impl.FactureDAOJdbc;
import fr.electronvert.facturation.dao.impl.FraisRelanceDAOJdbc;
import fr.electronvert.facturation.model.contrat.Contrat;
import fr.electronvert.facturation.model.facture.Facture;
import fr.electronvert.facturation.model.facture.FraisRelance;
import fr.electronvert.facturation.model.utilisateur.Utilisateur;
import fr.electronvert.facturation.services.FactureService;
import fr.electronvert.facturation.services.TotauxFraisRelance;
import fr.electronvert.facturation.servlet.util.FreeMarkerUtil;
import freemarker.template.TemplateException;
import org.xhtmlrenderer.pdf.ITextRenderer;

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

@WebServlet("/client/factures/pdf")
public class FacturePdfServlet extends HttpServlet {

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
        int factureId=Integer.parseInt(req.getParameter("factureId"));
        try {
            Facture facture = factureDAO.findById(factureId);
            Contrat contrat  = contratDAO.findById(facture.getContratId());
            List<FraisRelance> listFraisRelance = fraisRelanceDAO.findByFactureId(factureId);

            FactureService factureService = new FactureService(factureDAO, fraisRelanceDAO, contratDAO);
            TotauxFraisRelance totauxFrais = factureService.getTotauxFrais(facture);
            double totalFinal = factureService.getTotalMontantAPayerTTCAvecFrais(facture);

            String montantFraisRelance = String.format("%.2f", totauxFrais.getTtc()).replace(".", ",") + " €";
            String montantTotalFraisInclus = String.format("%.2f", totalFinal).replace(".", ",") + " €";

            Map<String, Object> model = new HashMap<>();
            model.put("utilisateur", utilisateur);
            model.put("facture", facture);
            model.put("contrat", contrat);
            model.put("libelleOffre", contrat.getLibelleOffreTarifaire(contrat.getOffreTarifaire()));
            model.put("libelleModeFacturation", contrat.getLibelleModeFacturation(contrat.getModeFacturation()));
            model.put("fraisRelance", listFraisRelance);
            model.put("montantFraisRelance", montantFraisRelance);
            model.put("montantTotalFraisInclus", montantTotalFraisInclus);

            freemarker.template.Configuration cfg = FreeMarkerUtil.getConfiguration();
            freemarker.template.Template template = cfg.getTemplate("facture-pdf.ftl");
            java.io.StringWriter sw = new java.io.StringWriter();
            template.process(model, sw);
            String html = sw.toString();

            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + facture.getReference() + ".pdf\"");
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(resp.getOutputStream());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        } catch (com.lowagie.text.DocumentException e) {
        throw new RuntimeException(e);
    }



}
}
