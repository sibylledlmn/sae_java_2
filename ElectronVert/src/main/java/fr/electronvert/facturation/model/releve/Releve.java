package fr.electronvert.facturation.model.releve;

import fr.electronvert.facturation.model.contrat.Contrat;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;

/**
 * Représente un relevé de compteur associé à un contrat.
 * <p>
 * Un relevé contient des index de consommation (total ou HP/HC),
 * une date de relevé et un type de relevé (mensuel, clôture, etc.).
 * Il permet de calculer la consommation réelle entre deux relevés.
 * </p>
 */
public class Releve implements Comparable<Releve> {

    // =====================
    // ATTRIBUTS
    // =====================

    private int id;
    private int contratId;

    /**
     * Type de relevé (mensuel, clôture, etc.).
     */
    private final TypeReleve typeReleve;

    /**
     * Date à laquelle le relevé est effectué.
     */
    private final LocalDate dateDeReleve;

    /**
     * Contrat auquel est rattaché le relevé.
     */
    private final Contrat contrat;

    /**
     * Index de consommation relevés.
     */
    private final Map<TypeConso, Double> index;

    // =====================
    // CONSTRUCTEUR
    // =====================

    /**
     * Construit un relevé pour un contrat donné.
     *
     * @param contrat contrat associé
     * @param typeReleve type de relevé
     * @param dateDeReleve date du relevé
     * @param index index de consommation
     *
     * @throws IllegalArgumentException si les paramètres sont invalides
     */
    public Releve(
            Contrat contrat,
            TypeReleve typeReleve,
            LocalDate dateDeReleve,
            Map<TypeConso, Double> index
    ) {

        if (contrat == null) {
            throw new IllegalArgumentException("Un relevé doit être rattaché à un contrat");
        }
        if (dateDeReleve == null) {
            throw new IllegalArgumentException("La date du relevé est obligatoire");
        }
        if (index == null || index.isEmpty()) {
            throw new IllegalArgumentException("Les index ne peuvent pas être vides");
        }
        for (Double valeur : index.values()) {
            if (valeur < 0) {
                throw new IllegalArgumentException("Les index ne peuvent pas être négatifs");
            }
        }
        if (typeReleve == null) {
            throw new IllegalArgumentException("Le type de relevé est obligatoire");
        }

        this.contrat = contrat;
        this.typeReleve = typeReleve;
        this.dateDeReleve = dateDeReleve;
        this.index = Map.copyOf(index);
    }

    // =====================
    // MÉTHODES MÉTIER
    // =====================

    /**
     * Calcule la consommation entre ce relevé et un relevé précédent.
     *
     * @param precedent relevé précédent
     * @return consommation par type
     *
     * @throws IllegalArgumentException si les relevés sont incompatibles
     * @throws IllegalStateException si une consommation négative est détectée
     */
    public Map<TypeConso, Double> calculerConsommation(Releve precedent) {

        if (precedent == null) {
            throw new IllegalArgumentException("Le relevé précédent est requis");
        }

        if (precedent.dateDeReleve.isAfter(this.dateDeReleve)) {
            throw new IllegalArgumentException("Ordre chronologique invalide entre relevés");
        }

        Map<TypeConso, Double> consommation = new EnumMap<>(TypeConso.class);

        for (TypeConso typeConso : index.keySet()) {
            double valeurActuelle = index.get(typeConso);
            double valeurPrecedente = precedent.index.getOrDefault(typeConso, 0.0);

            double delta = valeurActuelle - valeurPrecedente;

            if (delta < 0) {
                throw new IllegalStateException("Consommation négative détectée");
            }

            consommation.put(typeConso, delta);
        }

        return consommation;
    }

    // Constructeur pour reconstruction depuis la BDD

    public Releve(int id, int contratId, TypeReleve typeReleve, LocalDate dateDeReleve, Map<TypeConso, Double> index) {
        this.id = id;
        this.contrat = null;
        this.contratId = contratId;
        this.typeReleve = typeReleve;
        this.dateDeReleve = dateDeReleve;
        this.index = Map.copyOf(index);
    }

