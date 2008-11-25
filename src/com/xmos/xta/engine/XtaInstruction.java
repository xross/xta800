package com.xmos.xta.engine;

import java.util.ArrayList;

import com.xmos.isa.Instruction;
import com.xmos.xta.engine.resources.Channel;
import com.xmos.xta.engine.resources.Port;
import com.xmos.xta.engine.resources.Resource;
import com.xmos.xta.engine.resources.Resources;
import com.xmos.xta.engine.resources.Synchroniser;
import com.xmos.xta.engine.resources.Thread;
import com.xmos.xta.engine.resources.Timer;
import com.xmos.xta.engine.resources.Thread.StatusRegister;

public abstract class XtaInstruction extends Instruction {
	private static final boolean m_debug = false;
  
	String m_functionName;
    int m_functionOffset;	
    Attributes m_numCycles;
    int m_basicNumCycles;
    Config m_config;
    ArrayList<XtaInstruction> m_returnLocations; 
	
	XtaInstruction(String nmenonic, int opcode, int pc, int numBytes, 
			boolean isLoadStore, boolean canPause) {
		super(nmenonic, opcode, pc, numBytes, isLoadStore, canPause);
		m_functionName = null;
		m_functionOffset = 0;
		m_numCycles = new Attributes();
		m_basicNumCycles = 0;
		m_config = null;
		m_returnLocations = new ArrayList<XtaInstruction>();
	}

	public String toString(){
		return "0x" +Integer.toHexString(getPc()) + " " + getNmenonic() + " " + getOperandString();
	}
	
	public void addReturnLocation(XtaInstruction pc){
	    m_returnLocations.add(pc);
	}
	
	public abstract void execute(MachineState machineState, int thread);
    		   
	public ArrayList<XtaInstruction> getReturnLocations(){
	    return m_returnLocations;
	}
	
	public void setFunctionName(String functionName) {
		m_functionName = functionName;
	}
	
	public String getFunctionName() {
		return m_functionName;
	}
	
	public void setFunctionOffset(int functionOffset) {
		m_functionOffset = functionOffset;
	}
	
	public int getFunctionOffset() {
		return m_functionOffset;
	}
	
	public void setNumCycles(int numCycles) {
		// Makes sure the user cannot configure the instruction to take
		// less time than is physically possible.
		if (numCycles > m_basicNumCycles) {
		    m_numCycles.setNumIterations(numCycles);
		}
		else {
		    m_numCycles.setNumIterations(m_basicNumCycles);
		}
	}

	public int getNumCycles() {
		return m_numCycles.getNumIterations();
	}
	
	public void setTimeTaken(double timeTaken) {
		assert(m_config != null);		
        if (m_config.m_numThreads > 4) {
        	setNumCycles((int)((timeTaken * m_config.m_frequency) / (m_config.m_numThreads * 1000)));
        }
        else {
        	setNumCycles((int)((timeTaken * m_config.m_frequency) / (4 * 1000)));
        }
	}
	
	public double getTimeTaken() {
		assert(m_config != null);
    	double timeTaken = 0.0;    	
        if (m_config.m_numThreads > 4) {
        	timeTaken = ((m_config.m_numThreads * 1000) / m_config.m_frequency) * m_numCycles.getNumIterations();
        }
        else {
        	timeTaken = ((4.0 * 1000) / m_config.m_frequency) * m_numCycles.getNumIterations();
        }
        return timeTaken;
	}
	
	void setConfig(Config config) {
		m_config = config;
	}
	
	void setBasicNumCycles(int basicNumCycles) {
		m_basicNumCycles = basicNumCycles;
		m_numCycles.setNumIterations(basicNumCycles);
	}
	
	static Expression reverseMap(Expression value, int numRanges, int ranges[][], int mapsTo[][]) {
	    assert(value.canResolve());
		return new Expression(Instruction.reverseMap(value.getValue(), numRanges, ranges, mapsTo));
	}
	
	static void setException(MachineState state, Thread thread, int exceptionType) {}
	static long unsignedDivide(MachineState state, long a, long b, Thread thread) { return 0; }
	static long signedDivide(MachineState state, long a, long b, Thread thread) { return 0; }
	static long unsignedRemainder(MachineState state, long a, long b, Thread thread) { return 0; }
	static long signedRemainder(MachineState state, long a, long b, Thread thread) { return 0; }	
	
    void ADD(MachineState state, int thread) {
    	if (m_debug) 
    		System.out.printf("0x%08x: ADD\n", getPc());
    	
    	state.m_op1 = new Expression(Expression.OperatorType.ADD);
    	state.m_op1.addOperand(state.m_op2);
    	state.m_op1.addOperand(state.m_op3);
    }
    
    void AND(MachineState state, int thread) {
    	if (m_debug) 
        	System.out.printf("0x%08x: AND\n", getPc());    
    	
    	state.m_op1 = new Expression(Expression.OperatorType.AND);
    	state.m_op1.addOperand(state.m_op2);
    	state.m_op1.addOperand(state.m_op3);
    }
    
    void ANDNOT(MachineState state, int thread) {
    	if (m_debug) 
        	System.out.printf("0x%08x: ANDNOT\n", getPc());  
    	
    	state.m_op1 = new Expression(Expression.OperatorType.ANDNOT);
    	state.m_op1.addOperand(state.m_op2);
    	state.m_op1.addOperand(state.m_op3);
    }
    
    void ASHR(MachineState state, int thread) {
    	if (m_debug)     	
    	    System.out.printf("0x%08x: ASHR\n", getPc());   
    	
    	state.m_op1 = new Expression(Expression.OperatorType.ASHR);
    	state.m_op1.addOperand(state.m_op2);
    	state.m_op1.addOperand(state.m_op3);
    }
    
    void BAU(MachineState state, int thread) {
    	if (m_debug)     	
    	    System.out.printf("0x%08x: BAU\n", getPc());
    	
    	state.jump(state.m_op1);
    }
    
    void BITREV(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: BITREV\n", getPc());

    	state.m_op1 = new Expression(Expression.OperatorType.BITREV);
    	state.m_op1.addOperand(state.m_op2);
    }

    void BLA(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: BLA\n", getPc());

    	state.setLinkRegister(new Expression(getPc() + getNumBytes()), thread);
    	state.jump(state.m_op1);
    	setBranchLink(true);
    }

    void BLACP(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: BLACP\n", getPc());	
    	
    	Expression branchTarget = new Expression(0, 0, Expression.Type.MEMORY, Expression.Status.DEFINED);
    	branchTarget.addOperand(state.getConstPoolRegister(thread));
    	branchTarget.addOperand(state.m_op1); 	
    	state.setLinkRegister(new Expression(getPc() + getNumBytes()), thread);
    	state.jump(branchTarget);
        setBranchLink(true);    	
    }

