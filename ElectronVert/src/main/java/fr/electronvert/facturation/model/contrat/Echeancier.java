package fr.electronvert.facturation.model.contrat;

import fr.electronvert.facturation.model.facture.TauxTVA;

import java.time.LocalDate;

/**
 * Représente un échéancier de facturation pour un contrat.
 * <p>
 * Un échéancier permet de répartir une estimation annuelle
 * sur plusieurs mensualités fixes, avant une facture de
 * régularisation en fin de période.
 * </p>
 */
public class Echeancier {

    // =====================
    // CONSTANTES
    // =====================

    /**
     * Nombre de mensualités prévues dans un échéancier.
     */
    public static final int NOMBRE_MENSUALITES = 11;

    // =====================
    // ATTRIBUTS
    // =====================

    private int id;

    /**
     * Date de début de l'échéancier.
     */
    private final LocalDate dateDebut;


    /**
     * Nombre de mensualités déjà émises.
     */
    private int nbMensualitesEmises;

    /**
     * Montant HT d'une mensualité.
     */
    private final double montantMensualite;

    /**
     * Indique si l'échéancier peut émettre des mensualités
     */
    private boolean peutEmmetreMensualite;

    /**
     * Indique si l'échéancier est terminé.
     */
    private boolean termine;

    // =====================
    // CONSTRUCTEUR
    // =====================

    /**
     * Construit un échéancier à partir d'une estimation annuelle HT.
     *
     * @param dateDebut date de début de l'échéancier
     * @param estimationAnnuelleHT estimation annuelle hors taxes
     *
     * @throws IllegalArgumentException si l'estimation annuelle est invalide
     */
    public Echeancier(LocalDate dateDebut, double estimationAnnuelleHT) {
        if (estimationAnnuelleHT <= 0) {
            throw new IllegalArgumentException("Estimation annuelle invalide");
        }

        this.dateDebut = dateDebut;
        this.montantMensualite = estimationAnnuelleHT / NOMBRE_MENSUALITES;
        this.nbMensualitesEmises = 0;
        this.peutEmmetreMensualite = true;
        this.termine = false;
    }

    // Constructeur pour reconstruction depuis la BDD
    public Echeancier(int id, LocalDate dateDebut, double montantMensualite,
                      int nbMensualitesEmises, boolean peutEmmetreMensualite, boolean termine) {
        this.id = id;
        this.dateDebut = dateDebut;
        this.montantMensualite = montantMensualite;
        this.nbMensualitesEmises = nbMensualitesEmises;
        this.peutEmmetreMensualite = peutEmmetreMensualite;
        this.termine = termine;
    }

    // =====================
    // MÉTHODES MÉTIER
    // =====================

    /**
     * Indique si une nouvelle mensualité peut être émise.
     *
     * @return {@code true} si une mensualité peut être émise,
     *         {@code false} sinon
     */
    public boolean peutEmettreMensualite() {
        return peutEmmetreMensualite;
    }

    /**
     * Modifie si l'échéancier peut émettre des mensualités ou non
     *
     */
    public void setPeutEmmetreMensualite(boolean peutEmmetreMensualite) {
        this.peutEmmetreMensualite = peutEmmetreMensualite;
    }

    /**
     * Enregistre l'émission d'une mensualité.
     * Modifie le statut de peutEmettreMensualite en false si le nombre
     * maximum de mensualités est atteint
     *
     * @throws IllegalStateException si aucune mensualité ne peut être émise
     */
    public void enregistrerMensualiteEmise() {
        if (!peutEmettreMensualite()) {
            throw new IllegalStateException("Aucune mensualité ne peut être émise");
        }
        nbMensualitesEmises++;
        if(nbMensualitesEmises == NOMBRE_MENSUALITES) {
            peutEmmetreMensualite = false;
        }
    }



    /**
     * Termine définitivement l'échéancier.
     */
    public void terminer() {
        this.termine = true;
    }

    // =====================
    // GETTERS
    // =====================

    /**
     * Retourne le montant HT d'une mensualité.
     *
     * @return montant HT de la mensualité
     */
    public double getMontantMensualite() {
        return montantMensualite;
    }

    /**
     * Retourne le montant TTC d'une mensualité.
     *
     * @return montant TTC de la mensualité
     */
    public double getMontantMensualiteTTC() {
        return TauxTVA.NORMAL.calculerTTC(montantMensualite);
    }

    /**
     * Retourne le nombre de mensualités déjà émises.
     *
     * @return nombre de mensualités émises
     */
    public int getMensualitesEmises() {
        return nbMensualitesEmises;
    }

    /**
     * Retourne le nombre de mensualités restantes à émettre.
     *
     * @return nombre de mensualités restantes
     */
    public int getMensualitesRestantes() {
        return NOMBRE_MENSUALITES - nbMensualitesEmises;
    }

    /**
     * Indique si l'échéancier est terminé.
     *
     * @return {@code true} si l'échéancier est terminé
     */
    public boolean estTermine() {
        return termine;
    }

    /**
     * Retourne la date de début de l'échéancier.
     *
     * @return date de début
     */
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getDateDebut() {
        return dateDebut;
    }


}
