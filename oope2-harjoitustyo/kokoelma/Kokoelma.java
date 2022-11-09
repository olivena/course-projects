package harjoitustyo.kokoelma;
import harjoitustyo.apulaiset.Kokoava;
import harjoitustyo.dokumentit.Dokumentti;
import harjoitustyo.dokumentit.Uutinen;
import harjoitustyo.dokumentit.Vitsi;
import harjoitustyo.omalista.OmaLista;
import harjoitustyo.Kayttoliittyma;
import java.io.*;
import java.lang.Object;
import java.util.Scanner;
import java.util.LinkedList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Konkreettinen luokka kokoelman hallinnoinnille. 
 *
 * Olio-ohjelmoinnin perusteet II, kevät 2020
 *
 * version 5.0.
 * @author Olivia Takkinen, olivia.takkinen@tuni.fi
*/

public class Kokoelma extends Object implements Kokoava<Dokumentti> {
    public static final String EROTIN = "///";
    /** kätketty attribuutti. dokumentit sisältää KAIKKI dokumentit */
    private harjoitustyo.omalista.OmaLista<harjoitustyo.dokumentit.Dokumentti> dokumentit;
    /** apulista dokumenttien poistamiseen kokoelmasta */
    private harjoitustyo.omalista.OmaLista<harjoitustyo.dokumentit.Dokumentti> poistettavienLista;

    /* Oletusrakentaja Kokoelma-luokan attribuuttien liittämiseen olioihin.*/
    public Kokoelma() {
        OmaLista<harjoitustyo.dokumentit.Dokumentti> tyhjäLista = new OmaLista<harjoitustyo.dokumentit.Dokumentti>();
        dokumentit = tyhjäLista;
        harjoitustyo.omalista.OmaLista<harjoitustyo.dokumentit.Dokumentti> väliLista = 
        new harjoitustyo.omalista.OmaLista<harjoitustyo.dokumentit.Dokumentti>();
        poistettavienLista = väliLista;
    }

    /* Aksessorit */
    public harjoitustyo.omalista.OmaLista<harjoitustyo.dokumentit.Dokumentti> dokumentit() {
        return dokumentit;
    }
    public harjoitustyo.omalista.OmaLista<harjoitustyo.dokumentit.Dokumentti> poistettavienLista() {
        return poistettavienLista();
    }
    
    /**
     * Metodi tarkistaa ettei dokumenteista löydy jo valmiiksi lisättävää dokumenttia
     * @param uusi Käyttäjän antama dokumentti
     * @return vastaavaaEiLöydy palauttaa true, jos dokumenttia ei vielä löydy dokumenteista
     */
    public boolean tarkistaSamat(harjoitustyo.dokumentit.Dokumentti uusi) {
        int vastaava = 0;
        boolean vastaavaaEiLöydy = false;
        for (Dokumentti doc : dokumentit) {
            if (doc.equals(uusi)) {
                vastaava += 1;
            } 
        }
        if (vastaava == 0) {
            vastaavaaEiLöydy = true;
        } 
        return vastaavaaEiLöydy;
    }

    /**
     * Lisää saadun dokumentin muiden joukkoon, mikäli se on oikeanlainen
     * @param uusi dokumentteihin lisättävä dokumentti
     */
    public void lisää(harjoitustyo.dokumentit.Dokumentti uusi) {
        boolean vastaavaaEiLöydy = tarkistaSamat(uusi);
        boolean samaKategoria = false;

        //tarkitaa, että lisättävä dokumentti on samankaltainen muiden listattujen dokumenttien kanssa
        if (dokumentit.isEmpty()) {
            samaKategoria = true;
        } else {
            Dokumentti tarkistusDokumentti = dokumentit.get(0);
            if (((tarkistusDokumentti instanceof Uutinen) == true) && ((uusi instanceof Uutinen) == true)) {
                samaKategoria = true;
            } else if (((tarkistusDokumentti instanceof Vitsi) == true) && ((uusi instanceof Vitsi) == true)) {
                samaKategoria = true;
            }
        }

        if (vastaavaaEiLöydy == true && samaKategoria == true) {
            dokumentit.lisää(uusi);
        } else {
           Kayttoliittyma.virhe("virhe");
        } 
    }

    /**
     * Siirtää listaan kaikki dokumentit, jotka tulostetaan käyttäjälle
     * @return tulosta lista kaikista dokumenteista
     */
    public LinkedList<Dokumentti> tulostettavat() {
        LinkedList<Dokumentti> tulosta = new LinkedList<Dokumentti>();
        for (Dokumentti doc: dokumentit) {
            tulosta.add(doc);
        }
        return tulosta;
    }