    void BLAT(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: BLAT\n", getPc());	

    	Expression branchTarget = new Expression(0, 0, Expression.Type.MEMORY, Expression.Status.DEFINED);
    	branchTarget.addOperand(state.readRegister(11, thread));
    	branchTarget.addOperand(state.m_op1); 	
    	state.jump(branchTarget);
    	state.setLinkRegister(new Expression(getPc() + getNumBytes()), thread);
        setBranchLink(true);
    }

    void BLRB(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: BLRB\n", getPc());
    	
        Expression offset = new Expression(Expression.OperatorType.ASHL);
        offset.addOperand(state.m_op1);
        offset.addOperand(new Expression(1));
        
        Expression branchTarget = new Expression(Expression.OperatorType.SUB);
        branchTarget.addOperand(new Expression(getPc() + getNumBytes()));
        branchTarget.addOperand(offset);
        
    	state.jump(branchTarget);
    	state.setLinkRegister(new Expression(getPc() + getNumBytes()), thread);
        setBranchLink(true);
    }

    void BLRF(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: BLRF\n", getPc());
	
        Expression offset = new Expression(Expression.OperatorType.ASHL);
        offset.addOperand(state.m_op1);
        offset.addOperand(new Expression(1));
        
        Expression branchTarget = new Expression(Expression.OperatorType.ADD);
        branchTarget.addOperand(new Expression(getPc() + getNumBytes()));
        branchTarget.addOperand(offset);
        
    	state.jump(branchTarget);
    	state.setLinkRegister(new Expression(getPc() + getNumBytes()), thread);
        setBranchLink(true);
    }

    void BRBF(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: BRBF\n", getPc());
	
        Expression offset = new Expression(Expression.OperatorType.ASHL);
        offset.addOperand(state.m_op2);
        offset.addOperand(new Expression(1));
        
        Expression branchTarget = new Expression(Expression.OperatorType.SUB);
        branchTarget.addOperand(new Expression(getPc() + getNumBytes()));
        branchTarget.addOperand(offset);
        
    	state.branch(false, state.m_op1, branchTarget);
    }

    void BRBT(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: BRBT\n", getPc());
	
        Expression offset = new Expression(Expression.OperatorType.ASHL);
        offset.addOperand(state.m_op2);
        offset.addOperand(new Expression(1));
        
        Expression branchTarget = new Expression(Expression.OperatorType.SUB);
        branchTarget.addOperand(new Expression(getPc() + getNumBytes()));
        branchTarget.addOperand(offset);
        
    	state.branch(true, state.m_op1, branchTarget);
    }

    void BRBU(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: BRBU\n", getPc());
	
        Expression offset = new Expression(Expression.OperatorType.ASHL);
        offset.addOperand(state.m_op1);
        offset.addOperand(new Expression(1));
        
        Expression branchTarget = new Expression(Expression.OperatorType.SUB);
        branchTarget.addOperand(new Expression(getPc() + getNumBytes()));
        branchTarget.addOperand(offset);
        
    	state.jump(branchTarget);
    }

    void BRFF(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: BRFF\n", getPc());
	
        Expression offset = new Expression(Expression.OperatorType.ASHL);
        offset.addOperand(state.m_op2);
        offset.addOperand(new Expression(1));
        
        Expression branchTarget = new Expression(Expression.OperatorType.ADD);
        branchTarget.addOperand(new Expression(getPc() + getNumBytes()));
        branchTarget.addOperand(offset);
        
    	state.branch(false, state.m_op1, branchTarget);
    }

    void BRFT(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: BRFT\n", getPc());

        Expression offset = new Expression(Expression.OperatorType.ASHL);
        offset.addOperand(state.m_op2);
        offset.addOperand(new Expression(1));
    	
    	Expression branchTarget = new Expression(Expression.OperatorType.ADD);
    	branchTarget.addOperand(new Expression(getPc() + getNumBytes()));
        branchTarget.addOperand(offset);
    	
        state.branch(true, state.m_op1, branchTarget);
    }

    void BRFU(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: BRFU\n", getPc());
	
        Expression offset = new Expression(Expression.OperatorType.ASHL);
        offset.addOperand(state.m_op1);
        offset.addOperand(new Expression(1));
        
        Expression branchTarget = new Expression(Expression.OperatorType.ADD);
        branchTarget.addOperand(new Expression(getPc() + getNumBytes()));
        branchTarget.addOperand(offset);

        state.jump(branchTarget);
    }

    void BRU(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: BRU\n", getPc());
	
        Expression offset = new Expression(Expression.OperatorType.ASHL);
        offset.addOperand(state.m_op1);
        offset.addOperand(new Expression(1));
        
        Expression branchTarget = new Expression(Expression.OperatorType.ADD);
        branchTarget.addOperand(new Expression(getPc() + getNumBytes()));
        branchTarget.addOperand(offset);

    	state.jump(branchTarget);
    }

    void BYTEREV(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: BYTEREV\n", getPc());
	
    	state.m_op1 = new Expression(Expression.OperatorType.BYTEREV);
    	state.m_op1.addOperand(state.m_op2);
    }

    void CHKCT(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: CHKCT\n", getPc());	
    	/*
    	Resources resources = state.getResources();
        Expression resourceId = state.m_op1;
        Expression controlToken = state.m_op2;
        
        if(resourceId.canResolve() && controlToken.canResolve()){
        	int ct = controlToken.getValue();
            int resId = resourceId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
            Channel chan = state.getResources().getChannels()[resNum];
            
        } else {
            System.err.println("could not resolve which resource of CHKCT");
        } 
        */
    }

    void CLRE(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: CLRE\n", getPc());	
    	for(Thread t : state.getResources().getThreads()){
    		t.setStatusRegisterBit(StatusRegister.EEBLE, false);
    	}
    }
    
    void CLRPT(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: CLRPT\n", getPc());	
    	Resources resources = state.getResources();
        Expression resourceId = state.m_op1;
        
        if(resourceId.canResolve()){
            int resId = resourceId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
            if(resType != Resources.RES_TYPE_PORT){
            	System.err.println("CLRPT should be performed on a port resource");
            } else {
            	state.getResources().getPorts()[resNum].setWaitForTime(false);
            }
        } else {
            System.err.println("could not resolve which resource of CLRPT");
        } 
    }

