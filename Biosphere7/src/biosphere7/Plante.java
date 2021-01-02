/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biosphere7;

/**
 *
 * @author isoyturk
 */
public class Plante {

    private final String nomPlante;
    private final String reproduction;
    private final char symbole;
    private final TypePlante monType;

    /**
     * clase Plante
     * @param nomPlante nom de la plante
     * @param symbole symbole de plante
     * @param reproduction type de reproduction de la plante
     * @param monType type de la plante
     */
    public Plante(String nomPlante, char symbole, String reproduction, TypePlante monType) {
        this.nomPlante = nomPlante;
        this.reproduction = reproduction;
        this.symbole = symbole;
        this.monType = monType;
    }

    public String getReproduction() {
        return reproduction;
    }

    public String getNomPlante() {
        return nomPlante;
    }

    public char getSymbole() {
        return symbole;
    }

    /**
     * prendre un char et le convertir en String
     * @return 
     */
    public String getSymboleS() {
        String typePlante;
        switch (symbole) {
            case 'P':
                typePlante = "P";
                break;
            case 'S':
                typePlante = "S";
                break;
            case 'B':
                typePlante = "B";
                break;
            case 'D':
                typePlante = "D";
                break;
            case 'T':
                typePlante = "T";
                break;
            case 'H':
                typePlante = "H";
                break;
            default:
                typePlante = "E";
                break;
        }
        return typePlante;
    }

    public TypePlante getMonType() {
        return monType;
    }

    static Plante pommier() {
        return new Plante("Pommier", 'P', "Autofertile", TypePlante.lesTypesDePlante()[0]);
    }

    static Plante sureau() {
        return new Plante("Sureau", 'S', "Autofertile", TypePlante.lesTypesDePlante()[0]);
    }

    static Plante framboisier() {
        return new Plante("Framboisier", 'B', "Autofertile", TypePlante.lesTypesDePlante()[1]);
    }

    static Plante pommeDeTerre() {
        return new Plante("Pomme de terre", 'D', "Autofertile", TypePlante.lesTypesDePlante()[2]);
    }

    static Plante tomate() {
        return new Plante("Tomate", 'T', "Autostérile", TypePlante.lesTypesDePlante()[2]);
    }

    static Plante haricot() {
        return new Plante("Haricot", 'H', "Autostérile", TypePlante.lesTypesDePlante()[2]);
    }

    static Plante[] lesPlantes() {
        Plante plantes[] = {pommier(), sureau(), framboisier(), pommeDeTerre(), tomate(), haricot()};
        return plantes;
    }
}
