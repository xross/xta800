package com.xmos.xta.engine;

import com.xmos.isa.Instruction;

public abstract class XtaInstruction extends Instruction {
	private static final boolean m_debug = false;
  
	String m_functionName;
    int m_functionOffset;	
    int m_numCycles;
    int m_basicNumCycles;
    Config m_config;
	
	XtaInstruction(String nmenonic, int opcode, int pc, int numBytes, 
			boolean isLoadStore, boolean canPause) {
		super(nmenonic, opcode, pc, numBytes, isLoadStore, canPause);
		m_functionName = null;
		m_functionOffset = 0;
		m_numCycles = 0;
		m_basicNumCycles = 0;
		m_config = null;
	}

	public abstract void execute(MachineState machineState);
	
	//public Vector<LoopRoute> getMarkedLoopRoutes() { 
	//	return m_markedLoopRoutes; 
	//}
    		   
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
		    m_numCycles = numCycles;
		}
		else {
			m_numCycles = m_basicNumCycles;
		}
	}

	public int getNumCycles() {
		return m_numCycles;
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
        	timeTaken = ((m_config.m_numThreads * 1000) / m_config.m_frequency) * m_numCycles;
        }
        else {
        	timeTaken = ((4.0 * 1000) / m_config.m_frequency) * m_numCycles;
        }
        return timeTaken;
	}
	
	void setConfig(Config config) {
		m_config = config;
	}
	
	void setBasicNumCycles(int basicNumCycles) {
		m_basicNumCycles = basicNumCycles;
		m_numCycles = basicNumCycles;
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
	
    void ADD(MachineState state) {
    	if (m_debug) 
    		System.out.printf("0x%08x: ADD\n", getPc());
    	
    	state.m_op1 = new Expression(Expression.OperatorType.ADD);
    	state.m_op1.addOperand(state.m_op2);
    	state.m_op1.addOperand(state.m_op3);
    }
    
    void AND(MachineState state) {
    	if (m_debug) 
        	System.out.printf("0x%08x: AND\n", getPc());    
    	
    	state.m_op1 = new Expression(Expression.OperatorType.AND);
    	state.m_op1.addOperand(state.m_op2);
    	state.m_op1.addOperand(state.m_op3);
    }
    
    void ANDNOT(MachineState state) {
    	if (m_debug) 
        	System.out.printf("0x%08x: ANDNOT\n", getPc());  
    	
    	state.m_op1 = new Expression(Expression.OperatorType.ANDNOT);
    	state.m_op1.addOperand(state.m_op2);
    	state.m_op1.addOperand(state.m_op3);
    }
    
    void ASHR(MachineState state) {
    	if (m_debug)     	
    	    System.out.printf("0x%08x: ASHR\n", getPc());   
    	
    	state.m_op1 = new Expression(Expression.OperatorType.ASHR);
    	state.m_op1.addOperand(state.m_op2);
    	state.m_op1.addOperand(state.m_op3);
    }
    
    void BAU(MachineState state) {
    	if (m_debug)     	
    	    System.out.printf("0x%08x: BAU\n", getPc());
    	
    	state.jump(state.m_op1);
    }
    
    void BITREV(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: BITREV\n", getPc());

    	state.m_op1 = new Expression(Expression.OperatorType.BITREV);
    	state.m_op1.addOperand(state.m_op2);
    }

    void BLA(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: BLA\n", getPc());

    	state.setLinkRegister(new Expression(getPc() + getNumBytes()));
    	state.jump(state.m_op1);
    	setBranchLink(true);
    }

    void BLACP(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: BLACP\n", getPc());	
    	
    	Expression branchTarget = new Expression(0, 0, Expression.Type.MEMORY, Expression.Status.DEFINED);
    	branchTarget.addOperand(state.getConstPoolRegister());
    	branchTarget.addOperand(state.m_op1); 	
    	state.setLinkRegister(new Expression(getPc() + getNumBytes()));
    	state.jump(branchTarget);
        setBranchLink(true);    	
    }

    void BLAT(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: BLAT\n", getPc());	

    	Expression branchTarget = new Expression(0, 0, Expression.Type.MEMORY, Expression.Status.DEFINED);
    	branchTarget.addOperand(state.readRegister(11));
    	branchTarget.addOperand(state.m_op1); 	
    	state.jump(branchTarget);
    	state.setLinkRegister(new Expression(getPc() + getNumBytes()));
        setBranchLink(true);
    }

    void BLRB(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: BLRB\n", getPc());
    	
        Expression offset = new Expression(Expression.OperatorType.ASHL);
        offset.addOperand(state.m_op1);
        offset.addOperand(new Expression(1));
        
        Expression branchTarget = new Expression(Expression.OperatorType.SUB);
        branchTarget.addOperand(new Expression(getPc() + getNumBytes()));
        branchTarget.addOperand(offset);
        
    	state.jump(branchTarget);
    	state.setLinkRegister(new Expression(getPc() + getNumBytes()));
        setBranchLink(true);
    }

    void BLRF(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: BLRF\n", getPc());
	
        Expression offset = new Expression(Expression.OperatorType.ASHL);
        offset.addOperand(state.m_op1);
        offset.addOperand(new Expression(1));
        
        Expression branchTarget = new Expression(Expression.OperatorType.ADD);
        branchTarget.addOperand(new Expression(getPc() + getNumBytes()));
        branchTarget.addOperand(offset);
        
    	state.jump(branchTarget);
    	state.setLinkRegister(new Expression(getPc() + getNumBytes()));
        setBranchLink(true);
    }

    void BRBF(MachineState state) {
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

    void BRBT(MachineState state) {
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

    void BRBU(MachineState state) {
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

    void BRFF(MachineState state) {
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

    void BRFT(MachineState state) {
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

    void BRFU(MachineState state) {
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

    void BRU(MachineState state) {
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

    void BYTEREV(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: BYTEREV\n", getPc());
	
    	state.m_op1 = new Expression(Expression.OperatorType.BYTEREV);
    	state.m_op1.addOperand(state.m_op2);
    }

    void CHKCT(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: CHKCT\n", getPc());	

    	//TODO
    }

    void CLRE(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: CLRE\n", getPc());	

    	//TODO  
    }
    
    void CLRPT(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: CLRPT\n", getPc());	

    	//TODO
    }

    void CLRSR(MachineState state) {
    	if (m_debug)     
            System.out.printf("0x%08x: CLRSR\n", getPc());	

    	//TODO
    }

    void CLZ(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: CLZ\n", getPc());	

    	state.m_op1 = new Expression(Expression.OperatorType.CLZ);
    	state.m_op1.addOperand(state.m_op2);
    }

    void CRC32(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: CRC32\n", getPc());	

    	Expression result = new Expression(Expression.OperatorType.CRC32);
    	result.addOperand(state.m_op1);
    	result.addOperand(state.m_op2);
    	result.addOperand(state.m_op3);    	
    	state.m_op1 = result;
    }

    void CRC8(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: CRC8\n", getPc());	

    	state.m_op1 = new Expression(Expression.OperatorType.CRC8);
    	state.m_op1.addOperand(state.m_op2);
    	state.m_op1.addOperand(state.m_op3);
    	state.m_op1.addOperand(state.m_op4);
    }

    void DCALL(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: DCALL\n", getPc());	

    	//TODO
    }

    void DENTSP(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: DENTSP\n", getPc());
	
    	//TODO
    }

    void DGETREG(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: DGETREG\n", getPc());	

    	//TODO
    }

    void DIVS(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: DIVS\n", getPc());	
    	state.m_op1 = new Expression(Expression.OperatorType.DIVS);
    	state.m_op1.addOperand(state.m_op2);
    	state.m_op1.addOperand(state.m_op3);
    }

    void DIVU(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: DIVU\n", getPc());	
    	state.m_op1 = new Expression(Expression.OperatorType.DIVU);
    	state.m_op1.addOperand(state.m_op2);
    	state.m_op1.addOperand(state.m_op3);
    }

    void DRESTSP(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: DRESTSP\n", getPc());	

    	//TODO
    }

    void DRET(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: DRET\n", getPc());	

    	//TODO
    }

    void ECALLF(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: ECALLF\n", getPc());	

    	//TODO
    }

    void ECALLT(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: ECALLT\n", getPc());	

    	//TODO
    }

    void EDU(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: EDU\n", getPc());	

    	//TODO
    }

    void EEF(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: EEF\n", getPc());	

    	//TODO
    }

    void EET(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: EET\n", getPc());	

    	//TODO
    }

    void EEU(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: EEU\n", getPc());	

    	//TODO
    }

    void ENDIN(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: ENDIN\n", getPc());	

    	//TODO
    }

    void ENTSP(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: ENTSP\n", getPc());	
    	        	
    	assert state.m_op1.canResolve();    		    		
    	if (state.m_op1.getValue() != 0) {     		
            Expression stackPointer = state.getStackPointerRegister();
            Expression wordOffset = new Expression(state.m_op1.getValue() << Instruction.BYTES_PER_WORD_LOG2);

	        if (stackPointer.canResolve()) {
                // The address is resolvable so we can go ahead and 
	    	    // store the link register expression on the stack.    	        
	            stackPointer.resolve();
                state.writeMemory(stackPointer.getValue(), state.getLinkRegister());   
	        }        
        
            Expression newStackPointer = new Expression(Expression.OperatorType.SUB);
            newStackPointer.addOperand(stackPointer);
            newStackPointer.addOperand(wordOffset);        	

            state.setStackPointerRegister(newStackPointer);
    	}
    }

    void EQ(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: EQ\n", getPc());	

        state.m_op1 = new Expression(Expression.OperatorType.EQ);
        state.m_op1.addOperand(state.m_op2);
        state.m_op1.addOperand(state.m_op3);
    }

    void EXTDP(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: EXTDP\n", getPc());	

    	//TODO
    }

    void EXTSP(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: EXTSP\n", getPc());	

    	assert state.m_op1.canResolve();    		    		
        Expression stackPointer = state.getStackPointerRegister();
        Expression wordOffset = new Expression(state.m_op1.getValue() << Instruction.BYTES_PER_WORD_LOG2);

        Expression newStackPointer = new Expression(Expression.OperatorType.SUB);
        newStackPointer.addOperand(stackPointer);
        newStackPointer.addOperand(wordOffset);        	

        state.setStackPointerRegister(newStackPointer);
    }

    void FREER(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: FREER\n", getPc());	

    	//TODO
    }

    void FREET(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: FREET\n", getPc());	

    	//TODO
    }

    void GETD(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: GETD\n", getPc());	

    	//TODO
    }

    void GETED(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: GETED\n", getPc());	

    	//TODO
    }

    void GETET(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: GETET\n", getPc());	

    	//TODO
    }

    void GETID(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: GETID\n", getPc());	

    	//TODO
    }

    void GETKEP(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: GETKEP\n", getPc());	

    	//TODO
    }

    void GETKSP(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: GETKSP\n", getPc());	

    	//TODO
    }

    void GETPS(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: GETPS\n", getPc());	

    	//TODO
    }

    void GETR(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: GETR\n", getPc());	

    	//TODO
        state.m_op1 = new Expression(0, 0, Expression.Type.REGISTER, Expression.Status.UNDEFINED);
    }

    void GETSR(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: GETSR\n", getPc());	

    	//TODO
    }

    void GETST(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: GETST\n", getPc());	

    	//TODO
        state.m_op1 = new Expression(0, 0, Expression.Type.REGISTER, Expression.Status.UNDEFINED);
    }

    void GETTS(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: GETTS\n", getPc());	

    	//TODO
        state.m_op1 = new Expression(0, 0, Expression.Type.REGISTER, Expression.Status.UNDEFINED);
    }

    void IN(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: IN\n", getPc());	

    	//TODO
        state.m_op1 = new Expression(0, 0, Expression.Type.REGISTER, Expression.Status.UNDEFINED);
    }

    void INCT(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: INCT\n", getPc());	

    	//TODO
        state.m_op1 = new Expression(0, 0, Expression.Type.REGISTER, Expression.Status.UNDEFINED);
    }

    void INPW(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: INPW\n", getPc());	

    	//TODO
        state.m_op1 = new Expression(0, 0, Expression.Type.REGISTER, Expression.Status.UNDEFINED);
    }

    void INSHR(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: INSHR\n", getPc());	

    	//TODO
        state.m_op1 = new Expression(0, 0, Expression.Type.REGISTER, Expression.Status.UNDEFINED);
    }

    void INT(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: INT\n", getPc());	

    	//TODO
    	state.m_op1 = new Expression(0, 0, Expression.Type.REGISTER, Expression.Status.UNDEFINED);
    }

    void KCALL(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: KCALL\n", getPc());	

    	//TODO
    }

    void KENTSP(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: KENTSP\n", getPc());	

    	//TODO
    }

    void KRESTSP(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: KRESTSP\n", getPc());	

    	//TODO
    }

    void KRET(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: KRET\n", getPc());	

    	//TODO
    }

    void LD16S(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LD16S\n", getPc());	

    	//TODO
    }

    void LD8U(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LD8U\n", getPc());	

    	//TODO
    }

    void LDA16B(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDA16B\n", getPc());	

    	//TODO
    }

    void LDA16F(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDA16F\n", getPc());	

    	//TODO
    }

    void LDAPB(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDAPB\n", getPc());	

    	//TODO
    }

    void LDAPF(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDAPF\n", getPc());	

    	//TODO
    }

    void LDAWB(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDAWB\n", getPc());	

    	//TODO
    }

    void LDAWDP(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDAWDP\n", getPc());	

        Expression rhs = new Expression(Expression.OperatorType.ASHL);              
        rhs.addOperand(state.m_op2);
        rhs.addOperand(new Expression(2));
        
        state.m_op1 = new Expression(Expression.OperatorType.ADD);
        state.m_op1.addOperand(state.getDataPoolRegister());            
        state.m_op1.addOperand(rhs);            
    }

    void LDAWF(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDAWF\n", getPc());	
    	
    	Expression rhs = new Expression(Expression.OperatorType.ASHL);    	    	
    	rhs.addOperand(state.m_op3);
    	rhs.addOperand(new Expression(2));
    	
    	state.m_op1 = new Expression(Expression.OperatorType.ADD);
    	state.m_op1.addOperand(state.m_op2);	    	
    	state.m_op1.addOperand(rhs);	    	
    }

    void LDAWSP(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDAWSP\n", getPc());	
    	
    	assert state.m_op2.canResolve();    		    		
        Expression stackPointer = state.getStackPointerRegister();
        Expression wordOffset = new Expression(state.m_op2.getValue() << Instruction.BYTES_PER_WORD_LOG2);
            
        state.m_op1 = new Expression(Expression.OperatorType.ADD);
        state.m_op1.addOperand(stackPointer);
        state.m_op1.addOperand(wordOffset);        	
    }

    void LDC(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDC\n", getPc());
	
    	state.m_op1 = new Expression(state.m_op2);
    }

    void LDET(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDET\n", getPc());	

    	//TODO
    }

    void LDSED(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDSED\n", getPc());	

    	//TODO
    }

    void LDSPC(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDSPC\n", getPc());	

    	//TODO
    }

    void LDSSR(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDSSR\n", getPc());	

    	//TODO
    }

    void LDW(MachineState state) {
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

    void LDWCP(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDWCP\n", getPc());	

    	//TODO (Just assign an undefined value to the destination register for now...)    	
    	state.m_op1 = new Expression(state.m_op1Fld, 0, 
			    Expression.Type.REGISTER, Expression.Status.UNDEFINED);    	
    }

    void LDWCPL(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDWCPL\n", getPc());	

    	//TODO
    }

    void LDWDP(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDWDP\n", getPc());	

    	//TODO (Just assign an undefined value to the destination register for now...)    	
    	state.m_op1 = new Expression(state.m_op1Fld, 0, 
			    Expression.Type.REGISTER, Expression.Status.UNDEFINED);    	
    }

    void LDWSP(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDWSP\n", getPc());	
    	
    	assert state.m_op2.canResolve();
        Expression stackPointer = state.getStackPointerRegister();                
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

    void LMUL(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LMUL\n", getPc());	

    	//TODO (Just assign an undefined value to the destination registers for now...)    	
    	state.m_op1 = new Expression(state.m_op1Fld, 0, 
			    Expression.Type.REGISTER, Expression.Status.UNDEFINED);    	
    	state.m_op4 = new Expression(state.m_op4Fld, 0, 
			    Expression.Type.REGISTER, Expression.Status.UNDEFINED);    	
    }

    void LSS(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LSS\n", getPc());	

    	state.m_op1 = new Expression(Expression.OperatorType.LT);
    	state.m_op1.addOperand(state.m_op2);
    	state.m_op1.addOperand(state.m_op3);
    }

    void LSU(MachineState state) {
    	if (m_debug) 
    		System.out.printf("0x%08x: LSU\n", getPc());

    	state.m_op1 = new Expression(Expression.OperatorType.LT);
    	state.m_op1.addOperand(state.m_op2);
    	state.m_op1.addOperand(state.m_op3);
    }

    void MACC(MachineState state) {
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

    void MJOIN(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: MJOIN\n", getPc());	

    	//TODO
    }

    void MKMSK(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: MKMSK\n", getPc());	

    	//TODO
    }

    void MSYNC(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: MSYNC\n", getPc());	

    	//TODO
    }

    void MUL(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: MUL\n", getPc());	

    	//TODO (Just assign an undefined value to the destination register for now...)    	
    	state.m_op1 = new Expression(state.m_op1Fld, 0, 
			    Expression.Type.REGISTER, Expression.Status.UNDEFINED);    	
    }

    void NEG(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: NEG\n", getPc());
	
    	state.m_op1 = new Expression(Expression.OperatorType.NEG);
    	state.m_op1.addOperand(state.m_op2);
    }

    void NOT(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: NOT\n", getPc());
	
    	state.m_op1 = new Expression(Expression.OperatorType.NOT);
    	state.m_op1.addOperand(state.m_op2);
    }

    void OR(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: OR\n", getPc());
	
    	state.m_op1 = new Expression(Expression.OperatorType.OR);
    	state.m_op1.addOperand(state.m_op2);
    	state.m_op1.addOperand(state.m_op3);
    }

    void OUT(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: OUT\n", getPc());	

    	//TODO
    }

    void OUTCT(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: OUTCT\n", getPc());	

    	//TODO
    }

    void OUTPW(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: OUTPW\n", getPc());	

    	//TODO
    }

    void OUTSHR(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: OUTSHR\n", getPc());	

    	//TODO
    }

    void OUTT(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: OUTT\n", getPc());	

    	//TODO
    }

    void PEEK(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: PEEK\n", getPc());	

    	//TODO
    }

    void REMS(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: REMS\n", getPc());	

    	//TODO
    }

    void REMU(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: REMU\n", getPc());	

    	//TODO
    }

    void RETSP(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: RETSP\n", getPc());	

        Expression jumpTarget = state.getLinkRegister();                
        
    	assert state.m_op1.canResolve();
    	if (state.m_op1.getValue() != 0) {    	    
            Expression stackPointer = state.getStackPointerRegister();                
    	    if (stackPointer.canResolve()) {
                // The address is resolvable so we can read the expression from 'memory'
    	        stackPointer.resolve();
                int newStackPointerValue = stackPointer.getValue() + 
                    (state.m_op1.getValue() << Instruction.BYTES_PER_WORD_LOG2);
            
                state.setStackPointerRegister(new Expression(newStackPointerValue));                
    	        
    	        jumpTarget = state.readMemory(newStackPointerValue);
                state.setLinkRegister(jumpTarget);                
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

    void SETC(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SETC\n", getPc());	

    	//TODO
    }

    void SETCLK(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SETCLK\n", getPc());	

    	//TODO
    }

    void SETCP(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SETCP\n", getPc());	

    	//TODO
    }

    void SETD(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SETD\n", getPc());	

    	//TODO
    }

    void SETDP(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SETDP\n", getPc());	

    	//TODO
    }

    void SETEV(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SETEV\n", getPc());	

    	//TODO
    }

    void SETKEP(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SETKEP\n", getPc());	

    	//TODO
    }

    void SETPS(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SETPS\n", getPc());	

    	//TODO
    }

    void SETPSC(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SETPSC\n", getPc());	

    	//TODO
    }

    void SETPT(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SETPT\n", getPc());	

    	//TODO
    }

    void SETRDY(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SETRDY\n", getPc());	

    	//TODO
    }

    void SETSP(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SETSP\n", getPc());	

    	//TODO
    }

    void SETSR(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SETSR\n", getPc());	

    	//TODO
    }

    void SETTW(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SETTW\n", getPc());	

    	//TODO
    }

    void SETV(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SETV\n", getPc());	

    	//TODO
    }

    void SEXT(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SEXT\n", getPc());	

    	//TODO
    }

    void SHL(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SHL\n", getPc());	

    	//TODO
    }

    void SHR(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SHR\n", getPc());	

    	//TODO
    }

    void SSYNC(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SSYNC\n", getPc());	

    	//TODO
    }

    void ST16(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: ST16\n", getPc());	

    	//TODO
    }

    void ST8(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: ST8\n", getPc());	

    	//TODO
    }

    void STSED(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: STSED\n", getPc());	

    	//TODO
    }

    void STET(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: STET\n", getPc());	

    	//TODO
    }

    void STSPC(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: STSPC\n", getPc());	

    	//TODO
    }

    void STSSR(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: STSSR\n", getPc());	

    	//TODO
    }

    void STW(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: STW\n", getPc());	
    	
        if (state.m_op2.canResolve() && state.m_op3.canResolve()) {
            // The address is resolvable so we can write the expression from 'memory'
            state.m_op2.resolve();
            state.m_op3.resolve();
            state.writeMemory(state.m_op2.getValue() + state.m_op3.getValue(), state.m_op1);            
        }
    }

    void STWDP(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: STWDP\n", getPc());	

    	//TODO
    }

    void STWSP(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: STWSP\n", getPc());	

    	assert state.m_op2.canResolve();
        Expression stackPointer = state.getStackPointerRegister();                
        Expression wordOffset = new Expression(state.m_op2.getValue() << Instruction.BYTES_PER_WORD_LOG2);

        if (stackPointer.canResolve()) {
            // The stack pointer address is resolvable so we can store the expression to 'memory'
        	stackPointer.resolve();
        	wordOffset.resolve();
        	state.writeMemory(stackPointer.getValue() + wordOffset.getValue(), state.m_op1);
        }
    }

    void SUB(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SUB\n", getPc());
	
    	state.m_op1 = new Expression(Expression.OperatorType.SUB);
    	state.m_op1.addOperand(state.m_op2);
    	state.m_op1.addOperand(state.m_op3);
    }

    void SYNCR(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SYNCR\n", getPc());	

    	//TODO
    }

    void TESTLCL(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: TESTLCL\n", getPc());	

    	//TODO
    }

    void TESTCT(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: TESTCT\n", getPc());	

    	//TODO
    }

    void TESTWCT(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: TESTWCT\n", getPc());	

    	//TODO
    }

    void TINITCP(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: TINITCP\n", getPc());	

    	//TODO
    }

    void TINITDP(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: TINITDP\n", getPc());	

    	//TODO
    }

    void TINITLR(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: TINITLR\n", getPc());	

    	//TODO
    }

    void TINITPC(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: TINITPC\n", getPc());	

    	//TODO
    }

    void TINITSP(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: TINITSP\n", getPc());	

    	//TODO
    }

    void TSETMR(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: TSETMR\n", getPc());	

    	//TODO
    }

    void TSETR(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: TSETR\n", getPc());	

    	//TODO
    }

    void TSTART(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: TSTART\n", getPc());	

    	//TODO
    }

    void WAITEF(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: WAITEF\n", getPc());	

    	//TODO
    }

    void WAITET(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: WAITET\n", getPc());	

    	//TODO
    }

    void WAITEU(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: WAITEU\n", getPc());	

    	//TODO
    }

    void XOR(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: XOR\n", getPc());	

    	//TODO
    }

    void ZEXT(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: ZEXT\n", getPc());	

    	//TODO
    }
    
    void GETN(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: GETN\n", getPc());	

    	//TODO
    }
    
    void SETN(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: SETN\n", getPc());	

    	//TODO
    }
        
    void LADD(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LADD\n", getPc());	

    	//TODO
    }
    
    void LDAWCP(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDAWCP\n", getPc());	

    	//TODO
    }
    
    void MACCU(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: MACCU\n", getPc());	

    	//TODO
    }
    
    void MACCS(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: MACCS\n", getPc());	

    	//TODO
    }    
    
    void LDIVU(MachineState state) {
    	if (m_debug)     	
            System.out.printf("0x%08x: LDIVU\n", getPc());	

    	//TODO
    }
    
    void LSUB(MachineState state) {
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
    
    public void execute(MachineState state) {
    }
    
    public String getOperandString() {
    	return "";
    }
}