    void CLRSR(MachineState state, int thread) {
    	if (m_debug)     
            System.out.printf("0x%08x: CLRSR\n", getPc());	
    	
    	Thread currentThread = state.getResources().getThread(thread);
    	
    	assert(state.m_op1.canResolve());
    	int imm = state.m_op1.getValue();

    	if(((imm>>0)&1) == 1){
    		currentThread.setStatusRegisterBit(StatusRegister.EEBLE, false);
    	}
    	if(((imm>>1)&1) == 1){
    		currentThread.setStatusRegisterBit(StatusRegister.IEBLE, false);
    	}
    	if(((imm>>2)&1) == 1){
    		currentThread.setStatusRegisterBit(StatusRegister.INENB, false);
    	}
    	if(((imm>>3)&1) == 1){
    		currentThread.setStatusRegisterBit(StatusRegister.ININT, false);
    	}
    	if(((imm>>4)&1) == 1){
    		currentThread.setStatusRegisterBit(StatusRegister.INK, false);
    	}
    	if(((imm>>5)&1) == 1){
    		currentThread.setStatusRegisterBit(StatusRegister.SINK, false);
    	}
    	if(((imm>>6)&1) == 1){
    		currentThread.setStatusRegisterBit(StatusRegister.WAITING, false);
    	}
    	if(((imm>>7)&1) == 1){
    		currentThread.setStatusRegisterBit(StatusRegister.FAST, false);
    	}
    }

    void CLZ(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: CLZ\n", getPc());	

    	state.m_op1 = new Expression(Expression.OperatorType.CLZ);
    	state.m_op1.addOperand(state.m_op2);
    }

    void CRC32(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: CRC32\n", getPc());	

    	Expression result = new Expression(Expression.OperatorType.CRC32);
    	result.addOperand(state.m_op1);
    	result.addOperand(state.m_op2);
    	result.addOperand(state.m_op3);    	
    	state.m_op1 = result;
    }

    void CRC8(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: CRC8\n", getPc());	

    	state.m_op1 = new Expression(Expression.OperatorType.CRC8);
    	state.m_op1.addOperand(state.m_op2);
    	state.m_op1.addOperand(state.m_op3);
    	state.m_op1.addOperand(state.m_op4);
    }

    void DCALL(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: DCALL\n", getPc());	

    	//TODO
    }

    void DENTSP(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: DENTSP\n", getPc());
	
    	//TODO
    }

    void DGETREG(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: DGETREG\n", getPc());	

    	//TODO
    }

    void DIVS(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: DIVS\n", getPc());	
    	state.m_op1 = new Expression(Expression.OperatorType.DIVS);
    	state.m_op1.addOperand(state.m_op2);
    	state.m_op1.addOperand(state.m_op3);
    }

    void DIVU(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: DIVU\n", getPc());	
    	state.m_op1 = new Expression(Expression.OperatorType.DIVU);
    	state.m_op1.addOperand(state.m_op2);
    	state.m_op1.addOperand(state.m_op3);
    }

    void DRESTSP(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: DRESTSP\n", getPc());	

    	//TODO
    }

    void DRET(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: DRET\n", getPc());	

    	//TODO
    }

    void ECALLF(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: ECALLF\n", getPc());	

    	//TODO
    }

    void ECALLT(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: ECALLT\n", getPc());	

    	//TODO
    }

    void EDU(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: EDU\n", getPc());	
    	Resources resources = state.getResources();
        Expression resourceId = state.m_op1;
        
        if(resourceId.canResolve()){
            int resId = resourceId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
            if(	resType == Resources.RES_TYPE_CHANEND ){
            	Channel chan = resources.getChannels()[resNum];
            	chan.setEventEnabeled(false);
            } else if(	resType == Resources.RES_TYPE_PORT ){
            	Port port = resources.getPorts()[resNum];
            	port.setEventEnabeled(false);
            } else if(	resType == Resources.RES_TYPE_TIMER ){
            	Timer timer = resources.getTimers()[resNum];
            	timer.setEventEnabeled(false);
            } else {
            	System.err.println("Trying to perform EDU on an illegal resource");
            }
        } else {
            System.err.println("could not resolve which resource of EDU");
        } 
    }

    void EEF(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: EEF\n", getPc());	
    	Resources resources = state.getResources();
        Expression resourceId = state.m_op2;
        Expression gaurd = state.m_op1;
        if(resourceId.canResolve() && gaurd.canResolve()){
        	boolean set=false;
        	if(gaurd.getValue() == 0){
        		set = true;
        	}
            int resId = resourceId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
            if(	resType == Resources.RES_TYPE_CHANEND ){
            	Channel chan = resources.getChannels()[resNum];
            	chan.setEventEnabeled(set);
            } else if(	resType == Resources.RES_TYPE_PORT ){
            	Port port = resources.getPorts()[resNum];
            	port.setEventEnabeled(set);
            } else if(	resType == Resources.RES_TYPE_TIMER ){
            	Timer timer = resources.getTimers()[resNum];
            	timer.setEventEnabeled(set);
            } else {
            	System.err.println("Trying to perform EEF on an illegal resource");
            }
        } else {
            System.err.println("could not resolve which resource of EEF");
        } 
    }

    void EET(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: EET\n", getPc());	
    	Resources resources = state.getResources();
        Expression resourceId = state.m_op2;
        Expression gaurd = state.m_op1;
        if(resourceId.canResolve() && gaurd.canResolve()){
        	boolean set=false;
        	if(gaurd.getValue() != 0){
        		set = true;
        	}
            int resId = resourceId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
            if(	resType == Resources.RES_TYPE_CHANEND ){
            	Channel chan = resources.getChannels()[resNum];
            	chan.setEventEnabeled(set);
            } else if(	resType == Resources.RES_TYPE_PORT ){
            	Port port = resources.getPorts()[resNum];
            	port.setEventEnabeled(set);
            } else if(	resType == Resources.RES_TYPE_TIMER ){
            	Timer timer = resources.getTimers()[resNum];
            	timer.setEventEnabeled(set);
            } else {
            	System.err.println("Trying to perform EET on an illegal resource");
            }
        } else {
            System.err.println("could not resolve which resource of EET");
        }  
    }

    void EEU(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: EEU\n", getPc());	
    	Resources resources = state.getResources();
        Expression resourceId = state.m_op1;
        
        if(resourceId.canResolve()){
            int resId = resourceId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
            if(	resType == Resources.RES_TYPE_CHANEND ){
            	Channel chan = resources.getChannels()[resNum];
            	chan.setEventEnabeled(true);
            } else if(	resType == Resources.RES_TYPE_PORT ){
            	Port port = resources.getPorts()[resNum];
            	port.setEventEnabeled(true);
            } else if(	resType == Resources.RES_TYPE_TIMER ){
            	Timer timer = resources.getTimers()[resNum];
            	timer.setEventEnabeled(true);
            } else {
            	System.err.printf("%s 0x%8x\n" , "Trying to perform EEU on an illegal resource " , resId);
            }
        } else {
            System.err.println("could not resolve which resource of EEU");
        } 
    }

