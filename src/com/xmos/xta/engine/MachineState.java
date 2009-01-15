package com.xmos.xta.engine;

import java.util.ArrayList;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xeloader.XeLoader.Architecture;
import com.xmos.xta.engine.InstructionExecption.Error;
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
    
    private Expression m_branchTargetExpression;
    private Expression m_guardExpression;
    
    private boolean m_wasBranch;
    private boolean m_wasBranchResolved;
    
    private int m_branchTarget;
    private BranchState m_branchTaken;

    private boolean m_wasJump;
    private boolean m_wasJumpResolved;
    private int m_jumpTarget;
    private BranchState m_jumpTaken;

    private boolean m_wasEventWait;
    private boolean m_wasEventResolved;
    private ArrayList<Integer> m_eventTargets;
    
    private Resources m_resources;
    private Memory m_memory;
    
    private Architecture m_arch;
    
    MachineState(SymbolTable symbolTable, MemoryImage memoryImage, Architecture arch) {
    	m_arch = arch;
        m_memory = new Memory();
        m_resources = new Resources(memoryImage.getNodeId(), memoryImage.getCoreId());
        m_resources.addThread();
        setStackPointerRegister(new Expression(memoryImage.getMemoryBase() + memoryImage.getMemorySize()), 0);
        initialiseRegisters(symbolTable);
        initialiseMemory(memoryImage);
        m_eventTargets = new ArrayList<Integer>(); 
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

        m_wasBranch = machineState.m_wasBranch;
        m_wasBranchResolved = machineState.m_wasBranchResolved;
        m_wasEventWait = machineState.m_wasEventWait;
        m_branchTarget = machineState.m_branchTarget;
        m_wasJump = machineState.m_wasJump;
        m_wasJumpResolved = machineState.m_wasJumpResolved;
        m_jumpTarget = machineState.m_jumpTarget;

        m_resources = new Resources(machineState.getResources());
       
        m_memory = new Memory(machineState.m_memory);
        m_eventTargets = new ArrayList<Integer>(machineState.m_eventTargets); 
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

    boolean wasBranch() {
        return m_wasBranch;
    }

    boolean wasBranchResolved() {
        return m_wasBranchResolved;
    }

    int getBranchTarget() {
        return m_branchTarget;
    }

    boolean wasJump() {
        return m_wasJump;
    }

    boolean wasJumpResolved() {
        return m_wasJumpResolved;
    }

    int getJumpTarget() {
        return m_jumpTarget;
    }

    BranchState getJumpTaken() {
        return m_jumpTaken;
    }

    BranchState getBranchTaken() {
        return m_branchTaken;
    }

    void branch(boolean b, Expression guard, Expression branchTarget) throws Exception {
        m_branchTargetExpression = branchTarget;
		branchTarget.resolve();
        m_wasBranch = true;
        m_wasBranchResolved = branchTarget.isResolved();
        if (m_wasBranchResolved) {
			guard.resolve();
            m_guardExpression = guard;
            m_branchTarget = branchTarget.getValue();
            //if(!is16bitAligned(m_branchTarget) || !isWithinMemory(m_branchTarget)){
            if(!is16bitAligned(m_branchTarget)){
            	throw new InstructionExecption(Error.ET_ILLEGAL_PC);
            }
            if (guard.isResolved()) {
                if (guard.getValue() == 0) {
                    m_branchTaken = b ? BranchState.NOTTAKEN : BranchState.TAKEN;
                } else {
                    m_branchTaken = b ? BranchState.TAKEN : BranchState.NOTTAKEN;
                }
            } else {
                m_branchTaken = BranchState.UNKNOWN;
            }
        } else {
            m_branchTaken = BranchState.UNKNOWN;
        }
    }

    void jump(Expression jumpTarget) throws Exception{
		jumpTarget.resolve();
        m_wasJump = true;
        m_branchTargetExpression = jumpTarget;
        m_wasJumpResolved = jumpTarget.isResolved();
        if (m_wasJumpResolved) {
            m_jumpTarget = jumpTarget.getValue();
            //if(!is16bitAligned(m_jumpTarget) || !isWithinMemory(m_jumpTarget)){
            if(!is16bitAligned(m_jumpTarget)){
            	throw new InstructionExecption(Error.ET_ILLEGAL_PC);
            }
            m_jumpTaken = BranchState.TAKEN;
        } else {
            m_jumpTaken = BranchState.UNKNOWN;
        }
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

	void eventWait(Expression expression) throws Exception{
    	m_wasEventWait = true;
    	if(expression.canResolve()){
    		m_wasEventResolved = true;
    		int eventTarget = expression.getValue();
    		//if(!is16bitAligned(eventTarget) || !isWithinMemory(eventTarget)){
    		if(!is16bitAligned(eventTarget)){
            	throw new InstructionExecption(Error.ET_ILLEGAL_PC);
            }
    		m_eventTargets.add(eventTarget);
    		assert(m_eventTargets.size()<3);
    	} else {
    		m_wasEventResolved =false;
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
    
    void resetInstructionState() {
        m_wasBranch = false;
        m_wasBranchResolved = false;
        m_branchTarget = 0;
        m_wasJump = false;
        m_wasJumpResolved = false;
        m_jumpTarget = 0;
        m_wasEventWait = false;
    }

    public Expression getBranchTargetExpression() {
        return m_branchTargetExpression;
    }
    
    public Expression getGuardExpression() {
        return m_guardExpression;
    }

	public boolean wasEventWait() {
		return m_wasEventWait;
	}

	public ArrayList<Integer> getEventTarget() {
		return m_eventTargets;
	}

	public boolean wasEventResolved() {
		return m_wasEventResolved;
	}

	public void clearEventTargets() {
		m_eventTargets.clear();
	}

	public void releaseMemory() {
		m_op1 = null;
		m_op2 = null;
		m_op3 = null;
		m_op4 = null;
		m_op5 = null;
		m_op6 = null;
	    m_branchTargetExpression = null;
	    m_guardExpression = null;
	    m_eventTargets.clear();
	    m_resources = null;
	    m_memory = null;
	}

	

	

	



}
