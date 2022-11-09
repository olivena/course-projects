package harjoitustyo.apulaiset;

// Otetaan käyttöön Javan linkitetty lista.
import java.util.LinkedList;

/**
 * Dokumenttien kokoelman metodeja. Kiinnitä geneerinen tyyppi T tyypiksi
 * Dokumentti, kun toteutat tämän rajapinnan metodit Kokoelma-luokassa.
 * <p>
 * Harjoitustyö. Olio-ohjelmoinnin perusteet I, kevät 2020.
 *
 * @version 1.0
 * @author Jorma Laurikkala (jorma.laurikkala@tuni.fi), Informaatioteknologian
 * ja viestinnän tiedekunta, Tampereen yliopisto.
 */

public interface Kokoava<T> {
   /**
    * Lisää kokoelmaan käyttöliittymän kautta annetun dokumentin.
    * <p>
    * Metodi kutsuu oman listan lisää-metodia kokoelman dokumentit-attribuutin
    * kautta, jotta uusi dokumentti saadaan lisätyksi oikeaan paikkaan listalla.
    * <p>
    * Lisäys onnistuu, jos parametri liittyy dokumenttiin, jota voidaan vertailla 
    * Comparable-rajapinnan compareTo-metodilla ja jos kokoelmassa ei ole vielä
    * Dokumentti-luokan equals-metodilla mitaten samanlaista dokumenttia.
    * Null-arvon lisäys epäonnistuu aina.
    *
    * @param uusi viite lisättävään dokumenttiin.
    * @throws IllegalArgumentException jos dokumentin vertailu Comparable-rajapintaa
    * käyttäen ei ole mahdollista, listalla on jo equals-metodin mukaan sama
    * dokumentti eli dokumentti, jonka tunniste on sama kuin uuden dokumentin
    * tai parametri on null.
    * @see Ooperoiva
    */
   abstract public void lisää(T uusi) throws IllegalArgumentException;

   /**
    * Hakee kokoelmasta dokumenttia, jonka tunniste on sama kuin parametrin
    * arvo.
    * <p>
    * Tästä metodista on paljon hyötyä Kokoelma-luokassa, koska moni ohjelman
    * komennoista yksilöi dokumentin sen tunnisteen perusteella.
    *
    * @param tunniste haettavan dokumentin tunniste.
    * @return viite löydettyyn dokumenttiin. Paluuarvo on null, jos haettavaa
    * dokumenttia ei löydetty.
    */
   abstract public T hae(int tunniste);
}
