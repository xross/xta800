package com.xmos.xta.engine;

public class InstructionExecption extends Exception {

	private static final long serialVersionUID = 1L;
	
	public static enum Error {
		UNKNOWN_MEMORY_WRITE,
		ET_ILLEGAL_RESOURCE,
		UNKNOWN_RESOURCE,
		UNSUPPORTED,
		DEBUG_INSTRUCTION,
		KERNEL_INSTRUCTION,
		TRAP_INSTRUCTION,
		ET_RESOURCE_DEP,
		
	};
	
	private Error m_error;
	public InstructionExecption(Error error){
		super();
		m_error = error;
	}
	
	public Error getError(){
		return m_error;
	}
}
