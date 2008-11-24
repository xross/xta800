package com.xmos.xta.swing.codeflow;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Vector;

import com.xmos.xta.engine.XtaInstruction;

public class BasicBlock{
    
    private ArrayList<BasicBlock> m_nextBlocks;
    private int m_startPc;
    private int m_endPc;
    private ArrayList<XtaInstruction> m_instructions;
    private int m_depth;
    
    private Point m_location;
    

    public BasicBlock(){
        setDepth(-1);
        m_instructions = new ArrayList<XtaInstruction>();
        m_nextBlocks = new ArrayList<BasicBlock>();
    }
    
    public void addInstructions(Vector<XtaInstruction> instructions){
        assert(instructions.size()>0);
        
        for(XtaInstruction inst : instructions){
            if(!inst.isFnop()){
                m_instructions.add(inst);
            }
        }
        m_startPc = m_instructions.get(0).getPc();
        m_endPc = m_instructions.get(m_instructions.size()-1).getPc();
    }

    public void addInstruction(XtaInstruction instructions){
        assert(instructions!=null);
        if(!instructions.isFnop()){
            m_instructions.add(instructions);
        }
        m_startPc = m_instructions.get(0).getPc();
        m_endPc = m_instructions.get(m_instructions.size()-1).getPc();
    }
    
    public ArrayList<XtaInstruction> getInstructions(){
        return m_instructions;
    }
    
    public void addNextBlock(BasicBlock bb){
        assert(bb!=null);
        m_nextBlocks.add(bb);
    }
    public ArrayList<BasicBlock> getNextBasicBlocks(){
        return m_nextBlocks;
    }
    
    public int getStartPc(){
        return m_startPc;
    }
    public int getEndPc(){
        return m_endPc;
    }

    public boolean containsPc(int pc){
        for(XtaInstruction inst :m_instructions){
            if(pc == inst.getPc()){
                return true;
            }
        }
        return false;
    }

    public void setDepth(int depth) {
        this.m_depth = depth;
    }

    public int getDepth() {
        return m_depth;
    }

    public void setM_location(Point m_location) {
        this.m_location = m_location;
    }

    public Point getM_location() {
        return m_location;
    }
    
    
    
}




