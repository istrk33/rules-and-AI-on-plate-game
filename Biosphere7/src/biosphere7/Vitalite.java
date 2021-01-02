/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biosphere7;

/**
 *
 * @author ilker
 */
public class Vitalite {

    private int vitaliteR;
    private int vitaliteB;

    /**
     * classe vitalite, une instance pour les 2 joueurs
     * @param vitaliteR
     * @param vitaliteB 
     */
    public Vitalite(int vitaliteR, int vitaliteB) {
        this.vitaliteR = vitaliteR;
        this.vitaliteB = vitaliteB;
    }

    /**
     * compter les vitalites des 2 joueurs
     * @param plateau  plateau de jeu
     */
    public void compterVitalite(Case[][] plateau) {
        for (int lig = 0; lig < Coordonnees.NB_LIGNES; lig++) {
            for (int col = 0; col < Coordonnees.NB_COLONNES; col++) {
                if (plateau[lig][col].espece != Utils.CAR_VIDE && plateau[lig][col].couleur == Utils.CAR_ROUGE) {
                    vitaliteR += plateau[lig][col].vitalite;
                } else if (plateau[lig][col].espece != Utils.CAR_VIDE && plateau[lig][col].couleur == Utils.CAR_BLEU) {
                    vitaliteB += plateau[lig][col].vitalite;
                }
            }
        }
    }

    public int getVitaliteR() {
        return vitaliteR;
    }

    public int getVitaliteB() {
        return vitaliteB;
    }

}