    void ENDIN(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: ENDIN\n", getPc());	
    	Resources resources = state.getResources();
        Expression resourceId = state.m_op2;
        
        if(resourceId.canResolve()){
            int resId = resourceId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
            //TODO
            state.m_op2 = new Expression(1);
        } else {
            System.err.println("could not resolve which resource of ENDIN");
        } 
    }

    void ENTSP(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: ENTSP\n", getPc());	
    	        	
    	assert state.m_op1.canResolve();    		    		
    	if (state.m_op1.getValue() != 0) {     		
            Expression stackPointer = state.getStackPointerRegister(thread);
            Expression wordOffset = new Expression(state.m_op1.getValue() << Instruction.BYTES_PER_WORD_LOG2);

	        if (stackPointer.canResolve()) {
                // The address is resolvable so we can go ahead and 
	    	    // store the link register expression on the stack.    	        
	            stackPointer.resolve();
                state.writeMemory(stackPointer.getValue(), state.getLinkRegister(thread));   
	        }        
        
            Expression newStackPointer = new Expression(Expression.OperatorType.SUB);
            newStackPointer.addOperand(stackPointer);
            newStackPointer.addOperand(wordOffset);        	

            state.setStackPointerRegister(newStackPointer, thread);
    	}
    }

    void EQ(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: EQ\n", getPc());	

        state.m_op1 = new Expression(Expression.OperatorType.EQ);
        state.m_op1.addOperand(state.m_op2);
        state.m_op1.addOperand(state.m_op3);
    }

    void EXTDP(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: EXTDP\n", getPc());	
    	assert(state.m_op2.canResolve());
    	int imm = state.m_op2.getValue();
    	if(state.m_op1.canResolve()){
    		int value = state.m_op1.getValue() + (imm << Instruction.BITS_PER_WORD*2);
    		state.setDataPoolRegister(new Expression(value), thread);
    	} else {
    		System.err.println("could not resolve EXTDP");
    	}
    }

    void EXTSP(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: EXTSP\n", getPc());	

    	assert state.m_op1.canResolve();    		    		
        Expression stackPointer = state.getStackPointerRegister(thread);
        Expression wordOffset = new Expression(state.m_op1.getValue() << Instruction.BYTES_PER_WORD_LOG2);

        Expression newStackPointer = new Expression(Expression.OperatorType.SUB);
        newStackPointer.addOperand(stackPointer);
        newStackPointer.addOperand(wordOffset);        	

        state.setStackPointerRegister(newStackPointer, thread);
    }

    void FREER(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: FREER\n", getPc());	

    	Resources resources = state.getResources();
    	
    	Expression resourceId = state.m_op1;

    	if(resourceId.canResolve()){
    	    int resId = resourceId.getValue();
    	    int resNum = resources.decodeResNum(resId);
    	    int resType = resources.decodeResType(resId);
    	    if(resType == Resources.RES_TYPE_TIMER){
    	    	resources.getTimers()[resNum].setInUse(false);
    	    } else if(resType == Resources.RES_TYPE_CHANEND){
    	    	resources.getChannels()[resNum].setInUse(false);
    	    } else if(resType == Resources.RES_TYPE_SYNC){
    	    	resources.getSynchronisers()[resNum].setInUse(false);
    	    } else if(resType == Resources.RES_TYPE_THREAD){
    	    	resources.getThreads()[resNum].setInUse(false);
    	    } else if(resType == Resources.RES_TYPE_LOCK){
    	    	resources.getLocks()[resNum].setInUse(false);
    	    } else {
    	    	System.err.println("could not perform FREER on resource " + resType);
    	    }
    	} else {
    	    System.err.println("could not resolve FREER");
    	}
    }

    void FREET(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: FREET\n", getPc());	

    	//TODO
    }

    void GETD(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: GETD\n", getPc());	
    	Resources resources = state.getResources();
        Expression resourceId = state.m_op2;
        
        if(resourceId.canResolve()){
            int resId = resourceId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
            if(resType == Resources.RES_TYPE_TIMER){
    	    	//return the clock value
            	state.m_op1 = new Expression(0);
    	    } else if(resType == Resources.RES_TYPE_CHANEND){
    	    	//return the destionation
    	    	state.m_op1 = new Expression(0);
    	    } else if(resType == Resources.RES_TYPE_PORT){
    	    	//return the data to be matched
    	    	state.m_op1 = new Expression(0);
    	    } else if(resType == Resources.RES_TYPE_CLKBLK){
    	    	//return the divider
    	    	state.m_op1 = new Expression(0);
    	    } else {
    	    	System.err.println("could not perform GETD on resource " + resType);
    	    }
        } else {
            System.err.println("could not resolve which resource in GETD");
        } 
    }

    void GETED(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: GETED\n", getPc());	
    	state.writeRegister(11, state.getResources().getThread(thread).getED(), thread);
    }

    void GETET(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: GETET\n", getPc());	
    	state.writeRegister(11, state.getResources().getThread(thread).getET(), thread);
    }

    void GETID(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: GETID\n", getPc());	
    	state.writeRegister(11, new Expression(state.getResources().getThread(thread).getID()), thread);
    }

    void GETKEP(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: GETKEP\n", getPc());	
    	state.writeRegister(11, state.getResources().getThread(thread).getKEP(), thread);
    }

    void GETKSP(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: GETKSP\n", getPc());	
    	state.writeRegister(11, state.getResources().getThread(thread).getKSP(), thread);
    }

    void GETPS(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: GETPS\n", getPc());	

    	//TODO
    }

    void GETR(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: GETR\n", getPc());	
    	
    	Resources resources = state.getResources();
        Expression resourceId = state.m_op2;
        
        assert(resourceId.canResolve());
        
        int resType = resourceId.getValue();
        Resource res  = resources.getNewResource(resType);
        res.setInUse(true);
        res.setMaster(thread);
        int resourceValue = res.getResourceID();
        state.m_op1 = new Expression(resourceValue);
    }

    void GETSR(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: GETSR\n", getPc());	
    	assert(state.m_op1.canResolve());
    	int status = state.getResources().getThread(thread).getStatusRegister() & state.m_op1.getValue();
    	state.writeRegister(11, new Expression(status), thread);
    }

    void GETST(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: GETST\n", getPc());	
        Expression synchroniserId = state.m_op2;
        if(synchroniserId.canResolve()){
        	Resources resources = state.getResources();
        	int syncType = resources.decodeResType(synchroniserId.getValue());
        	int syncNumber = resources.decodeResNum(synchroniserId.getValue());
        	Synchroniser sync = resources.getSynchroniser(syncType, syncNumber);
        	sync.setInUse(true);
        	
        	//get a new thread
        	Resource res  = resources.getNewResource(Resources.RES_TYPE_THREAD);
        	assert(res!=null);
        	//attach it to the sync
        	sync.attachThread((Thread)res);
        	
        	
        	//return the res id new thread
        	state.m_op1 = new Expression(res.getResourceID());
        	
        	
        } else {
            System.err.println("could not resolve GETST");
        }       
    }

