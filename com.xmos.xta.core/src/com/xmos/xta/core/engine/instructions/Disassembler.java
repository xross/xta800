package com.xmos.xta.core.engine.instructions;

import java.util.HashMap;
import java.util.Vector;

import com.xmos.isa.Instruction;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xeloader.XeLoader;
import com.xmos.xta.core.engine.Config;

public class Disassembler {      
	public static Vector<Instruction> disassemble(
			MemoryImage memoryImage, long address, long size, XeLoader.Architecture architecture, Config config) {	

		XtaInstructionDecoder instructionDecoder = 
			XtaInstructionDecoder.createInstructionDecoder(architecture);
        
        Vector<Instruction> disassemblyVector = new Vector<Instruction>();
        
        int numBytesRead = 0;
        while (numBytesRead < size) {        	
        	long pc = address + numBytesRead;
        	int opcode = 0;
        	try {
        	    opcode = memoryImage.readInstructionWord(pc);
        	} catch (Exception e) {
        	    return null;
        	}
        	XtaInstruction instruction = instructionDecoder.decode(opcode, pc);    
            disassemblyVector.add(instruction);            
        	int instNumBytes = instruction.getNumBytes();        	        	
        	numBytesRead += instNumBytes;
        }
        return disassemblyVector;
	}
	
	public static HashMap<Long, String> createBranchTargetMap(
			SymbolTable symbolTable, Vector<Instruction> instructions) {
		
		HashMap<Long, String> branchTargetMap = new HashMap<Long, String>();		
		for (Instruction instruction : instructions) {
		    if (instruction.getBranchPcRelative() && !symbolTable.functionExists(instruction.getPc())) {
		    	long branchTargetPc = instruction.getBranchPcRelativeTargetPc();
		    	
		    	if (!branchTargetMap.containsKey(branchTargetPc)) {
		    		String branchTargetName = ".bt" + branchTargetMap.size();
		    		branchTargetMap.put(branchTargetPc, branchTargetName);
		    	}
		    }
		}		
		return branchTargetMap;
	}	
}
