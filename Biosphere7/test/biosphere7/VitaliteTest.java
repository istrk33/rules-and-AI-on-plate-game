/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biosphere7;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author isoyturk
 */
public class VitaliteTest {
    
    @Test
    public void compterVitaliteTest() {
        Vitalite v = new Vitalite(0, 0);
        Case[][] plateau = Utils.plateauDepuisTexte(PLATEAU);
        v.compterVitalite(plateau);
        assertEquals(35,v.getVitaliteR());
        assertEquals(19,v.getVitaliteB());
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
}
