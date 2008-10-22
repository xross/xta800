package com.xmos.xta.gui.codeflow;

import java.util.ArrayList;

import com.xmos.xta.engine.XtaInstruction;

public class InstructionLink {
    private XtaInstruction m_instruction;
    private ArrayList<XtaInstruction> m_nextInstructions;
    private ArrayList<XtaInstruction> m_previousInstruction;
    private boolean done = false;
    
    public InstructionLink(XtaInstruction inst){
        m_nextInstructions = new ArrayList<XtaInstruction>();
        m_previousInstruction = new ArrayList<XtaInstruction>();
        m_instruction = inst;
    }
    
    public void addNext(XtaInstruction inst){
        if(!m_nextInstructions.contains(inst) && inst.getPc()!= m_instruction.getPc()){
            m_nextInstructions.add(inst);
        }
    }
    
    public void addPrevious(XtaInstruction inst){
        if(!m_previousInstruction.contains(inst) && inst.getPc()!= m_instruction.getPc()){
            m_previousInstruction.add(inst);
        }
    }
    
    public XtaInstruction getInstruction(){
        return m_instruction;
    }
    
    public ArrayList<XtaInstruction> getNextInstructions(){
        return m_nextInstructions;
    }
    
    public ArrayList<XtaInstruction> getPreviousInstructions(){
        return m_previousInstruction;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isDone() {
        return done;
    }
    
}
