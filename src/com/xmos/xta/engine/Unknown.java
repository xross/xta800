package com.xmos.xta.engine;

import javax.swing.ImageIcon;

import com.xmos.xta.gui.XtaGui;

public class Unknown {
    /*
     * ReturnValue - engine could not determine the return value of the
     * instruction Pausable - engine does not know how long the instruction will
     * take Branch - engine does not know which branch to take Jump - engine
     * could not decide where to jump to
     */
    public static enum UnknownReason {
        ReturnValue, Pausable, Branch, Jump
    }

    private XtaInstruction m_inst;
    private UnknownReason m_reason;

    public Unknown(XtaInstruction inst) {
        m_inst = inst;
        if (inst.canPause()) {
            m_reason = UnknownReason.Pausable;
        } else {
            m_reason = UnknownReason.Branch;
        }
    }

    public int getPc() {
        return m_inst.getPc();
    }

    public String getUnknownString() {
        String str = "";
        if (m_reason == UnknownReason.ReturnValue) {
            str = "Return value from input on instruction ";
            str += m_inst.getNmenonic() + " " + m_inst.getOperandString() + " ";
            str += "(0x" + Integer.toHexString(m_inst.getPc()) + ") is unknown";
        } else if (m_reason == UnknownReason.Pausable) {
            str = "Time for input on instruction ";
            str += m_inst.getNmenonic() + " " + m_inst.getOperandString() + " ";
            str += "(0x" + Integer.toHexString(m_inst.getPc()) + ") is unknown";
        } else if (m_reason == UnknownReason.Branch) {
            str = "Direction of branch at instruction ";
            str += m_inst.getNmenonic() + " " + m_inst.getOperandString() + " ";
            str += "(0x" + Integer.toHexString(m_inst.getPc()) + ") is unknown";
        } else if (m_reason == UnknownReason.Jump) {
            str = "Direction of jump at instruction ";
            str += m_inst.getNmenonic() + " " + m_inst.getOperandString() + " ";
            str += "(0x" + Integer.toHexString(m_inst.getPc()) + ") is unknown";
        } else {
            assert (false);
        }
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
