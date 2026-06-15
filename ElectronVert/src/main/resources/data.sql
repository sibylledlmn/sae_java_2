-- ============================================================
-- ElectronVert - Données de test
-- Mot de passe : demo1234
-- Hash BCrypt du mot de passe ci-dessus
-- ============================================================

-- ============================================================
-- TARIFS (3 périodes historiques)
-- ============================================================

INSERT INTO tarif (date_debut, prix_kwh_classique, prix_kwh_hp, prix_kwh_hc, prix_abonnement_classique, prix_abonnement_hphc)
VALUES
('2024-01-01', 0.2272, 0.2700, 0.1470, 13.00, 16.20),
('2025-01-01', 0.2516, 0.2980, 0.1630, 13.68, 17.04),
('2026-01-01', 0.2701, 0.3198, 0.1750, 14.22, 17.73);

-- ============================================================
-- UTILISATEUR
-- ============================================================

INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, role)
VALUES ('Martin', 'Sophie', 'sophie.martin@demo.fr',
        '$2a$10$BzYZjDnFBamYywyzO5KvhuVCiZ5um9VkWkVHZySP1MOdqcJ1B1v.6',
        'CLIENT');

SET @client_id = LAST_INSERT_ID();

-- ============================================================
-- CONTRAT 1 — Classique / Réel
-- ============================================================

INSERT INTO contrat (client_id, adresse_postale, offre_tarifaire, mode_facturation, statut, date_souscription)
VALUES (@client_id, '12 rue des Acacias, 67000 Strasbourg', 'OffreClassique', 'REEL', 'ACTIF', '2024-03-01');

SET @contrat1_id = LAST_INSERT_ID();

-- Relevés contrat 1
INSERT INTO releve (contrat_id, type_releve, date_releve, index_total) VALUES
(@contrat1_id, 'OUVERTURE', '2024-03-01', 0.0),
(@contrat1_id, 'MENSUEL',   '2024-04-01', 320.0),
(@contrat1_id, 'MENSUEL',   '2024-05-01', 630.0),
(@contrat1_id, 'MENSUEL',   '2024-06-01', 910.0),
(@contrat1_id, 'MENSUEL',   '2025-01-01', 3200.0),
(@contrat1_id, 'MENSUEL',   '2025-02-01', 3540.0),
(@contrat1_id, 'MENSUEL',   '2025-03-01', 3870.0),
(@contrat1_id, 'MENSUEL',   '2026-03-01', 7100.0),
(@contrat1_id, 'MENSUEL',   '2026-04-01', 7390.0),
(@contrat1_id, 'MENSUEL',   '2026-05-01', 7660.0);

-- Factures contrat 1 — payées
INSERT INTO facture (reference, contrat_id, type_facture, date_emission, date_echeance, montant_ht, montant_tva, montant_ttc, statut)
VALUES ('EV-2025-00101', @contrat1_id, 'MENSUELLE', '2025-02-05', '2025-02-25', 79.20, 15.84, 95.04, 'PAYEE');
INSERT INTO paiement (facture_id, date_paiement, montant_paye) VALUES (LAST_INSERT_ID(), '2025-02-20', 95.04);

INSERT INTO facture (reference, contrat_id, type_facture, date_emission, date_echeance, montant_ht, montant_tva, montant_ttc, statut)
VALUES ('EV-2025-00142', @contrat1_id, 'MENSUELLE', '2025-03-05', '2025-03-25', 82.40, 16.48, 98.88, 'PAYEE');
INSERT INTO paiement (facture_id, date_paiement, montant_paye) VALUES (LAST_INSERT_ID(), '2025-03-22', 98.88);

INSERT INTO facture (reference, contrat_id, type_facture, date_emission, date_echeance, montant_ht, montant_tva, montant_ttc, statut)
VALUES ('EV-2026-00089', @contrat1_id, 'MENSUELLE', '2026-04-05', '2026-04-25', 75.60, 15.12, 90.72, 'PAYEE');
INSERT INTO paiement (facture_id, date_paiement, montant_paye) VALUES (LAST_INSERT_ID(), '2026-04-20', 90.72);

-- Facture contrat 1 — émise (à payer)
INSERT INTO facture (reference, contrat_id, type_facture, date_emission, date_echeance, montant_ht, montant_tva, montant_ttc, statut)
VALUES ('EV-2026-00134', @contrat1_id, 'MENSUELLE', '2026-06-05', '2026-06-25', 71.50, 14.30, 85.80, 'EMISE');

