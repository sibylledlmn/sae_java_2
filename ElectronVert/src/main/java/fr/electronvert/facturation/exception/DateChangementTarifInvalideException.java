package fr.electronvert.facturation.exception;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


/**
 * Exception levée lorsqu'une date de changement de tarif est invalide.
 * <p>
 * Dans le système de facturation ElectronVert, les changements de tarifs
 * sont strictement encadrés et ne peuvent intervenir qu'à des dates
 * réglementaires fixes :
 * </p>
 * <ul>
 *     <li>le <strong>1er février</strong></li>
 *     <li>le <strong>1er août</strong></li>
 * </ul>
 * <p>
 * Cette exception est déclenchée lorsqu'un tarif est créé ou programmé
 * avec une date d'entrée en vigueur ne correspondant pas à l'une de ces
 * deux dates autorisées.
 * </p>
 *
 * <p>
 * Le message d'erreur généré inclut :
 * </p>
 * <ul>
 *     <li>la date effectivement tentée</li>
 *     <li>un rappel des dates autorisées</li>
 * </ul>
 */


public class DateChangementTarifInvalideException extends RuntimeException {


    private static final String DATES_AUTORISEES = "1er février ou le 1er août";

    private final LocalDate dateTentee;

    protected static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("d MMMM yyyy");


    /**
     * Constructeur de l'exception.
     * <p>
     * Crée une exception avec un message descriptif incluant la date tentée
     * et les dates autorisées pour les changements de tarif.
     * </p>
     *
     * @param dateTentee la date à laquelle le changement de tarif a été tenté (non null)
     */
    public DateChangementTarifInvalideException(LocalDate dateTentee) {
        super("Un nouveau tarif ne peut pas entrer en vigueur le: " + dateTentee.format(FORMATTER) + ". " +
                "Un nouveau tarif peut entrer en vigueur le  " + DATES_AUTORISEES + ".");
        this.dateTentee = dateTentee;
    }




}

