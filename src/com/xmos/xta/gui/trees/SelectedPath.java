package com.xmos.xta.gui.trees;

public class SelectedPath {
    private int depth;
    
    private int pc;
    public SelectedPath(int depth, int pc) {
        super();
        this.setDepth(depth);
        this.setPc(pc);
    }
    public void setPc(int pc) {
        this.pc = pc;
    }
    public int getPc() {
        return pc;
    }
    public void setDepth(int depth) {
        this.depth = depth;
    }
    public int getDepth() {
        return depth;
    }
        
}
