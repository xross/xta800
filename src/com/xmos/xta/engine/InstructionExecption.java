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
	
	public String getMessage(){
		if(m_error == Error.UNKNOWN_MEMORY_WRITE){
			return "write to unknown memory location";
		} else if(m_error == Error.UNKNOWN_RESOURCE){
			return "cannot identify the recource";
		} else if(m_error == Error.UNIMPLIMENTED){
			return "instruction currently unimplimented, results may be incorrect";
		} else if(m_error == Error.DEBUG_INSTRUCTION){
			return "executed dubug instruction";
		} else if(m_error == Error.KERNEL_INSTRUCTION){
			return "executed kernel instruction";
		} else if(m_error == Error.TRAP_INSTRUCTION){
			return "executed trap instruction";
		} else if(m_error == Error.ET_RESOURCE_DEP){
			return "ET_RESOURCE_DEP error occoured";
		} else if(m_error == Error.ET_ILLEGAL_RESOURCE){
			return "ET_ILLEGAL_RESOURCE error occoured";
		} else if(m_error == Error.ET_ILLEGAL_INSTRUCTION){
			return "ET_ILLEGAL_INSTRUCTION error occoured";
		} else if(m_error == Error.ET_ARITHMETIC){
			return "ET_ARITHMETIC error occoured";
		} else if(m_error == Error.ET_ILLEGAL_PC){
			return "ET_ILLEGAL_PC error occoured";
		} else if(m_error == Error.ET_LOAD_STORE){
			return "ET_LOAD_STORE error occoured";
		} else if(m_error == Error.UNIDENTIFIEDVECTOR){
			return "could not identify a vector";
		} else return "unidentified error";
	}
}
