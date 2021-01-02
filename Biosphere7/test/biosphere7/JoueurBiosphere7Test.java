package biosphere7;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Tests unitaires de la classe JoueurBiosphere7.
 */
public class JoueurBiosphere7Test {

    int niveau = 8;
    char couleurJoueur = 'R';
    JoueurBiosphere7 j = new JoueurBiosphere7();

    @Test
    public void testAjouterLesFertilisations() {
        Case[][] plateau = Utils.plateauDepuisTexte(PLATEAU);
        j.plantesPossibles = Plante.lesPlantes();
        //coordonnées d'une plante à fertiliser
        Coordonnees coord = new Coordonnees(10, 0);
        Vitalite v = new Vitalite(0, 0);
        j.vr = v.getVitaliteR();
        for (int i = 0; i < j.plantesPossibles.length; i++) {
            if (plateau[coord.ligne][coord.colonne].espece == j.plantesPossibles[i].getSymbole() && plateau[coord.ligne][coord.colonne].couleur == Utils.CAR_ROUGE) {
                j.vr = j.ajouterLesFertilisations(j.vr, coord, plateau, i);
            }
        }
        assertEquals(v.getVitaliteR() + 3, j.vr);
    }

    @Test
    public void testAjouterVitaliteMmEspeceVoisine() {
        Case[][] plateau = Utils.plateauDepuisTexte(PLATEAU);
        Vitalite v = new Vitalite(0, 0);
        v.compterVitalite(plateau);
        j.vr = v.getVitaliteR();
        j.ajouterVitaliteCaseVoisineMmCouleur(plateau, new Coordonnees(1, 6));
        assertEquals(v.getVitaliteR() + plateau[new Coordonnees(1, 6).ligne][new Coordonnees(1, 6).colonne].vitalite, j.vr);
    }

    @Test
    public void testAppliquerEtouffer() {
        Case[][] plateau = Utils.plateauDepuisTexte(PLATEAU);
        Vitalite v = new Vitalite(0, 0);
        v.compterVitalite(plateau);
        j.vr = v.getVitaliteR();
        j.appliquerEtouffer(new Coordonnees(12, 1), plateau);
        assertEquals(v.getVitaliteR() - plateau[new Coordonnees(12, 1).ligne][new Coordonnees(12, 1).colonne].vitalite, j.vr);
    }

    @Test
    public void testSymbioseVitaliteMemeCouleur() {
        Case[][] plateau = Utils.plateauDepuisTexte(PLATEAU);
        //symbiose de nombre de voisins
        //sans voisins
        assertEquals(j.symbioseVitaliteMemeCouleur(new Coordonnees(2, 5), plateau, couleurJoueur), 0);
        //1 voisin
        assertEquals(j.symbioseVitaliteMemeCouleur(new Coordonnees(11, 11), plateau, couleurJoueur), 1);
        //2 voisins
        assertEquals(j.symbioseVitaliteMemeCouleur(new Coordonnees(3, 11), plateau, couleurJoueur), 2);
        //3 voisins
        assertEquals(j.symbioseVitaliteMemeCouleur(new Coordonnees(12, 1), plateau, couleurJoueur), 3);
        //4 voisins
        assertEquals(j.symbioseVitaliteMemeCouleur(new Coordonnees(3, 12), plateau, couleurJoueur), 4);
    }

    @Test
    public void testSymbioseVitalite() {
        Case[][] plateau = Utils.plateauDepuisTexte(PLATEAU);
        //symbiose de nombre de voisins
        //sans voisins
        assertEquals(j.symbioseVitalite(new Coordonnees(2, 1), plateau), 0);
        //1 voisin
        assertEquals(j.symbioseVitalite(new Coordonnees(2, 5), plateau), 1);
        //2 voisins
        assertEquals(j.symbioseVitalite(new Coordonnees(3, 11), plateau), 2);
        //3 voisins
        assertEquals(j.symbioseVitalite(new Coordonnees(12, 1), plateau), 3);
        //4 voisins
        assertEquals(j.symbioseVitalite(new Coordonnees(3, 12), plateau), 4);
    }

    @Test
    public void testNbVoisinsMemeEspece() {
        Case[][] plateau = Utils.plateauDepuisTexte(PLATEAU);
        //case d'une tomate avec 1 voisin de même espèce
        assertEquals(j.nbVoisinsMemeEspece(new Coordonnees(3, 13), plateau), 1);
        //case avec 4 voisins de même espèce 
        assertEquals(j.nbVoisinsMemeEspece(new Coordonnees(3, 12), plateau), 4);
    }

