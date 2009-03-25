package com.xmos.xta.engine;

import java.util.ArrayList;
import com.xmos.isa.Instruction;
import com.xmos.xeloader.XeLoader.Architecture;
import com.xmos.xta.engine.Expression.OperatorType;
import com.xmos.xta.engine.InstructionExecption.Error;
import com.xmos.xta.engine.resources.Channel;
import com.xmos.xta.engine.resources.EventableResource;
import com.xmos.xta.engine.resources.Port;
import com.xmos.xta.engine.resources.Resource;
import com.xmos.xta.engine.resources.Resources;
import com.xmos.xta.engine.resources.Synchroniser;
import com.xmos.xta.engine.resources.Thread;
import com.xmos.xta.engine.resources.Timer;
import com.xmos.xta.engine.resources.Thread.StatusRegister;

public abstract class XtaInstruction extends Instruction {
	private static final boolean INSTRUCTIONDEBUG = false;
	private static final boolean RESOURCEDEBUG  = false;
	private static final boolean m_uninplimented = false;
  
	private ArrayList<NextPcType> m_successors;
	private ArrayList<XtaInstruction> m_predessors;
	
	private String m_functionName;
    private int m_functionOffset;	
    private Attributes m_numCycles;
    private int m_basicNumCycles;
    private Config m_config;
  
    private boolean m_definesResource;
	private boolean m_usesResource;
	
	private boolean m_nextInstructionPcKnown;
	private int m_nextInstructionPc;
    
	private boolean m_isUnknown;
	private int m_basicBlockId;
	
	XtaInstruction(String nmenonic, Config config, int opcode, int pc, int numBytes, 
			boolean isLoadStore, boolean canPause, boolean usesResource, 
		    boolean definesResource, int numCycles) {
		super(nmenonic, opcode, pc, numBytes, isLoadStore, canPause);
		m_definesResource = definesResource;
		m_usesResource = usesResource;
		m_functionName = null;
		m_functionOffset = 0;
		m_numCycles = new Attributes();
		m_basicNumCycles = numCycles;
		m_numCycles.setNumIterations(numCycles);
		m_config = config;
		m_successors = new ArrayList<NextPcType>();
		m_predessors =  new ArrayList<XtaInstruction>();
		m_isUnknown = canPause;
		m_basicBlockId = 0;
	}

	public ArrayList<XtaInstruction> getPredessors(){
		return m_predessors;
	}
	
	public void addPredessor(XtaInstruction inst){
		m_predessors.add(inst);
	}
	
	public boolean isNextInstructionPcKnown(){
		return m_nextInstructionPcKnown;
	}
	
	public int getNextInstructionPc(){
		return m_nextInstructionPc;
	}
	
	public ArrayList<NextPcType> getSuccessors(){
		return m_successors;
	}
	public int successorPcCount(int pc){
		int count =0;
		for(NextPcType suc : m_successors){
			if(suc.getPc() == pc) count++;
		}
		return count;
	}
	
	public void addSuccessor(XtaInstruction successor, boolean isBranch){
		if(successor == null) return; 
		int foundBranch = 0;
		int foundNoBranch = 0;
		for(NextPcType pc : m_successors){
			if(pc.getInstruction().getPc() == successor.getPc() ){
				if(pc.isBranch()){
					foundBranch++;
				} else {
					foundNoBranch++;
				}
			}
		}
		if(foundBranch + foundNoBranch == 0) {
			m_successors.add(new NextPcType(successor, isBranch));
		} else if(isBranch ){
			if(foundBranch == 0){
				m_successors.add(new NextPcType(successor, isBranch));
			}
		} else if(!isBranch){
			if(foundNoBranch == 0){
				m_successors.add(new NextPcType(successor, isBranch));
			}
		}
		return;
	}
	
	public boolean getDefinesResource(){
		return m_definesResource;
	}
	
	public boolean getUsesResource(){
		return m_usesResource;
	}
	
	public String toString(){
		return "0x" + Integer.toHexString(getPc()) + " " + getNmenonic() + " " + getOperandString();
	}
	
	public abstract void execute(MachineState machineState, int thread) throws Exception;
	public abstract int getOperandField(String opName);    		   
	
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
	
	public void setNumCycles(long numCycles) {
		// Makes sure the user cannot configure the instruction to take
		// less time than is physically possible.
		if (numCycles > m_basicNumCycles) {
		    m_numCycles.setNumIterations(numCycles);
		}
		else {
		    m_numCycles.setNumIterations(m_basicNumCycles);
		}
		m_isUnknown = false;
	}
	
	public long getNumCycles() {
		return m_numCycles.getNumIterations();
	}

	public void resetNumCycles() {
	    m_numCycles.setNumIterations(m_basicNumCycles);
	    m_isUnknown = true;
	}
	
	public void setTimeTaken(double timeTaken) {
    	int threadFactor = (m_config.m_numThreads > 4) ? m_config.m_numThreads : 4;		
       	setNumCycles((int)((timeTaken * m_config.m_frequency) / (threadFactor * 1000)));
	}
	
	public double getTimeTaken() {
    	int threadFactor = (m_config.m_numThreads > 4) ? m_config.m_numThreads : 4;
        return ((double)(threadFactor * 1000) / (double)m_config.m_frequency) * m_numCycles.getNumIterations();
	}
	
	public boolean isUnknown() {
		return m_isUnknown;
	}
	
	public void setBasicBlockId(int basicBlockId) {
		m_basicBlockId = basicBlockId;
	}
	
	public int getBasicBlockId() {
		return m_basicBlockId;
	}
	
	void setConfig(Config config) {
		m_config = config;
	}

	static Expression reverseMap(Expression value, int numRanges, int ranges[][], int mapsTo[][]) throws Exception {
	    assert(value.canResolve());
		return new Expression(Instruction.reverseMap(value.getValue(), numRanges, ranges, mapsTo));
	}
	
	static void setException(MachineState state, Thread thread, int exceptionType) {}
	static long unsignedDivide(MachineState state, long a, long b, Thread thread) { return 0; }
	static long signedDivide(MachineState state, long a, long b, Thread thread) { return 0; }
	static long unsignedRemainder(MachineState state, long a, long b, Thread thread) { return 0; }
	static long signedRemainder(MachineState state, long a, long b, Thread thread) { return 0; }	
	
    void ADD(MachineState state, int thread) throws Exception {
		if (INSTRUCTIONDEBUG)
			System.out.printf("0x%08x: ADD\n", getPc());
		state.m_op1 = new Expression(Expression.OperatorType.ADD);
    	state.m_op1.addOperand(state.m_op2);
    	state.m_op1.addOperand(state.m_op3);
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    	state.m_op1.resolve();
	}
    
