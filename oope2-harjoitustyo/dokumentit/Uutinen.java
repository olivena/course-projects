package harjoitustyo.dokumentit;
import harjoitustyo.dokumentit.Dokumentti;
import java.time.*;
import java.time.format.DateTimeFormatter;
/* Konkreettinen luokka
*
* Olio-ohjelmoinnin perusteet II, kevät 2020
*
* version 5.0 - toimii oikein
* @author Olivia Takkinen, olivia.takkinen@tuni.fi
*/

public class Uutinen extends Dokumentti {
    /*Julkiset luokkavakiot*/
    /** tietojen erotin kaikille saatavilla olevana vakiona */

    public static final String EROTIN = "///";
    /* Attribuutit */
    /**Uutisen päivämäärä*/
    private LocalDate päivämäärä;
     
    /*Parametrillinen rakentaja */
    public Uutinen(int tun, LocalDate pvm, String teks) {
        super(tun, teks);
        päivämäärä(pvm);
    }

    /* Aksessorit*/
    /**Lukeva aksessori päivämäärälle 
     * @return päivämäärä päivämäärä
    */
    public LocalDate päivämäärä(){
        return päivämäärä;
    }
    /**
     * Asettava aksessori päivämäärälle 
     * @param pvm päivämäärän asettaminen
     * @throws IllegalArgumentException jos päivämäärä on tyhjäarvo
     */
    public void päivämäärä(LocalDate pvm) throws IllegalArgumentException {
        if (pvm == null) {
            throw new IllegalArgumentException();
        } else {
            päivämäärä = pvm; 
        }
    }

    /**
     * Muuttaa päivämäärän muodon muodosta 01-01-1111 muotoon 1.1.1111
     * @param päivämäärä päivämäärä vanhassa muodossa
     * @return muutettuPäivämäärä päivämäärä oikeassa muodossa
     */
    public String muutaPäivämääränMuoto(LocalDate päivämäärä) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy");
        String muutettuPäivämäärä = päivämäärä.format(formatter);
        return muutettuPäivämäärä;
    }

    /**
     * Muodostaa merkkijonon esityksen, joka koostuu tunnisteesta, erottimesta, 
     * päivämäärästä sekä tekstistä
     * @return tekstitiedosto merkkijonoesityksenä
     */
    @Override
    public String toString() {
        //muutetaan päivämäärän muotoa
        String päivämääräMuodossa = muutaPäivämääränMuoto(päivämäärä);
        //pilkotaan toString-metodista saatu arvo osiin.
        String[] pilkottu = super.toString().split("///");
        return pilkottu[0] + EROTIN + päivämääräMuodossa + EROTIN + pilkottu[1];
    }
}   