    void GETTS(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: GETTS\n", getPc());	
        Expression synchroniserId = state.m_op2;
        if(synchroniserId.canResolve()){
        	Resources resources = state.getResources();
            int resId = synchroniserId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
          //TODO
        } else {
            System.err.println("could not resolve GETTS");
        } 
    }

    void IN(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: IN\n", getPc());	
    	Resources resources = state.getResources();
        Expression resourceId = state.m_op2;
        
        if(resourceId.canResolve()){
            int resId = resourceId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
          //TODO
        } else {
            System.err.println("could not resolve which resource of IN");
        } 
    }

    void INCT(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: INCT\n", getPc());	
    	Resources resources = state.getResources();
        Expression resourceId = state.m_op2;
        
        if(resourceId.canResolve()){
            int resId = resourceId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
          //TODO
        } else {
            System.err.println("could not resolve which resource of INCT");
        } 
    }

    void INPW(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: INPW\n", getPc());	

    	//TODO
        state.m_op1 = new Expression(0, 0, Expression.Type.REGISTER, Expression.Status.UNDEFINED);
    }

    void INSHR(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: INSHR\n", getPc());	
    	Resources resources = state.getResources();
        Expression synchroniserId = state.m_op2;
        
        if(synchroniserId.canResolve()){
            int resId = synchroniserId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
          //TODO
        } else {
            System.err.println("could not resolve INSHR");
        } 
    }

    void INT(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: INT\n", getPc());	
    	Resources resources = state.getResources();
        Expression resourceId = state.m_op2;
        
        if(resourceId.canResolve()){
            int resId = resourceId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
          //TODO
        } else {
            System.err.println("could not resolve which resource of INT");
        } 
    }

    void KCALL(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: KCALL\n", getPc());	

    	//TODO
    }

    void KENTSP(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: KENTSP\n", getPc());	

    	//TODO
    }

    void KRESTSP(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: KRESTSP\n", getPc());	

    	//TODO
    }

    void KRET(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: KRET\n", getPc());	

    	//TODO
    }

    void LD16S(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LD16S\n", getPc());	

    	//TODO
    }

    void LD8U(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LD8U\n", getPc());	

    	//TODO
    }

    void LDA16B(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDA16B\n", getPc());	

    	//TODO
    }

    void LDA16F(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDA16F\n", getPc());	

    	//TODO
    }

    void LDAPB(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDAPB\n", getPc());	
    	assert(state.m_op2.canResolve());
    	int forward = state.m_op2.getValue();
    	int nextPc = state.getPc(thread) + getNumBytes();
    	state.m_op1 = new Expression(nextPc - (forward<<(Instruction.BYTES_PER_WORD_LOG2/2)));
    }

    void LDAPF(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDAPF\n", getPc());	
    	
    	assert(state.m_op1.canResolve());
    	int forward = state.m_op1.getValue();
    	int nextPc = state.getPc(thread) + getNumBytes();
    	
    	int number = nextPc + (forward<<(Instruction.BYTES_PER_WORD_LOG2/2));
    	state.writeRegister(11, new Expression(number), thread);
    }

    void LDAWB(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDAWB\n", getPc());	

    	//TODO
    }

    void LDAWDP(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDAWDP\n", getPc());	

        Expression rhs = new Expression(Expression.OperatorType.ASHL);              
        rhs.addOperand(state.m_op2);
        rhs.addOperand(new Expression(2));
        
        state.m_op1 = new Expression(Expression.OperatorType.ADD);
        state.m_op1.addOperand(state.getDataPoolRegister(thread));            
        state.m_op1.addOperand(rhs);            
    }

    void LDAWF(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDAWF\n", getPc());	
    	
    	Expression rhs = new Expression(Expression.OperatorType.ASHL);    	    	
    	rhs.addOperand(state.m_op3);
    	rhs.addOperand(new Expression(2));
    	
    	state.m_op1 = new Expression(Expression.OperatorType.ADD);
    	state.m_op1.addOperand(state.m_op2);	    	
    	state.m_op1.addOperand(rhs);	    	
    }

    void LDAWSP(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDAWSP\n", getPc());	
    	
    	assert state.m_op2.canResolve();    		    		
        Expression stackPointer = state.getStackPointerRegister(thread);
        Expression wordOffset = new Expression(state.m_op2.getValue() << Instruction.BYTES_PER_WORD_LOG2);
            
        state.m_op1 = new Expression(Expression.OperatorType.ADD);
        state.m_op1.addOperand(stackPointer);
        state.m_op1.addOperand(wordOffset);   
    }

    void LDC(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDC\n", getPc());
	
    	state.m_op1 = new Expression(state.m_op2);
    }

    void LDET(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDET\n", getPc());	

    	//TODO
    }

    void LDSED(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDSED\n", getPc());	

    	//TODO
    }

    void LDSPC(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDSPC\n", getPc());	

    	//TODO
    }

    void LDSSR(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDSSR\n", getPc());	

    	//TODO
    }

    void LDW(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDW\n", getPc());

    	if (state.m_op2.canResolve() && state.m_op3.canResolve()) {
    	    // The address is resolvable so we can read the expression from 'memory'
    		
    	    state.m_op2.resolve();
            state.m_op3.resolve();
            state.m_op1 = state.readMemory(state.m_op2.getValue() + state.m_op3.getValue());  
    	}
    	else
    	{
    	    // The address is not resolvable so use the memory reference as the expression.    	    
    	    state.m_op1 = new Expression(0, 0, Expression.Type.MEMORY, Expression.Status.DEFINED);
    	    state.m_op1.addOperand(state.m_op2);
    	    state.m_op1.addOperand(state.m_op3);    	
    	}
    }

    void LDWCP(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDWCP\n", getPc());	

    	Expression dp = state.getConstPoolRegister(thread);
    	Expression offset = state.m_op2;
    	if(offset.canResolve() && dp.canResolve()){
    		int address = dp.getValue() + ((offset.getValue())<<2);
    		Expression ex = state.readMemory(address);
    		state.m_op1 = ex; 
    	} else {
    		state.m_op1 = new Expression(0, 0, Expression.Type.MEMORY, Expression.Status.DEFINED);
    	}	
    }

    void LDWCPL(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDWCPL\n", getPc());	
    	Expression dp = state.getConstPoolRegister(thread);
    	Expression offset = state.m_op2;
    	if(offset.canResolve() && dp.canResolve()){
    		int address = dp.getValue() + ((offset.getValue())<<2);
    		Expression ex = state.readMemory(address);
    		state.m_op1 = ex; 
    	} else {
    		Expression ex = new Expression(0, 0, Expression.Type.MEMORY, Expression.Status.DEFINED);
    		state.getResources().getThread(thread).getRegisters().writeRegister(11, ex); 
    	}	
    }

