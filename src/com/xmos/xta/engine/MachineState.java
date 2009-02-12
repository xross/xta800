package com.xmos.xta.engine;

import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xeloader.XeLoader.Architecture;
import com.xmos.xta.engine.resources.Resources;
import com.xmos.xta.engine.resources.Thread;

public class MachineState {
	public static final boolean DEBUG = false;
	
    public static enum BranchState {
        TAKEN, NOTTAKEN, UNKNOWN
    }

    public static enum InstructionSet {
    	REVA, REVB
    }
    
    public final static int CP_REG_NUM = 12;
    public final static int DP_REG_NUM = 13;
    public final static int SP_REG_NUM = 14;
    public final static int LR_REG_NUM = 15;
    public final static int STACK_OFFSET_SPC = 1; 	  
    public final static int STACK_OFFSET_SSR = 2; 	 
    public final static int STACK_OFFSET_SED = 3; 	 
    public final static int STACK_OFFSET_ET = 4; 
    int m_opcode;

    int m_op1Fld;
    int m_op2Fld;
    int m_op3Fld;
    int m_op4Fld;
    int m_op5Fld;
    int m_op6Fld;

    public Expression m_op1;
    Expression m_op2;
    Expression m_op3;
    Expression m_op4;
    Expression m_op5;
    Expression m_op6;
    
    private Resources m_resources;
    private Memory m_memory;
	public boolean m_branched;    
    private Architecture m_arch;
    
    MachineState(SymbolTable symbolTable, MemoryImage memoryImage, Architecture arch) {
    	m_arch = arch;
        m_memory = new Memory();
        m_resources = new Resources(memoryImage.getNodeId(), memoryImage.getCoreId());
        m_resources.addThread();
        m_branched = false;
        setStackPointerRegister(new Expression(memoryImage.getMemoryBase() + memoryImage.getMemorySize()), 0);
        initialiseRegisters(symbolTable);
        initialiseMemory(memoryImage); 
    }

    public Architecture getArchitecture(){
    	return m_arch;
    }
    
    private int m_memoryBase;
    private int m_memorySize;
    
    private void initialiseMemory(MemoryImage memoryImage) {
    	m_memoryBase = memoryImage.getMemoryBase();
    	m_memorySize = memoryImage.getMemorySize();
    	
    	//FIXME -this doesn't look right!
    	m_memory.blockWrite(0x10000, 0x10000, memoryImage);
	}

	private void initialiseRegisters(SymbolTable symbolTable) {
		int cpStart = symbolTable.symbolAddress("_cp");
		int dpStart = symbolTable.symbolAddress("_dp");
		for(Thread thread : m_resources.getThreads()){
			thread.getRegisters().writeRegister(DP_REG_NUM, new Expression(dpStart));
			thread.getRegisters().writeRegister(CP_REG_NUM, new Expression(cpStart));
		}
	}

	MachineState(MachineState machineState) {
		m_arch = machineState.m_arch;
        m_opcode = machineState.m_opcode;
        m_op1Fld = machineState.m_op1Fld;
        m_op2Fld = machineState.m_op2Fld;
        m_op3Fld = machineState.m_op3Fld;
        m_op4Fld = machineState.m_op4Fld;
        m_op5Fld = machineState.m_op5Fld;
        m_op6Fld = machineState.m_op6Fld;

        m_op1 = machineState.m_op1;
        m_op2 = machineState.m_op2;
        m_op3 = machineState.m_op3;
        m_op4 = machineState.m_op4;
        m_op5 = machineState.m_op5;
        m_op6 = machineState.m_op6;
        m_resources = new Resources(machineState.getResources());
        m_memory = new Memory(machineState.m_memory);
        m_branched = machineState.m_branched;
    }

    public Resources getResources(){
        return m_resources;
    }
    
    void setPc(int pc, int threadNo) {
    	m_resources.getCurrentIBuffer(threadNo).setPc(pc);
    }

    int getPc(int thread) {
        return m_resources.getCurrentIBuffer(thread).getPc();
    }

    void setOpcode(int opcode) {
        m_opcode = opcode;
    }

    int getOpcode() {
        return m_opcode;
    }

    public boolean isWithinMemory(int target) {
		if(target >= m_memoryBase && target <= m_memoryBase + m_memorySize){
			return true;
		} else {
			return false;
		}
	}

	public boolean is16bitAligned(int target) {
		if((target & 0x1) == 0){ 
			return true;
		} else {
			return false;
		}
	}

    boolean canIssue(int numBytes, int thread) {
        return m_resources.getCurrentIBuffer(thread).canIssue(numBytes);
    }

    int fetch(int thread) {
    	if (m_resources.getCurrentIBuffer(thread).getNumBytes()<6){
    		 return m_resources.getCurrentIBuffer(thread).fetch();
    	}
    	return 0;
    }

    void issue(int numBytes, int thread) {
    	m_resources.getCurrentIBuffer(thread).issue(numBytes);
    }

    int branchTo(int branchTarget, boolean isLoadStore, int thread) {
    	return m_resources.getCurrentIBuffer(thread).branchTo(branchTarget, isLoadStore);
    }

    int getNumIBufferBytes(int thread) {
        return m_resources.getCurrentIBuffer(thread).getNumBytes();
    }

    Expression getConstPoolRegister(int thread) {
        return m_resources.getThread(thread).getRegisters().readRegister(CP_REG_NUM);
    }

    void setConstPoolRegister(Expression value, int thread) {
    	m_resources.getThread(thread).getRegisters().writeRegister(CP_REG_NUM, value);
    }

