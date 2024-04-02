package it.unicam.cs.asdl2324.mp1;

import java.util.HashSet;
import java.util.Set;
import java.util.LinkedList;


/**
 *
 * @author Luca Tesei (template) **DAVIDE MAFALDA email: davide.mafalda@studenti.unicam.it** (implementazione)
 *
 */
public class LinkedListDisjointSets implements DisjointSets {
    //creo una nuova lista di liste di elementi disgiunti
    private LinkedList<LinkedList<DisjointSetElement>> disjointSets;

    /**
     * Crea una collezione vuota di insiemi disgiunti.
     */
    public LinkedListDisjointSets() {
        disjointSets=  new LinkedList<>();
    }

    /*
     * Nella rappresentazione con liste concatenate un elemento è presente in
     * qualche insieme disgiunto se il puntatore al suo elemento rappresentante 
     * (ref1) non è null.
     */
    @Override
    public boolean isPresent(DisjointSetElement e) {
        //itero all'interno delle liste
        for (LinkedList<DisjointSetElement> set : disjointSets) {
            //se l'elemento è presente all'interno di uno di essi return true, senno false
            if (set.contains(e)) {
                return true;
            }
        }
        return false;
    }

    /*
     * Nella rappresentazione con liste concatenate un nuovo insieme disgiunto è
     * rappresentato da una lista concatenata che contiene l'unico elemento. Il
     * rappresentante deve essere l'elemento stesso e la cardinalità deve essere
     * 1.
     */
    @Override
    public void makeSet(DisjointSetElement e) {
        if(e== null) throw new NullPointerException("void makeSet: e is null");
        // Verifica se l'elemento è già presente
        if (isPresent(e)) {
            //in caso tiro un'eccezione
            throw new IllegalArgumentException("void makeSet: L'elemento è già presente in un insieme disgiunto");
        }

        // creazione di un nuovo insieme
        LinkedList<DisjointSetElement> newSet = new LinkedList<>();
        //aggiungo il nostro elemento
        newSet.add(e);
        //lo setto come rappresentante
        e.setRef1(e);
        //la nuova dimensione sarà 1
        e.setNumber(e.getNumber()+1);
        //aggiungo l'insieme alla lista di insiemi disgiunti
        disjointSets.add(newSet);
    }



    /*
     * Nella rappresentazione con liste concatenate per trovare il
     * rappresentante di un elemento basta far riferimento al suo puntatore
     * ref1.
     */
    @Override
    public DisjointSetElement findSet(DisjointSetElement e) {
        //faccio i dovuti controlli
        if(e== null) throw new NullPointerException("void findSet: e is null");

        if (!isPresent(e)) {
            throw new IllegalArgumentException("void findSet: L'elemento è già presente in un insieme disgiunto");
        }
        //return del rappresentante dell'insieme in cui è presente l'elemento che cerchiamo

        return e.getRef1();
    }

    /*
     * Dopo l'unione di due insiemi effettivamente disgiunti il rappresentante
     * dell'insieme unito è il rappresentate dell'insieme che aveva il numero
     * maggiore di elementi tra l'insieme di cui faceva parte {@code e1} e
     * l'insieme di cui faceva parte {@code e2}. Nel caso in cui entrambi gli
     * insiemi avevano lo stesso numero di elementi il rappresentante
     * dell'insieme unito è il rappresentante del vecchio insieme di cui faceva
     * parte {@code e1}.
     * 
     * Questo comportamento è la risultante naturale di una strategia che
     * minimizza il numero di operazioni da fare per realizzare l'unione nel
     * caso di rappresentazione con liste concatenate.
     * 
     */
    @Override
    public void union(DisjointSetElement e1, DisjointSetElement e2) {
        //effettuo i dovuti controlli
        if (e1 == null || e2 == null)
            throw new NullPointerException("Gli elementi non possono essere nulli");
        if (!isPresent(e1) || !isPresent(e2))
            throw new IllegalArgumentException("un elemento non esiste");
        DisjointSetElement highRep;
        DisjointSetElement lowRep;
        //salvo in due variabili i rappresentati dell'insieme in cui sono contenuti gli elementi passati come parametro
        DisjointSetElement e1Rep = findSet(e1);
        DisjointSetElement e2Rep = findSet(e2);
        //la dimensione sarà uguale alla somma delle dimensioni dei due insiemi
        int newSize = e1Rep.getNumber() + e2.getNumber();
        //in caso gli elementi siano già nello stesso insieme esco dal metodo
        if (e1Rep == e2Rep)
            return;
        //determino quale dei due rappresentanti è nell'insieme maggiore e quale è nell'insieme minore
        if (e1Rep.getNumber() < e2Rep.getNumber()) {
            lowRep = e1Rep;
            highRep = e2Rep;
        } else {
            lowRep = e2Rep;
            highRep = e1Rep;
        }

            DisjointSetElement current = highRep;//t1
            DisjointSetElement current2 = lowRep;//t2

        //con queste iterazioni riusciamo ad unire i due insiemi,
        //impostare come nuovo rappresentante, il rappresentante dell'insieme precedentemente maggiore
        //impostare la nuova size per ogni elemento degli insiemi
        //questo collegando i due insiemi facendo riferimento ai loro rappresentanti
            while (current.getRef2() != null) {
                current.setNumber(newSize);
                current = current.getRef2();
            }

            current.setNumber(newSize);
            current.setRef2(lowRep);

            current2.setNumber(newSize);
            current2.setRef1(highRep);


            while (current2.getRef2() != null) {
                current2 = current2.getRef2();
                current2.setRef1(highRep);
                current2.setNumber(newSize);
            }


            current2.setNumber(newSize);
            current2.setRef1(highRep);
        }



    @Override
    public Set<DisjointSetElement> getCurrentRepresentatives() {
        Set<DisjointSetElement> rappresentanti = new HashSet<>();
        //itero dentro la lista di insieme disgiunti
        for (LinkedList<DisjointSetElement> set : disjointSets) {
            //solo in caso l'insieme non sia vuoto
            if (!set.isEmpty()) {
                DisjointSetElement rep = set.element().getRef1(); // Assumendo che il primo elemento sia il rappresentante
                //aggiungo alla nuova lista il rappresentante
                rappresentanti.add(rep);
            }
        }
        return rappresentanti;
    }




    @Override
    public Set<DisjointSetElement> getCurrentElementsOfSetContaining(DisjointSetElement e) {
        //faccio i dovuti controlli
        if (e == null) throw new NullPointerException("getCurrentElements: parametro nullo passato");
        if (!isPresent(e)) throw new IllegalArgumentException("getCurrentElements: paramentro passato non presente");
        //salvo in una variabile il rappresentante dell'insieme in cui è presente l'elemento che cerco
        DisjointSetElement rep = e.getRef1();
        Set<DisjointSetElement> newSet = new HashSet<>();
        //itero dentro la lista di insieme disgiunti
            while(rep.getRef2()!=null) {
                newSet.add(rep);
                rep=rep.getRef2();
             }
        newSet.add(rep);
        // Restituisco l'insieme contenente gli elementi dell'insieme di e
        return newSet;
    }

        @Override
        public int getCardinalityOfSetContaining (DisjointSetElement e){
            //effettuo i dovuto controlli
            if (e == null) throw new NullPointerException("getCardinality: elemento passato nullo");
            if (!isPresent(e))
                throw new IllegalArgumentException("getCardinality: elemento non presente in alcun insieme");
            //return della grandezza dell'insieme dell'elemento passato
            return e.getRef1().getNumber();
        }

    }
