package com.xmos.xta.engine.resources;

import com.xmos.xta.engine.Expression;
import com.xmos.xta.engine.InstructionBuffer;
import com.xmos.xta.engine.Registers;

public class Thread implements Resource{
    
    private Registers m_registers;
    private InstructionBuffer m_iBuffer;
    private int m_SR;
    private int m_ID;
    
    private boolean m_cbInUse;
    private boolean m_cbMSync;
    private boolean m_cbSSync;
    private int m_cbMasterBits;

    private Expression m_SPC;
    private Expression m_SSR;
    private Expression m_ET;
    private Expression m_ED;
    private Expression m_SED;
    private Expression m_KEP;
    private Expression m_KSP;
    
    public enum StatusRegister {
    	EEBLE, IEBLE, INENB,ININT,
        INK, SINK, WAITING, FAST
    }
    
    public Thread(int threadNo){
    	m_cbInUse = false;
    	m_ID = threadNo;
        m_registers = new Registers();
        m_iBuffer = new InstructionBuffer();
    }
    
    public Thread(Thread thread){
        m_registers = new Registers(thread.m_registers);
        m_ID = thread.m_ID;
        m_iBuffer = new InstructionBuffer(thread.m_iBuffer);
    }

    public void setControlBits(int controlBits){
    	if((controlBits&1) == 1){
    		m_cbInUse = true;
    	} else {
    		m_cbInUse = false;
    	}
    	if((controlBits&2) == 2){
    		m_cbMSync = true;
    	} else {
    		m_cbMSync = false;
    	}
    	if((controlBits&3) == 3){
    		m_cbSSync = true;
    	} else {
    		m_cbSSync = false;
    	}
    	m_cbMasterBits = (controlBits>>8)&0xFF;
    }
    
    public int getControlBits(){
    	int cb = 0;
    	if(m_cbInUse){
    		cb |=1;
    	}
		if(m_cbMSync){
			cb |=2;		
    	}
		if(m_cbSSync){
			cb |=4;
		}
		cb |= (m_cbMasterBits<<8);
		return cb;
    }

	public boolean isInUse() {
		return m_cbInUse;
	}
	
    public void setInUse(boolean b){
    	m_cbInUse = b;
    }
    public void setMSyncBit(boolean b){
    	m_cbMSync = b;
    }
    public void setSSyncBit(boolean b){
    	m_cbSSync = b;
    }
    public void setMasterBits(int master){
    	m_cbMasterBits = master;
    }
    
    public int getID() {
		return m_ID;
	}

	public InstructionBuffer getIBuffer() {
		return m_iBuffer;
	}
	
    public int getPc(){
    	return m_iBuffer.getPc();
    }
    
    public void setPc(int pc){
    	m_iBuffer.setPc(pc);
    }
    
    public Registers getRegisters(){
        return m_registers;
    }
    
    public void setStatusRegister(int newSR){
    	m_SR = newSR;
    }
    
    public int getStatusRegister(){
    	return m_SR;
    }
    
    public void setStatusRegisterBit(StatusRegister bit, boolean b){
    	if(bit == StatusRegister.EEBLE){
    		if(b) m_SR |= 1;
    		else m_SR &= ~1;
    	} else if(bit == StatusRegister.IEBLE){
    		if(b) m_SR |= 2;
    		else m_SR &= ~2;
    	} else if(bit == StatusRegister.INENB){
    		if(b) m_SR |= 4;
    		else m_SR &= ~4;
    	} else if(bit == StatusRegister.ININT){
    		if(b) m_SR |= 8;
    		else m_SR &= ~8;
    	} else if(bit == StatusRegister.INK){
    		if(b) m_SR |= 16;
    		else m_SR &= ~16;
    	} else if(bit == StatusRegister.SINK){
    		if(b) m_SR |= 32;
    		else m_SR &= ~32;
    	} else if(bit == StatusRegister.WAITING){
    		if(b) m_SR |= 64;
    		else m_SR &= ~64;
    	} else if(bit == StatusRegister.FAST){
    		if(b) m_SR |= 128;
    		else m_SR &= ~128;
    	} else {
    		assert(false);
    	}
    }

    
    
	public Expression getSPC() {
		return m_SPC;
	}

	public void setSPC(Expression m_spc) {
		m_SPC = m_spc;
	}

	public Expression getSSR() {
		return m_SSR;
	}

	public void setSSR(Expression m_ssr) {
		m_SSR = m_ssr;
	}

	public Expression getET() {
		return m_ET;
	}

	public void setET(Expression m_et) {
		m_ET = m_et;
	}

	public Expression getED() {
		return m_ED;
	}

	public void setED(Expression m_ed) {
		m_ED = m_ed;
	}

	public Expression getSED() {
		return m_SED;
	}

	public void setSED(Expression m_sed) {
		m_SED = m_sed;
	}

	public Expression getKEP() {
		return m_KEP;
	}

	public void setKEP(Expression m_kep) {
		m_KEP = m_kep;
	}

	public Expression getKSP() {
		return m_KSP;
	}

	public void setKSP(Expression m_ksp) {
		m_KSP = m_ksp;
	}

	public int getResourceID() {
		int id=0;
		id = Resources.RES_TYPE_SYNC;
		int regId = 0;
		id = id | (regId << 3);
		id = id | (m_ID << 7);
		return id;
	}
 
    
    
}
