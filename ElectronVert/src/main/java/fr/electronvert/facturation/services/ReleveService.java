package fr.electronvert.facturation.services;

import fr.electronvert.facturation.dao.ReleveDAO;
import fr.electronvert.facturation.model.releve.Releve;
import fr.electronvert.facturation.model.releve.TypeConso;
import fr.electronvert.facturation.servlet.viewmodel.ReleveViewModel;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ReleveService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ReleveDAO releveDAO;

    public ReleveService(ReleveDAO releveDAO) {
        this.releveDAO = releveDAO;
    }

    /**
     * Retourne les relevés d'un client avec la consommation calculée entre relevés consécutifs
     * du même contrat. Les relevés sont triés du plus récent au plus ancien.
     *
     * @param clientId    identifiant du client
     * @param contratRefs map contratId → libellé à afficher (ex: "Contrat n°12")
     */
    public List<ReleveViewModel> getRelevesAvecConso(int clientId, Map<Integer, String> contratRefs) throws SQLException {
        // récupère releves du client avec le plus récent en premier
        List<Releve> relevesDsc = releveDAO.findAllByClientId(clientId);

        // On inverse pour parcourir chronologiquement et calculer les deltas
        List<Releve> relevesAsc = new ArrayList<>(relevesDsc);
        Collections.reverse(relevesAsc);

        // Pour chaque contrat, on mémorise le relevé précédent
        Map<Integer, Releve> dernierParContrat = new HashMap<>();
        List<ReleveViewModel> result = new ArrayList<>();

        // La map "dernierParContrat" associe chaque contrat à son dernier relevé traité.
        // Elle contient au maximum un relevé par contrat, et se met à jour à chaque itération :
        //   - .get()  → lit le précédent AVANT de traiter le relevé courant
        //   - .put()  → écrase l'entrée avec le relevé courant, qui deviendra le précédent au prochain tour
        // Ainsi, pour chaque relevé r, on peut calculer la consommation par rapport au relevé du même
        // contrat traité juste avant, sans jamais mélanger les relevés de contrats différents.
        for (Releve r : relevesAsc) {
            Releve precedent = dernierParContrat.get(r.getContratId());
            String ref = contratRefs.getOrDefault(r.getContratId(), "Contrat n°" + r.getContratId());
            result.add(buildViewModel(r, precedent, ref));
            dernierParContrat.put(r.getContratId(), r);
        }

        // On remet en DESC pour l'affichage (du plus récent au plus ancien)
        Collections.reverse(result);
        return result;
    }

    private ReleveViewModel buildViewModel(Releve r, Releve precedent, String contratRef) {
        String date = r.getDateDeReleve().format(FMT);
        String typeLibelle = libelleType(r);
        boolean isHPHC = r.getIndex().containsKey(TypeConso.HP);

        if (isHPHC) {
            String indexHP = formaterIndex(r.getIndex().get(TypeConso.HP));
            String indexHC = formaterIndex(r.getIndex().get(TypeConso.HC));
            String consoHP = null;
            String consoHC = null;

            if (precedent != null && precedent.getIndex().containsKey(TypeConso.HP)) {
                try {
                    Map<TypeConso, Double> conso = r.calculerConsommation(precedent);
                    consoHP = formaterConso(conso.get(TypeConso.HP));
                    consoHC = formaterConso(conso.get(TypeConso.HC));
                } catch (Exception ignored) {}
            }

            return new ReleveViewModel(date, typeLibelle, contratRef, indexHP, indexHC, consoHP, consoHC);
        } else {
            String indexTotal = formaterIndex(r.getIndex().get(TypeConso.TOTAL));
            String consoTotal = null;

            if (precedent != null && precedent.getIndex().containsKey(TypeConso.TOTAL)) {
                try {
                    Map<TypeConso, Double> conso = r.calculerConsommation(precedent);
                    consoTotal = formaterConso(conso.get(TypeConso.TOTAL));
                } catch (Exception ignored) {}
            }

            return new ReleveViewModel(date, typeLibelle, contratRef, indexTotal, consoTotal);
        }
    }

    private String libelleType(Releve r) {
        return switch (r.getTypeReleve()) {
            case MENSUEL        -> "Mensuel";
            case OUVERTURE      -> "Ouverture";
            case CLOTURE        -> "Clôture";
            case REGULARISATION -> "Régularisation";
        };
    }

    private String formaterIndex(Double val) {
        if (val == null) return "—";
        return String.format("%,.0f", val).replace(",", "\u00a0");
    }

    private String formaterConso(Double val) {
        if (val == null) return null;
        return String.format("%,.0f kWh", val).replace(",", "\u00a0");
    }
}
