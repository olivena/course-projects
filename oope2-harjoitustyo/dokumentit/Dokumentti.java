package harjoitustyo.dokumentit;
import harjoitustyo.apulaiset.Tietoinen;
import java.util.LinkedList;

/** 
 *Abstrakti juuriluokka dokumenteille
* <p>
* Olio-ohjelmoinnin perusteet II, kevät 2020
* <p>
* version 5.0 - toimii oikein
* @author Olivia Takkinen, olivia.takkinen@tuni.fi
*/

public abstract class Dokumentti implements Comparable <Dokumentti>, Tietoinen <Dokumentti> {
    /*Julkiset luokkavakiot*/
    /** tietojen erotin kaikille saatavilla olevana vakiona */
    public static final String EROTIN = "///";

    /*Attribuutit*/

    /**Dokumentin yksilöivä kokonaisluku.*/
    private int tunniste;

    /**Dokumentin teksti.*/
    private String teksti;

    /** Parametrillinen rakentaja 
     * @param tun muuttuja tunnisteelle
     * @param teks muuttuja tekstille
    */
    public Dokumentti(int tun, String teks) {
        tunniste(tun);
        teksti(teks);
    }

    /*
    * Object-luokan metodien korvaukset.
    */

    /*Aksessorit*/
    /**Lukeva aksessori tunnisteelle 
     * @return tunniste dokumentin tunniste
    */
    public int tunniste() {
        return tunniste;
    }
    
    /**
     * Asettava aksessori tunnisteelle.  
     * @param tun tunnisteen muuttuja
     * @throws IllegalArgumentException jos parametrin arvo on 0 tai negatiivinen
     */
    public void tunniste(int tun) throws IllegalArgumentException {
        if (tun <= 0 ) {
            throw new IllegalArgumentException();
        } else {
            tunniste = tun;
        }
    }

    /**Lukeva aksessori tekstille 
     * @return teksti dokumentin teksti
    */
    public String teksti() {
        return teksti;
    }
    /**Asettava aksessori tekstille
     * @param teks muuttuja tekstille
     * @throws IllegalArgumentException jos parametrin arvo on null tai tyhjä
    */
    public void teksti(String teks) throws IllegalArgumentException{
        if (teks == null || teks == "") {
            throw new IllegalArgumentException();
        } else {
            teksti = teks;
        }
    }

    /**
     * muodostaa dokumentin merkkijonon esityksen, joka koostuu tunnisteesta,
     * erottimesta ja tekstistä.
     * @return dokumentin merkkijonoesityksen
     */
    @Override
    public String toString() {
        // Hyödynnetään vakiota, jotta ohjelma olisi joustavampi
        return tunniste + EROTIN + teksti;
    }

    /**
    * vertailee dokumenttien tunnisteita
    * @param obj vertailtava objekti
    * @return true jos tunnisteet täsmäävät, muuten false
    */
    @Override
    public boolean equals(Object obj) {
        try {
            /*Asetetaan olioon Dokumentti-luokan viite, jotta voidaan kutsua Dokumentti-luokan
            * akksessoreita
            */
            Dokumentti toinen = (Dokumentti)obj;
            return (toinen.tunniste() == this.tunniste());
        } catch (Exception e) {
            return false;
        }
    }
    /**
     * @return tulos hashCodesta
     */
    @Override
    public int hashCode() {
        int tulos = 17;
        tulos = 31 * tulos + (String.valueOf(tunniste) == null ? 0 : String.valueOf(tunniste).hashCode());
        return tulos;
    }
    
    /**
     * Comparable-rajapinta, jossa vertaillaan tunnisteita
     * @param vertailtavaDokumentti dokumentti jota vertaillaan 
     * @return paluuArvo 0 jos dokumenttien tunnisteet ovat samat, -1 jos
     * tunniste on pienempi verrattavaan nähden, muuten 1
     */
    @Override
    public int compareTo(Dokumentti vertailtavaDokumentti) {
        int paluuArvo = 1;
        if (tunniste < vertailtavaDokumentti.tunniste()) {
            paluuArvo = -1;
        }
        else if (tunniste == vertailtavaDokumentti.tunniste()) {
            paluuArvo = 0;
        }
        return paluuArvo;
    }