    @Test
    public void testDisseminationSterile() {
        Case[][] plateau = Utils.plateauDepuisTexte(PLATEAU);
        Vitalite v = new Vitalite(0, 0);
        v.compterVitalite(plateau);
        j.vr = v.getVitaliteR();
        //Tomate avec un voisin dont la vitalite min est 2
        j.disseminationSterile(new Coordonnees(4, 12), plateau, couleurJoueur);
        assertEquals(v.getVitaliteR() + 6, j.vr);
    }

    @Test
    public void testNbVoisinsPossible() {
        Case[][] plateau = Utils.plateauDepuisTexte(PLATEAU);
        //avec 4 voisins
        assertTrue(j.nbVoisinsPossible(new Coordonnees(3, 12), plateau) == 0);
        assertFalse(j.nbVoisinsPossible(new Coordonnees(1, 12), plateau) == 0);
        //avec 3 voisins
        assertTrue(j.nbVoisinsPossible(new Coordonnees(1, 12), plateau) == 1);
        assertFalse(j.nbVoisinsPossible(new Coordonnees(3, 12), plateau) == 1);
        //avec 2 voisins
        assertTrue(j.nbVoisinsPossible(new Coordonnees(2, 12), plateau) == 2);
        assertFalse(j.nbVoisinsPossible(new Coordonnees(3, 5), plateau) == 2);
        //avec 1 voisin
        assertTrue(j.nbVoisinsPossible(new Coordonnees(3, 5), plateau) == 3);
        assertFalse(j.nbVoisinsPossible(new Coordonnees(2, 12), plateau) == 3);
        //sans voisins
        assertTrue(j.nbVoisinsPossible(new Coordonnees(4, 3), plateau) == 4);
        assertFalse(j.nbVoisinsPossible(new Coordonnees(3, 5), plateau) == 4);
    }

    @Test
    public void testVitaliteMin() {
        Case[][] plateau = Utils.plateauDepuisTexte(PLATEAU);
        //tableau qui est censé être remplie des vitalités des cases voisines
        int[] vitaliteVoisine = {2, 4, 5, 3};
        assertTrue(j.vitaliteMin(vitaliteVoisine, new Coordonnees(0, 10), plateau) == 1);
        assertFalse(j.vitaliteMin(vitaliteVoisine, new Coordonnees(0, 10), plateau) == 2);
    }

    @Test
    public void testDisseminationFertile() {
        Case[][] plateau = Utils.plateauDepuisTexte(PLATEAU);
        Vitalite v = new Vitalite(0, 0);
        v.compterVitalite(plateau);
        //4 voisins
        j.vr = v.getVitaliteR();
        j.disseminationFertile(new Coordonnees(3, 12), plateau, couleurJoueur);
        assertEquals(35, j.vr);
        //3 voisins
        j.vr = v.getVitaliteR();
        j.disseminationFertile(new Coordonnees(12, 1), plateau, couleurJoueur);
        assertEquals(35 + 1, j.vr);
        //2 voisins
        j.vr = v.getVitaliteR();
        j.disseminationFertile(new Coordonnees(0, 12), plateau, couleurJoueur);
        assertEquals(35 + 2, j.vr);
        //1 voisins
        j.vr = v.getVitaliteR();
        j.disseminationFertile(new Coordonnees(2, 5), plateau, couleurJoueur);
        assertEquals(35 + 3, j.vr);
        //0 voisins
        j.vr = v.getVitaliteR();
        j.disseminationFertile(new Coordonnees(4, 3), plateau, couleurJoueur);
        assertEquals(35 + 4, j.vr);
        /*33,21*/

    }

    @Test
    public void caseVoisineMemeEspece() {
        Case[][] plateau = Utils.plateauDepuisTexte(PLATEAU);
        //2 de coordonnées avec la même espèce
        assertTrue(j.estCaseVoisineMemeEspece(new Coordonnees(7, 8), new Coordonnees(6, 8), plateau));
        //2 coordonnées avec des espèces différentes
        assertFalse(j.estCaseVoisineMemeEspece(new Coordonnees(1, 6), new Coordonnees(1, 7), plateau));
    }

