package com.xmos.xta.engine;

import java.util.HashMap;
import java.util.Vector;

import com.xmos.isa.Instruction;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xeloader.XeLoader;
import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.engine.instructions.XtaInstructionDecoder;

public class Disassembler {      
	public static Vector<Instruction> disassemble(
			MemoryImage memoryImage, int address, int size, XeLoader.Architecture architecture, Config config) {	

		XtaInstructionDecoder instructionDecoder = 
			XtaInstructionDecoder.createInstructionDecoder(architecture);
        
        int fromAddress = address;        
        if ((fromAddress < memoryImage.getMemoryBase()) || 
        	(fromAddress >= (memoryImage.getMemoryBase() + memoryImage.getMemorySize()))) {
        	return null;
        }
        
        int toAddress = address + size;        
        if ((toAddress < memoryImage.getMemoryBase()) || 
            (toAddress >= (memoryImage.getMemoryBase() + memoryImage.getMemorySize()))) {
            	return null;
        }
       
        Vector<Instruction> disassemblyVector = new Vector<Instruction>();
        
        int numBytesRead = 0;
        while (numBytesRead < size) {        	
        	int pc = address + numBytesRead;
        	int opcode = 0;
        	try {
        	    opcode = memoryImage.readInstructionWord(pc);
        	} catch (Exception e) {
        	    // Do nothing
        	}
        	XtaInstruction instruction = instructionDecoder.decode(opcode, pc, config);    
            disassemblyVector.add(instruction);            
        	int instNumBytes = instruction.getNumBytes();        	        	
        	numBytesRead += instNumBytes;
        }
        return disassemblyVector;
	}
	
	public static HashMap<Integer, String> createBranchTargetMap(
			SymbolTable symbolTable, Vector<Instruction> instructions) {
		
		HashMap<Integer, String> branchTargetMap = new HashMap<Integer, String>();		
		for (Instruction instruction : instructions) {
		    if (instruction.getBranchPcRelative() && !symbolTable.functionExists(instruction.getPc())) {
		    	int branchTargetPc = instruction.getBranchPcRelativeTargetPc();
		    	
		    	if (!branchTargetMap.containsKey(branchTargetPc)) {
		    		String branchTargetName = ".bt" + branchTargetMap.size();
		    		branchTargetMap.put(branchTargetPc, branchTargetName);
		    	}
		    }
		}		
		return branchTargetMap;
	}	
}
