package com.xmos.xta.engine;

import com.xmos.xta.engine.expression.Expression;

public class Unknown {
    /*
     * ReturnValue - engine could not determine the return value of the
     * instruction Pausable - engine does not know how long the instruction will
     * take Branch - engine does not know which branch to take Jump - engine
     * could not decide where to jump to
     */
    public static enum UnknownType {
        GUARD, PAUSABLETIME, BRANCHTARGET, JUMP, FALSEUNKNOWN, UNKNOWN
    }

    private XtaInstruction m_inst;
    private UnknownType m_reason;
    private Expression m_guard;
    private Expression m_branchTarget;
    private Block m_block;
    private boolean m_show;
    
    public Unknown(Block block, XtaInstruction inst) {
    	initialise(block, inst);
    	m_reason = UnknownType.PAUSABLETIME;
        m_show = true;
        
    }    
    
    public Unknown(Block block, XtaInstruction inst, Expression guard, Expression branchTarget) {
    	initialise(block, inst);
    	m_guard = guard;
        m_branchTarget = branchTarget;
        if(!branchTarget.canResolve()){
            m_reason = UnknownType.BRANCHTARGET;
        } else if(!guard.canResolve()){
            m_reason = UnknownType.GUARD;
        } else {
            m_reason = UnknownType.FALSEUNKNOWN;    
        }
    }
    
    private void initialise(Block block, XtaInstruction inst){
    	m_inst = inst;
    	m_block = block;
    	m_guard = new Expression(0, 0, Expression.Type.REGISTER, Expression.Status.UNDEFINED);
        m_reason = UnknownType.UNKNOWN;
        m_branchTarget = new Expression(0, 0, Expression.Type.REGISTER, Expression.Status.UNDEFINED);
        m_show = true;
    }
    
    
    public int getPc() {
    	if(m_inst == null) return 0;
    	else return m_inst.getPc();
    }

    public int getPathId() {
    	if(m_block == null) return 0;
    	else return m_block.getId();
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
    public UnknownType getUnknownType(){
    	return m_reason;
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
    
    public boolean getShow(){
    	return m_show;
    }
    public void setShow(boolean show){
    	m_show= show;
    }
}
