package harjoitustyo.apulaiset;
import java.util.Comparator;

/**
 * Oman listan metodit määrittelevä rajapinta, joka toteutetaan OmaLista-luokassa.
 * Metodien on oltava täysin sovelluksesta riippumattomia siten, että niillä
 * voidaan käsitellä minkä tahansa tyyppisiä alkioita sisältävää listaa.
 * <p>
 * Kaikki kurssilaiset toteuttavat rajapinnan abstraktin metodin. Oletusmetodin
 * korvaavat vain laajan harjoitustyön tekijät.
 * <p>
 * Harjoitustyö. Olio-ohjelmoinnin perusteet I, kevät 2020.
 *
 * @version 1.0
 * @author Jorma Laurikkala (jorma.laurikkala@tuni.fi), Informaatioteknologian
 * ja viestinnän tiedekunta, Tampereen yliopisto.
 */

public interface Ooperoiva<E> {
   /**
    * Lisää listalle uuden alkion sen suuruusjärjestyksen mukaiseen paikkaan.
    * <p>
    * Listan alkioiden välille muodostuu kasvava suuruusjärjestys, jos lisäys
    * tehdään vain tällä metodilla, koska uusi alkio lisätään listalle siten,
    * että alkio sijoittuu kaikkien itseään pienempien tai yhtä suurien alkioiden
    * jälkeen ja ennen kaikkia itseään suurempia alkioita. Alkioiden suuruusjärjestys
    * päätellään Comparable-rajapinnan compareTo-metodilla.
    * <p>
    * Jos parametri viittaa esimerkiksi Integer-olioon, jonka arvo on 2 ja listan
    * muut tietoalkiot ovat arvoltaan 0 ja 3, on listan sisältö metodin suorittamisen
    * jälkeen [0, 2, 3], koska {@literal 0 < 2 < 3}.
    * <p>
    * Käytä toteutuksessa SuppressWarnings-annotaatiota, jotta kääntäjä ei valita
    * vaarallisesta tyyppimuunnoksesta.
    *
    * @param uusi viite olioon, jonka luokan tai luokan esivanhemman oletetaan
    * toteuttaneen Comparable-rajapinnan.
    * @throws IllegalArgumentException jos lisäys epäonnistui, koska uutta alkiota
    * ei voitu vertailla. Vertailu epäonnistuu, kun parametri on null-arvoinen
    * tai siihen liittyvän olion luokka tai luokan esivanhempi ei vastoin oletuksia
    * toteuta Comparable-rajapintaa.
    */
   @SuppressWarnings({"unchecked"})
   public abstract void lisää(E uusi) throws IllegalArgumentException;

   /**
    * Lajittelee listan sisällön annettua vertailijaa käyttäen.
    * <p>
    * Lajittelun tulee olla vakaa siten, että yhtä suurten alkioiden keskinäinen
    * järjestys säilyy. Lajitteluun voi käyttää Javan API:n omia lajittelualgoritmeja
    * tai itse toteutettua algoritmia. Tärkeintä on se, että lajittelussa hyödynnetään
    * parametrina saatua vertailijaa alkioiden keskinäisessä vertailussa.
    * <p>
    * Käytä SuppressWarnings-annotaatiota, jos toteutat metodin siten, että kääntäjä
    * valittaa vaarallisesta tyyppimuunnoksesta.
    * <p>
    * VAIN HARJOITUSTYÖN LAAJAN VERSION TEKIJÖILLE.
    *
    * @param vertailija viite vertailijaan, joka on Comparator-rajapinnan toteuttava
    * metodi eli lambda.
    * @throws IllegalArgumentException jos parametri on null-arvoinen tai lajittelun
    * aikana tapahtuu mikä tahansa poikkeus.
    */
   default public void lajittele(Comparator<? super E> vertailija)
   throws IllegalArgumentException {
      throw new UnsupportedOperationException();
   }
}