    void LDWDP(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDWDP\n", getPc());		
    	
    	Expression dp = state.getDataPoolRegister(thread);
    	Expression offset = state.m_op2;
    	if(offset.canResolve() && dp.canResolve()){
    		int address = dp.getValue() + ((offset.getValue())<<2);
    		Expression ex = state.readMemory(address);
    		state.m_op1 = ex; 
    	} else {
    		state.m_op1 = new Expression(0, 0, Expression.Type.MEMORY, Expression.Status.DEFINED);
    	}	
    }

    void LDWSP(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDWSP\n", getPc());	
    	
    	assert state.m_op2.canResolve();
        Expression stackPointer = state.getStackPointerRegister(thread);                
        Expression wordOffset = new Expression(state.m_op2.getValue() << Instruction.BYTES_PER_WORD_LOG2);

        if (stackPointer.canResolve()) {
            // The stack pointer address is resolvable so we can load the expression from 'memory'
        	stackPointer.resolve();
        	wordOffset.resolve();
            state.m_op1 = state.readMemory(stackPointer.getValue() + wordOffset.getValue());    	    
        }
        else {
    	    // The stack pointer address is not resolvable so use the memory reference as the expression.    	    
    	    state.m_op1 = new Expression(0, 0, Expression.Type.MEMORY, Expression.Status.DEFINED);
    	    state.m_op1.addOperand(stackPointer);
    	    state.m_op1.addOperand(wordOffset);    	        	
        }
    }

    void LMUL(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LMUL\n", getPc());	

    	//TODO (Just assign an undefined value to the destination registers for now...)    	
    	state.m_op1 = new Expression(state.m_op1Fld, 0, 
			    Expression.Type.REGISTER, Expression.Status.UNDEFINED);    	
    	state.m_op4 = new Expression(state.m_op4Fld, 0, 
			    Expression.Type.REGISTER, Expression.Status.UNDEFINED);    	
    }

    void LSS(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LSS\n", getPc());	

    	state.m_op1 = new Expression(Expression.OperatorType.LT);
    	state.m_op1.addOperand(state.m_op2);
    	state.m_op1.addOperand(state.m_op3);
    }

    void LSU(MachineState state, int thread) {
    	if (m_debug) 
    		System.out.printf("0x%08x: LSU\n", getPc());

    	state.m_op1 = new Expression(Expression.OperatorType.LT);
    	state.m_op1.addOperand(state.m_op2);
    	state.m_op1.addOperand(state.m_op3);
    }

    void MACC(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: MACC\n", getPc());	

        state.m_op1 = new Expression(Expression.OperatorType.MACC_LOW);
        state.m_op1.addOperand(state.m_op2);
        state.m_op1.addOperand(state.m_op3);
        state.m_op1.addOperand(state.m_op5);
        state.m_op1.addOperand(state.m_op6);

        state.m_op4 = new Expression(Expression.OperatorType.MACC_HIGH);
        state.m_op4.addOperand(state.m_op2);
        state.m_op4.addOperand(state.m_op3);
        state.m_op4.addOperand(state.m_op5);
        state.m_op4.addOperand(state.m_op6);
    }

    void MJOIN(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: MJOIN\n", getPc());	

    	Resources resources = state.getResources();
        
        Expression synchroniserId = state.m_op1;
        
        if(synchroniserId.canResolve()){
            int resId = synchroniserId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
          //TODO
        } else {
            System.err.println("could not resolve MJOIN");
        } 
    }

    void MKMSK(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: MKMSK\n", getPc());	

    	//TODO
    }

    void MSYNC(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: MSYNC\n", getPc());	

    	Resources resources = state.getResources();
    	
        Expression synchroniserId = state.m_op1;
        
        if(synchroniserId.canResolve()){
            int resId = synchroniserId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
            Synchroniser sync = resources.getSynchroniser(resType, resNum);
            
            
        } else {
            System.err.println("could not resolve MSYNC");
        } 
    }

    void MUL(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: MUL\n", getPc());	

    	//TODO (Just assign an undefined value to the destination register for now...)    	
    	state.m_op1 = new Expression(state.m_op1Fld, 0, 
			    Expression.Type.REGISTER, Expression.Status.UNDEFINED);    	
    }

    void NEG(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: NEG\n", getPc());
	
    	state.m_op1 = new Expression(Expression.OperatorType.NEG);
    	state.m_op1.addOperand(state.m_op2);
    }

    void NOT(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: NOT\n", getPc());
	
    	state.m_op1 = new Expression(Expression.OperatorType.NOT);
    	state.m_op1.addOperand(state.m_op2);
    }

    void OR(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: OR\n", getPc());
	
    	state.m_op1 = new Expression(Expression.OperatorType.OR);
    	state.m_op1.addOperand(state.m_op2);
    	state.m_op1.addOperand(state.m_op3);
    }

    void OUT(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: OUT\n", getPc());	
    	Resources resources = state.getResources();
        Expression synchroniserId = state.m_op2;
        
        if(synchroniserId.canResolve()){
            int resId = synchroniserId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
          //TODO
        } else {
            System.err.println("could not resolve OUT");
        } 
    }

    void OUTCT(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: OUTCT\n", getPc());	
    	Resources resources = state.getResources();
        Expression synchroniserId = state.m_op1;
        
        if(synchroniserId.canResolve()){
            int resId = synchroniserId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
          //TODO
        } else {
            System.err.println("could not resolve OUTCT");
        } 
    }

    void OUTPW(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: OUTPW\n", getPc());	

    	//TODO
    }

    void OUTSHR(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: OUTSHR\n", getPc());	
    	Resources resources = state.getResources();
        Expression synchroniserId = state.m_op2;
        
        if(synchroniserId.canResolve()){
            int resId = synchroniserId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
          //TODO
        } else {
            System.err.println("could not resolve OUTSHR");
        } 
    }

    void OUTT(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: OUTT\n", getPc());	
    	Resources resources = state.getResources();
        Expression synchroniserId = state.m_op2;
        
        if(synchroniserId.canResolve()){
            int resId = synchroniserId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
          //TODO
        } else {
            System.err.println("could not resolve OUTT");
        } 
    }

    void PEEK(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: PEEK\n", getPc());	
    	Resources resources = state.getResources();
        Expression synchroniserId = state.m_op2;
        
        if(synchroniserId.canResolve()){
            int resId = synchroniserId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
          //TODO
        } else {
            System.err.println("could not resolve PEEK");
        } 
    }

    void REMS(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: REMS\n", getPc());	

    	//TODO
    }

    void REMU(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: REMU\n", getPc());	

    	//TODO
    }

