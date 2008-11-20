package com.xmos.xta.engine.resources;

import com.xmos.xta.engine.Expression;
import com.xmos.xta.engine.InstructionBuffer;
import com.xmos.xta.engine.MachineState;
import com.xmos.xta.engine.Registers;

public class Thread implements Resource{
    
    private int m_threadNo;
    private Registers m_registers;
    private InstructionBuffer m_iBuffer;
    public enum StatusRegister {
    	EEBLE, IEBLE, INENB,ININT,
        INK, SINK, WAITING, FAST
    }
    private boolean EEBLE, IEBLE, INENB,ININT, INK, SINK, WAITING, FAST;
    
    public enum ControlBits {
    	INUSE, MSYNC,
    	SSYNC, MASTER
    }
    private boolean INUSE, MSYNC, SSYNC;
    private int MASTER;
    
    public Thread(int threadNo){
    	INUSE = false;
        m_threadNo = threadNo;
        m_registers = new Registers();
        m_iBuffer = new InstructionBuffer();
    }
    
    public Thread(Thread thread){
    	INUSE = thread.INUSE;
        m_registers = new Registers(thread.m_registers);
        m_threadNo = thread.m_threadNo;
        m_iBuffer = new InstructionBuffer(thread.m_iBuffer);
    }
    
    public void setControlBit(ControlBits bit, boolean set){
    	if(bit == ControlBits.INUSE){
    		INUSE = set;
    	} else if(bit == ControlBits.MSYNC){
    		MSYNC = set;
    	} else if(bit == ControlBits.SSYNC){
    		SSYNC = set;
    	} else assert(false);

    }
    public void setControlBit(ControlBits bit, int set){
    	if(bit == ControlBits.MASTER){
    		MASTER = set;
    	} else assert(false);
    }
    
    public boolean getControlBit(ControlBits bit){
    	if(bit == ControlBits.INUSE){
    		return INUSE;
    	} else if(bit == ControlBits.MSYNC){
    		return MSYNC;
    	} else if(bit == ControlBits.SSYNC){
    		return SSYNC;
    	} 
    	assert(false);
    	return false;
    }
    
    public int getMasterControlBit(){
    	return MASTER;
    }
    
    
	public void setStatusRegisterBit(StatusRegister bit, boolean set){
		if(bit == StatusRegister.EEBLE){
			EEBLE = set;
		} else if(bit == StatusRegister.FAST){
			FAST = set;
		} else if(bit == StatusRegister.IEBLE){
			IEBLE = set;
		} else if(bit == StatusRegister.INENB){
			INENB = set;
		} else if(bit == StatusRegister.ININT){
			ININT = set;
		} else if(bit == StatusRegister.INK){
			INK = set;
		} else if(bit == StatusRegister.SINK){
			SINK = set;
		} else if(bit == StatusRegister.WAITING){
			WAITING = set;
		} else 
			assert(false);
	}
	
	public boolean getStatusRegisterBit(StatusRegister bit){
		if(bit == StatusRegister.EEBLE){
			 return EEBLE;
		} else if(bit == StatusRegister.FAST){
			return FAST;
		} else if(bit == StatusRegister.IEBLE){
			return IEBLE;
		} else if(bit == StatusRegister.INENB){
			return INENB;
		} else if(bit == StatusRegister.ININT){
			return ININT;
		} else if(bit == StatusRegister.INK){
			return INK;
		} else if(bit == StatusRegister.SINK){
			return SINK;
		} else if(bit == StatusRegister.WAITING){
			return WAITING;
		} else {
			assert(false);
		}
		return false;
	}
    
    public int getNumber() {
		return m_threadNo;
	}

	public int getPortWidth() {
		return 0;
	}

	public int getRegId() {
		return 0;
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
    
    public boolean isInUse(){
        return INUSE;
    }
    
    public void setInUse(boolean b) {
    	INUSE = b;
    	if(b)
    		SSYNC = true;
    }
    
    public void FREER() {
    	INUSE = false;
    }

    public void TINITCP(Expression expression) {
        
        
    }

    public void TINITDP(Expression expression) {
        
        
    }

    public void TINITLR(Expression expression) {
        
        
    }

    public void TINITPC(Expression expression) {
        
        
    }

    public void TINITSP(Expression expression) {
        
        
    }

    public void TSETR(Expression m_op2, Expression m_op3) {
        
        
    }

    public void TSTART() {
        
        
    }
    public void GETST(MachineState state) {
		// TODO Auto-generated method stub
		
	}

	public void MJOIN(MachineState state) {
		// TODO Auto-generated method stub
		
	}

	public void MSYNC(MachineState state) {
		// TODO Auto-generated method stub
		
	}

    public void CHKCT(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
        
    }

    public void CLRPT() {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
        
    }

    public void EDU() {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
        
    }

    public void EEF(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
        
    }

    public void EET(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
        
    }

    public void EEU() {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
        
    }

    public void ENDIN(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
        
    }

    public void GETD(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
        
    }

    public void GETR(int dest, int scr) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
        
    }

    public void GETTS(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
        
    }

    public void IN(Expression destRegister) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
        
    }

    public void INCT(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
        
    }

    public void INSHR(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
        
    }

    public void INT(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
        
    }

    public void OUT(Expression scr1) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
        
    }

    public void OUTCT(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
        
    }

    public void OUTSHR(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
        
    }

    public void OUTT(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
        
    }

    public void PEEK(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
        
    }

    public void SETC(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
        
    }

    public void SETCLK(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
        
    }

    public void SETD(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
        
    }

    public void SETEV(Expression readRegister) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
        
    }

    public void SETPSC(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
        
    }

    public void SETPT(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
        
    }

    public void SETRDY(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
        
    }

    public void SETTW(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
        
    }

    public void SETV(Expression readRegister) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
        
    }

    public void TESTCT(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
        
    }

    public void TESTLCL(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
        
    }

    public void TESTWCT(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
        
    }

	
	



    
   
}
