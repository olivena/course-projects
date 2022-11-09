package harjoitustyo;
import harjoitustyo.dokumentit.Dokumentti;
import harjoitustyo.kokoelma.Kokoelma;
import harjoitustyo.omalista.OmaLista;
import java.io.*;
import java.util.Scanner;
import java.util.LinkedList;

/**
* Ohjelman käyttöliittymä. Kaikki käyttäjän toiminta kulkee luoka kautta.
* 
* Olio-ohjelmoinnin perusteet II, kevät 2020
*
* version 5.0
* @author Olivia Takkinen, olivia.takkinen@tuni.fi
*/

public class Kayttoliittyma {
    public static final String LOPETUS = "Program terminated.";
    public static final String VIRHE = "Error!";
    public static final String PUUTTUVATIEDOSTO = "Missing file!";
    public static final String ARGUMENTTIVIRHE = "Wrong number of command-line arguments!";
    Scanner lukija = new Scanner(System.in);
    Kokoelma kokoelmaluokka = new Kokoelma(); 

    private OmaLista<harjoitustyo.dokumentit.Dokumentti> dokumentit = 
    new OmaLista<harjoitustyo.dokumentit.Dokumentti>();

    public static void virhe(String virhe) {
        System.out.println(VIRHE);
    }
    public void parametrillisetKomennot(String komento, LinkedList<String> sulkusanat, String tiedosto)
    throws FileNotFoundException {
        String[] toiminnot = komento.split("\\s", 2);
        String toiminto = toiminnot[0];
        String tarkentava = toiminnot[1];

        if (toiminto.equals("find")) {
            /* tulostaa käyttäjälle dokumentin tunnisteen jossa hakusana mainitaan.*/
            LinkedList<Integer> sanatLöydetty = kokoelmaluokka.haeSanalla(tarkentava);
            if (!sanatLöydetty.isEmpty()) {
                for (int dokumentinTunniste : sanatLöydetty) {
                    System.out.println(dokumentinTunniste);
                }
            }
        }
        else if (tarkentava.contains(" ")) {
            if (toiminto.equals("add")) {
                /* lisää dokumentin kokoelmaan. */
                Dokumentti lisättävädokumentti = kokoelmaluokka.dokumentiksi(tarkentava);
                if (lisättävädokumentti == null) {
                    System.out.println(VIRHE);
                } else {
                    kokoelmaluokka.lisää(lisättävädokumentti);
                }
            } else {
                System.out.println(VIRHE);
            }
        }  else {  
            switch (toiminto) {
                case "print":
                    /* Tulostaa pyydetyn dokumentin käyttäjälle */
                    if (tarkentava.matches("[0-9]+")) {
                        int tulostettavanTunniste = Integer.parseInt(tarkentava);
                        harjoitustyo.dokumentit.Dokumentti tulostettava = kokoelmaluokka.hae(tulostettavanTunniste);
                        if (tulostettava == null) {
                            System.out.println(VIRHE);
                        } else {
                            System.out.println(tulostettava);                 
                        }
                    } else {
                        System.out.println(VIRHE);
                    }
                    break;
                case "remove":
                    /* poistaa dokumentin kokoelmasta, dokumentin valinta tunnisteen avulla.*/
                    if (tarkentava.matches("[0-9]+")) {
                        int poistettavanTunniste = Integer.parseInt(tarkentava);
                        boolean poistettavaLöytyy = kokoelmaluokka.poistaDokumentti(poistettavanTunniste);
                        if (poistettavaLöytyy == false) {
                            System.out.println(VIRHE);
                        }
                    } else {
                        System.out.println(VIRHE);
                    }
                    break;
                case "polish":
                    /* poistaa dokumenteista sanoja sekä merkit.*/
                    kokoelmaluokka.dokumenttienSiivous(sulkusanat, tarkentava);
                    break;
                default: 
                    System.out.println(VIRHE);
            }
        }
    }

    /** 
     * Metodin tehtävänä on ilmoittaa käyttäjälle ohjelman käynnistymisestä,
     * ladata käyttäjän antamat tiedostot sekä kysyä käyttäjältä komentoja.
     * 
     * @param tiedosto sisältää tiedostomuodossa ladattavat dokumentit
     * @param sulkusanalista sisältää sulkusanat tiedostomuodossa
     * @throws FileNotFoundException jos tiedostoja ei löydy
     */
    //String tiedosto, String sulkusanalista
    public void aloitus(String[] args) throws FileNotFoundException {
        boolean ohjelmaJatkuu = true;
        boolean kaiutaKomennot = false;
        
        System.out.println("Welcome to L.O.T.");

        if (args.length != 2) {
            System.out.println(ARGUMENTTIVIRHE);
            System.out.println(LOPETUS);
        } else {
            File tiedostoFile = new File(args[0]);
            File sulkusanalistaFile = new File(args[1]);
            //Tiedoston lataus Kokoelma-luokan kautta
            if (!tiedostoFile.exists()|| !sulkusanalistaFile.exists()) {
                System.out.println(PUUTTUVATIEDOSTO);
                System.out.println(LOPETUS);
            } else {
                //dokumenttien kerääminen
                String tiedosto = args[0];
                String sulkusanalista = args[1];
                dokumentit = kokoelmaluokka.lataus(tiedosto);
                if (dokumentit.isEmpty()) {
                    System.out.println(PUUTTUVATIEDOSTO);
                    System.out.println(LOPETUS);
                    ohjelmaJatkuu = false;
                } 

                //Sulkusanojen listan muodostaminen
                LinkedList<String> sulkusanat = kokoelmaluokka.sulkusanalistanLataus(sulkusanalista);
                if (sulkusanat.isEmpty()) {
                    System.out.println(PUUTTUVATIEDOSTO);
                    System.out.println(LOPETUS);
                    ohjelmaJatkuu = false;
                }

                while (ohjelmaJatkuu) { 
                    System.out.println("Please, enter a command:");
                    String komento = lukija.nextLine();
                    if (kaiutaKomennot == true) {
                        System.out.println(komento);
                    }
                    if (komento.contains(" ")) {
                        parametrillisetKomennot(komento, sulkusanat, tiedosto);
                    } else {
                        switch (komento) {
                            case "quit":
                                System.out.println(LOPETUS);
                                ohjelmaJatkuu = false;
                                break;
                            case "echo":
                                /* käynnistää tai lopettaa komentojen kaiuttamisen. */
                                if (kaiutaKomennot == false) {
                                    System.out.println(komento);
                                    kaiutaKomennot = true;
                                } else {
                                    kaiutaKomennot = false;
                                }
                                break;
                            case "reset":
                                /* lataa dokumenttitiedoston uudelleen ja poistaa kaikki aiemmin tehdyt muutokset.*/
                                kokoelmaluokka.lataus(tiedosto);
                                break;
                            case "print":
                                /* Tulostaa kaikki dokumentit*/
                                LinkedList<Dokumentti> tulosta = kokoelmaluokka.tulostettavat();
                                for (int i = 0; i < tulosta.size(); i++) {
                                    System.out.println(tulosta.get(i));
                                }
                                break;
                            default:
                                System.out.println(VIRHE);
                                break;
                        }
                    }
                }
            }
        }
    }
}
