package com.fitlogtimer.constants;

import java.util.List;

public final class SuggestedExerciseColors {

    private SuggestedExerciseColors() {}

    // Liste complète des couleurs proposées, en hexadécimal
    public static final List<String> COLORS = List.of(
//           *** 20 ***
//            "#2B3A4A", // Bleu acier foncé
//            "#4A5D6B", // Bleu ardoise
//            "#3E4A59", // Bleu-gris (DC)
//            "#1A3D54", // Bleu marine
//            "#3D5A6C", // Bleu denim
//            "#4B2E2E", // Marron rougeâtre
//            "#5A3A2A", // Brun cuir
//            "#3E2C1C", // Brun chocolat
//            "#3A4B3A", // Vert sapin
//            "#2D4D3D", // Vert forêt
//            "#4B3D4A", // Violet foncé
//            "#5A3D5A", // Prune
//            "#3D2C3D", // Aubergine
//            "#4A2E3A", // Bordeaux
//            "#3D1A1A", // Rouge brique
//            "#333333", // Gris anthracite
//            "#454545", // Gris fer
//            "#2B2B2B", // Noir chaud
//            "#5E4A00", // Doré olive
//            "#4A3B20"  // Bronze


//            // Bleus (7 nuances)
//            "#1A2E40", // Bleu nuit
//  "#2B3A4A", // Bleu acier
//          "#3D4E5D", // Bleu ardoise
//          "#1A3D54", // Bleu marine
//          "#2D4D6E", // Bleu royal
//          "#4A6178", // Bleu grisé
//          "#5C6A77", // Bleu lagon (DC30)
//
//          // Verts (5 nuances)
//          "#1E3D2E", // Vert pin
//          "#2D4D3D", // Vert forêt
//          "#3A5A4A", // Vert sapin
//          "#4B5E4C", // Vert mousse (DCS)
//          "#2E4A3A", // Vert jade
//
//          // Rouges/Marrons (6 nuances)
//          "#3D1A1A", // Rouge foncé
//          "#4A2E2E", // Brique
//          "#5A3A2A", // Cuir
//          "#3E2C1C", // Chocolat
//          "#4A1E1E", // Bourgogne
//          "#381819", // Brun rouge (DO)
//
//          // Violets/Prunes (4 nuances)
//          "#3D2C3D", // Aubergine
//          "#4A3A4A", // Violet foncé
//          "#5A4A66", // Lilas sombre (DM)
//          "#3D0C3C", // Prune (DE)
//
//          // Gris/Noirs (5 nuances)
//          "#1E1E1E", // Noir chaud
//          "#2B2B2B", // Charbon (SH)
//          "#3A3A3A", // Anthracite
//          "#4A4A4A", // Gris fer
//          "#2C3539", // Acier bleuté (TVH)
//
//          // Dorés/Terres (3 nuances)
//          "#5E4A00", // Or vieux
//          "#4A3B20", // Bronze
//          "#5A471C"  // Moutarde

            //PALETTE 40
            "#1A2B3C", "#3C4F6E", "#264653", "#2F4858", "#1E3D59", "#0B3D91", "#000080", // Bleus
            "#4E3A65", "#6A3973", "#301934", "#581845", // Violets
            "#3E322F", "#6F3E37", "#4B2E2E", "#381819", "#5C3A21", // Bruns
            "#013220", "#2E8B57", "#4B5E4C", "#0B3D0B", "#014421", "#3A5F0B", // Verts
            "#3D3C2C", "#6B6B47", "#5A471C", "#7A4F00", "#3E3200", "#5E4A00", // Jaune/ocre/olive
            "#612F2F", "#A53A3A", "#8B0000", "#A33C3C", "#5E2B2B", // Rouges
            "#3B3B3B", "#5C6A77", "#1E1E1E", "#7A8C9B", "#2C3539", "#1A237E" // Gris/bleu acier

            //PALETTE 30
//            "#1A2B3C", "#3C4F6E", "#264653", "#2F4858", "#1E3D59", // Bleus
//            "#4E3A65", "#6A3973", "#301934", // Violets
//            "#3E322F", "#6F3E37", "#4B2E2E", "#381819", // Bruns
//            "#013220", "#2E8B57", "#4B5E4C", "#0B3D0B", // Verts
//            "#3D3C2C", "#6B6B47", "#5A471C", "#7A4F00", // Olives / Ocre
//            "#612F2F", "#A53A3A", "#8B0000", "#5E4A00", // Rouges / Moutarde
//            "#3B3B3B", "#5C6A77", "#1E1E1E", "#7A8C9B", "#2C3539", "#1A237E" // Gris, acier, indigo

            //            "#3E4A59", // DC
//            "#5C6A77", // DC30
//            "#4B5E4C", // DCS
//            "#5A4A66", // DM
//            "#5A6B7C", // D15
//            "#7A8C9B", // DCD
//            "#A53A3A", // DL
//            "#000000", // Noir (B)
//            "#2B2B2B", // Gris charbon (SH)
//            "#0B3D91", // Bleu nuit
//            "#581845", // Bordeaux
//            "#014421", // Vert sapin
//            "#003B46", // Bleu pétrole
//            "#3B3B3B", // Gris anthracite
//            "#4B2E2E", // Marron foncé
//            "#1A237E", // Indigo foncé (HSQ)
//            "#3D0C3C", // Prune (DE)
//            "#0B3D0B", // Vert forêt
//            "#000080", // Bleu marine
//            "#1E1E1E", // Gris plomb
//            "#8B0000", // Rouge foncé
//            "#381819", // Brun chocolat (DO)
//            "#301934", // Violet foncé
//            "#2C3539", // Acier bleuté (TVH)
//            "#3B3C36", // Olive foncée (TH)
//            "#191970", // Bleu minuit
//            "#2F4F4F",  // Ardoise foncée
//            "#5E4A00", // Jaune moutarde foncé
//            "#664200", // Orange brûlé
//            "#6B4D22", // Terre de Sienne
//            "#4D3D14", // Or antique
//            "#5C3A21", // Ocre brun
//            "#3A2E0B", // Jaune kaki foncé
//            "#7F5E00", // Doré profond
//            "#6E4C1E", // Cuivré foncé
//            "#5D4500", // Ambre sombre
//            "#4A3B20", // Bronze patiné
//            "#8C6D00", // Miel foncé
//            "#523E1A", // Caramel profond
//            "#3E3200", // Olive dorée foncée
//            "#7A4F00", // Cognac
//            "#5A471C", // Moutarde vieilli
//            "#453A1F"  // Jaune-terre foncé
    );
}