    /**
     * etsii parametrina saatuja sanoja dokumenteista ja palauttaa dokumentin tunnisteen
     * josta etsittävä sana löytyi
     * @param termit santa joita haetaan dokumenteista
     * @return sanatLöydetty lista dokumenttien tunnisteista, joista hakusanat löytyvät
    */
    public LinkedList<Integer> haeSanalla(String termit) {
        LinkedList<Integer> sanatLöydetty = new LinkedList<Integer>();
        String[] etsittävätSanat = termit.split("\\s");
        for (Dokumentti doc: dokumentit) {
            LinkedList<String> löydetytSanat = new LinkedList<String>();
            String[] tekstiSanakerrallaan = (doc.teksti()).split("\\s");
            for (String sana : tekstiSanakerrallaan) {
                for (String hae : etsittävätSanat) {
                    if (sana.equals(hae)) {
                        if (!löydetytSanat.contains(sana)) {
                            löydetytSanat.add(sana);
                        }
                    }
                }
            }
            int lisättävä = doc.tunniste();
            if (löydetytSanat.size() == etsittävätSanat.length) {
                sanatLöydetty.add(lisättävä);
            }
        }
        return sanatLöydetty;
    }

    /**
     * hakee dokumentin tunnisteen perusteella muista dokumenteista
     * @param haeTunniste haettavan dokumentin tunniste
     * @return doc jos dokumentti löytyy tunnisteella, muuten paluuarvo on Null
     */
    public Dokumentti hae(int haeTunniste) {
        for (Dokumentti doc : dokumentit) {
            if (doc.tunniste() == haeTunniste) {
                return doc;
            }
        }
        return null;
    }

    /**
     * Lisää dokumentin poistettavienLista -listaan mikäli tunniste vastaa dokumenttia
     * @param poistettava poistettavan dokumentin tunniste
     * @return poistettavaLöytyy palauttaa true, jos tunnisteella löytyy poistettava dokumentti
    */
    public boolean poistaDokumentti(int poistettava) {
        boolean poistettavaLöytyy = false;
        for (Dokumentti doc : dokumentit) {
            if (doc.tunniste() == poistettava) {
                poistettavienLista.add(doc);
                poistettavaLöytyy = true;
            }
        }
        dokumentit.removeAll(poistettavienLista);
        return poistettavaLöytyy;
    }

    /**
     * poistaa dokumenteista ylimääräiset merkit sekä sanat Dokumentti-luokassa
     * @param sulkusanat lista poistettavista sanoista
     * @param tarkentava poistettavat merkit 
     */
    public void dokumenttienSiivous(LinkedList<String> sulkusanat, String tarkentava) {
        for (Dokumentti doc : dokumentit) {
            doc.siivoa(sulkusanat, tarkentava);
        }
    }

    /**
     * Muuttaa saadun syötteen dokumentiksi, palauttaa null, jos saatu rivi on vääränlainen
     * @param rivi tiedostonrivi
     * @return uusiuutinen, jos saatu rivi vastaa uutista tai uusivitsi, jos saatu rivi vastaa vitsiä,
     * null, jos rivi on vääränlainen
     */
    public Dokumentti dokumentiksi(String rivi) {
        String[] riviOsina = rivi.split(EROTIN);
        if (!riviOsina[0].contains(" ")) {
            int tunniste = Integer.parseInt(riviOsina[0]);
            if (riviOsina[1].contains(".")) { //uutinen
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy");
                LocalDate päivämäärä = LocalDate.parse(riviOsina[1], formatter);
                Uutinen uusiuutinen = new Uutinen(tunniste, päivämäärä, riviOsina[2]);
                return uusiuutinen;
            } else { //vitsi
                Vitsi uusivitsi = new Vitsi(tunniste, riviOsina[1], riviOsina[2]);
                return uusivitsi;
            }
        } else {
            return null;
        }
    }

    /**
     * Tiedostojen lataaminen dokumenteiksi kokoelmaan
     * @param tiedosto ladattava tekstitiedosto
     * @return dokumentit lista dokumenteista
     * @throws FileNotFoundException poikkeus jos tiedostoa ei löydy
     */
    public OmaLista<harjoitustyo.dokumentit.Dokumentti> lataus(String tiedosto) throws FileNotFoundException {
        try {
            //jos lista sisältää dokumentteja, ne poistetaan 
            if (!(dokumentit.isEmpty())) {
                dokumentit.removeAll(dokumentit);
            }
            FileInputStream fis = new FileInputStream(tiedosto);
            Scanner lukija = new Scanner(fis);
            String rivi = "";
            while (lukija.hasNextLine()) {
                rivi = lukija.nextLine();
                Dokumentti lisättävä = dokumentiksi(rivi);
                dokumentit.lisää(lisättävä);
            }
            lukija.close();
            return dokumentit;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    /**
     * Sulkusanatiedoston lataaminen linkitetyksi listaksi
     * @param sulkusanalista poistettavat sanat
     * @return aikaansaatulista 
     * @throws FileNotFoundException jos tiedostoa ei löydy
     */
    public LinkedList<String> sulkusanalistanLataus(String sulkusanalista) throws FileNotFoundException {
        try {
            LinkedList<String> aikaansaatulista = new LinkedList<String>();
            FileInputStream fis = new FileInputStream(sulkusanalista);
            Scanner lukija = new Scanner(fis);
            String rivi = "";
            while (lukija.hasNextLine()) {
                rivi = lukija.nextLine();
                aikaansaatulista.add(rivi);
            }
            lukija.close();
            return aikaansaatulista;
        } catch (FileNotFoundException e) {
            return null;
        }
    }
}

