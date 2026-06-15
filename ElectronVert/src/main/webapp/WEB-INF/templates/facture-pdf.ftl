<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="fr">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>Facture ${facture.reference}</title>
    <style type="text/css">
        * { margin: 0; padding: 0; box-sizing: border-box; }

        body {
            font-family: Arial, sans-serif;
            font-size: 12px;
            color: #1c2b22;
            padding: 40px;
        }

        /* HEADER */
        .header {
            display: block;
            margin-bottom: 32px;
            border-bottom: 2px solid #3ec97a;
            padding-bottom: 16px;
        }

        .logo-name {
            font-size: 20px;
            font-weight: bold;
            color: #1a5a32;
        }

        .logo-sub {
            font-size: 11px;
            color: #7a9a82;
            margin-top: 2px;
        }

        .facture-title {
            font-size: 22px;
            font-weight: bold;
            color: #1a5a32;
            text-align: right;
        }

        .facture-ref {
            font-size: 12px;
            color: #7a9a82;
            text-align: right;
            margin-top: 4px;
        }

        /* DEUX COLONNES */
        .two-col {
            display: block;
            margin-bottom: 28px;
        }

        .col-left {
            display: inline-block;
            width: 48%;
            vertical-align: top;
        }

        .col-right {
            display: inline-block;
            width: 48%;
            vertical-align: top;
            text-align: right;
        }

        .section-title {
            font-size: 10px;
            font-weight: bold;
            color: #7a9a82;
            text-transform: uppercase;
            letter-spacing: 0.05em;
            margin-bottom: 6px;
        }

        .info-line {
            font-size: 12px;
            color: #1c2b22;
            margin-bottom: 3px;
        }

        .info-bold {
            font-weight: bold;
        }

        /* TABLEAU MONTANTS */
        .table-montants {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
        }

        .table-montants th {
            font-size: 10px;
            font-weight: bold;
            color: #7a9a82;
            text-transform: uppercase;
            letter-spacing: 0.05em;
            text-align: left;
            padding: 8px 0;
            border-bottom: 1px solid #e6f0e8;
        }

        .table-montants th.right { text-align: right; }

        .table-montants td {
            padding: 9px 0;
            border-bottom: 1px solid #f0f6f2;
            font-size: 12px;
        }

        .table-montants td.right { text-align: right; }

        .table-montants tr:last-child td { border-bottom: none; }

        /* TOTAUX */
        .totaux {
            width: 100%;
            border-collapse: collapse;
            margin-top: 8px;
        }

        .totaux td {
            padding: 5px 0;
            font-size: 12px;
        }

        .totaux td.label { color: #4a6a52; }
        .totaux td.value { text-align: right; font-weight: bold; }

        .total-final td {
            border-top: 2px solid #3ec97a;
            padding-top: 10px;
            font-size: 14px;
            font-weight: bold;
            color: #1a5a32;
        }

        /* FRAIS RELANCE */
        .frais-section {
            margin-top: 20px;
            margin-bottom: 20px;
        }

        .frais-title {
            font-size: 10px;
            font-weight: bold;
            color: #c0392b;
            text-transform: uppercase;
            letter-spacing: 0.05em;
            margin-bottom: 6px;
        }

        /* BADGE STATUT */
        .badge {
            display: inline-block;
            padding: 3px 10px;
            border-radius: 4px;
            font-size: 11px;
            font-weight: bold;
        }

        .badge-payee  { background: #dff5e8; color: #1a5a32; }
        .badge-emise  { background: #f0f0f0; color: #555; }
        .badge-impayee { background: #fdecea; color: #c0392b; }

        /* FOOTER */
        .footer {
            margin-top: 40px;
            padding-top: 12px;
            border-top: 1px solid #e6f0e8;
            font-size: 10px;
            color: #7a9a82;
            text-align: center;
        }
    </style>
</head>
<body>

<!-- HEADER -->
<div class="header">
    <table width="100%"><tr>
        <td>
            <div class="logo-name">ElectronVert</div>
            <div class="logo-sub">Fournisseur d'énergie</div>
        </td>
        <td style="text-align: right;">
            <div class="facture-title">FACTURE</div>
            <div class="facture-ref">${facture.reference}</div>
        </td>
    </tr></table>
</div>

<!-- INFOS CLIENT + FACTURE -->
<table width="100%" style="margin-bottom: 28px;">
    <tr>
        <td width="50%" valign="top">
            <div class="section-title">Client</div>
            <div class="info-line info-bold">${utilisateur.prenom} ${utilisateur.nom}</div>
            <div class="info-line">${contrat.adressePostale}</div>
        </td>
        <td width="50%" valign="top" style="text-align: right;">
            <div class="section-title">Détails de la facture</div>
            <div class="info-line">Date d'émission : <span class="info-bold">${facture.dateEmission}</span></div>
            <div class="info-line">Date d'échéance : <span class="info-bold">${facture.dateEcheance}</span></div>
            <div class="info-line" style="margin-top: 6px;">
                Statut :
                <#if facture.statut == "PAYEE">
                    <span class="badge badge-payee">Payée</span>
                <#elseif facture.statut == "IMPAYEE">
                    <span class="badge badge-impayee">Impayée</span>
                <#else>
                    <span class="badge badge-emise">Émise</span>
                </#if>
            </div>
        </td>
    </tr>
</table>

<!-- INFOS CONTRAT -->
<div style="margin-bottom: 24px;">
    <div class="section-title">Contrat</div>
    <table width="100%"><tr>
        <td><div class="info-line">N° contrat : <span class="info-bold">CT-${contrat.id}</span></div></td>
        <td><div class="info-line">Offre : <span class="info-bold">${libelleOffre}</span></div></td>
        <td><div class="info-line">Mode de facturation : <span class="info-bold">${libelleModeFacturation}</span></div></td>
    </tr></table>
</div>

<!-- MONTANTS -->
<div class="section-title">Détail des montants</div>
<table class="table-montants">
    <thead>
        <tr>
            <th>Description</th>
            <th class="right">Montant HT</th>
            <th class="right">TVA (20%)</th>
            <th class="right">Montant TTC</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>Consommation électrique — ${facture.type}</td>
            <td class="right">${facture.montantHT?string["#,##0.00"]} €</td>
            <td class="right">${facture.montantTVA?string["#,##0.00"]} €</td>
            <td class="right">${facture.montantTTC?string["#,##0.00"]} €</td>
        </tr>
        <#if fraisRelance?has_content>
        <#list fraisRelance as frais>
        <tr>
            <td style="color: #c0392b;">Frais de relance n°${frais.numeroRelance} — ${frais.dateRelance}</td>
            <td class="right">${frais.montantHT?string["#,##0.00"]} €</td>
            <td class="right">${frais.montantTVA?string["#,##0.00"]} €</td>
            <td class="right">${frais.montantTTC?string["#,##0.00"]} €</td>
        </tr>
        </#list>
        </#if>
    </tbody>
</table>

<!-- TOTAL -->
<table width="40%" style="margin-left: 60%; margin-top: 12px;">
    <tr>
        <td class="label" style="color: #4a6a52; padding: 4px 0;">Sous-total HT</td>
        <td style="text-align: right; padding: 4px 0;">${facture.montantHT?string["#,##0.00"]} €</td>
    </tr>
    <tr>
        <td class="label" style="color: #4a6a52; padding: 4px 0;">TVA</td>
        <td style="text-align: right; padding: 4px 0;">${facture.montantTVA?string["#,##0.00"]} €</td>
    </tr>
    <#if fraisRelance?has_content>
    <tr>
        <td class="label" style="color: #c0392b; padding: 4px 0;">Frais de relance</td>
        <td style="text-align: right; padding: 4px 0; color: #c0392b;">${montantFraisRelance}</td>
    </tr>
    </#if>
    <tr style="border-top: 2px solid #3ec97a;">
        <td style="font-size: 14px; font-weight: bold; color: #1a5a32; padding-top: 8px;">TOTAL À PAYER</td>
        <td style="text-align: right; font-size: 14px; font-weight: bold; color: #1a5a32; padding-top: 8px;">${montantTotalFraisInclus}</td>
    </tr>
</table>

<!-- FOOTER -->
<div class="footer">
    ElectronVert — Fournisseur d'énergie — contact@electronvert.fr
</div>

</body>
</html>
