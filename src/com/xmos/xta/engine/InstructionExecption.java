package com.xmos.xta.engine;

import java.util.HashSet;

public class InstructionExecption extends Exception {

	private static final long serialVersionUID = 1L;
	
	public static enum Error {
		UNKNOWN_MEMORY_WRITE,
		
		UNKNOWN_RESOURCE,
		UNIMPLIMENTED,
		
		DEBUG_INSTRUCTION,
		KERNEL_INSTRUCTION,
		TRAP_INSTRUCTION,
		
		ET_RESOURCE_DEP, 
		ET_ILLEGAL_RESOURCE,
		ET_ILLEGAL_INSTRUCTION,
		ET_ARITHMETIC,
		ET_ILLEGAL_PC,
		ET_LOAD_STORE,
		UNIDENTIFIEDVECTOR,
		
	};
	
	private static HashSet<Error> m_fatal;
	
	private Error m_error;
	public InstructionExecption(Error error){
		super();
		m_fatal = new HashSet<Error>();
		//m_fatal.add();
		m_error = error;
	}
	
	public Error getError(){
		return m_error;
	}
	
	public boolean isFatal(){
		return m_fatal.contains(this);
	}
}
