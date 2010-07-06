package com.xmos.xta.core.engine.analysis.controlflows.functionmapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xmos.libs.util.IteratableMap;
import com.xmos.libs.util.NonIteratableMap;
import com.xmos.libs.util.NonIteratableSet;
import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.analysis.binary.InstructionDecoder;
import com.xmos.xta.core.engine.analysis.controlflows.FunctionContainer;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgInstructionNode;

public class FunctionMapper {
    private List<Function> m_functions;
	private Map<Long, Function> m_pcToFunction;
	
    public FunctionMapper(Map<CfgInstructionNode, CfgInstructionNode> functionInstuctionToOriginal, 
			InstructionDecoder instructionDecoder, Set<FunctionContainer> functionContainers, 
			Map<String, Set<Long>> labels) throws AbortedException {

        m_pcToFunction = new NonIteratableMap<Long, Function>();
        m_functions = createFunctions(instructionDecoder, labels, 
        		functionContainers, functionInstuctionToOriginal);
	}

    public List<Function> getFunctions() {
        return m_functions;
    }

	public Function getFunction(long pc) {
		return m_pcToFunction.get(pc);
	}

	public boolean areProfilablePcs(Set<Long> pcs) {
		for (long pc : pcs) {
			boolean found = false;
			for (Function function : m_functions){
				if (function.containsPc(pc)){
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;
	}

    private List<Function> createFunctions(InstructionDecoder instructionDecoder, 
    		Map<String, Set<Long>> labels, Set<FunctionContainer> functionContainers,
    		Map<CfgInstructionNode, CfgInstructionNode> functionInstuctionToOriginal) {
    	
    	List<AssemblyLine> assemblyLines = createAssemblyLines(labels, instructionDecoder);
    	
    	List<CfgInstructionNode> instList = instructionDecoder.getInstructions();
    	Map<Long, Function> pcToFunction = new IteratableMap<Long, Function>();
    	
    	Set<CfgInstructionNode> starts = new NonIteratableSet<CfgInstructionNode>();
    	for (FunctionContainer fc : functionContainers){
    		starts.add(functionInstuctionToOriginal.get(fc.getStartInstruction()));
    	}
    	
    	for (FunctionContainer fc : functionContainers){	    		
    		CfgInstructionNode inst = functionInstuctionToOriginal.get(fc.getStartInstruction());
    		
    		int startIndex = instList.indexOf(inst);
    		List<AssemblyLine> functionAssembly = new ArrayList<AssemblyLine>();
    		functionAssembly.add(assemblyLines.get(startIndex));
    		int index = startIndex +1;
    		while (index < instList.size() && !starts.contains(instList.get(index))) {
    			functionAssembly.add(assemblyLines.get(index));
    			index++;
    		}

    		String fnName = fc.getFunctionName();
    		
    		Function function = new Function(fnName, functionAssembly);
    		for (AssemblyLine assembly : functionAssembly) {
    			m_pcToFunction.put(assembly.getLineNumber(), function);
    		}

    		pcToFunction.put(inst.getPc(), function);
    	}

    	// Sort the functions into ascending pc order
    	List<Long> pcs = new LinkedList<Long>();
    	pcs.addAll(pcToFunction.keySet());
    	Collections.sort(pcs);

    	List<Function> sortedFunctions = new LinkedList<Function>();
    	for (long pc : pcs){
    		sortedFunctions.add(pcToFunction.get(pc));
    	}
    	return sortedFunctions;
    }

	private List<AssemblyLine> createAssemblyLines(Map<String, Set<Long>> labels, 
			InstructionDecoder instructionDecoder) {
		
		List<AssemblyLine> asmLines = new ArrayList<AssemblyLine>();
		for (CfgInstructionNode instNode : instructionDecoder.getInstructions()){
			AssemblyLine assLine = new AssemblyLine(instNode, labels);
			asmLines.add(assLine);
		}
        return asmLines;
    }
}
