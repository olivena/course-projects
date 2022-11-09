package harjoitustyo.apulaiset;

// Otetaan käyttöön Javan linkitetty lista ja järjestetty sanakirja.
import java.util.LinkedList;
import java.util.TreeMap;

/**
 * Luokkahierarkialle yhteisiä hakumetodeja. Toteuta tämä rajapinta Dokumentti-luokassa
 * ja kiinnitä geneerinen tyyppi T tyypiksi Dokumentti.
 * <p>
 * Kaikki kurssilaiset toteuttavat rajapinnan abstraktin metodit. Oletusmetodin
 * korvaavat vain laajan harjoitusyön tekijät.
 * <p>
 * Harjoitustyö. Olio-ohjelmoinnin perusteet I, kevät 2020.
 *
 * @version 1.0
 * @author Jorma Laurikkala (jorma.laurikkala@tuni.fi), Informaatioteknologian
 * ja viestinnän tiedekunta, Tampereen yliopisto.
 */

public interface Tietoinen<Dokumentti> {
   /**
    * Tutkii sisältääkö dokumentin teksti kaikki siitä haettavat sanat. Kunkin
    * listan sanan Li on vastattava yhtä tai useampaa dokumentin sanan Dj
    * esiintymää täysin (Li.equals(Dj) == true), jotta sanat täsmäävät. Jos parametrin
    * arvo on esimerkiksi ["cat, "dog"], niin hakusanat ja dokumentti täsmäävät
    * vain ja ainostaan, jos dokumentissa esiintyy vähintään kerran sanat "cat"
    * ja "dog". Dokumentin sanan osajonon ei katsota vastaavan hakusanaa.
    * Esimerkiksi hakusana "cat" ja dokumentin sana "catnip" eivät täsmää.
    *
    * @param hakusanat lista dokumentin tekstistä haettavia sanoja.
    * @return true jos kaikki listan sanat löytyvät dokumentista. Muuten palautetaan
    * false.
    * @throws IllegalArgumentException jos sanalista on tyhjä tai se on null-arvoinen.
    */

   /** */
   abstract public boolean sanatTäsmäävät(LinkedList<String> hakusanat)
   throws IllegalArgumentException;

   /**
    * Muokkaa dokumenttia siten, että tiedonhaku on helpompaa ja tehokkaampaa.
    * <p>
    * Metodi poistaa ensin dokumentin tekstistä kaikki annetut välimerkit ja
    * muuntaa sitten kaikki kirjainmerkit pieniksi ja poistaa lopuksi kaikki
    * sulkusanojen esiintymät.
    * <p>
    * Välimerkit annetaan merkkijonona. Jos testistä haluttaisiin poistaa esimerkiksi
    * pilkut ja pisteet, olisi jälkimmäisen parametrin arvo ",.". Sulkusanan Si ja
    * dokumentin sanan Dj on vastattava täysin toisiaan (Si.equals(Dj) == true),
    * jotta Dj poistetaan. Sulkusanoja poistettaessa on huolehdittava siitä,
    * että tekstin alkuun, tekstin sanojen väliin tai tekstin loppuun ei jää ylimääräisiä
    * väliliyöntejä. Näin ollen tekstin alussa ja lopussa ei ole välejä ja sanojen
    * välissä on yksi välilyönti myös siivouksen jälkeen.
    *
    * @param sulkusanat lista dokumentin tekstistä poistettavia sanoja.
    * @param välimerkit dokumentin tekstistä poistettavat välimerkit merkkijonona.
    * @throws IllegalArgumentException jos jompikumpi parametri on tyhjä tai null-
    * arvoinen.
    */
   abstract public void siivoa(LinkedList<String> sulkusanat, String välimerkit)
   throws IllegalArgumentException;



   /**
    * Laskee sanojen esiintymien lukumäärät dokumentissa. Sana - frekvenssi -parit
    * palautetaan avaimen (sanan) mukaan järjestetyssä sanakirjassa.
    * <p>
    * VAIN HARJOITUSTYÖN LAAJAN VERSION TEKIJÖILLE.
    * <p>
    * @return sanojen frekvenssit sanojen mukaan järjestetyssä sanakirjassa.
    */
   default public TreeMap<String, Integer> laskeFrekvenssit() {
      throw new UnsupportedOperationException();
   }
}