    // =====================
    // GETTERS
    // =====================

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getContratId() { return contratId; }

    public TypeReleve getTypeReleve() {
        return typeReleve;
    }

    public LocalDate getDateDeReleve() {
        return dateDeReleve;
    }

    public Contrat getContrat() {
        return contrat;
    }

    public Map<TypeConso, Double> getIndex() {
        return index;
    }

    // =====================
    // AFFICHAGE
    // =====================

    /**
     * Résumé court du relevé pour les listes.
     *
     * @return résumé du relevé
     */
    public String toResume() {
        StringBuilder sb = new StringBuilder();
        sb.append(typeReleve).append(" - ").append(dateDeReleve);

        if (index.containsKey(TypeConso.TOTAL)) {
            sb.append(" | Index : ")
                    .append(String.format("%.0f kWh", index.get(TypeConso.TOTAL)));
        } else if (index.containsKey(TypeConso.HP) && index.containsKey(TypeConso.HC)) {
            sb.append(" | HP : ")
                    .append(String.format("%.0f kWh", index.get(TypeConso.HP)));
            sb.append(", HC : ")
                    .append(String.format("%.0f kWh", index.get(TypeConso.HC)));
        }

        return sb.toString();
    }

    /**
     * Affichage détaillé du relevé.
     *
     * @return représentation textuelle complète
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("=== RELEVÉ ===\n");
        sb.append("Type : ").append(typeReleve).append("\n");
        sb.append("Date : ").append(dateDeReleve).append("\n");
        sb.append("Index :\n");

        for (Map.Entry<TypeConso, Double> entry : index.entrySet()) {
            sb.append("  ")
                    .append(entry.getKey())
                    .append(" : ")
                    .append(String.format("%.0f kWh", entry.getValue()))
                    .append("\n");
        }

        return sb.toString();
    }

    /**
     * Formate la consommation entre un relevé précédent et ce relevé.
     *
     * @param precedent Le relevé précédent
     * @param consommation La carte des consommations par type calculée entre les deux relevés
     * @return Une chaîne formatée représentant la consommation sur la période
     */
    public String affichageConsommationEntreDeuxReleves(Releve precedent, Map<TypeConso, Double> consommation) {
        StringBuilder ligne = new StringBuilder();
        ligne.append(String.format("Du %s au %s :",
                precedent.getDateDeReleve(),
                this.dateDeReleve
        ));

        // Formatage selon le type d'offre
        if (consommation.containsKey(TypeConso.HP) && consommation.containsKey(TypeConso.HC)) {
            // Contrat HP/HC - affiche HP, HC et total
            ligne.append(String.format("\n  • HP : %.2f kWh", consommation.get(TypeConso.HP)));
            ligne.append(String.format("\n  • HC : %.2f kWh", consommation.get(TypeConso.HC)));

            // Calcul et affichage du total pour HP/HC
            double total = consommation.get(TypeConso.HP) + consommation.get(TypeConso.HC);
            ligne.append(String.format("\n  • TOTAL : %.2f kWh", total));
        } else if (consommation.containsKey(TypeConso.TOTAL)) {
            // Contrat Classique - affiche seulement le total
            ligne.append(String.format("\n  • Consommation : %.2f kWh", consommation.get(TypeConso.TOTAL)));
        }

        return ligne.toString();
    }

    // =====================
    // COMPARAISON
    // =====================

    /**
     * Compare deux relevés par date.
     *
     * @param autre relevé à comparer
     * @return comparaison basée sur la date du relevé
     */
    @Override
    public int compareTo(Releve autre) {
        if (autre == null) {
            throw new NullPointerException("Le relevé à comparer ne peut pas être nul");
        }
        return this.dateDeReleve.compareTo(autre.dateDeReleve);
    }
}