    @Test
    public void testEstCaseVide() {
        Case[][] plateau = Utils.plateauDepuisTexte(PLATEAU);
        //case vide
        assertTrue(j.estCaseVide(plateau, new Coordonnees(0, 0)));
        //case vide
        assertFalse(j.estCaseVide(plateau, new Coordonnees(0, 10)));
    }

    @Test
    public void testCaseVoisineMemeJoueur() {
        Case[][] plateau = Utils.plateauDepuisTexte(PLATEAU);
        //plante rouge
        assertTrue(j.estCaseVoisineMemeJoueur(new Coordonnees(3, 12), plateau, couleurJoueur));
        //plante bleu
        assertFalse(j.estCaseVoisineMemeJoueur(new Coordonnees(2, 1), plateau, couleurJoueur));
    }

    @Test
    public void testEstEspecePlante() {
        Case[][] plateau = Utils.plateauDepuisTexte(PLATEAU);
        //case avec plante
        assertTrue(j.estEspecePlante(new Coordonnees(2, 1), plateau));
        //case vide
        assertFalse(j.estEspecePlante(new Coordonnees(0, 0), plateau));
    }

    @Test
    public void testActionsPossibles() {
        JoueurBiosphere7 joueur = new JoueurBiosphere7();
        Case[][] plateau = Utils.plateauDepuisTexte(PLATEAU);
        int niveau = 8;
        String[] actionsPossibles = joueur.actionsPossibles(plateau, couleurJoueur, niveau);

        //test planter une plante
        assertTrue(Utils.actionsPossiblesContient(actionsPossibles, "PaA,36,19"));
        assertFalse(Utils.actionsPossiblesContient(actionsPossibles, "PaK,36,19"));
        //test couper une plante
        assertTrue(Utils.actionsPossiblesContient(actionsPossibles, "CdM,32,19"));
        assertFalse(Utils.actionsPossiblesContient(actionsPossibles, "CaA,32,19"));
        //test disseminer sterile
        assertTrue(Utils.actionsPossiblesContient(actionsPossibles, "IdL,39,19"));
        assertFalse(Utils.actionsPossiblesContient(actionsPossibles, "IdL,36,19"));
        //test disseminer fertile
        assertTrue(Utils.actionsPossiblesContient(actionsPossibles, "IbG,38,19"));
        assertFalse(Utils.actionsPossiblesContient(actionsPossibles, "IbG,42,19"));
        //test fertilisation
        assertTrue(Utils.actionsPossiblesContient(actionsPossibles, "FbG,37,19"));
        assertFalse(Utils.actionsPossiblesContient(actionsPossibles, "FbG,35,19"));
    }
    final String PLATEAU
            = "   A   B   C   D   E   F   G   H   I   J   K   L   M   N \n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "a|   |   |   |   |   |   |   |   |   |   |DR1|   |PR1|   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "b|   |   |   |   |   |   |BR1|TB1|   |DR1|   |BB2|SB1|   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "c|   |HB1|   |   |   |SB1|   |   |   |   |   |   |TR1|   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "d|   |   |DB1|   |   |BB1|   |   |   |   |DR1|TR2|TR7|TR3|\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "e|   |   |   |HR1|   |   |   |   |   |   |   |   |TR2|   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "f|   |   |   |   |   |   |   |   |   |   |PB1|   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "g|   |   |   |   |   |   |   |BB1|   |   |   |   |TB1|   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "h|   |   |   |   |TR1|   |   |BB2|   |PB1|   |SB1|   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "i|   |   |   |   |   |HR1|   |   |   |PR1|   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "j|HB1|   |   |   |   |   |   |   |   |   |PR1|   |   |PB1|\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "k|TR1|   |   |HB1|   |   |   |   |   |   |   |TR1|   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "l|   |BR1|   |   |   |   |   |   |   |HR1|   |HR2|   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "m|SR1|SR1|   |   |   |   |   |   |BB1|   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "n|   |DR1|   |   |BR1|   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+";

   
    final String PLATEAU_VIDE
            = "   A   B   C   D   E   F   G   H   I   J   K   L   M   N \n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "a|   |   |   |   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "b|   |   |   |   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "c|   |   |   |   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "d|   |   |   |   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "e|   |   |   |   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "f|   |   |   |   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "g|   |   |   |   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "h|   |   |   |   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "i|   |   |   |   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "j|   |   |   |   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "k|   |   |   |   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "l|   |   |   |   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "m|   |   |   |   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "n|   |   |   |   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n";

}
