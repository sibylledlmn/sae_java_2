-- ============================================================
-- ElectronVert - Schema de base de donnees
-- ============================================================

CREATE TABLE utilisateur (
    id           INT          NOT NULL AUTO_INCREMENT,
    nom          VARCHAR(100) NOT NULL,
    prenom       VARCHAR(100) NOT NULL,
    email        VARCHAR(255) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    role         VARCHAR(20)  NOT NULL,
    PRIMARY KEY (id)
);

-- ============================================================

CREATE TABLE tarif (
    id                        INT          NOT NULL AUTO_INCREMENT,
    date_debut                DATE         NOT NULL,
    prix_kwh_classique        DECIMAL(8,4) NOT NULL,
    prix_kwh_hp               DECIMAL(8,4) NOT NULL,
    prix_kwh_hc               DECIMAL(8,4) NOT NULL,
    prix_abonnement_classique DECIMAL(8,2) NOT NULL,
    prix_abonnement_hphc      DECIMAL(8,2) NOT NULL,
    PRIMARY KEY (id)
);

-- ============================================================

CREATE TABLE contrat (
    id                             INT          NOT NULL AUTO_INCREMENT,
    client_id                      INT          NOT NULL,
    adresse_postale                VARCHAR(255) NOT NULL,
    offre_tarifaire                VARCHAR(20)  NOT NULL,
    offre_tarifaire_future         VARCHAR(20)  NULL,
    mode_facturation               VARCHAR(20)  NOT NULL,
    mode_facturation_future        VARCHAR(20)  NULL,
    statut                         VARCHAR(20)  NOT NULL DEFAULT 'ACTIF',
    date_souscription              DATE         NOT NULL,
    date_fin                       DATE         NULL,
    frais_changement_offre_attente DECIMAL(8,2) NOT NULL DEFAULT 0.00,
    solde_crediteur                DECIMAL(8,2) NOT NULL DEFAULT 0.00,
    facturation_terminee           BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    FOREIGN KEY (client_id) REFERENCES utilisateur(id)
);

-- ============================================================

CREATE TABLE echeancier (
    id                    INT          NOT NULL AUTO_INCREMENT,
    contrat_id            INT          NOT NULL,
    date_debut            DATE         NOT NULL,
    montant_mensualite    DECIMAL(8,2) NOT NULL,
    nb_mensualites_emises INT          NOT NULL DEFAULT 0,
    termine               BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    FOREIGN KEY (contrat_id) REFERENCES contrat(id)
);

-- ============================================================

CREATE TABLE releve (
    id          INT           NOT NULL AUTO_INCREMENT,
    contrat_id  INT           NOT NULL,
    type_releve VARCHAR(20)   NOT NULL,
    date_releve DATE          NOT NULL,
    index_total DECIMAL(10,2) NULL,
    index_hp    DECIMAL(10,2) NULL,
    index_hc    DECIMAL(10,2) NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (contrat_id) REFERENCES contrat(id)
);

-- ============================================================

CREATE TABLE facture (
    id                              INT           NOT NULL AUTO_INCREMENT,
    reference                       VARCHAR(30)   NOT NULL UNIQUE,
    contrat_id                      INT           NOT NULL,
    type_facture                    VARCHAR(30)   NOT NULL,
    date_emission                   DATE          NOT NULL,
    date_echeance                   DATE          NOT NULL,
    date_prochaine_relance          DATE          NULL,
    montant_ht                      DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    montant_tva                     DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    montant_ttc                     DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    statut                          VARCHAR(20)   NOT NULL DEFAULT 'EMISE',
    contient_frais_changement_offre BOOLEAN       NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    FOREIGN KEY (contrat_id) REFERENCES contrat(id)
);

-- ============================================================

CREATE TABLE paiement (
    id              INT           NOT NULL AUTO_INCREMENT,
    facture_id      INT           NULL,
    echeancier_id   INT           NULL,
    date_paiement   DATE          NOT NULL,
    montant_paye    DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (facture_id) REFERENCES facture(id),
    FOREIGN KEY (echeancier_id) REFERENCES echeancier(id)
);

-- ============================================================

CREATE TABLE frais_relance (
    id             INT          NOT NULL AUTO_INCREMENT,
    facture_id     INT          NOT NULL,
    numero_relance INT          NOT NULL,
    date_relance   DATE         NOT NULL,
    montant_ht     DECIMAL(8,2) NOT NULL,
    montant_tva    DECIMAL(8,2) NOT NULL,
    montant_ttc    DECIMAL(8,2) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (facture_id) REFERENCES facture(id)
);

-- ============================================================

CREATE TABLE demande_modification_contrat (
    id                       INT         NOT NULL AUTO_INCREMENT,
    contrat_id               INT         NOT NULL,
    type_demande             VARCHAR(50) NOT NULL,
    date_demande             DATE        NOT NULL,
    statut                   VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE',
    nouvelle_offre           VARCHAR(20) NULL,
    nouveau_mode_facturation VARCHAR(20) NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (contrat_id) REFERENCES contrat(id)
);
