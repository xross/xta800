package com.xmos.xta.engine;

import javax.swing.ImageIcon;

import com.xmos.xta.swing.XtaGui;

public class Unknown {
    /*
     * ReturnValue - engine could not determine the return value of the
     * instruction Pausable - engine does not know how long the instruction will
     * take Branch - engine does not know which branch to take Jump - engine
     * could not decide where to jump to
     */
    public static enum UnknownReason {
        GUARD, PAUSABLETIME, BRANCHTARGET, JUMP, FALSEUNKNOWN
    }

    private XtaInstruction m_inst;
    private UnknownReason m_reason;
    private Expression m_guard;
    private Expression m_branchTarget;
    
    public Unknown(XtaInstruction inst) {
        m_inst = inst;
        m_reason = UnknownReason.PAUSABLETIME;
    }
    
    
    public Unknown(XtaInstruction inst, Expression guard, Expression branchTarget) {
        m_guard = guard;
        m_branchTarget = branchTarget;
        m_inst = inst;
        if(!branchTarget.canResolve()){
            m_reason = UnknownReason.BRANCHTARGET;
        } else if(!guard.canResolve()){
            m_reason = UnknownReason.GUARD;
        } else {
            m_reason = UnknownReason.FALSEUNKNOWN;
            
        }
    }
    
    public int getPc() {
        return m_inst.getPc();
    }

    public String getUnknownString() {
        String str = "";
        /*
        if (m_reason == UnknownReason.GUARD) {
            str = "Return value from input on instruction ";
            str += m_inst.getNmenonic() + " " + m_inst.getOperandString() + " ";
            str += "(0x" + Integer.toHexString(m_inst.getPc()) + ") is unknown (" + m_guard.expressionString() + ")";
        } else if (m_reason == UnknownReason.PAUSABLETIME) {
            str = "Time for input on instruction ";
            str += m_inst.getNmenonic() + " " + m_inst.getOperandString() + " ";
            str += "(0x" + Integer.toHexString(m_inst.getPc()) + ") is unknown";
        } else if (m_reason == UnknownReason.BRANCHTARGET) {
            str = "Direction of branch at instruction ";
            str += m_inst.getNmenonic() + " " + m_inst.getOperandString() + " ";
            str += "(0x" + Integer.toHexString(m_inst.getPc()) + ") is unknown (" + m_branchTarget.expressionString() + ")";
        } else if (m_reason == UnknownReason.FALSEUNKNOWN) {
            str = "know about this loop ";
            str += m_inst.getNmenonic() + " " + m_inst.getOperandString() + " ";
        } else {
            assert (false);
        }
        */
        return str;
    }

    public ImageIcon getIcon() {
        ImageIcon icon;
        try {
            icon = new ImageIcon(XtaGui.class.getResource("warning.png"));
        } catch (Exception e) {
            return new ImageIcon();
        }
        return icon;
    }
}
