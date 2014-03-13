package com.xmos.xta.test.pseudorandom;

import java.util.HashSet;
import java.util.Set;

public class ReferenceTree<E> {

    private E m_object;
    private Set<ReferenceTree<E>> m_next;
    private ReferenceTree<E> m_prev;

    public ReferenceTree(E object){
        m_object = object;
        m_next = new HashSet<ReferenceTree<E>>();
        m_prev = null;
    }

    public ReferenceTree(E current, ReferenceTree<E> prev){
        m_object = current;
        m_next = new HashSet<ReferenceTree<E>>();
        m_prev = prev;
    }

    public E getObject(){
        return m_object;
    }

    public ReferenceTree<E> getPrev(){
        return m_prev;
    }

    public Set<ReferenceTree<E>> getNext(){
        return m_next;
    }

}
