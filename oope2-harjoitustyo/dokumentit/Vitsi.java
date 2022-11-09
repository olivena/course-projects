package harjoitustyo.dokumentit;
import harjoitustyo.dokumentit.Dokumentti;
/* Konkreettinen luokka vitseille
*
* Olio-ohjelmoinnin perusteet II, kevät 2020
*
* version 5.0 - toimii oikein
* @author Olivia Takkinen, olivia.takkinen@tuni.fi
*/

public class Vitsi extends Dokumentti {
    /*Julkiset luokkavakiot*/
    /** tietojen erotin kaikille saatavilla olevana vakiona */
    public static final String EROTIN = "///";

    /*Attribuutit*/
    /**Vitsin luokan tieto.*/
    //laji ei saa olla null tai tyhjä
    private String laji;

    /*Parametrillinen rakentaja */
    public Vitsi(int tun, String l, String teks) {
        super(tun, teks);
        laji(l);
    }

    /* Aksessorit*/
    /**Lukeva aksessori lajille 
     * @return laji vitsin laji
    */
    public String laji() {
        return laji;
    } 
    /**Asettava aksessori lajille
     * @param l lajimuuttuja
     * @throws IllegalArgumentException jos muuttuja on tyhjäarvo
    */
    public void laji(String l) throws IllegalArgumentException {
        if (l == null || l == "") {
            throw new IllegalArgumentException();
        } else {
            laji = l;
        }
    }
    /**
     * Muodostaa merkkijonon esityksen, joka koostuu tunnisteesta, erottimesta, 
     * päivämäärästä sekä tekstistä
     * @return tekstitiedosto merkkijonoesityksenä
     */
    @Override
    public String toString() {
        //pilkotaan toString-metodista saatu arvo osiin.
        String[] pilkottu = super.toString().split("///");
        return pilkottu[0] + EROTIN + laji + EROTIN + pilkottu[1];
    }
}