package it.unicam.cs.asdl2324.mp1;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;


/**
 *
 * @author Luca Tesei (template) **DAVIDE MAFALDA email: davide.mafalda@studenti.unicacm.it** (implementazione)
 *
 * @param <E>
 *                il tipo degli elementi del multiset
 */
public class MyMultiset<E> implements Multiset<E> {

    private Map<E, Integer> multiset;
    private static final int MaxInteger = Integer.MAX_VALUE;
    private int modifiche;
    private    int size;

    /**
     * Crea un multiset vuoto.
     */
    public MyMultiset() {
        multiset = new HashMap<>();
        this.modifiche =0;
        this.size=0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int count(Object element) {
        //qui lancio un'eccezione se l'elemento è nullo
        if (element == null) {
            throw new NullPointerException(" Int count: L'elemento non può essere nullo");
        }

        Integer frequenza = multiset.get(element);//ritorna il conteggio dell'elemento passato
        if (frequenza != null) {
            return frequenza;
        }
       return 0;
}

    @Override
    public int add(E element, int occurrences) {
        //controllo che l'elemento passato non sia nullo e che le occorrenze non siano negative/maggiori di Integer.MAX_VALUE
        if (element == null) {
            throw new NullPointerException("Int add: L'elemento non può essere nullo");
        }
        if (occurrences < 0 || occurrences > MaxInteger) {
            throw new IllegalArgumentException("Int add: Le occorrenze devono essere positive e non maggiori di Integer.MAX_VALUE");
        }
        //"getOrDefault" imposta la variabile frequenza a 0 se l'elemento non c'è, sennò prende le sue ricorrenze
        int frequenza = multiset.getOrDefault(element, 0);
            //con il cast a long controllo che il risultato dell'add sia ancora in range
            if ((long) frequenza + occurrences > Integer.MAX_VALUE)
                throw new IllegalArgumentException();
            //se l'elemento è presente nel multiset, aggiorno il conteggio dell'elemento con occorrenze
            multiset.put(element, frequenza + occurrences);
            this.size += occurrences;
            modifiche++;
            return frequenza;
    }


    @Override
    public void add(E element) {
        if (element == null)
            throw new NullPointerException("Void add: elemento passato nullo");
        //"getOrDefault" imposta la variabile frequenza a 0 se l'elemento non c'è, sennò prende le sue ricorrenze
        int frequenza= multiset.getOrDefault(element,0);
        //aggiungo una ricorrenza dell'elemento passato come parametro
            multiset.put(element, multiset.getOrDefault(element,frequenza)+1);
            size++;
            modifiche++;
            //cast da int a long per veriricare che le occorrenze rientrino nel valore massimo memorizzabile in un int
        if ((long) frequenza + 1 > Integer.MAX_VALUE)
            throw new IllegalArgumentException("");
    }

    @Override
    public int remove(Object element, int occurrences) {
        if(element==null) throw new NullPointerException("Int remove: elemento passato nullo");
        if(occurrences<0) throw new IllegalArgumentException("Int remove: occurrences minore di zero");
        //"getOrDefault" imposta la variabile frequenza a 0 se l'elemento non c'è, sennò prende le sue ricorrenze
        Integer frequenza = multiset.getOrDefault(element,0);
            //se l'elemento è contenuto nel multiset
            if(multiset.containsKey(element))
                //e le occorrenze da rimuovere sono minori o uguali al loro valore attuale
                if(occurrences<frequenza) {
                    //rimuoviamo il numero di occorrenze passate come parametro dal multiset
                    multiset.put((E) element, frequenza - occurrences);
                    //diminuisco la dimensione tante quante sono le occorrenze rimosse
                }

                else

                {
                    //senno rimuovo ogni ricorrenza dell'elemento
                    multiset.remove(element);

                }
                size -= occurrences;
                if(occurrences!=0) modifiche++;
                return frequenza;

    }

    @Override
    public boolean remove(Object element) {
        if(element==null) throw new NullPointerException("Boolean remove: elemento passato nullo");
        //
        Integer frequenza = multiset.getOrDefault(element,0);
        //se l'elemento è presente in multiset ,diminuiamo di 1 le sue occorrenze
            if(multiset.containsKey(element)) {
               //diminuisco di uno le sue ricorrenze
                multiset.put((E) element, frequenza - 1);
                modifiche++;

                return true;
            }
            size--;
            //se l'operazione non avviene con successo ritorniamo false
            return false;

    }

    @Override
    public int setCount(E element, int count) {
        if(count<0) throw new IllegalArgumentException("int setCount: passato un count minore di 0");
        if(element== null) throw new NullPointerException("int setCount: passato un elemento nullo");

        Integer frequenza= multiset.getOrDefault(element, 0);
        //cambio il conteggio delle occorrenze dell'elemento con quelle passate come parametro
        multiset.put((E) element,count);
        //
        if(count>frequenza) size+=count-frequenza; else size-=frequenza-count;
        //aumento il numero di modifiche solo se le occorrenze sono diverse
        if(count!=frequenza) modifiche++;
        return frequenza;
    }

    @Override
    public Set<E> elementSet() {
        //creo una nuova lista concatenata dove salvreremo gli elementi del nostro multiset
        Set<E> newList = new LinkedHashSet<>();
        //itero all'interno del multiset
        for (Map.Entry<E, Integer> entry : multiset.entrySet()) {
            //salvo ogni elemento nella variabile "Elemento"
            E elemento = entry.getKey();
            //aggiungo alla nuova lista ogni elemento
            newList.add(elemento);
        }
        //ritorno il nuovo insieme
        return newList;
    }
    @Override
    public Iterator<E> iterator() {
        return new MyMultisetIterator();
    }

    private class MyMultisetIterator implements Iterator<E> {

        private final Iterator<Map.Entry<E, Integer>> entryIterator = multiset.entrySet().iterator();
        private Map.Entry<E, Integer> newEntry;
        private int occurrencesLeft;
        private int modificheAttese;
        //salviamo il numero di modifiche apportate al multiset
        public MyMultisetIterator() {
            this.modificheAttese = modifiche;
        }

        @Override
        public boolean hasNext() {
            //controllo eventuali modifiche apportate al multiset
            checkMod();
            //se non ci sono più elementi nel multiset ritorniamo false
            return occurrencesLeft > 0 || entryIterator.hasNext();
        }

        @Override
        public E next() {
            //controllo le modifiche
            checkMod();
            //in assenza di altri elementi ritorniamo l'eccezione
            if (!hasNext()) {
                throw new NoSuchElementException("Nessun elemento rimasto");
            }
                 //se ci sono ancora degli elementi
            if (occurrencesLeft > 0) {
                //diminuisco le occorrenze rimanenti e return dell'elemento
                occurrencesLeft--;
                return newEntry.getKey();
            }
            //scorro i valori nel loro ordinamento prendendo la newentry
            newEntry = entryIterator.next();
            //salvo le occorrenze rimanenti del nostro elemento
            occurrencesLeft = newEntry.getValue() - 1;
            //se non ci sono più elementi nel multiset ritorniamo l'eccezione

            if (occurrencesLeft < 0) {
                throw new IllegalStateException("Il numero di occorrenze è negativo");
            }
            //return dell'elemento
            return newEntry.getKey();
        }
        //controllo che non siano state effettuate delle modifiche al multiset durante l'iterazione
        private void checkMod() {
            if (modifiche != modificheAttese)   {
                throw new ConcurrentModificationException("Modifica strutturale durante l'iterazione");
            }
        }
    }



    @Override
    public boolean contains(Object element) {
        if(element==null) throw new NullPointerException("Boolean contains: elemento passato null");
            //itero all'interno del multiset, se trovo l'elemento interessato ritorno true
            for (Map.Entry<E, Integer> entry : multiset.entrySet()) {
                if(entry.getKey().equals(element))   return true;
            }
            return false;
    }

    @Override
    public void clear() {
        multiset.clear();
        size=0;
        modifiche++;
    }

    @Override
    public boolean isEmpty() {
        if (size == 0){
            return true;
        }

        return false;

    }

    /*
     * Due multinsiemi sono uguali se e solo se contengono esattamente gli
     * stessi elementi (utilizzando l'equals della classe E) con le stesse
     * molteplicità.
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        //effettuo tutti i dovuti controlli per verificare che due multinsiemi siano uguali
        if (this == obj) return true;
        if (!(obj instanceof MyMultiset)) return false;
        Multiset<E> other =  (Multiset<E>) obj;
        if (this.size() != other.size()) {
            return false;
        }
        for (Map.Entry<E, Integer> entry : multiset.entrySet()) {
            if (other.count(entry.getKey()) != this.count(entry.getKey())) {
                return false;
            }
        }
        return true;
    }

    /*
     * Da ridefinire in accordo con la ridefinizione di equals.
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = 1;
        int prime= 31;
        for (Map.Entry<E, Integer> entry : multiset.entrySet()) {
            result = prime * result + (entry.getKey() == null ? 0 : entry.getKey().hashCode());
            result = prime * result + entry.getValue();
        }
        return result;
    }

}
