package fr.electronvert.facturation.model.facture;

/**
 * Énumération représentant les différents états possibles d'une facture.
 * <p>
 * Le statut d'une facture permet de suivre son cycle de vie,
 * depuis son émission jusqu'à son paiement ou son passage en impayé.
 * </p>
 */
public enum StatutFacture {

        /**
         * Facture émise et en attente de règlement.
         */
        EMISE,

        /**
         * Facture réglée par le client.
         */
        PAYEE,

        /**
         * Facture non réglée à la date d'échéance.
         */
        IMPAYEE
}