-- Facture contrat 1 — impayée avec 2 relances
INSERT INTO facture (reference, contrat_id, type_facture, date_emission, date_echeance, date_prochaine_relance, montant_ht, montant_tva, montant_ttc, statut)
VALUES ('EV-2026-00058', @contrat1_id, 'MENSUELLE', '2026-03-05', '2026-03-25', '2026-06-25', 68.80, 13.76, 82.56, 'IMPAYEE');

SET @facture_relance1_id = LAST_INSERT_ID();

INSERT INTO frais_relance (facture_id, numero_relance, date_relance, montant_ht, montant_tva, montant_ttc)
VALUES
(@facture_relance1_id, 1, '2026-04-10', 15.00, 3.00, 18.00),
(@facture_relance1_id, 2, '2026-05-10', 15.00, 3.00, 18.00);

-- ============================================================
-- CONTRAT 2 — HP/HC / Réel
-- ============================================================

INSERT INTO contrat (client_id, adresse_postale, offre_tarifaire, mode_facturation, statut, date_souscription)
VALUES (@client_id, '5 avenue de la Forêt, 67200 Strasbourg', 'OffreHPHC', 'REEL', 'ACTIF', '2025-06-01');

SET @contrat2_id = LAST_INSERT_ID();

-- Relevés contrat 2 (HP/HC)
INSERT INTO releve (contrat_id, type_releve, date_releve, index_hp, index_hc) VALUES
(@contrat2_id, 'OUVERTURE', '2025-06-01', 0.0,    0.0),
(@contrat2_id, 'MENSUEL',   '2025-07-01', 210.0,  140.0),
(@contrat2_id, 'MENSUEL',   '2025-08-01', 430.0,  275.0),
(@contrat2_id, 'MENSUEL',   '2026-03-01', 2100.0, 1350.0),
(@contrat2_id, 'MENSUEL',   '2026-04-01', 2290.0, 1470.0),
(@contrat2_id, 'MENSUEL',   '2026-05-01', 2460.0, 1580.0);

-- Factures contrat 2 — payées
INSERT INTO facture (reference, contrat_id, type_facture, date_emission, date_echeance, montant_ht, montant_tva, montant_ttc, statut)
VALUES ('EV-2026-00091', @contrat2_id, 'MENSUELLE', '2026-04-05', '2026-04-25', 88.30, 17.66, 105.96, 'PAYEE');
INSERT INTO paiement (facture_id, date_paiement, montant_paye) VALUES (LAST_INSERT_ID(), '2026-04-23', 105.96);

-- Facture contrat 2 — émise (à payer)
INSERT INTO facture (reference, contrat_id, type_facture, date_emission, date_echeance, montant_ht, montant_tva, montant_ttc, statut)
VALUES ('EV-2026-00136', @contrat2_id, 'MENSUELLE', '2026-06-05', '2026-06-25', 92.10, 18.42, 110.52, 'EMISE');

-- ============================================================
-- CONTRAT 3 — Classique / Échéancier (clôturé)
-- ============================================================

INSERT INTO contrat (client_id, adresse_postale, offre_tarifaire, mode_facturation, statut, date_souscription, date_fin, facturation_terminee)
VALUES (@client_id, '8 rue du Moulin, 67500 Haguenau', 'OffreClassique', 'ECHEANCIER', 'CLOTURE', '2024-01-01', '2025-01-01', TRUE);

SET @contrat3_id = LAST_INSERT_ID();

INSERT INTO echeancier (contrat_id, date_debut, montant_mensualite, nb_mensualites_emises, termine)
VALUES (@contrat3_id, '2024-01-01', 90.00, 12, TRUE);

SET @echeancier_id = LAST_INSERT_ID();

INSERT INTO facture (reference, contrat_id, type_facture, date_emission, date_echeance, montant_ht, montant_tva, montant_ttc, statut)
VALUES ('EV-2024-00011', @contrat3_id, 'ECHEANCIER', '2024-02-01', '2024-02-15', 75.00, 15.00, 90.00, 'PAYEE');
INSERT INTO paiement (facture_id, date_paiement, montant_paye) VALUES (LAST_INSERT_ID(), '2024-02-10', 90.00);

INSERT INTO facture (reference, contrat_id, type_facture, date_emission, date_echeance, montant_ht, montant_tva, montant_ttc, statut)
VALUES ('EV-2024-00045', @contrat3_id, 'REGULARISATION', '2025-01-10', '2025-01-25', 42.00, 8.40, 50.40, 'PAYEE');
INSERT INTO paiement (facture_id, date_paiement, montant_paye) VALUES (LAST_INSERT_ID(), '2025-01-20', 50.40);
