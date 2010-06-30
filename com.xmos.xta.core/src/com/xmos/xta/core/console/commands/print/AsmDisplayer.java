package com.xmos.xta.core.console.commands.print;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.xmos.libs.isa.Instruction;
import com.xmos.libs.util.IteratableSet;
import com.xmos.libs.util.NonIteratableMap;
import com.xmos.libs.xeloader.MemoryImage;
import com.xmos.libs.xeloader.SymbolTable;
import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.binary.Disassembler;
import com.xmos.xta.core.engine.analysis.binary.XtaInstruction;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.functionmapper.Function;
import com.xmos.xta.core.engine.functionmapper.FunctionMapper;
import com.xmos.xta.core.engine.utils.CoreId;

public class AsmDisplayer {
	private Engine m_engine;
	private Console m_console;

	public AsmDisplayer(Engine engine, Console console) {
		m_engine = engine;
		m_console = console;
	}

    public void display(Node node, CoreId coreId) {
  	    List<XtaInstruction> instructions = node.getInstructions();

    	MemoryImage memoryImage = m_engine.getMemoryImage(coreId);
    	SymbolTable symbolTable = m_engine.getSymbolTable(coreId);

        if (instructions.size() > 0) {
	    	Map<String, Set<Long>> asmLinesCovered =
	    		createAsmPcsCovered(instructions, m_engine.getFunctionMapper(coreId));

	        for (Iterator<Map.Entry<String, Set<Long>>> iter =
	        	asmLinesCovered.entrySet().iterator(); iter.hasNext();) {

	            Map.Entry<String, Set<Long>> entry =
	                iter.next();

	            String functionName = entry.getKey();
	            Set<Long> functionLinesCovered = entry.getValue();

	            long functionAddress = symbolTable.functionAddress(functionLinesCovered.iterator().next());
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

	            NonIteratableMap<Long, String> branchTargetMap = Disassembler.createBranchTargetMap(
	            		symbolTable, disassemblyVector);

	            for (Instruction instruction : disassemblyVector) {
	            	String disassembly = String.format("%-7s 0x%05x %s %s %s",
	            		getBranchDest(branchTargetMap, instruction),
	            		instruction.getPc(),
                        instruction.getMnemonic(),
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

    private Map<String, Set<Long>> createAsmPcsCovered(
    		List<XtaInstruction> instructions, FunctionMapper functionMapper) {

        Map<String, Set<Long>> asmLinesCovered = new NonIteratableMap<String, Set<Long>>();
        for (XtaInstruction inst : instructions){
            Function function = functionMapper.getFunction(inst.getPc());
            if (function != null) {
	            String instString = function.getName();
	            if (asmLinesCovered.containsKey(instString)){
	                asmLinesCovered.get(instString).add(inst.getPc());
	            } else {
	                Set<Long> treeList = new IteratableSet<Long>();
	                treeList.add(inst.getPc());
	                asmLinesCovered.put(instString, treeList);
	            }
            }
        }
        return asmLinesCovered;
	}

    private String getBranchDest(NonIteratableMap<Long, String> branchTargetMap,
    		Instruction instruction) {

	    String branchDest = "";
    	long destPc = instruction.getPc();
	    if (branchTargetMap.containsKey(destPc)) {
		    branchDest = branchTargetMap.get(destPc);
	    }
	    return branchDest;
    }

    private String getBranchTarget(NonIteratableMap<Long, String> branchTargetMap,
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
