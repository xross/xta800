package com.xmos.xta.console.commands.print;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Vector;

import com.xmos.isa.Instruction;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xta.console.Console;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.instructions.Disassembler;
import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.sourcemapper.Function;
import com.xmos.xta.sourcemapper.SourceMapper;

public class AsmDisplayer {
	private Engine m_engine;
	private Console m_console;
	
	public AsmDisplayer(Engine engine, Console console) {
		m_engine = engine;
		m_console = console;
	}
	
    public void display(Node node) {     	      

  	    List<XtaInstruction> instructions = node.getInstructions();
  	    
    	MemoryImage memoryImage = m_engine.getMemoryImage();
    	SymbolTable symbolTable = m_engine.getSymbolTable();
    	    	
        if (instructions.size() > 0) { 
	    	HashMap<String, TreeSet<Long>> asmLinesCovered = 
	    		createAsmPcsCovered(instructions, m_engine.getSourceMapper());
	    	
	        for (Iterator<Map.Entry<String, TreeSet<Long>>> iter = 
	        	asmLinesCovered.entrySet().iterator(); iter.hasNext();) {
	        	
	            Map.Entry<String, TreeSet<Long>> entry = 
	                (Map.Entry<String, TreeSet<Long>>)iter.next();    
	            
	            String functionName = (String)entry.getKey();                    
	            TreeSet<Long> functionLinesCovered = (TreeSet<Long>)entry.getValue();

	            long functionAddress = symbolTable.functionAddress(functionLinesCovered.first());
	            long functionSize = symbolTable.functionSize(functionAddress);  
	            
	            //TODO figure out if this can actually happen
	            if ((functionAddress == 0) && (functionSize == 0)) {
	            	// We can't seem to find the actual address + size (maybe the information
	            	// is missing from the symbol table, so just use the highest and lowest 
	            	// address from the path to be displayed.
	            	functionAddress = getLowestAddress(instructions);
	            	functionSize = getHighestAddress(instructions) - functionAddress;
	            }
	            
	            m_console.display(String.format("<%s>:\n", functionName));	            
	            Vector<Instruction> disassemblyVector = Disassembler.disassemble(
	            		memoryImage, functionAddress, functionSize, m_engine.getXeLoader().getArchitecture(),
	            		m_engine.getConfig());
	            
	            HashMap<Long, String> branchTargetMap = Disassembler.createBranchTargetMap(
	            		symbolTable, disassemblyVector);	            
	            
	            for (Instruction instruction : disassemblyVector) {
	            	String disassembly = String.format("%-7s 0x%05x %s %s %s", 
	            		getBranchDest(branchTargetMap, instruction), 
	            		instruction.getPc(),
                        instruction.getNmenonic(), 
                        instruction.getOperandString(),
                		getBranchTarget(branchTargetMap, instruction, symbolTable));
	            	
	            	if (functionLinesCovered.contains(instruction.getPc()))
	            		m_console.display(String.format("+ %s\n", disassembly));
	            	else
	            		m_console.display(String.format("  %s\n", disassembly));	            		
	            }
	            m_console.display("\n");
	            m_console.display("...\n");
	            m_console.display("\n");    	            
	        }
        }
    }

    private HashMap<String, TreeSet<Long>> createAsmPcsCovered(
    		List<XtaInstruction> instructions, SourceMapper sourceMapper) {
			
        HashMap<String, TreeSet<Long>> asmLinesCovered = new HashMap<String, TreeSet<Long>>();
        for (XtaInstruction inst : instructions){
            Function function = sourceMapper.getFunction(inst.getPc());
            if (function != null) {
	            String instString = function.getText();
	            if (asmLinesCovered.containsKey(instString)){
	                asmLinesCovered.get(instString).add(inst.getPc());
	            } else {
	                TreeSet<Long> treeList = new TreeSet<Long>();
	                treeList.add(inst.getPc());
	                asmLinesCovered.put(instString, treeList);
	            }
            }
        }
        return asmLinesCovered;
	}
    
    private String getBranchDest(HashMap<Long, String> branchTargetMap, 
    		Instruction instruction) {
    	
	    String branchDest = "";	    	    
    	long destPc = instruction.getPc();    	
	    if (branchTargetMap.containsKey(destPc)) {
		    branchDest = branchTargetMap.get(destPc);
	    }
	    return branchDest;
    }

    private String getBranchTarget(HashMap<Long, String> branchTargetMap, 
    		Instruction instruction, SymbolTable symbolTable) {
    	
	    String branchTarget = "";	
    	if (instruction.getBranchPcRelative()) {
	    	long targetPc = instruction.getBranchPcRelativeTargetPc();	    	
    		if (symbolTable.functionExists(targetPc)) {
    		    branchTarget = "<" + symbolTable.functionName(targetPc) + ">";	
    		}
    		else {
		        if (branchTargetMap.containsKey(targetPc)) {
		    	    branchTarget = "<" + branchTargetMap.get(targetPc) + ">";
		        }
    		}
    	}
	    return branchTarget;
    }
    
    private long getLowestAddress(List<XtaInstruction> instructions) {
    	long address = Long.MAX_VALUE;    	
		for (Instruction instruction : instructions) {
			if (instruction.getPc() < address) {
				address = instruction.getPc();
			}
		}
        return address;	
    }
    
    private long getHighestAddress(List<XtaInstruction> instructions) {
    	long address = Long.MIN_VALUE;    	
		for (Instruction instruction : instructions) {
			if (instruction.getPc() > address) {
				address = instruction.getPc();
			}
		}
        return address;	
    }
}
