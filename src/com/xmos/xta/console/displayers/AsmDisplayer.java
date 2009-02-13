package com.xmos.xta.console.displayers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.Vector;

import com.xmos.isa.Instruction;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xeobject.Function;
import com.xmos.xeobject.XeObject;
import com.xmos.xta.console.Console;
import com.xmos.xta.engine.Disassembler;
import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.XtaInstruction;

public class AsmDisplayer {
	private Engine m_engine;
	private Console m_console;
	private int m_pathId;
	
	public AsmDisplayer(Engine engine, Console console, int pathId) {
		m_engine = engine;
		m_console = console;
		m_pathId = pathId;
	}
	
    public void display() {     	
    	
    	//this should check to see that there is a route to display first
    	if(m_engine.anythingToDisplay() == false){
    		//error and dont print
    		System.err.println("asm displayer shouldnt be displaying anything");
    	}
    	
    	MemoryImage memoryImage = m_engine.getMemoryImage();
    	SymbolTable symbolTable = m_engine.getSymbolTable();
    	
    	Block path = Block.getPath(m_engine.getRoute(), m_pathId);
        if (path.getInstructions(false).size() > 0) { 
	    	HashMap<String, TreeSet<Integer>> asmLinesCovered = createAsmPcsCovered(path, m_engine.getXeObject(), false);
	    	
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
	            		memoryImage, functionAddress, functionSize, m_engine.getXeLoader().getArchitecture(),
	            		m_engine.getConfig());
	            
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

    private HashMap<String, TreeSet<Integer>> createAsmPcsCovered(
			Block block, XeObject xeObject, boolean recurse) {
			
        HashMap<String, TreeSet<Integer>> asmLinesCovered = new HashMap<String, TreeSet<Integer>>();
        for(XtaInstruction inst : block.getInstructions(recurse)){
            int pc = inst.getPc();
            HashMap<Integer, Function> functionMap = xeObject.getPcToFunctionMap();
            Function function = functionMap.get(pc);
            if (function != null) {
	            String instString = function.getText();
	            if (asmLinesCovered.containsKey(instString)){
	                asmLinesCovered.get(instString).add(inst.getPc());
	            } else {
	                TreeSet<Integer> treeList = new TreeSet<Integer>();
	                treeList.add(inst.getPc());
	                asmLinesCovered.put(instString, treeList);
	            }
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