    void AND(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG) 
        	System.out.printf("0x%08x: AND\n", getPc());    
    	state.m_op1 = new Expression(Expression.OperatorType.AND);
    	state.m_op1.addOperand(state.m_op2);
    	state.m_op1.addOperand(state.m_op3);
    	state.m_op1.resolve();
    	
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }
    
    void ANDNOT(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG) 
        	System.out.printf("0x%08x: ANDNOT\n", getPc());  
    	Expression result = new Expression(Expression.OperatorType.ANDNOT);
    	result.addOperand(state.m_op1);
    	result.addOperand(state.m_op2);
    	state.m_op1 = result;
    	state.m_op1.resolve();
    	
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }
    
    void ASHR(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
    	    System.out.printf("0x%08x: ASHR\n", getPc());   
    	Expression result = new Expression(Expression.OperatorType.ASHR);
    	result.addOperand(state.m_op2);
    	result.addOperand(state.m_op3);
    	state.m_op1 = result;
    	state.m_op1.resolve();
    	
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }
    
    void BAU(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
    	    System.out.printf("0x%08x: BAU\n", getPc());
    	state.m_op1.resolve();
    	
    	if(state.m_op1.canResolve()){
    		m_nextInstructionPc = state.m_op1.getValue();
    		m_nextInstructionPcKnown = true;
    	} else {
    		m_nextInstructionPcKnown = false;
    	}
    	//state.setBranched(true);
    	state.setBranchProperties(true, false, null, state.m_op1, false);
    }
    
    void BITREV(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: BITREV\n", getPc());
    	state.m_op1 = new Expression(Expression.OperatorType.BITREV);
    	state.m_op1.addOperand(state.m_op2);
    	state.m_op1.resolve();
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    }

    void BLA(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: BLA\n", getPc());
    	state.setLinkRegister(new Expression(getPc() + getNumBytes()), thread);
    	
    	state.getLinkRegister(thread).resolve();
    	state.setBranched(true);
    	//state.jump(state.m_op1);
    	if(state.m_op1.canResolve()){
    		m_nextInstructionPc = state.m_op1.getValue();
    		m_nextInstructionPcKnown = true;
    	} else {
    		m_nextInstructionPcKnown = false;
    	}
    	setBranchLink(true);
    }

    void BLACP(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: BLACP\n", getPc());	
    	Expression loadLocation = new Expression(OperatorType.ADD);
    	Expression cp = state.getConstPoolRegister(thread);
    	
    	Expression offset = new Expression(OperatorType.SHL);
    	offset.addOperand(state.m_op1);
    	offset.addOperand(new Expression(Instruction.NUM_BYTES_PER_WORD_LOG2));
    	
    	loadLocation.addOperand(cp);
    	loadLocation.addOperand(offset);
    	state.setBranched(true);
    	
    	if(loadLocation.canResolve()){
    		Expression branchTarget = state.readMemory(loadLocation.getValue());
    		branchTarget.resolve();
    		
    		if(branchTarget.canResolve()){
    			m_nextInstructionPc = branchTarget.getValue();
        		m_nextInstructionPcKnown = true;
    		} else {
    			m_nextInstructionPcKnown = false;
    		}
    		
    		//state.jump(branchTarget);
    		state.setLinkRegister(new Expression(getPc() + getNumBytes()), thread);
    		state.getLinkRegister(thread).resolve();
    		setBranchLink(true);    
    	} else {
    		m_nextInstructionPcKnown = false;
    	}	
    }

    void BLAT(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: BLAT\n", getPc());	
    	Expression loadLocation = new Expression(OperatorType.ADD);
    	Expression table = state.readRegister(11, thread);
    	
    	Expression offset = new Expression(OperatorType.SHL);
    	offset.addOperand(state.m_op1);
    	offset.addOperand(new Expression(Instruction.NUM_BYTES_PER_WORD_LOG2));
    	
    	loadLocation.addOperand(table);
    	loadLocation.addOperand(offset);
    	state.setBranched(true);
    	
    	if(loadLocation.canResolve()){
    		Expression branchTarget = state.readMemory(loadLocation.getValue());
    		branchTarget.resolve();
    		
    		if(branchTarget.canResolve()){
    			m_nextInstructionPc = branchTarget.getValue();
    			m_nextInstructionPcKnown = true;
    		} else {
    			m_nextInstructionPcKnown = false;
    		}
    		
    		//state.jump(branchTarget);
    		state.setLinkRegister(new Expression(getPc() + getNumBytes()), thread);
    		state.getLinkRegister(thread).resolve();
    		setBranchLink(true);    
    	} else {
    		m_nextInstructionPcKnown = false;
    	}
    }

    void BLRB(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: BLRB\n", getPc());
    	
        Expression offset = new Expression(Expression.OperatorType.ASHL);
        offset.addOperand(state.m_op1);
        offset.addOperand(new Expression(1));
    	state.setBranched(true);
        
        Expression branchTarget = new Expression(Expression.OperatorType.SUB);
        branchTarget.addOperand(new Expression(getPc() + getNumBytes()));
        branchTarget.addOperand(offset);
        branchTarget.resolve();
        
        if(branchTarget.canResolve()){
			m_nextInstructionPc = branchTarget.getValue();
			m_nextInstructionPcKnown = true;
		} else {
			m_nextInstructionPcKnown = false;
		}
        
    	//state.jump(branchTarget);
    	state.setLinkRegister(new Expression(getPc() + getNumBytes()), thread);
    	state.getLinkRegister(thread).resolve();
        setBranchLink(true);
    }

    
    void BLRF(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: BLRF\n", getPc());
	
        Expression offset = new Expression(Expression.OperatorType.ASHL);
        offset.addOperand(state.m_op1);
        offset.addOperand(new Expression(1));
    	state.setBranched(true);
        
        Expression branchTarget = new Expression(Expression.OperatorType.ADD);
        branchTarget.addOperand(new Expression(getPc() + getNumBytes()));
        branchTarget.addOperand(offset);
        branchTarget.resolve();
        
        if(branchTarget.canResolve()){
			m_nextInstructionPc = branchTarget.getValue();
			m_nextInstructionPcKnown = true;
		} else {
			m_nextInstructionPcKnown = false;
		}
        
    	//state.jump(branchTarget);
    	state.setLinkRegister(new Expression(getPc() + getNumBytes()), thread);
    	state.getLinkRegister(thread).resolve();
        setBranchLink(true);
    }

    void BRBF(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: BRBF\n", getPc());
	
        Expression offset = new Expression(Expression.OperatorType.ASHL);
        offset.addOperand(state.m_op2);
        offset.addOperand(new Expression(1));
    	state.setBranched(true);
        
        Expression branchTarget = new Expression(Expression.OperatorType.SUB);
        branchTarget.addOperand(new Expression(getPc() + getNumBytes()));
        branchTarget.addOperand(offset);
        branchTarget.resolve();
        
        if(state.m_op1.canResolve() && branchTarget.canResolve()){
        	if(state.m_op1.getValue() == 0 ){
        		m_nextInstructionPc = branchTarget.getValue();
        	} else {
        		m_nextInstructionPc = getPc() + getNumBytes();
        	}
        	m_nextInstructionPcKnown = true;
        } else {
        	m_nextInstructionPcKnown = false;
        }
    	//state.branch(false, state.m_op1, branchTarget, this);
    }

    void BRBT(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: BRBT\n", getPc());
	
        Expression offset = new Expression(Expression.OperatorType.ASHL);
        offset.addOperand(state.m_op2);
        offset.addOperand(new Expression(1));
    	state.setBranched(true);
        
        Expression branchTarget = new Expression(Expression.OperatorType.SUB);
        branchTarget.addOperand(new Expression(getPc() + getNumBytes()));
        branchTarget.addOperand(offset);
        branchTarget.resolve();
        if(state.m_op1.canResolve() && branchTarget.canResolve()){
        	if(state.m_op1.getValue() != 0 ){
        		m_nextInstructionPc = branchTarget.getValue();
        	} else {
        		m_nextInstructionPc = getPc() + getNumBytes();
        	}
        	m_nextInstructionPcKnown = true;
        } else {
        	m_nextInstructionPcKnown = false;
        }
        
    	//state.branch(true, state.m_op1, branchTarget, this);
    }

    void BRBU(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: BRBU\n", getPc());
	
        Expression offset = new Expression(Expression.OperatorType.ASHL);
        offset.addOperand(state.m_op1);
        offset.addOperand(new Expression(1));

    	state.setBranched(true);
        Expression branchTarget = new Expression(Expression.OperatorType.SUB);
        branchTarget.addOperand(new Expression(getPc() + getNumBytes()));
        branchTarget.addOperand(offset);
        branchTarget.resolve();
        if( branchTarget.canResolve()){
        	m_nextInstructionPc = branchTarget.getValue();
        	m_nextInstructionPcKnown = true;
        } else {
        	m_nextInstructionPcKnown = false;
        }
    	//state.jump(branchTarget);
    }

    void BRFF(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: BRFF\n", getPc());
	
        Expression offset = new Expression(Expression.OperatorType.ASHL);
        offset.addOperand(state.m_op2);
        offset.addOperand(new Expression(1));
    	state.setBranched(true);
        
        Expression branchTarget = new Expression(Expression.OperatorType.ADD);
        branchTarget.addOperand(new Expression(getPc() + getNumBytes()));
        branchTarget.addOperand(offset);
        branchTarget.resolve();
        if(state.m_op1.canResolve() && branchTarget.canResolve()){
        	if(state.m_op1.getValue() == 0 ){
        		m_nextInstructionPc = branchTarget.getValue();
        	} else {
        		m_nextInstructionPc = getPc() + getNumBytes();
        	}
        	m_nextInstructionPcKnown = true;
        } else {
        	m_nextInstructionPcKnown = false;
        }
        //state.branch(false, state.m_op1, branchTarget, this);
    }

    void BRFT(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: BRFT\n", getPc());

        Expression offset = new Expression(Expression.OperatorType.ASHL);
        offset.addOperand(state.m_op2);
        offset.addOperand(new Expression(1));
    	state.setBranched(true);
    	
    	Expression branchTarget = new Expression(Expression.OperatorType.ADD);
    	branchTarget.addOperand(new Expression(getPc() + getNumBytes()));
        branchTarget.addOperand(offset);
    	branchTarget.resolve();
    	if(state.m_op1.canResolve() && branchTarget.canResolve()){
        	if(state.m_op1.getValue() != 0 ){
        		m_nextInstructionPc = branchTarget.getValue();
        	} else {
        		m_nextInstructionPc = getPc() + getNumBytes();
        	}
        	m_nextInstructionPcKnown = true;
        } else {
        	m_nextInstructionPcKnown = false;
        }
    	//state.branch(true, state.m_op1, branchTarget, this);
    }

    void BRFU(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: BRFU\n", getPc());
	
        Expression offset = new Expression(Expression.OperatorType.ASHL);
        offset.addOperand(state.m_op1);
        offset.addOperand(new Expression(1));
    	state.setBranched(true);
        
        Expression branchTarget = new Expression(Expression.OperatorType.ADD);
        branchTarget.addOperand(new Expression(getPc() + getNumBytes()));
        branchTarget.addOperand(offset);
        branchTarget.resolve();
        if(branchTarget.canResolve()){
            m_nextInstructionPc = branchTarget.getValue();
        	m_nextInstructionPcKnown = true;
        } else {
        	m_nextInstructionPcKnown = false;
        }
    }

    void BRU(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: BRU\n", getPc());
	
        Expression offset = new Expression(Expression.OperatorType.ASHL);
        offset.addOperand(state.m_op1);
        offset.addOperand(new Expression(Instruction.NUM_BYTES_PER_WORD_LOG2/2));
    	state.setBranched(true);
        
        Expression branchTarget = new Expression(Expression.OperatorType.ADD);
        branchTarget.addOperand(new Expression(getPc() + getNumBytes()));
        branchTarget.addOperand(offset);
        branchTarget.resolve();
        if(branchTarget.canResolve()){
            m_nextInstructionPc = branchTarget.getValue();
        	m_nextInstructionPcKnown = true;
        } else {
        	m_nextInstructionPcKnown = false;
        }
    	//state.jump(branchTarget);
    }

    void BYTEREV(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: BYTEREV\n", getPc());
    	state.m_op1 = new Expression(Expression.OperatorType.BYTEREV);
    	state.m_op1.addOperand(state.m_op2);
    	state.m_op1.resolve();
    	
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void CHKCT(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: CHKCT\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    
    
    void CLRE(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: CLRE\n", getPc());	
    	for(Thread t : state.getResources().getThreads()){
    		t.setStatusRegisterBit(StatusRegister.EEBLE, false);
    	}
    	for(Timer t : state.getResources().getTimers()){
    		t.setEventEnabeled(false);
    	}
    	
    	for(Channel c : state.getResources().getChannels()){
    		c.setEventEnabeled(false);
    	}
    	
    	for(Port p : state.getResources().getPorts()){
    		p.setEventEnabeled(false);
    	}
    	
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    }
    
    void CLRPT(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: CLRPT\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void CLRSR(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     
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
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void CLZ(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: CLZ\n", getPc());	
    	
    	state.m_op1 = new Expression(Expression.OperatorType.CLZ);
    	state.m_op1.addOperand(state.m_op2);
    	state.m_op1.resolve();
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void CRC32(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: CRC32\n", getPc());	
    	
    	Expression result = new Expression(Expression.OperatorType.CRC32);
    	result.addOperand(state.m_op1);
    	result.addOperand(state.m_op2);
    	result.addOperand(state.m_op3);    	
    	state.m_op1 = result;
    	state.m_op1.resolve();
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void CRC8(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: CRC8\n", getPc());	
    	
    	Expression scrdest = new Expression(Expression.OperatorType.CRC8_SCRDEST);
    	Expression dest = new Expression(Expression.OperatorType.CRC8_DEST);
    	 
    	scrdest.addOperand(state.m_op4);
    	scrdest.addOperand(state.m_op2);
    	scrdest.addOperand(state.m_op3);
    	
    	dest.addOperand(state.m_op4);
    	dest.addOperand(state.m_op2);
    	dest.addOperand(state.m_op3);
    	
    	if(scrdest.canResolve()){
    		scrdest = new Expression(scrdest.getValue());
    	}
    	if(dest.canResolve()){
    		dest = new Expression(dest.getValue());
    	}
    	
    	state.m_op4 = scrdest;
    	state.m_op1 = dest;
    	state.m_op1.resolve();
    	state.m_op4.resolve();
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    }

    void DCALL(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: DCALL\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.DEBUG_INSTRUCTION);
    	}
    	
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void DENTSP(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: DENTSP\n", getPc());
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.DEBUG_INSTRUCTION);
    	}
    	
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void DGETREG(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: DGETREG\n", getPc());
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.DEBUG_INSTRUCTION);
    	}
    	
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void DIVS(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: DIVS\n", getPc());	
    	
    	state.m_op1 = new Expression(Expression.OperatorType.DIVS);
    	state.m_op1.addOperand(state.m_op2);
    	state.m_op1.addOperand(state.m_op3);
    	state.m_op1.resolve();
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void DIVU(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: DIVU\n", getPc());
    		
    	state.m_op1 = new Expression(Expression.OperatorType.DIVU);
    	state.m_op1.addOperand(state.m_op2);
    	state.m_op1.addOperand(state.m_op3);
    	state.m_op1.resolve();
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void DRESTSP(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: DRESTSP\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.DEBUG_INSTRUCTION);
    	}
    	
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void DRET(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: DRET\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.DEBUG_INSTRUCTION);
    	}
    	
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void ECALLF(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: ECALLF\n", getPc());	
    	if(m_uninplimented) {
    		//throw new InstructionExecption(Error.TRAP_INSTRUCTION);
    	}
    	
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void ECALLT(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: ECALLT\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.TRAP_INSTRUCTION);
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void EDU(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: EDU\n", getPc());	
    	Resources resources = state.getResources();
        Expression resourceId = state.m_op1;

    	
        m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
        
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
            	throw new InstructionExecption(Error.ET_RESOURCE_DEP);
            }
        } else {
        	throw new InstructionExecption(Error.UNKNOWN_RESOURCE);
        } 
    	
    }

    void EEF(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: EEF\n", getPc());	
    	Resources resources = state.getResources();
        Expression resourceId = state.m_op2;
        Expression gaurd = state.m_op1;

    	
        m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
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
            	throw new InstructionExecption(Error.ET_RESOURCE_DEP);
            }
        } else {
        	throw new InstructionExecption(Error.UNKNOWN_RESOURCE);
        } 
    }

    void EET(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: EET\n", getPc());	
    	Resources resources = state.getResources();
        Expression resourceId = state.m_op2;
        Expression gaurd = state.m_op1;

    	
        m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
        
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
            	throw new InstructionExecption(Error.ET_RESOURCE_DEP);
            }
        } else {
        	throw new InstructionExecption(Error.UNKNOWN_RESOURCE);
        } 
    }

    void EEU(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: EEU\n", getPc());	
    	Resources resources = state.getResources();
        Expression resourceId = state.m_op1;

    	
        m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
        
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
            	throw new InstructionExecption(Error.ET_RESOURCE_DEP);
            }
        } else {
        	throw new InstructionExecption(Error.UNKNOWN_RESOURCE);
        } 
    }

    void ENDIN(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: ENDIN\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void ENTSP(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: ENTSP\n", getPc());	

    	
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	assert state.m_op1.canResolve();    		    		
    	if (state.m_op1.getValue() != 0) {     		
            Expression stackPointer = state.getStackPointerRegister(thread);
            Expression wordOffset = new Expression(state.m_op1.getValue() << Instruction.NUM_BYTES_PER_WORD_LOG2);
	        if (stackPointer.canResolve()) {
                // The address is resolvable so we can go ahead and 
	    	    // store the link register expression on the stack.    	        
	            stackPointer.resolve();
	            
	            int address = stackPointer.getValue();
	            //if(!state.isWithinMemory(address) || !state.is16bitAligned(address) ){
	            if(!state.is16bitAligned(address) ){
	            	throw new InstructionExecption(Error.ET_LOAD_STORE);
	            }
                state.writeMemory(address, state.getLinkRegister(thread));   
	        }        
            Expression newStackPointer = new Expression(Expression.OperatorType.SUB);
            newStackPointer.addOperand(stackPointer);
            newStackPointer.addOperand(wordOffset);        	

            state.setStackPointerRegister(newStackPointer, thread);
            state.getStackPointerRegister(thread).resolve();
    	} else {
    	    // Not actually do a load
    	    //setIsLoadStore(false);
    	}
    	
    }

    void EQ(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: EQ\n", getPc());	

    	
        state.m_op1 = new Expression(Expression.OperatorType.EQ);
        state.m_op1.addOperand(state.m_op2);
        state.m_op1.addOperand(state.m_op3);
        state.m_op1.resolve();
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
        
    }

    void EXTDP(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: EXTDP\n", getPc());	
    	assert(state.m_op1.canResolve());
    	int imm = state.m_op1.getValue() << Instruction.NUM_BYTES_PER_WORD_LOG2;
    	

    	
    	Expression offset = new Expression(OperatorType.SUB);
    	offset.addOperand(state.getDataPoolRegister(thread) );
    	offset.addOperand(new Expression(imm));
    	
    	offset.resolve();
    	if(offset.canResolve()){
    		int value = offset.getValue();
    		state.setDataPoolRegister(new Expression(value), thread);
    	} else {
    		//maybe dodgy as is circular
    		state.setDataPoolRegister(offset, thread);
    	}
    	state.getDataPoolRegister(thread).resolve();
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void EXTSP(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: EXTSP\n", getPc());	
    	assert(state.m_op1.canResolve());
    	int imm = state.m_op1.getValue() << Instruction.NUM_BYTES_PER_WORD_LOG2;
    	

    	
    	Expression offset = new Expression(OperatorType.SUB);
    	offset.addOperand(state.getStackPointerRegister(thread));
    	offset.addOperand(new Expression(imm));
    	
    	offset.resolve();
    	if(offset.canResolve()){
    		int value = offset.getValue();
    		state.setStackPointerRegister(new Expression(value), thread);
    	} else {
    		//maybe dodgy as is circular
    		state.setStackPointerRegister(offset, thread);
    	}
    	state.getStackPointerRegister(thread).resolve();
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

   

    void FREET(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: FREET\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

   

    void GETED(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: GETED\n", getPc());	
    	state.writeRegister(11, state.getResources().getThread(thread).getED(), thread);
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    }

    void GETET(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: GETET\n", getPc());	
    	state.writeRegister(11, state.getResources().getThread(thread).getET(), thread);
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    }

    void GETID(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: GETID\n", getPc());	
    	state.writeRegister(11, new Expression(state.getResources().getThread(thread).getID()), thread);
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    }

    void GETKEP(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: GETKEP\n", getPc());	
    	state.writeRegister(11, state.getResources().getThread(thread).getKEP(), thread);
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    }

    void GETKSP(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: GETKSP\n", getPc());	
    	state.writeRegister(11, state.getResources().getThread(thread).getKSP(), thread);
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    }

    void GETPS(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: GETPS\n", getPc());
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	
    }

  
    void GETSR(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: GETSR\n", getPc());	
    	assert(state.m_op1.canResolve());
    	int status = state.getResources().getThread(thread).getStatusRegister() & state.m_op1.getValue();
    	state.writeRegister(11, new Expression(status), thread);
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();

    	
    }

    
    void GETTS(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: GETTS\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    }

    void IN(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: IN\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    }

    void INCT(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: INCT\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    }

    void INPW(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: INPW\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void INSHR(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: INSHR\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    }

    void INT(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: INT\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    }

    void KCALL(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: KCALL\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    }

    void KENTSP(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: KENTSP\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    }

    void KRESTSP(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: KRESTSP\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    }

    void KRET(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: KRET\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    }
    void LD16S(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: LD16S\n", getPc());	
    	
    	Expression address = new Expression(OperatorType.ADD);
    	
    	Expression offset = new Expression(OperatorType.SHL);
    	offset.addOperand(state.m_op3);
    	offset.addOperand(new Expression(Instruction.NUM_BYTES_PER_WORD_LOG2/2));
    	
    	address.addOperand(state.m_op2);
    	address.addOperand(offset);
    	
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    	if(address.canResolve()){
    		if(!state.isWithinMemory(address.getValue())){
    			throw new InstructionExecption(Error.ET_LOAD_STORE);
    		}
    		state.m_op1 = state.readShort(address.getValue());
    	} else {
    		state.m_op1 = new Expression(0,0,Expression.Type.MEMORY, Expression.Status.UNDEFINED);
    	}
    	state.m_op1.resolve();
    	
    	
    }

    void LD8U(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: LD8U\n", getPc());	

    	Expression address = new Expression(OperatorType.ADD);
    	
    	address.addOperand(state.m_op2);
    	address.addOperand(state.m_op3);
    	
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    	if(address.canResolve()){
    		if(!state.isWithinMemory(address.getValue())){
    			throw new InstructionExecption(Error.ET_LOAD_STORE);
    		}
    		state.m_op1 = state.readByte(address.getValue());
    	} else {
    		state.m_op1 = new Expression(0,0,Expression.Type.MEMORY, Expression.Status.UNDEFINED);
    	}
    	state.m_op1.resolve();
    	
    	
    }

    void LDA16B(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: LDA16B\n", getPc());	
    	Expression result = new Expression(OperatorType.SUB);
    	
    	Expression offset = new Expression(OperatorType.SHL);
    	offset.addOperand(state.m_op3);
    	offset.addOperand(new Expression(Instruction.NUM_BYTES_PER_WORD_LOG2/2));
    	
    	result.addOperand(state.m_op2);
    	result.addOperand(offset);
    	
    	state.m_op1 = result;
    	state.m_op1.resolve();
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    }

    void LDA16F(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: LDA16F\n", getPc());	
    	Expression result = new Expression(OperatorType.ADD);
    	
    	Expression offset = new Expression(OperatorType.SHL);
    	offset.addOperand(state.m_op3);
    	offset.addOperand(new Expression(Instruction.NUM_BYTES_PER_WORD_LOG2/2));
    	
    	result.addOperand(state.m_op2);
    	result.addOperand(offset);
    	
    	state.m_op1 = result;
    	state.m_op1.resolve();
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    }

    void LDAPB(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: LDAPB\n", getPc());	
    	assert(state.m_op1.canResolve());
    	int offset = state.m_op1.getValue();
    	int nextPc = state.getPc(thread) + getNumBytes();
    	int number = nextPc - (offset<<(Instruction.NUM_BYTES_PER_WORD_LOG2/2));
    	state.writeRegister(11, new Expression(number), thread);
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    }

    void LDAPF(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: LDAPF\n", getPc());	
    	assert(state.m_op1.canResolve());
    	int offset = state.m_op1.getValue();
    	int nextPc = state.getPc(thread) + getNumBytes();
    	int number = nextPc + (offset<<(Instruction.NUM_BYTES_PER_WORD_LOG2/2));
    	state.writeRegister(11, new Expression(number), thread);
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    }

    void LDAWB(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: LDAWB\n", getPc());	
    	Expression result = new Expression(OperatorType.SUB);
    	
    	Expression offset = new Expression(OperatorType.SHL);
    	offset.addOperand(state.m_op3);
    	offset.addOperand(new Expression(Instruction.NUM_BYTES_PER_WORD_LOG2));
    	
    	result.addOperand(state.m_op2);
    	result.addOperand(offset);
    	
    	state.m_op1 = result;
    	state.m_op1.resolve();
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    }

    void LDAWDP(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: LDAWDP\n", getPc());	

        Expression rhs = new Expression(Expression.OperatorType.ASHL);              
        rhs.addOperand(state.m_op2);
        rhs.addOperand(new Expression(Instruction.NUM_BYTES_PER_WORD_LOG2));
        
        state.m_op1 = new Expression(Expression.OperatorType.ADD);
        state.m_op1.addOperand(state.getDataPoolRegister(thread));            
        state.m_op1.addOperand(rhs);  
        state.m_op1.resolve();
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    }

    void LDAWF(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: LDAWF\n", getPc());	
    	
    	Expression rhs = new Expression(Expression.OperatorType.ASHL);    	    	
    	rhs.addOperand(state.m_op3);
    	rhs.addOperand(new Expression(Instruction.NUM_BYTES_PER_WORD_LOG2));
    	
    	state.m_op1 = new Expression(Expression.OperatorType.ADD);
    	state.m_op1.addOperand(state.m_op2);	    	
    	state.m_op1.addOperand(rhs);	
    	state.m_op1.resolve();
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    }

    void LDAWSP(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: LDAWSP\n", getPc());	
    	
    	assert state.m_op2.canResolve();    		    		
        Expression stackPointer = state.getStackPointerRegister(thread);
        Expression wordOffset = new Expression(state.m_op2.getValue() << Instruction.NUM_BYTES_PER_WORD_LOG2);
            
        state.m_op1 = new Expression(Expression.OperatorType.ADD);
        state.m_op1.addOperand(stackPointer);
        state.m_op1.addOperand(wordOffset);  
        state.m_op1.resolve();
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    }

    void LDC(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: LDC\n", getPc());
    	assert(state.m_op2.canResolve());
    	state.m_op1 = new Expression(state.m_op2.getValue());
    	state.m_op1.resolve();
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    }

    void LDET(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: LDET\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    }

    void LDSED(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: LDSED\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    }

    void LDSPC(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: LDSPC\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void LDSSR(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: LDSSR\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }
    
    void LDW(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)   {
    		
    		System.out.printf("0x%08x: LDW\n", getPc());
    	}
            

    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    	if (state.m_op2.canResolve() && state.m_op3.canResolve()) {
    	    // The address is resolvable so we can read the expression from 'memory'
    		
    	    state.m_op2.resolve();
            state.m_op3.resolve();
            int base = state.m_op2.getValue();
            int offset = state.m_op3.getValue() << Instruction.NUM_BYTES_PER_WORD_LOG2;
            int address  = base + offset;
            
            state.m_op1 = state.readMemory( address) ;  
    	}
    	else
    	{
    		 state.m_op1 = new Expression(0, 0, Expression.Type.MEMORY, Expression.Status.UNDEFINED);  	   	
    	}
    	state.m_op1.resolve();
    	
    }

    void LDWCP(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: LDWCP\n", getPc());	

    	Expression dp = state.getConstPoolRegister(thread);
    	Expression offset = state.m_op2;
    	
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	if(offset.canResolve() && dp.canResolve()){
    		int address = dp.getValue() + ((offset.getValue()) << Instruction.NUM_BYTES_PER_WORD_LOG2);
    		Expression ex = state.readMemory(address);
    		state.m_op1 = ex; 
    	} else {
    		state.m_op1 = new Expression(0, 0, Expression.Type.MEMORY, Expression.Status.DEFINED);
    	}	
    	state.m_op1.resolve();
    	
    }

    void LDWCPL(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: LDWCPL\n", getPc());	
    	Expression dp = state.getConstPoolRegister(thread);
    	Expression offset = state.m_op2;
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    	if(offset.canResolve() && dp.canResolve()){
    		int address = dp.getValue() + ((offset.getValue()) << Instruction.NUM_BYTES_PER_WORD_LOG2);
    		Expression ex = state.readMemory(address);
    		state.m_op1 = ex; 
    	} else {
    		Expression ex = new Expression(0, 0, Expression.Type.MEMORY, Expression.Status.DEFINED);
    		state.getResources().getThread(thread).getRegisters().writeRegister(11, ex); 
    	}	
    	state.m_op1.resolve();
    	
    }

    void LDWDP(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: LDWDP\n", getPc());		
    	
    	Expression dp = state.getDataPoolRegister(thread);
    	Expression offset = state.m_op2;
    	
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    	if(offset.canResolve() && dp.canResolve()){
    		int address = dp.getValue() + ((offset.getValue()) << Instruction.NUM_BYTES_PER_WORD_LOG2);
    		Expression ex = state.readMemory(address);
    		state.m_op1 = ex; 
    	} else {
    		state.m_op1 = new Expression(0, 0, Expression.Type.MEMORY, Expression.Status.DEFINED);
    	}	
    	state.m_op1.resolve();
    	
    }

    void LDWSP(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: LDWSP\n", getPc());	
    	
    	assert state.m_op2.canResolve();
        Expression stackPointer = state.getStackPointerRegister(thread);                
        Expression wordOffset = new Expression(state.m_op2.getValue() << Instruction.NUM_BYTES_PER_WORD_LOG2);

        m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
        if (stackPointer.canResolve()) {
            stackPointer.resolve();
        	wordOffset.resolve();
            state.m_op1 = state.readMemory(stackPointer.getValue() + wordOffset.getValue());    	    
        }
        else {
        	 state.m_op1 = new Expression(0, 0, Expression.Type.MEMORY, Expression.Status.UNDEFINED);  	    	        	
        }
        state.m_op1.resolve();
    	
    	
    }

    void LMUL(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: LMUL\n", getPc());	

    	if (state.m_op2.canResolve() &&
    			state.m_op3.canResolve() &&
    			state.m_op5.canResolve() &&
    			state.m_op6.canResolve()) {
	    	long lprod = (((long)(state.m_op2.getValue()))&0xffffffffL) *
	        (((long)(state.m_op3.getValue()))&0xffffffffL);
			long lacc = lprod + (((long)(state.m_op5.getValue()))&0xffffffffL) +
			               (((long)(state.m_op6.getValue()))&0xffffffffL);
			
			if (state.getArchitecture() == Architecture.REVA) {
				state.m_op4 = new Expression((int)(lacc >> 32));
	    		state.m_op1 = new Expression(((int)(lacc & 0xffffffff)));
			}
			else if ((state.getArchitecture() == Architecture.REVB) ||
					 (state.getArchitecture() == Architecture.REVC)) {
				state.m_op1 = new Expression((int)(lacc >> 32));
	    		state.m_op4 = new Expression(((int)(lacc & 0xffffffff)));
			} else {
				assert(false);
			}
    	} else {
    		state.m_op1 = new Expression(state.m_op1Fld, 0, Expression.Type.REGISTER, Expression.Status.UNDEFINED);
    		state.m_op4 = new Expression(state.m_op4Fld, 0, Expression.Type.REGISTER, Expression.Status.UNDEFINED);
    	}
    	state.m_op1.resolve();
    	state.m_op4.resolve();
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void LSS(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: LSS\n", getPc());	

    	state.m_op1 = new Expression(Expression.OperatorType.LTS);
    	state.m_op1.addOperand(state.m_op2);
    	state.m_op1.addOperand(state.m_op3);
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void LSU(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG) 
    		System.out.printf("0x%08x: LSU\n", getPc());

    	state.m_op1 = new Expression(Expression.OperatorType.LTU);
    	state.m_op1.addOperand(state.m_op2);
    	state.m_op1.addOperand(state.m_op3);
    	state.m_op1.resolve();
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void MACC(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: MACC\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}

    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }
    

    void MKMSK(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: MKMSK\n", getPc());
    	assert(state.m_op2.canResolve());
    	int shift = state.m_op2.getValue();
    	long mask = (1<<shift) - 1;
    	if(shift == 32){
    		mask = 0xFFFFFFFF;
    	}
    	state.m_op1 = new Expression((int)mask);
    	state.m_op1.resolve();
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    

    void MUL(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: MUL\n", getPc());	
    	Expression result = new Expression(OperatorType.MUL);
    	result.addOperand(state.m_op2);
    	result.addOperand(state.m_op3);
    	result.resolve();
    	state.m_op1 = result;
    	state.m_op1.resolve();
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void NEG(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: NEG\n", getPc());
	
    	state.m_op1 = new Expression(Expression.OperatorType.NEG);
    	state.m_op1.addOperand(state.m_op2);
    	state.m_op1.resolve();
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void NOT(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: NOT\n", getPc());
	
    	state.m_op1 = new Expression(Expression.OperatorType.NOT);
    	state.m_op1.addOperand(state.m_op2);
    	state.m_op1.resolve();
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void OR(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: OR\n", getPc());
    	state.m_op1 = new Expression(Expression.OperatorType.OR);
    	state.m_op1.addOperand(state.m_op2);
    	state.m_op1.addOperand(state.m_op3);
    	state.m_op1.resolve();
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void OUT(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: OUT\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void OUTCT(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: OUTCT\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void OUTPW(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: OUTPW\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void OUTSHR(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: OUTSHR\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void OUTT(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: OUTT\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void PEEK(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: PEEK\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void REMS(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: REMS\n", getPc());	
    	Expression result = new Expression(OperatorType.REMS);
    	result.addOperand(state.m_op2);
    	result.addOperand(state.m_op3);
    	state.m_op1 = result;
    	state.m_op1.resolve();
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void REMU(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: REMU\n", getPc());	
    	Expression result = new Expression(OperatorType.REMU);
    	result.addOperand(state.m_op2);
    	result.addOperand(state.m_op3);
    	state.m_op1 = result;
    	state.m_op1.resolve();
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void RETSP(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: RETSP\n", getPc());	

        Expression jumpTarget = state.getLinkRegister(thread);                
        
    	assert state.m_op1.canResolve();
    	if (state.m_op1.getValue() != 0) {    	    
            Expression stackPointer = state.getStackPointerRegister(thread);                
    	    if (stackPointer.canResolve()) {
                // The address is resolvable so we can read the expression from 'memory'
    	        stackPointer.resolve();
                int newStackPointerValue = stackPointer.getValue() + 
                    (state.m_op1.getValue() << Instruction.NUM_BYTES_PER_WORD_LOG2);
            
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
    	    //setIsLoadStore(false);
    	}
    	
    	jumpTarget.resolve();
    	
    	if(jumpTarget.canResolve()){
    		m_nextInstructionPcKnown = true;
        	m_nextInstructionPc = jumpTarget.getValue();
    	} else {
    		m_nextInstructionPcKnown = false;
    	}
    	
        //state.jump(jumpTarget);
        setReturn(true);
    }

    
   
    void SETCP(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: SETCP\n", getPc());	
    	state.setConstPoolRegister(state.m_op1, thread);
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }


    void SETDP(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: SETDP\n", getPc());	
    	state.setDataPoolRegister(state.m_op1, thread);
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    

    void SETKEP(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: SETKEP\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void SETPS(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: SETPS\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void SETPSC(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: SETPSC\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    }

    void SETPT(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: SETPT\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    

    void SETSP(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: SETSP\n", getPc());	
    	state.setStackPointerRegister(state.m_op1, thread);
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void SETSR(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: SETSR\n", getPc());	
    	state.getResources().getThread(thread).setSSR(state.m_op1);
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void SETTW(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: SETTW\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }
  
   

    void SEXT(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: SEXT\n", getPc());	
    	Expression result = new Expression(OperatorType.SEXT);
    	result.addOperand(state.m_op1);
    	result.addOperand(state.m_op2);
    	state.m_op1 = result;
    	state.m_op1.resolve();
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void SHL(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: SHL\n", getPc());	
    	Expression result = new Expression(OperatorType.SHL);
    	result.addOperand(state.m_op2);
    	result.addOperand(state.m_op3);
    	state.m_op1 = result;
    	state.m_op1.resolve();
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void SHR(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: SHR\n", getPc());	
    	Expression result = new Expression(OperatorType.SHR);
    	result.addOperand(state.m_op2);
    	result.addOperand(state.m_op3);
    	state.m_op1 = result;
    	state.m_op1.resolve();
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void SSYNC(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: SSYNC\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void ST16(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: ST16\n", getPc());
    	
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    	if(state.m_op2.canResolve() && state.m_op3.canResolve()){
    		int base = state.m_op2.getValue();
    		int offset = state.m_op3.getValue() << (Instruction.NUM_BITS_PER_WORD/2) ;
    		int address = base + offset;
    		state.writeShort(address, state.m_op1);
    	} else {
    		throw new InstructionExecption(Error.UNKNOWN_MEMORY_WRITE);
    	}
    	
    }

    void ST8(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: ST8\n", getPc());	
    	if(state.m_op2.canResolve() && state.m_op3.canResolve()){
    		int base = state.m_op2.getValue();
    		int offset = state.m_op3.getValue();
    		int address = base + offset;
    		state.writeByte(address, state.m_op1);
    	} else {
    		state.m_op1 = new Expression(state.m_op1Fld, 0, Expression.Type.REGISTER, Expression.Status.UNDEFINED);
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void STSED(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: STSED\n", getPc());	
    	
    	Expression sed = state.getResources().getThread(thread).getSED();
    	
    	Expression sp = state.getStackPointerRegister(thread);
    	
    	if(sed.canResolve() && sp.canResolve()){
    		int address = sp.getValue() + (MachineState.STACK_OFFSET_SED << Instruction.NUM_BYTES_PER_WORD_LOG2);
    		state.writeMemory(address, sed);
    	} else {
    		throw new InstructionExecption(Error.UNKNOWN_MEMORY_WRITE);
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void STET(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: STET\n", getPc());	
    	Expression et = state.getResources().getThread(thread).getET();
    	
    	Expression sp = state.getStackPointerRegister(thread);
    	
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    	if(et.canResolve() && sp.canResolve()){
    		int address = sp.getValue() + (MachineState.STACK_OFFSET_ET << Instruction.NUM_BYTES_PER_WORD_LOG2);
    		state.writeMemory(address, et);
    	} else {
    		throw new InstructionExecption(Error.UNKNOWN_MEMORY_WRITE);
    	}
    	
    }

    void STSPC(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: STSPC\n", getPc());	
    	Expression spc = state.getResources().getThread(thread).getSPC();
    	
    	Expression sp = state.getStackPointerRegister(thread);
    	
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    	if(spc.canResolve() && sp.canResolve()){
    		int address = sp.getValue() + (MachineState.STACK_OFFSET_SPC << Instruction.NUM_BYTES_PER_WORD_LOG2);
    		state.writeMemory(address, spc);
    	} else {
    		throw new InstructionExecption(Error.UNKNOWN_MEMORY_WRITE);
    	}
    	
    }

    void STSSR(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: STSSR\n", getPc());	
    	Expression spc = state.getResources().getThread(thread).getSSR();
    	
    	Expression sp = state.getStackPointerRegister(thread);
    	
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    	if(spc.canResolve() && sp.canResolve()){
    		int address = sp.getValue() + (MachineState.STACK_OFFSET_SSR << Instruction.NUM_BYTES_PER_WORD_LOG2);
    		state.writeMemory(address, spc);
    	} else {
    		throw new InstructionExecption(Error.UNKNOWN_MEMORY_WRITE);
    	}
    	
    }

    void STW(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: STW\n", getPc());	
    	
    	m_nextInstructionPcKnown = true;
            m_nextInstructionPc = getPc() + getNumBytes();
    	
        if (state.m_op2.canResolve() && state.m_op3.canResolve()) {
            state.m_op2.resolve();
            state.m_op3.resolve();
            int base = state.m_op2.getValue();
            int offset = state.m_op3.getValue() << Instruction.NUM_BYTES_PER_WORD_LOG2;
            int address =  base + offset;
            state.writeMemory(address, state.m_op1);  
        } else {
        	throw new InstructionExecption(Error.UNKNOWN_MEMORY_WRITE);
        }
    	
    }

    void STWDP(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: STWDP\n", getPc());	
    	
    	assert state.m_op2.canResolve();
        Expression dataPointer = state.getDataPoolRegister(thread);                
        Expression wordOffset = new Expression(state.m_op2.getValue() << Instruction.NUM_BYTES_PER_WORD_LOG2);

        if (dataPointer.canResolve()) {
            dataPointer.resolve();
        	wordOffset.resolve();
        	int base  = dataPointer.getValue();
        	int offset = wordOffset.getValue();
        	int address =  base + offset;
        	state.writeMemory(address, state.m_op1);
        	m_nextInstructionPcKnown = true;
        	m_nextInstructionPc = getPc() + getNumBytes();
        } else {
        	m_nextInstructionPcKnown = true;
        	m_nextInstructionPc = getPc() + getNumBytes();
        	throw new InstructionExecption(Error.UNKNOWN_MEMORY_WRITE);
        }
    	
    }

    void STWSP(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: STWSP\n", getPc());	

    	assert state.m_op2.canResolve();
        Expression stackPointer = state.getStackPointerRegister(thread);                
        Expression wordOffset = new Expression(state.m_op2.getValue() << Instruction.NUM_BYTES_PER_WORD_LOG2);

        m_nextInstructionPcKnown = true;
        	m_nextInstructionPc = getPc() + getNumBytes();
        
        if (stackPointer.canResolve()) {
            // The stack pointer address is resolvable so we can store the expression to 'memory'
        	stackPointer.resolve();
        	wordOffset.resolve();
        	int base  = stackPointer.getValue();
        	int offset = wordOffset.getValue();
        	int address =  base + offset;
        	state.writeMemory(address, state.m_op1);
        } else {
        	throw new InstructionExecption(Error.UNKNOWN_MEMORY_WRITE);
        }
    	
    }

    void SUB(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: SUB\n", getPc());
    	state.m_op1 = new Expression(Expression.OperatorType.SUB);
    	state.m_op1.addOperand(state.m_op2);
    	state.m_op1.addOperand(state.m_op3);
    	state.m_op1.resolve();
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void SYNCR(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: SYNCR\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void TESTLCL(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: TESTLCL\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void TESTCT(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: TESTCT\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void TESTWCT(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: TESTWCT\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

   

	void TSETMR(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: TSETMR\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }


    void WAITEF(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: WAITEF\n", getPc());	
    	//state.eventWait(false, false, state.m_op1);

    	m_nextInstructionPcKnown = false;


 
    }

    void WAITET(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: WAITET\n", getPc());	
    	//state.eventWait(false, true, state.m_op1);
    	m_nextInstructionPcKnown = false;
    }

    void WAITEU(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: WAITEU\n", getPc());	
    	//state.eventWait(true, false,  null);
    	m_nextInstructionPcKnown = false;
    	/*
    	ArrayList<Integer> takenVectors = new ArrayList<Integer>();
    	state.clearEventTargets();
    	for (Timer timer : state.getResources().getTimers()){
    		if (timer.isEventEnabeled()){
    			
				state.eventWait(timer.getVector());
    			if(timer.getVector().canResolve()){
    				takenVectors.add(timer.getVector().getValue());
    			}
    			if (RESOURCEDEBUG) {
					System.out.println("    timer:" + timer.getVector().expressionString());
    	    	}
    		}
    	}
		for (Port port : state.getResources().getPorts()) {
			if (port.isEventEnabeled()){
    			
				state.eventWait(port.getVector());
				if(port.getVector().canResolve()){
    				takenVectors.add(port.getVector().getValue());
    			}
    			if (RESOURCEDEBUG) {
					System.out.println("    port:" + port.getVector().expressionString());
    	    	}
    		}
		}
		for (Channel channel : state.getResources().getChannels()) {
			if (channel.isEventEnabeled()){
    			
				state.eventWait(channel.getVector());
				if(channel.getVector().canResolve()){
    				takenVectors.add(channel.getVector().getValue());
    			}
    			if (RESOURCEDEBUG) {
					System.out.println("    channel:" + channel.getVector().expressionString());
    	    	}
    		}
		}
		//TODO
		for(Expression vector : state.getResources().getThread(thread).getUnknownResourceEventVectors()){
			if(vector.canResolve()){
				int destination = vector.getValue();
				if(!takenVectors.contains(destination))
					state.eventWait(vector);
				if (RESOURCEDEBUG) {
					System.out.println("    unknown resource:" + vector.expressionString());
		    	}
			} else {
				state.eventWait(vector);
				if (RESOURCEDEBUG) {
					System.out.println("    unknown resource:" + vector.expressionString());
		    	}
			}
			
			
		}
		
		 state.getResources().getThread(thread).clearUnknownResourceEventVectors();
		*/
    }

    void XOR(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: XOR\n", getPc());	
    	Expression result = new Expression(OperatorType.XOR);
    	result.addOperand(state.m_op2);
    	result.addOperand(state.m_op3);
    	result.resolve();
    	state.m_op1 = result;
    	state.m_op1.resolve();
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void ZEXT(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: ZEXT\n", getPc());	
    	Expression result = new Expression(OperatorType.ZEXT);
    	result.addOperand(state.m_op1);
    	result.addOperand(state.m_op2);
    	if(result.canResolve()){
    		state.m_op1 = new Expression(result);
    	} else {
    		state.m_op1 = result;
    	}
    	state.m_op1.resolve();
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }
    
    void GETN(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: GETN\n", getPc());	

    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }
    
    void SETN(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: SETN\n", getPc());	

    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }
        
    void LADD(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: LADD\n", getPc());	
    	Expression result = new Expression(OperatorType.LADD_LOWER);
    	result.addOperand(state.m_op2);
    	result.addOperand(state.m_op3);
    	result.addOperand(state.m_op5);

    	Expression carry = new Expression(OperatorType.LADD_CARRY);
    	result.addOperand(state.m_op2);
    	result.addOperand(state.m_op3);
    	result.addOperand(state.m_op5);
    	if(carry.canResolve() && result.canResolve()){
    		state.m_op4 = new Expression(carry.getValue());
    		state.m_op1 = new Expression(result.getValue());
    	} else {
    		state.m_op4 = carry;
    		state.m_op1 = result;
    	}
    	state.m_op1.resolve();
    	state.m_op4.resolve();
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }
    
    void LDAWCP(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: LDAWCP\n", getPc());	
		Expression offset = new Expression(Expression.OperatorType.SHL);
		offset.addOperand(state.m_op1);
		offset.addOperand(new Expression(Instruction.NUM_BYTES_PER_WORD_LOG2));
		Expression regValue = new Expression(Expression.OperatorType.ADD);
		regValue.addOperand(state.getConstPoolRegister(thread));
		regValue.addOperand(offset);
		regValue.resolve();
    	state.writeRegister(11, regValue, thread);
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }
    
    void MACCU(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: MACCU\n", getPc());	
    	Expression high =new Expression(OperatorType.MACCU_HIGH);
    	Expression low = new Expression(OperatorType.MACCU_LOW);

    	high.addOperand(state.m_op1);
    	high.addOperand(state.m_op4);
    	high.addOperand(state.m_op2);
    	high.addOperand(state.m_op3);

    	low.addOperand(state.m_op1);
    	low.addOperand(state.m_op4);
    	low.addOperand(state.m_op2);
    	low.addOperand(state.m_op3);
        
    	if(high.canResolve() && low.canResolve()){
    		state.m_op4 = new Expression(low.getValue());
    		state.m_op1 = new Expression(high.getValue());
    	} else {
    		state.m_op4 = low;
    		state.m_op1 = high;
    	}
    	state.m_op1.resolve();
    	state.m_op4.resolve();
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }
    
    void MACCS(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: MACCS\n", getPc());	
    	Expression high =  new Expression(OperatorType.MACCS_HIGH);
    	high.addOperand(state.m_op1);
    	high.addOperand(state.m_op4);
    	high.addOperand(state.m_op2);
    	high.addOperand(state.m_op3);
    	Expression low = new Expression(OperatorType.MACCS_LOW);
    	low.addOperand(state.m_op1);
    	low.addOperand(state.m_op4);
    	low.addOperand(state.m_op2);
    	low.addOperand(state.m_op3);
    	if(high.canResolve() && low.canResolve()){
    		state.m_op4 = new Expression(low.getValue());
    		state.m_op1 = new Expression(high.getValue());
    	} else {
    		state.m_op4 = low;
    		state.m_op1 = high;
    	}
    	state.m_op1.resolve();
    	state.m_op4.resolve();
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }    
    
    void LDIVU(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: LDIVU\n", getPc());	
    	Expression high =  new Expression(OperatorType.LDIVU_HIGH);
    	high.addOperand(state.m_op1);
    	high.addOperand(state.m_op4);
    	high.addOperand(state.m_op2);
    	high.addOperand(state.m_op3);
    	Expression low = new Expression(OperatorType.LDIVU_LOW);
    	low.addOperand(state.m_op1);
    	low.addOperand(state.m_op4);
    	low.addOperand(state.m_op2);
    	low.addOperand(state.m_op3);
    	if(high.canResolve() && low.canResolve()){
    		state.m_op4 = new Expression(low.getValue());
    		state.m_op1 = new Expression(high.getValue());
    	} else {
    		state.m_op4 = low;
    		state.m_op1 = high;
    	}
    	state.m_op1.resolve();
    	state.m_op4.resolve();
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }
    
    void LSUB(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: LSUB\n", getPc());	
    	
    	Expression result = new Expression(OperatorType.LSUB_LOWER);
    	result.addOperand(state.m_op2);
    	result.addOperand(state.m_op3);
    	result.addOperand(state.m_op5);

    	Expression carry = new Expression(OperatorType.LSUB_CARRY);
    	carry.addOperand(state.m_op2);
    	carry.addOperand(state.m_op3);
    	carry.addOperand(state.m_op5);
    	
    	if(carry.canResolve() && result.canResolve()){
    		state.m_op4 = new Expression(carry.getValue());
    		state.m_op1 = new Expression(result.getValue());
    	} else {
    		state.m_op4 = carry;
    		state.m_op1 = result;
    	}
    	state.m_op1.resolve();
    	state.m_op4.resolve();
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }
    
    
    /*
     * Resource Instructions
     */
    
    
    void FREER(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: FREER\n", getPc());	

    	Expression resourceId = state.m_op1;

    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    	if(resourceId.canResolve()){
    		Resources resources = state.getResources();
    	    int resId = resourceId.getValue();
    	    int resNum = resources.decodeResNum(resId);
    	    int resType = resources.decodeResType(resId);
    	    if(resType == Resources.RES_TYPE_TIMER || resType == Resources.RES_TYPE_CHANEND ||
    	    		resType == Resources.RES_TYPE_SYNC ||  resType == Resources.RES_TYPE_THREAD ||
    	    		resType == Resources.RES_TYPE_LOCK){
    	    resources.getResource(resType, resNum).setInUse(false);
    	    } else {
    	    	throw new InstructionExecption(Error.ET_RESOURCE_DEP);
    	    }
        } else {
        	throw new InstructionExecption(Error.UNKNOWN_RESOURCE);
        } 
    	
    }
    
    void GETST(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: GETST\n", getPc());	
    	
    	Expression synchroniserId = state.m_op2;
    	
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    	if(synchroniserId.canResolve()){
    		Resources resources = state.getResources();
    		int resNo = synchroniserId.getValue();
    		int resType = resources.decodeResType(resNo);
    		int resNum = resources.decodeResType(resNo);
    		
    		if(resType == Resources.RES_TYPE_SYNC){
    			
    			Resource res = resources.getResource(resType, resNum);
    			res.setInUse(true);
    			Resource newRes = resources.getNewResource(Resources.RES_TYPE_THREAD);
    			
    			if(newRes == null){
    				throw new InstructionExecption(Error.ET_RESOURCE_DEP);
    			} else {
    				Synchroniser sync = (Synchroniser)res;
    				sync.attachResource(res);
    				state.m_op1 = new Expression(res.getResourceID());
    			}
    		} else {
    			throw new InstructionExecption(Error.ET_ILLEGAL_RESOURCE);
    		}
    	} 
    	
    	
    }

    void MSYNC(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: MSYNC\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }
    
    void MJOIN(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: MJOIN\n", getPc());	

    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }
    
    void TINITCP(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: TINITCP\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void TINITDP(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: TINITDP\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void TINITLR(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: TINITLR\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void TINITPC(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: TINITPC\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    }

    void TINITSP(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: TINITSP\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }
    
    void TSTART(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: TSTART\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }

    void TSETR(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: TSETR\n", getPc());	
    	Resources resources = state.getResources();
        Expression resourceId = state.m_op3;
        
        m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
        
        if(resourceId.canResolve()){
            int resId = resourceId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
            Thread child = resources.getThread(resType, resNum);
            if(child == null){
            	throw new InstructionExecption(Error.UNKNOWN_RESOURCE);
            } else {
            	child.getRegisters().writeRegister(state.m_op1Fld, state.m_op2);
            }
        } else {
            throw new InstructionExecption(Error.ET_ILLEGAL_RESOURCE);
        } 
    	
    }

    void SETV(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: SETV\n", getPc());	
    	
    	Resources resources = state.getResources();
    	Expression resourceId = state.m_op1;
    	Expression reg_11 = state.readRegister(11, thread);
    	
    	
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    	if (resourceId.canResolve()){
    		int resId = resourceId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);

            if (resType == Resources.RES_TYPE_CHANEND || resType == Resources.RES_TYPE_PORT
            		|| resType == Resources.RES_TYPE_TIMER) {
            	EventableResource er = (EventableResource) state.getResources().getResource(resType, resNum);
            	er.setVector(reg_11);
            	if (RESOURCEDEBUG){
            		System.out.println("resource (resNum: " + resNum + " resType: " + resType + 
            				") set vector to " + reg_11.getStringValue());
            	}
            } else {
            	//FIXME - check that this is correct
            	throw new InstructionExecption(Error.ET_RESOURCE_DEP);
            }
    	} else {
    		Thread t = resources.getThread(thread);
    		t.addUnknownEventVector(reg_11);
    		if (RESOURCEDEBUG){
        		System.out.println("resource (resNum: ? resType: ?) set vector to " + reg_11.getStringValue());
        	}
    	}
    	
    }
    
    void SETEV(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: SETEV\n", getPc());	
    	
    	Resources resources = state.getResources();
    	Expression resourceId = state.m_op1;
    	
    	if(resourceId.canResolve()){
    		int resId = resourceId.getValue();
            int resNum = resources.decodeResNum(resId);
            int resType = resources.decodeResType(resId);
            
            Expression reg_11 = state.readRegister(11, thread);
            
            if(resType == Resources.RES_TYPE_CHANEND || resType == Resources.RES_TYPE_PORT
            		|| resType == Resources.RES_TYPE_TIMER){
            	EventableResource er = (EventableResource) state.getResources().getResource(resType, resNum);
            	er.setEnvironmentVector(reg_11);
            	if(RESOURCEDEBUG){
            		System.out.println("resource (resNum: " + resNum + " resType: " + resType + 
            				") set environment vector to " + reg_11.getStringValue());
            	}
            } else {
            	//FIXME - check that this is correct
            	throw new InstructionExecption(Error.ET_RESOURCE_DEP);
            }
    	} else {
    		throw new InstructionExecption(Error.UNKNOWN_RESOURCE);
    	} 
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }
    
    void SETC(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: SETC\n", getPc());	
    	
    	//ensure that the resource is valid
    	

    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    }

    void SETD(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: SETD\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }
    
    void GETD(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: GETD\n", getPc());	
    	Resources resources = state.getResources();
        Expression resourceId = state.m_op2;
        
        m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
        
        if(resourceId.canResolve()){
            int resId = resourceId.getValue();
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
            	throw new InstructionExecption(Error.ET_RESOURCE_DEP);
            }
        } else {
        	throw new InstructionExecption(Error.UNKNOWN_RESOURCE);
        } 
    }
    
    void SETCLK(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: SETCLK\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }
    
    void SETRDY(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: SETRDY\n", getPc());	
    	if(m_uninplimented) {
    		throw new InstructionExecption(Error.UNIMPLIMENTED, getPc());
    	}
    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    }
    
    void GETR(MachineState state, int thread) throws Exception {
    	if (INSTRUCTIONDEBUG)     	
            System.out.printf("0x%08x: GETR\n", getPc());	
    	
    	Resources resources = state.getResources();
    	Expression resourceId = state.m_op2;
    	assert(resourceId.canResolve());
    	int resType = resourceId.getValue();

    	m_nextInstructionPcKnown = true;
    	m_nextInstructionPc = getPc() + getNumBytes();
    	
        boolean resourceAvaliable = resources.freeResourceAvaliable(resType);
        
        if(resourceAvaliable){
        	Resource res  = resources.getNewResource(resType);
        	res.setInUse(true);
	        res.setMaster(thread);
        	int resourceValue = res.getResourceID();
	        state.m_op1 = new Expression(resourceValue);
	        state.m_op1.resolve();
        } else {
        	//this is a genuine exception
        	throw new InstructionExecption(Error.ET_RESOURCE_DEP);
        }
    }

	public Config getConfig() {
		return m_config;
	}
}

class NullInstruction extends XtaInstruction {    
    NullInstruction(String nmenonic, Config config, int opcode, int pc, int numBytes) {
        super(nmenonic, config, opcode, pc, numBytes, false, false, false, false, 1);
        setIsFnop(true);
    }
    
    public void execute(MachineState state, int thread) throws Exception {
    }

    public int getOperandField(String opName) {
    	return 0;
    }
    
    public String getOperandString() {
    	return "";
    }

	public int getDefResource() {
		return 0;
	}

	public int getUseResource() {
		return 0;
	}
}