    /** 
     * Metodi etsii tekstin seasta hakusanoja
     * @param hakusanat lista etsittävistä sanoista
     * @return paluuArvo true jos sanat löytyvät tekstistä, false jos ei löydy
     * @throws IllegalArgumentException jos hakusanat on tyhjä lista
     */
    public boolean sanatTäsmäävät(LinkedList<String> hakusanat) 
    throws IllegalArgumentException {
        if (hakusanat != null && !(hakusanat.isEmpty())) {
            String[] pilkottuteksti = teksti.split("[, \\ .\\s ]");
            boolean paluuArvo = false;
            int paluu = 0;
            for (int i = 0; i < hakusanat.size(); i++) {
                for (int j = 0; j < pilkottuteksti.length; j++) {
                    (pilkottuteksti[i]).toLowerCase();
                    if ((hakusanat.get(i)).equals(pilkottuteksti[j])) {
                    paluu++;
                    }
                }
            } 
            if (paluu >= hakusanat.size()) {
                paluuArvo = true;
            } else {
                paluuArvo = false;
            }
            return paluuArvo;
        } else {
            throw new IllegalArgumentException();
        }
    }
     
    /**
     * poistaa annetut välimerkit sanasta
     * @param sana dokumentin sana, josta tarkistetaan välimerkit
     * @param välimerkit poistettavat välimerkit sanasta
     * @return sana välimerkeistä siivottu sana
     */
    public String poistaVälimerkit(String sana, String välimerkit){
        String[] poistettavatMerkit = välimerkit.split("");
        for (String välimerkki : poistettavatMerkit) {
            if (sana.contains(välimerkki)) {
                sana = sana.replace(välimerkki, "");
            }
        }
        sana = sana.toLowerCase();
        return sana;        
    }

    /**
     * Metodi siivoaa dokumentit turhista sanoista sekä käyttäjän antamista välimerkeistä
     * @param sulkusanat poistettavat sanat
     * @param välimerkit poistettavat välimerkit
     * @throws IllegalArgumentException jos tiedostot ovat tyhjiä
     */
    public void siivoa(LinkedList<String> sulkusanat, String välimerkit) 
    throws IllegalArgumentException {
        try {
            if (sulkusanat.isEmpty() || välimerkit == null || välimerkit == "") {
                throw new IllegalArgumentException();
            }
            else {
                //poistaa tekstistä välimerkit, sekä muuttaa koko tekstin pieniin kirjaimiin
                String[] dokumentinSanat = teksti.split(" ");
                LinkedList<String> siivottuTeksti = new LinkedList<String>();
                LinkedList<String> poistettavienLista = new LinkedList<String>();

                //poistaa dokumentin tekstistä käyttäjän antamat merkit
                for (String sana : dokumentinSanat) {
                    String siivottuSana = poistaVälimerkit(sana, välimerkit);
                    siivottuTeksti.add(siivottuSana);
                }
                //poistaa tekstistä sulkusanalistan mukaiset sanat
                for (String sanaTekstistä : siivottuTeksti) {
                    for (String sulkusana : sulkusanat) { 
                        if (sanaTekstistä.equals(sulkusana)) {
                            poistettavienLista.add(sanaTekstistä);
                        }
                    }
                }
                siivottuTeksti.removeAll(poistettavienLista);

                //tekstin yhteenkokoaminen
                teksti = "";
                int indeksi = 0;
                for (String tekstiinLisättäväSana : siivottuTeksti) {
                    if (indeksi == (siivottuTeksti.size() - 1)) {
                        teksti += tekstiinLisättäväSana;
                    } else {
                        teksti += tekstiinLisättäväSana + " ";
                    }
                    indeksi++;
                }
                teksti = teksti.trim().replaceAll(" +", " ");
            } 
        } catch (NullPointerException e) {
            throw new IllegalArgumentException(); 
        }  
    }
}
