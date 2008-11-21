package com.xmos.xta.swing;

import com.xmos.xta.engine.Block;

public class RouteSelection {

    private Block m_root;
    private Block m_selection;
    public void setM_root(Block m_root) {
        this.m_root = m_root;
    }
    public Block getM_root() {
        return m_root;
    }
    public void setM_selection(Block m_selection) {
        this.m_selection = m_selection;
    }
    public Block getM_selection() {
        return m_selection;
    }
    public RouteSelection(Block m_root, Block m_selection) {
        super();
        this.m_root = m_root;
        this.m_selection = m_selection;
    }
    
    public RouteSelection() {
    }
    public String toString(){
        return "" + m_root + " " + m_selection;
    }
}
