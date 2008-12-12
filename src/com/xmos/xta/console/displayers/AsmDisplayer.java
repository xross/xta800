package com.xmos.xta.console.displayers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.Vector;

import com.xmos.isa.Instruction;
import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xeloader.XeLoader;
import com.xmos.xeobject.Function;
import com.xmos.xeobject.XeObject;
import com.xmos.xta.console.Console;
import com.xmos.xta.engine.Disassembler;
import com.xmos.xta.engine.Displayer;
import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.Route;
import com.xmos.xta.engine.XtaInstruction;

public class AsmDisplayer implements Displayer {
	Console m_console;
	int m_pathId;
	XeLoader.Architecture m_architecture;
	private XeObject m_xeObject;
	
	public AsmDisplayer(Console console, int pathId, XeLoader.Architecture architecture,  XeObject xeObject) {
		m_console = console;
		m_pathId = pathId;
		m_xeObject = xeObject;
		m_architecture = architecture;
	}
	
    public void displayRoute(Route route, DebugInfo debugInfo, 
    		MemoryImage memoryImage, SymbolTable symbolTable) {

    	Block path = DisplayerHelper.getPath(route, m_pathId);
    	if (path == null) {
			m_console.display(String.format("Error: invalid path id: %d\n", m_pathId));        		
			return;
    	}
    	
        if (path.getInstructions(false).size() > 0) { 
	    	HashMap<String, TreeSet<Integer>> asmLinesCovered = createAsmPcsCovered(path, m_xeObject, false);
	    	
	        for (Iterator<Map.Entry<String, TreeSet<Integer>>> iter = 
	        	asmLinesCovered.entrySet().iterator(); iter.hasNext();) {
	        	
	            Map.Entry<String, TreeSet<Integer>> entry = 
	                (Map.Entry<String, TreeSet<Integer>>)iter.next();    
	            
	            String functionName = (String)entry.getKey();                    
	            TreeSet<Integer> functionLinesCovered = (TreeSet<Integer>)entry.getValue();

	            int functionAddress = symbolTable.functionAddress(functionLinesCovered.first());
	            int functionSize = symbolTable.functionSize(functionAddress);  
	            
	            if ((functionAddress == 0) && (functionSize == 0)) {
	            	// We can't seem to find the actual address + size (maybe the information
	            	// is missing from the symbol table, so just use the highest and lowest 
	            	// address from the path to be displayed.
	            	functionAddress = getLowestAddress(path);
	            	functionSize = getHighestAddress(path) - functionAddress;
	            }
	            
	            m_console.display(String.format("<%s>:\n", functionName));	            
	            Vector<Instruction> disassemblyVector = Disassembler.disassemble(
	            		memoryImage, functionAddress, functionSize, m_architecture);
	            
	            HashMap<Integer, String> branchTargetMap = Disassembler.createBranchTargetMap(
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

	public void notify(String message, String argument) {
	}

    private HashMap<String, TreeSet<Integer>> createAsmPcsCovered(
			Block block, XeObject xeObject, boolean recurse) {
			
        HashMap<String, TreeSet<Integer>> asmLinesCovered = new HashMap<String, TreeSet<Integer>>();
        for(XtaInstruction inst : block.getInstructions(recurse)){
            int pc = inst.getPc();
            HashMap<Integer, Function> functionMap = xeObject.getPcToFunctionMap();
            String instString = functionMap.get(pc).getText();
            if(asmLinesCovered.containsKey(instString)){
                asmLinesCovered.get(instString).add(inst.getPc());
            } else {
                TreeSet<Integer> treeList = new TreeSet<Integer>();
                treeList.add(inst.getPc());
                asmLinesCovered.put(instString, treeList);
            }
        }
        return asmLinesCovered;
	}
    
    private String getBranchDest(HashMap<Integer, String> branchTargetMap, 
    		Instruction instruction) {
    	
	    String branchDest = "";	    	    
    	int destPc = instruction.getPc();    	
	    if (branchTargetMap.containsKey(destPc)) {
		    branchDest = branchTargetMap.get(destPc);
	    }
	    return branchDest;
    }

    private String getBranchTarget(HashMap<Integer, String> branchTargetMap, 
    		Instruction instruction, SymbolTable symbolTable) {
    	
	    String branchTarget = "";	
    	if (instruction.getBranchPcRelative()) {
	    	int targetPc = instruction.getBranchPcRelativeTargetPc();	    	
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
    
    private int getLowestAddress(Block path) {
    	int address = Integer.MAX_VALUE;    	
		for (Instruction instruction : path.getInstructions(false)) {
			if (instruction.getPc() < address) {
				address = instruction.getPc();
			}
		}
        return address;	
    }
    
    private int getHighestAddress(Block path) {
    	int address = Integer.MIN_VALUE;    	
		for (Instruction instruction : path.getInstructions(false)) {
			if (instruction.getPc() > address) {
				address = instruction.getPc();
			}
		}
        return address;	
    }
}
