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
public class TypePlante {

    private final String nomType;
    private final int bonusFertilisation;

    /**
     * Classe de type de la plante
     * @param nomType nom du type de la plante
     * @param bonusFertilisation le bonus de fertilisation
     */
    public TypePlante(String nomType, int bonusFertilisation) {
        this.nomType = nomType;
        this.bonusFertilisation = bonusFertilisation;
    }

    public String getNomType() {
        return nomType;
    }

    public int getBonusFertilisation() {
        return bonusFertilisation;
    }

    static TypePlante arbre() {
        return new TypePlante("Arbre", 1);
    }

    static TypePlante arbuste() {
        return new TypePlante("Arbuste", 2);
    }

    static TypePlante legume() {
        return new TypePlante("Légumes", 3);
    }

   static TypePlante[]lesTypesDePlante(){
       TypePlante[] typePlantes={arbre(),arbuste(),legume()};
       return typePlantes;
   }
}