    void RETSP(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: RETSP\n", getPc());	

        Expression jumpTarget = state.getLinkRegister(thread);                
        
    	assert state.m_op1.canResolve();
    	if (state.m_op1.getValue() != 0) {    	    
            Expression stackPointer = state.getStackPointerRegister(thread);                
    	    if (stackPointer.canResolve()) {
                // The address is resolvable so we can read the expression from 'memory'
    	        stackPointer.resolve();
                int newStackPointerValue = stackPointer.getValue() + 
                    (state.m_op1.getValue() << Instruction.BYTES_PER_WORD_LOG2);
            
                state.setStackPointerRegister(new Expression(newStackPointerValue), thread);                
    	        
    	        jumpTarget = state.readMemory(newStackPointerValue);
                state.setLinkRegister(jumpTarget, thread);                
    	    }
    	    else {
                // The address is not resolvable so this path has hit a dead end..         
    	        jumpTarget = new Expression(0, 0, Expression.Type.MEMORY, Expression.Status.DEFINED);
    	        jumpTarget.addOperand(stackPointer);
    	        jumpTarget.addOperand(state.m_op1);            	    
    	    }    	        	    
    	} else {
    	    // Not actually do a load
    	    setIsLoadStore(false);
    	}
    	
    	
        state.jump(jumpTarget);
        setReturn(true);
    }

    void SETC(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SETC\n", getPc());	
    	Resources resources = state.getResources();
        Expression resourceId = state.m_op1;
        
        if(resourceId.canResolve()){
            int resId = resourceId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
          //TODO
        } else {
            System.err.println("could not resolve which resource in SETC");
        } 
    }

    void SETCLK(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SETCLK\n", getPc());	
    	Resources resources = state.getResources();
        Expression resourceId = state.m_op2;
        
        if(resourceId.canResolve()){
            int resId = resourceId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
          //TODO
        } else {
            System.err.println("could not resolve which resource in SETCLK");
        } 
    }

    void SETCP(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SETCP\n", getPc());	

    	//TODO
    }

    void SETD(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SETD\n", getPc());	
    	Resources resources = state.getResources();
        Expression resourceId = state.m_op2;
        
        if(resourceId.canResolve()){
            int resId = resourceId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
          //TODO
        } else {
            System.err.println("could not resolve which resource of SETD");
        } 
    }

    void SETDP(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SETDP\n", getPc());	

    	//TODO
    }

    void SETEV(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SETEV\n", getPc());	
    	Resources resources = state.getResources();
        Expression resourceId = state.m_op1;
        
        if(resourceId.canResolve()){
            int resId = resourceId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
          //TODO
        } else {
            System.err.println("could not resolve which resource of SETEV");
        }     
    }

    void SETKEP(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SETKEP\n", getPc());	

    	//TODO
    }

    void SETPS(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SETPS\n", getPc());	

    	//TODO
    }

    void SETPSC(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SETPSC\n", getPc());	
    	Resources resources = state.getResources();
        Expression resourceId = state.m_op2;
        
        if(resourceId.canResolve()){
            int resId = resourceId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
          //TODO
        } else {
            System.err.println("could not resolve which resource of SETPSC");
        } 
    }

    void SETPT(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SETPT\n", getPc());	
    	Resources resources = state.getResources();
        Expression resourceId = state.m_op2;
        
        if(resourceId.canResolve()){
            int resId = resourceId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
          //TODO
        } else {
            System.err.println("could not resolve which resource in SETPT");
        } 
    }

    void SETRDY(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SETRDY\n", getPc());	
    	Resources resources = state.getResources();
        Expression resourceId = state.m_op2;
        
        if(resourceId.canResolve()){
            int resId = resourceId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
          //TODO
        } else {
            System.err.println("could not resolve which resource in SETRDY");
        } 
    }

    void SETSP(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SETSP\n", getPc());	

    	//TODO
    }

    void SETSR(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SETSR\n", getPc());	

    	//TODO
    }

    void SETTW(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SETTW\n", getPc());	
    	Resources resources = state.getResources();
        assert(resources!=null);
    	
    	Expression resourceId = state.m_op2;
        
        if(resourceId.canResolve()){
            int resId = resourceId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
          //TODO
        } else {
            System.err.println("could not resolve which resource of SETTW");
        } 
    }

    void SETV(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SETV\n", getPc());	
    	Resources resources = state.getResources();
        Expression resourceId = state.m_op1;
        
        if(resourceId.canResolve()){
            int resId = resourceId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
            Expression reg11 = state.readRegister(11, thread);
          if(resType == Resources.RES_TYPE_CHANEND){
        	  state.getResources().getChannels()[resNum].setEventVector(reg11);
          } else if(resType == Resources.RES_TYPE_PORT){
        	  state.getResources().getPorts()[resNum].setEventVector(reg11);
          } else if(resType == Resources.RES_TYPE_TIMER){
        	  state.getResources().getTimers()[resNum].setEventVector(reg11);
          } else {
        	  System.out.println("cannot SETV on a resoutce type: " + resType);
          }
        } else {
            System.err.println("could not resolve which resource of SETV");
        }     
    	
    }

    void SEXT(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SEXT\n", getPc());	

    	//TODO
    }

    void SHL(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SHL\n", getPc());	

    	//TODO
    }

    void SHR(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SHR\n", getPc());	

    	//TODO
    }

    void SSYNC(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SSYNC\n", getPc());	

    	//TODO
    }

    void ST16(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: ST16\n", getPc());	

    	//TODO
    }

    void ST8(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: ST8\n", getPc());	

    	//TODO
    }

    void STSED(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: STSED\n", getPc());	

    	//TODO
    }

    void STET(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: STET\n", getPc());	

    	//TODO
    }

    void STSPC(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: STSPC\n", getPc());	

    	//TODO
    }

    void STSSR(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: STSSR\n", getPc());	

    	//TODO
    }

    void STW(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: STW\n", getPc());	
    	
        if (state.m_op2.canResolve() && state.m_op3.canResolve()) {
            // The address is resolvable so we can write the expression from 'memory'
            state.m_op2.resolve();
            state.m_op3.resolve();
            int valueToStore = state.m_op2.getValue() + state.m_op3.getValue();
            
            state.writeMemory(valueToStore, state.m_op1);            
        }
    }

    void STWDP(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: STWDP\n", getPc());	

    	//TODO
    }

    void STWSP(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: STWSP\n", getPc());	

    	assert state.m_op2.canResolve();
        Expression stackPointer = state.getStackPointerRegister(thread);                
        Expression wordOffset = new Expression(state.m_op2.getValue() << Instruction.BYTES_PER_WORD_LOG2);

        if (stackPointer.canResolve()) {
            // The stack pointer address is resolvable so we can store the expression to 'memory'
        	stackPointer.resolve();
        	wordOffset.resolve();
        	state.writeMemory(stackPointer.getValue() + wordOffset.getValue(), state.m_op1);
        }
    }

