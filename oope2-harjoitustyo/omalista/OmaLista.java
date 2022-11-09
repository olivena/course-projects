package harjoitustyo.omalista;
import harjoitustyo.apulaiset.Ooperoiva;
import java.util.LinkedList;

/* 
*
* Olio-ohjelmoinnin perusteet II, kevät 2020
*
* version 5.0 - toimii oikein
* @author Olivia Takkinen, olivia.takkinen@tuni.fi
*/

/** 
 *  Lisää elementtejä OmaanListaan järjestyksessä.
 * @param <E>
 */
@SuppressWarnings({"unchecked"}) 
public class OmaLista<E> extends LinkedList<E> implements Ooperoiva<E> {
    @Override
    public void lisää(E uusi) throws IllegalArgumentException {
        if (uusi instanceof Comparable) {
            Boolean jatkuu = true;
            int ind = 0;
            while(jatkuu) {
                if (ind < this.size()) {
                    if (((Comparable)this.get(ind)).compareTo(uusi) == 1 && ind == 0) {
                        add(ind, uusi);
                        jatkuu = false; 
                    } else if (((Comparable)this.get(ind)).compareTo(uusi) <= 0) {
                        ind++;
                    } else {
                        add((ind), uusi);  
                        jatkuu = false; 
                    }
                } else {
                    add(uusi);
                    jatkuu = false;
                }
            }
        } else {
            throw new IllegalArgumentException();
        }
    }
}
