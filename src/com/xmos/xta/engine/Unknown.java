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
    public static enum UnknownType {
        GUARD, PAUSABLETIME, BRANCHTARGET, JUMP, FALSEUNKNOWN
    }

    private XtaInstruction m_inst;
    private UnknownType m_reason;
    private Expression m_guard;
    private Expression m_branchTarget;
    
    public Unknown(XtaInstruction inst) {
        m_inst = inst;
        m_reason = UnknownType.PAUSABLETIME;
    }    
    
    public Unknown(XtaInstruction inst, Expression guard, Expression branchTarget) {
        m_guard = guard;
        m_branchTarget = branchTarget;
        m_inst = inst;
        if(!branchTarget.canResolve()){
            m_reason = UnknownType.BRANCHTARGET;
        } else if(!guard.canResolve()){
            m_reason = UnknownType.GUARD;
        } else {
            m_reason = UnknownType.FALSEUNKNOWN;
            
        }
    }
    
    public int getPc() {
        return m_inst.getPc();
    }

    public String getReason() {
        String str = "";
        if (m_reason == UnknownType.GUARD) {
            str = "Return value from input on instruction ";
            str += m_inst.getNmenonic() + " " + m_inst.getOperandString() + " ";
            str += "(0x" + Integer.toHexString(m_inst.getPc()) + ") is unknown (" + m_guard.expressionString() + ")";
        } else if (m_reason == UnknownType.PAUSABLETIME) {
            str = "Time for input on instruction ";
            str += m_inst.getNmenonic() + " " + m_inst.getOperandString() + " ";
            str += "(0x" + Integer.toHexString(m_inst.getPc()) + ") is unknown";
        } else if (m_reason == UnknownType.BRANCHTARGET) {
            str = "Direction of branch at instruction ";
            str += m_inst.getNmenonic() + " " + m_inst.getOperandString() + " ";
            str += "(0x" + Integer.toHexString(m_inst.getPc()) + ") is unknown (" + m_branchTarget.expressionString() + ")";
        } else if (m_reason == UnknownType.FALSEUNKNOWN) {
            str = "know about this loop ";
            str += m_inst.getNmenonic() + " " + m_inst.getOperandString() + " ";
        } else {
            assert (false);
        }
        return str;
    }

    public String getTypeString() {
        switch (m_reason) {
        case GUARD:
        	return "Guard";
        case PAUSABLETIME:
        	return "Pausable Time";
        case BRANCHTARGET:
        	return "Branch Target";
        case JUMP:
        	return "Jump";
        case FALSEUNKNOWN:
        	return "False Unknown";        	
        }
        assert(false);
        return "";
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