    Expression getDataPoolRegister(int thread) {
        return m_resources.getThread(thread).getRegisters().readRegister(DP_REG_NUM);
    }

    void setDataPoolRegister(Expression value, int thread) {
    	m_resources.getThread(thread).getRegisters().writeRegister(DP_REG_NUM, value);
    }

    Expression getStackPointerRegister(int thread) {
        assert(m_resources.getThread(thread).getRegisters() != null);
        return m_resources.getThread(thread).getRegisters().readRegister(SP_REG_NUM);
    }

    void setStackPointerRegister(Expression value, int thread) {
    	m_resources.getThread(thread).getRegisters().writeRegister(SP_REG_NUM, value);
    }

    Expression getLinkRegister(int thread) {
        return m_resources.getThread(thread).getRegisters().readRegister(LR_REG_NUM);
    }

    void setLinkRegister(Expression value, int thread) {
    	m_resources.getThread(thread).getRegisters().writeRegister(LR_REG_NUM, value);
    }

    Expression readRegister(int regNum, int thread) {
    	Expression e = m_resources.getThread(thread).getRegisters().readRegister(regNum);
    	if (e == null){
    		return new Expression(0, 0, Expression.Type.REGISTER, Expression.Status.UNDEFINED);
    	} else {
    		return e;
    	}
        
    }

    void writeRegister(int regNum, Expression regValue, int thread) {
    	m_resources.getThread(thread).getRegisters().writeRegister(regNum, regValue);
    }

    Expression readMemory(int address) throws Exception {
    	
    	Expression read = m_memory.wordRead(address);
    	if (read.canResolve()){
    		if (DEBUG) System.out.println("read 0x" + Integer.toHexString(read.getValue())  + " from 0x" + 
    				Integer.toHexString(address));
    	} else {
    		if (DEBUG) System.out.println("read unknown from 0x" + Integer.toHexString(address));
    	}
        return read;
    }
    public Expression readShort(int address) throws Exception {
    	Expression read = m_memory.halfRead(address);
    	if (read.canResolve()){
    		if (DEBUG) System.out.println("read word 0x" + Integer.toHexString(read.getValue())  + " from 0x" + 
    				Integer.toHexString(address));
    	} else {
    		if (DEBUG) System.out.println("read word unknown from 0x" + Integer.toHexString(address));
    	}
        return read;
	}
    Expression readByte(int address) throws Exception {
    	
    	Expression read = m_memory.byteRead(address);
    	if (read.canResolve()){
    		if (DEBUG) System.out.println("read byte 0x" + Integer.toHexString(read.getValue())  + " from 0x" + 
    				Integer.toHexString(address));
    	} else {
    		if (DEBUG) System.out.println("read byte unknown from 0x" + Integer.toHexString(address));
    	}
        return read;
    }

    void writeMemory(int address, Expression value) throws Exception {
    	if (value.canResolve()){
    		if (DEBUG) System.out.println("\twrite to memory: 0x" + Integer.toHexString(value.getValue()) + " at 0x" 
        			+Integer.toHexString(address));
        } else {
        	if (DEBUG) System.out.println("\twrite to memory:  unknown to 0x" + Integer.toHexString(address));
        }
        m_memory.wordWrite(address, value);
    }
    
    
    public void writeByte(int address, Expression value) throws Exception {
    	if (value.canResolve()){
    		if (DEBUG) System.out.println("\twrite to memory: 0x" + Integer.toHexString(value.getValue()) + " at 0x" 
        			+Integer.toHexString(address));
        } else {
        	if (DEBUG) System.out.println("\twrite to memory:  unknown to 0x" + Integer.toHexString(address));
        }
        m_memory.byteWrite(address, value);
	}
    
    public void writeShort(int address, Expression value) throws Exception {
    	if (value.canResolve()){
    		if (DEBUG) System.out.println("\twrite to memory: 0x" + Integer.toHexString(value.getValue()) + " at 0x" 
        			+Integer.toHexString(address));
        } else {
        	if (DEBUG) System.out.println("\twrite to memory:  unknown to 0x" + Integer.toHexString(address));
        }
        m_memory.shortWrite(address, value);
	}

	public void releaseMemory() {
		m_op1 = null;
		m_op2 = null;
		m_op3 = null;
		m_op4 = null;
		m_op5 = null;
		m_op6 = null;
	    m_resources = null;
	    m_memory = null;
	}

	public void setBranched(boolean branched){
		m_branched = branched;
	}

	public boolean branched() {
		return m_branched;
	}

	public void resetState(){
		m_branched = false;
		m_branchTargetKnown = false;
		m_guardRegister = null;
		m_branchTargetRegister = null;
		m_branchedOnTrue = false;
	}
	
	private boolean m_branchTargetKnown;
	private Expression m_guardRegister;
	private Expression m_branchTargetRegister;
	private boolean m_branchedOnTrue;
	
	public void setBranchProperties(boolean branched, boolean branchTargetKnown,
			Expression guardRegister, Expression branchTargetRegister, boolean branchedOnTrue){
		m_branched = branched;
		m_branchTargetKnown = branchTargetKnown;
		m_guardRegister = guardRegister;
		m_branchTargetRegister = branchTargetRegister;
		m_branchedOnTrue = branchedOnTrue;
	}
	
	public boolean branchTargetKnown() {
		return m_branchTargetKnown;
	}

	public Expression getGuardRegister() {
		return m_guardRegister;
	}

	public Expression getBranchTargetRegister() {
		return m_branchTargetRegister;
	}

	public boolean branchedOnTrue(){
		return m_branchedOnTrue;
	}

	

	



}