    void SUB(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SUB\n", getPc());
	
    	state.m_op1 = new Expression(Expression.OperatorType.SUB);
    	state.m_op1.addOperand(state.m_op2);
    	state.m_op1.addOperand(state.m_op3);
    }

    void SYNCR(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SYNCR\n", getPc());	

    	//TODO
    }

    void TESTLCL(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: TESTLCL\n", getPc());	
    	Resources resources = state.getResources();
        Expression resourceId = state.m_op2;
        
        if(resourceId.canResolve()){
            int resId = resourceId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
          //TODO
        } else {
            System.err.println("could not resolve which resource of TESTLCL");
        } 
    }

    void TESTCT(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: TESTCT\n", getPc());	
    	Resources resources = state.getResources();
        Expression resourceId = state.m_op2;
        
        if(resourceId.canResolve()){
            int resId = resourceId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
          //TODO
        } else {
            System.err.println("could not resolve which resource of TESTCT");
        } 
    }

    void TESTWCT(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: TESTWCT\n", getPc());	
    	Resources resources = state.getResources();
        Expression resourceId = state.m_op2;
        
        if(resourceId.canResolve()){
            int resId = resourceId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
          //TODO
        } else {
            System.err.println("could not resolve which resource of TESTWCT");
        } 
    }

    void TINITCP(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: TINITCP\n", getPc());	
    	Resources resources = state.getResources();
        Expression resourceId = state.m_op2;
        
        if(resourceId.canResolve()){
            int resId = resourceId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
            Thread child = resources.getThread(resType, resNum);
           
            child.getRegisters().writeRegister(MachineState.CP_REG_NUM, state.m_op1);
        } else {
            System.err.println("could not resolve which resource of TINITLR");
        } 
    }

    void TINITDP(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: TINITDP\n", getPc());	
    	Resources resources = state.getResources();
        Expression resourceId = state.m_op2;
        
        if(resourceId.canResolve()){
            int resId = resourceId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
            Thread child = resources.getThread(resType, resNum);
            
            child.getRegisters().writeRegister(MachineState.DP_REG_NUM, state.m_op1);
        } else {
            System.err.println("could not resolve which resource of TINITLR");
        } 
    }

    void TINITLR(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: TINITLR\n", getPc());	
    	Resources resources = state.getResources();
        Expression resourceId = state.m_op2;
        
        if(resourceId.canResolve()){
            int resId = resourceId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
            Thread child = resources.getThread(resType, resNum);
            child.getRegisters().writeRegister(MachineState.LR_REG_NUM, state.m_op1);
        } else {
            System.err.println("could not resolve which resource of TINITLR");
        } 
    }

    void TINITPC(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: TINITPC\n", getPc());	
    	Resources resources = state.getResources();
        Expression resourceId = state.m_op2;
        
        if(resourceId.canResolve()){
            int resId = resourceId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
            Thread child = resources.getThread(resType, resNum);
            if(state.m_op1.canResolve()){
            	child.setPc(state.m_op1.getValue());
            } else {
            	System.err.println("could not resolve pc to set thread pc to");
            }
          //TODO
           // resources.getThread(resType, resNum).TINITPC(state.m_op1);
        } else {
            System.err.println("could not resolve which resource of TINITPC");
        } 
    }

    void TINITSP(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: TINITSP\n", getPc());	
    	Resources resources = state.getResources();
        Expression resourceId = state.m_op2;
        
        if(resourceId.canResolve()){
            int resId = resourceId.getValue();
            printResId(resId);
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
            Thread child = resources.getThread(resType, resNum);
            child.getRegisters().writeRegister(MachineState.SP_REG_NUM, state.m_op1);
        } else {
            System.err.println("could not resolve which resource of TINITLR");
        } 
    }

    private void printResId(int resId) {
		System.out.println("Type: " + (resId &0xf) + " Number: " + ((resId>>8)&0xf));
		
	}

	void TSETMR(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: TSETMR\n", getPc());	
    	
    	//TODO
    }

    void TSETR(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: TSETR\n", getPc());	
    	Resources resources = state.getResources();
        Expression resourceId = state.m_op3;
        
        if(resourceId.canResolve()){
            int resId = resourceId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
            
            Thread child = resources.getThread(resType, resNum);
            child.getRegisters().writeRegister(state.m_op1Fld, state.m_op2);
        } else {
            System.err.println("could not resolve which resource of TSETR");
        } 
    }

    void TSTART(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: TSTART\n", getPc());	
    	Resources resources = state.getResources();
        Expression resourceId = state.m_op1;
        
        if(resourceId.canResolve()){
            int resId = resourceId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
            //resources.getThread(resType, resNum).TSTART();
        } else {
            System.err.println("could not resolve which resource of TSTART");
        } 
    }

    void WAITEF(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: WAITEF\n", getPc());	
    	
    	
    	
    	
    }

    void WAITET(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: WAITET\n", getPc());	

    	//TODO
    }

    void WAITEU(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: WAITEU\n", getPc());	
    	
    	state.clearEventVectors();
    	for(Timer timer : state.getResources().getTimers()){
    		if(timer.isEventEnabeled()){
    			state.eventWait(timer.getEventVector());
    		}
    	}
		for (Port port : state.getResources().getPorts()) {
			if(port.isEventEnabeled()){
    			state.eventWait(port.getEventVector());
    		}
		}
		for (Channel channel : state.getResources().getChannels()) {
			if(channel.isEventEnabeled()){
    			state.eventWait(channel.getEventVector());
    		}
		}
    }

    void XOR(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: XOR\n", getPc());	

    	//TODO
    }

    void ZEXT(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: ZEXT\n", getPc());	

    	//TODO
    }
    
    void GETN(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: GETN\n", getPc());	

    	//TODO
    }
    
    void SETN(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SETN\n", getPc());	

    	//TODO
    }
        
    void LADD(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LADD\n", getPc());	

    	//TODO
    }
    
    void LDAWCP(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDAWCP\n", getPc());	

    	//TODO
    }
    
    void MACCU(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: MACCU\n", getPc());	

    	//TODO
    }
    
    void MACCS(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: MACCS\n", getPc());	

    	//TODO
    }    
    
    void LDIVU(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDIVU\n", getPc());	

    	//TODO
    }
    
    void LSUB(MachineState state, int thread) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LSUB\n", getPc());	

    	//TODO
    }
     
}

class NullInstruction extends XtaInstruction {    
    NullInstruction(String nmenonic, int opcode, int pc, int numBytes) {
        super(nmenonic, opcode, pc, numBytes, false, false);
        setIsFnop(true);
    }
    
    public void execute(MachineState state, int thread) {
    }
    
    public String getOperandString() {
    	return "";
    }
}


